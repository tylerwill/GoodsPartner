package com.goodspartner.service.impl;

import com.goodspartner.entity.AddressExternal;
import com.goodspartner.entity.AddressStatus;
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

    private AddressExternalService addressExternalService;

    @BeforeEach
    public void setUp() {
        addressExternalService = new DefaultAddressExternalService(repository);
    }

    @Test
    void testFindAllShouldReturnListAddressExternalDtoAndCallAllCollaboratorsMethods() {
        List<AddressExternal> addressExternalList = createAddressExternalList();

        when(repository.findAll()).thenReturn(addressExternalList);

        List<AddressExternal> resultAddressExternalList = addressExternalService.findAll();

        assertEquals(addressExternalList.size(), resultAddressExternalList.size());
        assertEquals(addressExternalList.get(0), resultAddressExternalList.get(0));
        assertEquals(addressExternalList.get(1), resultAddressExternalList.get(1));

        verify(repository).findAll();
    }

    @Test
    void testUpdateShouldReturnAddressExternalDtoAndCallAllCollaboratorsMethods() {
        AddressExternal incomeAddressExternal = createAddressExternal(CLIENT_NAME_A);
        AddressExternal.OrderAddressId id = incomeAddressExternal.getOrderAddressId();

        when(repository.findById(id)).thenReturn(Optional.of(incomeAddressExternal));
        when(repository.save(incomeAddressExternal)).thenReturn(incomeAddressExternal);

        AddressExternal updatedAddressExternalDto = addressExternalService.update(incomeAddressExternal);

        assertEquals(incomeAddressExternal, updatedAddressExternalDto);

        verify(repository).save(incomeAddressExternal);
        verify(repository).findById(id);
    }

    @Test
    void testDeleteShouldCallAllCollaboratorsMethods() {
        AddressExternal incomeAddressExternal = createAddressExternal(CLIENT_NAME_A);
        AddressExternal.OrderAddressId id = incomeAddressExternal.getOrderAddressId();

        when(repository.findById(id)).thenReturn(Optional.of(incomeAddressExternal));
        doNothing().when(repository).delete(incomeAddressExternal);

        addressExternalService.delete(incomeAddressExternal);

        verify(repository).findById(id);
        verify(repository).delete(incomeAddressExternal);
    }

    private List<AddressExternal> createAddressExternalList() {
        return new ArrayList<>(Arrays.asList(createAddressExternal(CLIENT_NAME_A),
                createAddressExternal(CLIENT_NAME_B)));
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
