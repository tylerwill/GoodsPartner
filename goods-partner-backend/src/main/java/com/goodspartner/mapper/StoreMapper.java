package com.goodspartner.mapper;

import com.goodspartner.dto.MapPoint;
import com.goodspartner.dto.StoreDto;
import com.goodspartner.entity.Store;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import static com.goodspartner.dto.MapPoint.AddressStatus.KNOWN;

@Mapper(componentModel = "spring")
public interface StoreMapper {

    @Mapping(target = "mapPoint", source = "store", qualifiedByName = "getMapPoint")
    StoreDto toStoreDto(Store store);

    @Named("getMapPoint")
    default MapPoint getMapPoint(Store store) {
        return MapPoint.builder()
                .address(store.getAddress())
                .latitude(store.getLatitude())
                .longitude(store.getLongitude())
                .status(KNOWN)
                .build();
    }
}
