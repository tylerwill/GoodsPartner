package com.goodspartner.service.impl;

import com.goodspartner.dto.AddressExternalDto;
import com.goodspartner.entity.AddressExternal;
import com.goodspartner.entity.AddressStatus;
import com.goodspartner.mapper.AddressExternalMapper;
import com.goodspartner.repository.AddressExternalRepository;
import com.goodspartner.service.AddressExternalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(value = MockitoExtension.class)
public class DefaultAddressExternalServiceTest {
    private final static String CLIENT_NAME_A = "Pasha";
    private final static String CLIENT_NAME_B = "Sasha";
    @Mock
    private AddressExternalRepository repository;
    @Mock
    private AddressExternalMapper mapper;

    private AddressExternalService addressExternalService;

    @BeforeEach
    public void setUp() {
        addressExternalService = new DefaultAddressExternalService(repository, mapper);
    }

    @Test
    void testFindAllShouldReturnListAddressExternalDtoAndCallAllCollaboratorsMethods() {
        List<AddressExternal> addressExternalList = createAddressExternalList();
        List<AddressExternalDto> expectedExternalDtoList = createExternalDtoList();

        when(repository.findAll()).thenReturn(addressExternalList);
        when(mapper.toAddressExternalDto(addressExternalList.get(0))).thenReturn(expectedExternalDtoList.get(0));
        when(mapper.toAddressExternalDto(addressExternalList.get(1))).thenReturn(expectedExternalDtoList.get(1));

        List<AddressExternalDto> resultExternalDtoList = addressExternalService.findAll();

        assertEquals(expectedExternalDtoList.size(), resultExternalDtoList.size());
        assertEquals(expectedExternalDtoList.get(0), resultExternalDtoList.get(0));
        assertEquals(expectedExternalDtoList.get(1), resultExternalDtoList.get(1));

        verify(repository).findAll();
        verify(mapper).toAddressExternalDto(addressExternalList.get(0));
        verify(mapper).toAddressExternalDto(addressExternalList.get(1));
    }

    @Test
    void testUpdateShouldReturnAddressExternalDtoAndCallAllCollaboratorsMethods() {
        AddressExternalDto incomeAddressExternalDto = createAddressExternalDto(CLIENT_NAME_A);
        AddressExternal expectedAddressExternal = createAddressExternal(CLIENT_NAME_A);
        AddressExternal.OrderAddressId id = expectedAddressExternal.getOrderAddressId();

        when(mapper.toAddressExternal(incomeAddressExternalDto)).thenReturn(expectedAddressExternal);
        when(repository.findById(id)).thenReturn(Optional.of(expectedAddressExternal));
        when(repository.save(expectedAddressExternal)).thenReturn(expectedAddressExternal);
        when(mapper.toAddressExternalDto(expectedAddressExternal)).thenReturn(incomeAddressExternalDto);

        AddressExternalDto updatedAddressExternalDto = addressExternalService.update(incomeAddressExternalDto);

        assertEquals(incomeAddressExternalDto, updatedAddressExternalDto);

        verify(repository).save(expectedAddressExternal);
        verify(repository).findById(id);
        verify(mapper).toAddressExternal(incomeAddressExternalDto);
        verify(mapper).toAddressExternalDto(expectedAddressExternal);
    }

    @Test
    void testDeleteShouldCallAllCollaboratorsMethods() {
        AddressExternalDto incomeAddressExternalDto = createAddressExternalDto(CLIENT_NAME_A);
        AddressExternal expectedAddressExternal = createAddressExternal(CLIENT_NAME_A);
        AddressExternal.OrderAddressId id = expectedAddressExternal.getOrderAddressId();

        when(mapper.toAddressExternal(incomeAddressExternalDto)).thenReturn(expectedAddressExternal);
        when(repository.findById(id)).thenReturn(Optional.of(expectedAddressExternal));
        doNothing().when(repository).delete(expectedAddressExternal);

        addressExternalService.delete(incomeAddressExternalDto);

        verify(mapper).toAddressExternal(incomeAddressExternalDto);
        verify(repository).findById(id);
        verify(repository).delete(expectedAddressExternal);
    }

    private List<AddressExternalDto> createExternalDtoList() {
        return new ArrayList<>(Arrays.asList(createAddressExternalDto(CLIENT_NAME_A),
                createAddressExternalDto(CLIENT_NAME_B)));
    }

    private List<AddressExternal> createAddressExternalList() {
        return new ArrayList<>(Arrays.asList(createAddressExternal(CLIENT_NAME_A),
                createAddressExternal(CLIENT_NAME_B)));
    }

    private AddressExternalDto createAddressExternalDto(String clientName) {
        return new AddressExternalDto("Zoloti vorota",
                clientName, "KNOWN", "Zoloti vorota",
                48.5689, 30.7898);
    }

    private AddressExternal createAddressExternal(String clientName) {
        AddressExternal addressExternal = new AddressExternal();
        addressExternal.setOrderAddressId(new AddressExternal.OrderAddressId("Zoloti vorota", clientName));
        addressExternal.setStatus(AddressStatus.KNOWN);
        addressExternal.setValidAddress("Zoloti vorota");
        addressExternal.setLatitude(48.5689);
        addressExternal.setLongitude(30.7898);

        return addressExternal;
    }
}
