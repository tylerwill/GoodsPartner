package com.goodspartner.service.impl;

import com.goodspartner.dto.DeliveryDto;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.DeliveryStatus;
import com.goodspartner.entity.Route;
import com.goodspartner.entity.RouteStatus;
import com.goodspartner.entity.User;
import com.goodspartner.exception.CarNotFoundException;
import com.goodspartner.exception.DeliveryNotFoundException;
import com.goodspartner.exception.delivery.IllegalDeliveryStateForDeletion;
import com.goodspartner.mapper.DeliveryMapper;
import com.goodspartner.repository.CarRepository;
import com.goodspartner.repository.DeliveryRepository;
import com.goodspartner.service.DeliveryService;
import com.goodspartner.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.goodspartner.entity.DeliveryFormationStatus.ORDERS_LOADING;
import static com.goodspartner.entity.DeliveryStatus.DRAFT;
import static com.goodspartner.entity.User.UserRole.DRIVER;

@Slf4j
@AllArgsConstructor
@Service
public class DefaultDeliveryService implements DeliveryService {

    private static final Sort DEFAULT_DELIVERY_SORT = Sort.by(Sort.Direction.DESC, "deliveryDate");

    private final DeliveryMapper deliveryMapper;
    private final DeliveryRepository deliveryRepository;
    private final CarRepository carRepository;

    private final UserService userService;

    @Override
    @Transactional(readOnly = true)
    public List<Delivery> findAll() {
        return Optional.of(userService.findByAuthentication())
                .filter(user -> DRIVER.equals(user.getRole()))
                .map(this::findDeliveriesByDriver)
                .orElseGet(() -> deliveryRepository.findAll(DEFAULT_DELIVERY_SORT));
    }

    private List<Delivery> findDeliveriesByDriver(User driver) {
        return carRepository.findCarByDriver(driver)
                .map(car -> deliveryRepository.findDeliveriesByCarAndStatus(car, DEFAULT_DELIVERY_SORT))
                .orElseThrow(() -> new CarNotFoundException(driver));
    }

    @Transactional
    @Override
    public Delivery delete(UUID deliveryId) {
        Delivery deliveryToDelete = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException(deliveryId));

        if (DRAFT != deliveryToDelete.getStatus()) {
            throw new IllegalDeliveryStateForDeletion();
        }

        // Hard delete entries in Draft status
        deliveryRepository.delete(deliveryToDelete);

        return deliveryToDelete;
    }

    @Override
    @Transactional(readOnly = true)
    public Delivery findById(UUID deliveryId) {
        return deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new DeliveryNotFoundException(deliveryId));
    }

    @Override
    public DeliveryDto findByStatusAndDeliveryDate(DeliveryStatus status, LocalDate date) {
        Delivery delivery = deliveryRepository.findByStatusAndDeliveryDate(status, date)
                .orElseThrow(() -> new DeliveryNotFoundException(status, date));
        return deliveryMapper.toDeliveryDto(delivery);
    }

    @Override
    public List<DeliveryDto> findByStatusAndDeliveryDateBetween(DeliveryStatus status, LocalDate dateFrom, LocalDate dateTo) {
        return deliveryRepository.findByStatusAndDeliveryDateBetween(status, dateFrom, dateTo)
                .stream()
                .map(deliveryMapper::toDeliveryDto)
                .toList();
    }

    @Transactional
    @Override
    public Delivery processDeliveryStatus(Route route) {
        Delivery delivery = deliveryRepository.findById(route.getDelivery().getId())
                .orElseThrow(() -> new DeliveryNotFoundException(route.getDelivery().getId()));
        if (isAllRoutesCompleted(delivery)) {
            delivery.setStatus(DeliveryStatus.COMPLETED);
            deliveryRepository.save(delivery);
            log.info("Delivery ID {} was automatically close due to all Routes are COMPLETED", route.getId());
        }
        return delivery;
    }

    private boolean isAllRoutesCompleted(Delivery delivery) {
        return delivery.getRoutes().stream()
                .filter(route -> !route.getStatus().equals(RouteStatus.COMPLETED))
                .findFirst()
                .isEmpty();
    }

    @Override
    @Transactional
    public Delivery add(DeliveryDto deliveryDto) {
        deliveryRepository.findByDeliveryDate(deliveryDto.getDeliveryDate())
                .ifPresent(delivery -> {
                    throw new IllegalArgumentException("There is already delivery on date: " + deliveryDto.getDeliveryDate());
                });

        deliveryDto.setStatus(DRAFT);
        deliveryDto.setFormationStatus(ORDERS_LOADING);

        return deliveryRepository.save(deliveryMapper.toDelivery(deliveryDto));
    }

    @Override
    public void update(Delivery delivery) {
        if (Objects.isNull(delivery.getId())) {
            throw new IllegalArgumentException("Not a persistent delivery by date: " + delivery.getDeliveryDate());
        }
        deliveryRepository.save(delivery); // assume to update delivery with id;
    }
}
