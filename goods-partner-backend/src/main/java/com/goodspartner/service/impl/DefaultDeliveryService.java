package com.goodspartner.service.impl;

import com.goodspartner.dto.DeliveryDto;
import com.goodspartner.entity.Delivery;
import com.goodspartner.exceptions.DeliveryNotFoundException;
import com.goodspartner.mapper.DeliveryMapper;
import com.goodspartner.repository.DeliveryRepository;
import com.goodspartner.service.DeliveryService;
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

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryDto> findAll() {
        return deliveryMapper.deliveriesToDeliveryDtos(deliveryRepository.findAll());
    }

    @Override
    public void delete(UUID id) {
        deliveryRepository.findById(id)
                .orElseThrow(() -> new DeliveryNotFoundException("There is no delivery with id: " + id));
        deliveryRepository.deleteById(id);
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
    public void add(DeliveryDto deliveryDto) {
        Optional<Delivery> optionalDelivery = deliveryRepository.findByDeliveryDate(deliveryDto.getDeliveryDate());
        if (optionalDelivery.isPresent()) {
            throw new IllegalArgumentException("There is already delivery on date: " + deliveryDto.getDeliveryDate());
        }

        deliveryRepository.save(deliveryMapper.deliveryDtoToDelivery(deliveryDto));
    }

    @Override
    @Transactional
    public void update(UUID id, DeliveryDto deliveryDto) {
        Delivery updatedDelivery = deliveryRepository.findById(id)
                .map(delivery -> deliveryMapper.update(delivery, deliveryDto))
                .orElseThrow(() -> new DeliveryNotFoundException("Delivery not found"));

        deliveryRepository.save(updatedDelivery);
    }

}
