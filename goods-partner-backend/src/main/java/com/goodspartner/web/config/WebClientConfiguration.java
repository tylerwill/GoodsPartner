package com.goodspartner.web.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.util.ResourceUtils;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class WebClientConfiguration {
    private static final int MAX_IN_MEMORY_SIZE = 10485760; // Required to parse huge response from 3d Party services
    private static final String ENDPOINT_PATH = "/api/v1/mock1CoData/";
    private static final String MOCKED1C_DATA_RESOURCE_LOCATION = "classpath:mock1CoData/";
    @Value("${server.port}")
    private int port;

    @Bean
    public WebClient webClient() {
        try (Stream<Path> walk = Files.walk(ResourceUtils.getFile(MOCKED1C_DATA_RESOURCE_LOCATION).toPath())) {
            List<String> mocked1cData = walk.filter(Files::isRegularFile)
                    .map(x -> x.getFileName().toString()).collect(Collectors.toList());
            return WebClient.builder()
                    .exchangeStrategies(getExchangeStrategies())
                    .filter(urlModifyingFilter(mocked1cData))
                    .build();
        } catch (Exception e) {
            return WebClient.builder()
                    .exchangeStrategies(getExchangeStrategies())
                    .build();
        }
    }

    private ExchangeStrategies getExchangeStrategies() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return ExchangeStrategies
                .builder()
                .codecs(configurer -> configurer.defaultCodecs().jackson2JsonDecoder(
                        new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON)))
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(MAX_IN_MEMORY_SIZE))
                .build();
    }

    private ExchangeFilterFunction urlModifyingFilter(List<String> mocked1cData) {
        return (clientRequest, nextFilter) -> mocked1cData.stream()
                .filter(file -> clientRequest.url().toString().contains(FilenameUtils.removeExtension(file)))
                .findFirst()
                .map(fileName -> {
                    URI uri = ServletUriComponentsBuilder.fromCurrentContextPath()
                            .port(port)
                            .path(ENDPOINT_PATH + fileName)
                            .build().toUri();
                    log.info("Mocked oDATA URL {}", uri);
                    ClientRequest filteredRequest = ClientRequest.from(clientRequest)
                            .url(uri)
                            .build();
                    return nextFilter.exchange(filteredRequest);
                })
                .orElse(nextFilter.exchange(clientRequest));
    }


}


