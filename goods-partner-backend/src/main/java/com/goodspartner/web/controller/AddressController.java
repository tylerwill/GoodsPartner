package com.goodspartner.web.controller;

import com.goodspartner.dto.AddressExternalDto;
import com.goodspartner.entity.AddressExternal;
import com.goodspartner.mapper.AddressExternalMapper;
import com.goodspartner.service.AddressExternalService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/addresses", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AddressController {

    private final AddressExternalService service;
    private final AddressExternalMapper mapper;

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGISTICIAN')")
    @GetMapping
    @ApiOperation(value = "Get all GrandeDolceAddresses",
            notes = "Return list of AddressesExternalDto",
            response = List.class)
    @ResponseStatus(HttpStatus.OK)
    public List<AddressExternalDto> getAll() {
        return service
                .findAll()
                .stream()
                .map(mapper::toAddressExternalDto)
                .toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGISTICIAN')")
    @PutMapping
    @ApiOperation(value = "Edit AddressExternal",
            notes = "Provide an AddressExternalDto")
    @ResponseStatus(HttpStatus.OK)
    public AddressExternalDto update(
            @ApiParam(value = "Edited AddressExternalDto", type = "AddressExternalDto", required = true)
            @RequestBody AddressExternalDto addressExternalDto) {
        AddressExternal convertedAddressExternal = mapper.toAddressExternal(addressExternalDto);
        AddressExternal updatedAddressExternal = service.update(convertedAddressExternal);
        return mapper.toAddressExternalDto(updatedAddressExternal);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LOGISTICIAN')")
    @DeleteMapping
    @ApiOperation(value = "Delete AddressExternal",
            notes = "Provide an AddressExternalDto")
    @ResponseStatus(HttpStatus.OK)
    public void delete(
            @ApiParam(value = "Deleted AddressExternalDto", type = "AddressExternalDto", required = true)
            @RequestBody AddressExternalDto addressExternalDto) {
        AddressExternal convertedAddressExternal = mapper.toAddressExternal(addressExternalDto);

        service.delete(convertedAddressExternal);
    }
}
