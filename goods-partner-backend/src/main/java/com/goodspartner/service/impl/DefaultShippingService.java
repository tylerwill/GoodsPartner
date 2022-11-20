package com.goodspartner.service.impl;

import com.goodspartner.dto.ProductShippingDto;
import com.goodspartner.mapper.ProductShippingMapper;
import com.goodspartner.repository.CarLoadRepository;
import com.goodspartner.service.ShippingService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class DefaultShippingService implements ShippingService {
    private final ProductShippingMapper productShippingMapper;
    private final CarLoadRepository carLoadRepository;

    @Override
    public List<ProductShippingDto> findByDeliveryId(UUID id) {
        return productShippingMapper.getCarloadByProduct(carLoadRepository.findByDeliveryId(id));

    }
}
