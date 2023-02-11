package com.goodspartner.web.controller;

import com.goodspartner.dto.SettingsDto;
import com.goodspartner.service.SettingsCache;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/settings", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class SettingsController {

    private final SettingsCache settingsCache;

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping
    @ApiOperation(value = "Get all Settings",
            notes = "Return SettingsDto",
            response = SettingsDto.class)
    @ResponseStatus(HttpStatus.OK)
    public SettingsDto getAll() {
        return settingsCache.getSettings();
    }

    /**
     * Configurations always present even if DB is empty
     */
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping
    @ApiOperation(value = "Update Settings",
            notes = "Provide an SettingsDto",
            consumes = "SettingsDto.class")
    @ResponseStatus(HttpStatus.OK)
    public SettingsDto updateSettings(@RequestBody SettingsDto settingsDto) {
        return settingsCache.updateSettings(settingsDto);
    }
}
