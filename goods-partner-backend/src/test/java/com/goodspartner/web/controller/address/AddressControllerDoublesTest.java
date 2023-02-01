package com.goodspartner.web.controller.address;

import com.goodspartner.dto.AddressExternalDto;
import com.goodspartner.entity.AddressExternal;
import com.goodspartner.mapper.AddressExternalMapper;
import com.goodspartner.service.AddressExternalService;
import com.goodspartner.web.controller.AddressController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(value = {MockitoExtension.class})
public class AddressControllerDoublesTest {
    private AddressExternal addressA;
    private AddressExternal addressB;
    private AddressExternalDto addressDtoA;
    private AddressExternalDto addressDtoB;
    @Mock
    private AddressExternalService service;

    @Mock
    private AddressExternalMapper mapper;

    private AddressController controller;

    @BeforeEach
    void setUp() {
        controller = new AddressController(service, mapper);
        addressA = mock(AddressExternal.class);
        addressB = mock(AddressExternal.class);
        addressDtoA = mock(AddressExternalDto.class);
        addressDtoB = mock(AddressExternalDto.class);
    }

    @Test
    void testFindAllShouldCallMapperAndServiceInsideMethod() {
        List<AddressExternalDto> expectedAddressExternalDtoList = List.of(addressDtoA, addressDtoB);

        List<AddressExternal> stubList = List.of(addressA, addressB);
        when(service.findAll()).thenReturn(stubList);
        when(mapper.toAddressExternalDto(stubList.get(0))).thenReturn(addressDtoA);
        when(mapper.toAddressExternalDto(stubList.get(1))).thenReturn(addressDtoB);

        List<AddressExternalDto> resultList = controller.getAll();

        assertEquals(expectedAddressExternalDtoList, resultList);

        verify(service).findAll();
        verify(mapper).toAddressExternalDto(stubList.get(0));
        verify(mapper).toAddressExternalDto(stubList.get(1));
    }

    @Test
    void testUpdateShouldCallMapperAndServiceInsideMethod() {
        when(mapper.toAddressExternal(addressDtoA)).thenReturn(addressA);
        when(service.update(addressA)).thenReturn(addressA);
        when(mapper.toAddressExternalDto(addressA)).thenReturn(addressDtoA);

        AddressExternalDto resultUpdatedAddressExternalDto = controller.update(addressDtoA);

        assertEquals(addressDtoA, resultUpdatedAddressExternalDto);

        verify(mapper).toAddressExternal(addressDtoA);
        verify(mapper).toAddressExternalDto(addressA);
        verify(service).update(addressA);
    }

    @Test
    void testDeleteShouldCallMapperAndServiceInsideMethod() {
        when(mapper.toAddressExternal(addressDtoA)).thenReturn(addressA);
        doNothing().when(service).delete(addressA);

        controller.delete(addressDtoA);

        verify(mapper).toAddressExternal(addressDtoA);
        verify(service).delete(addressA);
    }
}
