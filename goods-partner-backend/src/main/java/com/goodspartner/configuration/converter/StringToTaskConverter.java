package com.goodspartner.configuration.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goodspartner.dto.TaskDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class StringToTaskConverter implements Converter<String, TaskDto> {
    private final ObjectMapper objectMapper;

    @Override
    public TaskDto convert(String source) {
        try {
            return objectMapper.readValue(source, TaskDto.class);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
