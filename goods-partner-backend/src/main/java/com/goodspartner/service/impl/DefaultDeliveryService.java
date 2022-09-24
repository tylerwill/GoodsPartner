package com.goodspartner.service.impl;

import com.goodspartner.dto.DeliveryDto;
import com.goodspartner.dto.StoreDto;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.DeliveryStatus;
import com.goodspartner.exceptions.DeliveryModifyException;
import com.goodspartner.exceptions.DeliveryNotFoundException;
import com.goodspartner.mapper.DeliveryMapper;
import com.goodspartner.repository.DeliveryRepository;
import com.goodspartner.service.CalculateRouteService;
import com.goodspartner.service.CarLoadService;
import com.goodspartner.service.DeliveryService;
import com.goodspartner.service.StoreService;
import com.goodspartner.web.controller.response.RoutesCalculation;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Service
public class DefaultDeliveryService implements DeliveryService {

    private final DeliveryMapper deliveryMapper;
    private final DeliveryRepository deliveryRepository;
    private final StoreService storeFactory;
    private final CalculateRouteService calculateRouteService;
    private final CarLoadService carLoadService;


    @Override
    @Transactional(readOnly = true)
    public List<DeliveryDto> findAll() {
        return deliveryMapper.deliveriesToDeliveryDtos(deliveryRepository.findAll());
    }

    @Override
    public DeliveryDto delete(UUID id) {
        Delivery deliveryToDelete = deliveryRepository.findById(id)
                .orElseThrow(() -> new DeliveryNotFoundException("There is no delivery with id: " + id));
        deliveryRepository.deleteById(id);

        return deliveryMapper.toDeliveryDtoResult(new DeliveryDto(), deliveryToDelete);
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryDto findById(UUID id) {
        return deliveryRepository.findById(id)
                .map(deliveryMapper::deliveryToDeliveryDto)
                .orElseThrow(() -> new DeliveryNotFoundException("There is no delivery with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryDto findByDeliveryDate(LocalDate date) {
        return deliveryRepository.findByDeliveryDate(date)
                .map(deliveryMapper::deliveryToDeliveryDto)
                .orElseThrow(() -> new DeliveryNotFoundException("There is no delivery on date: " + date));
    }

    @Override
    @Transactional
    public DeliveryDto add(DeliveryDto deliveryDto) {
        Optional<Delivery> optionalDelivery = deliveryRepository.findByDeliveryDate(deliveryDto.getDeliveryDate());
        if (optionalDelivery.isPresent()) {
            throw new IllegalArgumentException("There is already delivery on date: " + deliveryDto.getDeliveryDate());
        }

        Delivery addedDelivery = deliveryRepository.save(deliveryMapper.deliveryDtoToDelivery(deliveryDto));

        return deliveryMapper.toDeliveryDtoResult(new DeliveryDto(), addedDelivery);
    }

    @Override
    @Transactional
    public DeliveryDto update(UUID id, DeliveryDto deliveryDto) {
        Delivery deliveryToUpdate = deliveryRepository.findById(id)
                .map(delivery -> deliveryMapper.update(delivery, deliveryDto))
                .orElseThrow(() -> new DeliveryNotFoundException("Delivery not found"));

        Delivery updatedDelivery = deliveryRepository.save(deliveryToUpdate);
        return deliveryMapper.toDeliveryDtoResult(new DeliveryDto(), updatedDelivery);
    }

    @Override
    @Transactional
    public DeliveryDto calculateDelivery(UUID deliveryID) {
        DeliveryDto deliveryDto = this.findById(deliveryID);
        checkBeforeCalculate(deliveryDto);

        StoreDto store = storeFactory.getMainStore();
        List<RoutesCalculation.RouteDto> routes = calculateRouteService.calculateRoutes(deliveryDto.getOrders(), store);
        List<RoutesCalculation.CarLoadDto> carsDetails = carLoadService.map(routes, deliveryDto.getOrders());

        deliveryDto.setRoutes(routes);
        deliveryDto.setCarLoads(carsDetails);

        Delivery delivery = deliveryMapper.deliveryDtoToDelivery(deliveryDto);
        delivery.setOrders(delivery.getCarLoads().stream()
                .flatMap(carLoad -> carLoad.getOrders().stream()).toList());
        Delivery save = deliveryRepository.save(delivery);

        return deliveryMapper.deliveryToDeliveryDto(save);
    }

    @Override
    @Transactional
    public DeliveryDto reCalculateDelivery(UUID deliveryID) {
        DeliveryDto deliveryDto = this.findById(deliveryID);
        Delivery delivery = deliveryMapper.deliveryDtoToDelivery(deliveryDto);
        delivery.removeRoutes();
        delivery.removeCarLoads();
        deliveryRepository.save(delivery);

        return this.calculateDelivery(deliveryID);
    }

    private void checkBeforeCalculate(DeliveryDto deliveryDto) {
        if (deliveryDto.getStatus() != DeliveryStatus.DRAFT) {
            throw new DeliveryModifyException("Delivery modification not allowed");
        }

        if (deliveryDto.getOrders().isEmpty()) {
            throw new DeliveryNotFoundException("No orders for delivery ID: " + deliveryDto.getId());
        }

        if (!deliveryDto.getRoutes().isEmpty() || !deliveryDto.getCarLoads().isEmpty()) {
            throw new DeliveryModifyException("Delivery already calculated ID: " + deliveryDto.getId());
        }
    }
}
