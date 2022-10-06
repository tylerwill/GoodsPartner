package com.goodspartner.mapper;

import com.goodspartner.dto.ProductLoadDto;
import com.goodspartner.dto.ProductShippingDto;
import com.goodspartner.entity.Car;
import com.goodspartner.entity.CarLoad;
import com.google.common.annotations.VisibleForTesting;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

@Component
public class ProductShippingMapper {

    public List<ProductShippingDto> getCarloadByProduct(List<CarLoad> carLoads) {
        if (carLoads == null) {
            return null;
        }

        Map<String, List<ProductLoadDto>> shippingProductMap = getProductLoadMap(carLoads);

        return getProductShippingList(shippingProductMap);
    }

    @VisibleForTesting
    Map<String, List<ProductLoadDto>> getProductLoadMap(List<CarLoad> carLoads) {
        return carLoads.stream()
                .flatMap(carLoad -> carLoad.getOrders().stream()
                        .flatMap(orderExternal -> orderExternal.getProducts().stream()
                                .map(product -> Pair.of(product.getProductName(), new ProductLoadDto(
                                        orderExternal.getOrderNumber(),
                                        this.getCarNameAndLicencePlate(carLoad.getCar()),
                                        product.getAmount(),
                                        product.getUnitWeight(),
                                        product.getTotalProductWeight()))
                                )
                        )
                )
                .collect(groupingBy(Pair::getFirst, mapping(Pair::getSecond, toList())));
    }

    @VisibleForTesting
    List<ProductShippingDto> getProductShippingList(Map<String, List<ProductLoadDto>> shippingProductMap) {
        List<ProductShippingDto> productShippingDtos = new ArrayList<>(1);
        for (Map.Entry<String, List<ProductLoadDto>> entry : shippingProductMap.entrySet()) {
            String article = entry.getKey();
            int amount = 0;
            double weight = 0;
            for (ProductLoadDto productLoadDto : entry.getValue()) {
                amount += productLoadDto.getAmount();
                weight += productLoadDto.getTotalWeight();
            }
            productShippingDtos.add(new ProductShippingDto(article, amount, weight, entry.getValue()));
        }

        return productShippingDtos;
    }

    @VisibleForTesting
    String getCarNameAndLicencePlate(Car car) {
        return String.format("%s (%s)", car.getName(), car.getLicencePlate());
    }
}
