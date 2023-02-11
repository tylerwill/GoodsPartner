package com.goodspartner.mapper;

import com.goodspartner.configuration.properties.PropertyAggregate;
import com.goodspartner.dto.SettingsDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SettingsMapper {

    SettingsDto mapAggregate(PropertyAggregate settings);

    // TODO strategy map not null
    void update(@MappingTarget PropertyAggregate propertyAggregate, SettingsDto settingsDto);
}
