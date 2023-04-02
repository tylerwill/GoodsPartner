package com.goodspartner.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goodspartner.annotations.SettingsAllocation;
import com.goodspartner.dto.ClientBusinessPropertiesDto;
import com.goodspartner.dto.ClientRoutingPropertiesDto;
import com.goodspartner.dto.SettingsDto;
import com.goodspartner.entity.Setting;
import com.goodspartner.entity.SettingsCategory;
import com.goodspartner.entity.SettingsGroup;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.goodspartner.entity.SettingsCategory.BUSINESS;
import static com.goodspartner.entity.SettingsCategory.ROUTING;
import static com.goodspartner.entity.SettingsGroup.CLIENT;

@Component
public class SettingParser {

    private final ObjectMapper objectMapper;

    private final Map<Setting.SettingKey, BiConsumer<SettingsDto, String>> keyToSettingFunctionMap;

    public SettingParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.keyToSettingFunctionMap = Map.of(
                toKey(CLIENT, ROUTING),
                (settingsDto, properties) -> settingsDto.setClientRoutingProperties(mapProps(objectMapper, properties, ClientRoutingPropertiesDto.class)),
                toKey(CLIENT, BUSINESS),
                (settingsDto, properties) -> settingsDto.setClientBusinessProperties(mapProps(objectMapper, properties, ClientBusinessPropertiesDto.class))
        );
    }

    public List<Setting> getSettingsList(SettingsDto settingExternalDto) {
        return Stream.of(settingExternalDto.getClientRoutingProperties(),
                        settingExternalDto.getClientBusinessProperties())
                .filter(Objects::nonNull)
                .map(this::toSetting)
                .collect(Collectors.toList());
    }

    @SneakyThrows
    public SettingsDto getSettingsDto(List<Setting> settingsExternals) {
        SettingsDto settingsDto = new SettingsDto();
        for (Setting settingsExternal : settingsExternals) {
            keyToSettingFunctionMap.get(settingsExternal.getSettingKey()).accept(settingsDto, settingsExternal.getProperties());
        }
        return settingsDto;
    }

    @SneakyThrows
    Setting toSetting(Object property) {
        Class<?> clazz = property.getClass();
        SettingsAllocation allocation = clazz.getAnnotation(SettingsAllocation.class);
        Objects.requireNonNull(allocation);

        SettingsGroup group = allocation.group();
        SettingsCategory category = allocation.category();
        String properties = objectMapper.writeValueAsString(property);

        Setting settingsExternal = new Setting();
        settingsExternal.setSettingKey(new Setting.SettingKey(group, category));
        settingsExternal.setProperties(properties);

        return settingsExternal;
    }

    private static Setting.SettingKey toKey(SettingsGroup settingsGroup, SettingsCategory settingsCategory) {
        return Setting.SettingKey.builder()
                .group(settingsGroup)
                .category(settingsCategory)
                .build();
    }

    @SneakyThrows
    private static <T> T mapProps(ObjectMapper objectMapper, String properties, Class<T> clazz) {
        return objectMapper.readValue(properties, clazz);
    }
}
