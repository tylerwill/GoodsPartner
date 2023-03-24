package com.goodspartner.service.document.impl;

import com.goodspartner.configuration.properties.ClientProperties;
import com.goodspartner.exception.EmptyIntegrationCallResult;
import com.goodspartner.service.document.FileFetcher;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

public class DefaultFileFetcher implements FileFetcher {

    private static final String ROOT_QUALITY_PATH = "C:\\Посвідчення якості";
    private static final String VALID_SEPARATOR = "/";
    private static final String INVALID_SEPARATOR = "\\";
    private static final String ENCODED_SPACE = "%20";

    private final ClientProperties clientProperties;
    private final WebClient webClient;

    public DefaultFileFetcher(ClientProperties clientProperties, WebClient webClient) {
        this.clientProperties = clientProperties;
        this.webClient = webClient;
    }

    @Override
    public String updateUrl(String url) {
        return url
                .replace(ROOT_QUALITY_PATH, clientProperties.getClientServerURL() + clientProperties.getDocumentsUriPrefix())
                .replace(INVALID_SEPARATOR, VALID_SEPARATOR)
                .replace(org.apache.commons.lang3.StringUtils.SPACE, ENCODED_SPACE);
    }

    @Override
    public byte[] getFileThroughInternet(String url) throws Exception {
        URI uri = getURI(url);
        return getFile(uri);
    }

    private URI getURI(String url) throws URISyntaxException {
        return new URI(url);
    }

    private byte[] getFile(URI uri) {
        return Optional.ofNullable(
                webClient.get()
                        .uri(uri)
                        .retrieve()
                        .bodyToMono(byte[].class)
                        .block()
        ).orElseThrow(EmptyIntegrationCallResult::new);
    }
}