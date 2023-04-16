package com.goodspartner.mapper;

import com.goodspartner.dto.AddressExternalDto;
import com.goodspartner.dto.MapPoint;
import com.goodspartner.entity.AddressExternal;
import com.goodspartner.entity.AddressStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AddressExternalMapperTest {

    AddressExternalMapper addressExternalMapper = new AddressExternalMapperImpl();

    @Test
    void shouldConvertAddressExternalToAddressExternalDto() {
        AddressExternal instanceAddressExternalToBeConverted = createAddressExternal();
        AddressExternalDto expectedAddressExternalDto = createAddressExternalDto();

        AddressExternalDto resultAddressExternalDto = addressExternalMapper.toAddressExternalDto(instanceAddressExternalToBeConverted);

        assertTrue(AddressExternalDto.class.isAssignableFrom(resultAddressExternalDto.getClass()));
        assertEquals(expectedAddressExternalDto, resultAddressExternalDto, "content of result and expected AddressExternalDto should be identical");
    }

    private AddressExternalDto createAddressExternalDto() {
        MapPoint mapPoint = MapPoint.builder()
                .address("Zoloti vorota")
                .latitude(48.5689)
                .longitude(30.7898)
                .status(AddressStatus.KNOWN)
                .build();
        return new AddressExternalDto("Zoloti vorota",
                "Pasha", mapPoint);
    }

    private AddressExternal createAddressExternal() {
        AddressExternal addressExternal = new AddressExternal();
        addressExternal.setOrderAddressId(new AddressExternal.OrderAddressId("Zoloti vorota", "Pasha"));
        addressExternal.setStatus(AddressStatus.KNOWN);
        addressExternal.setValidAddress("Zoloti vorota");
        addressExternal.setLatitude(48.5689);
        addressExternal.setLongitude(30.7898);

        return addressExternal;
    }


}
