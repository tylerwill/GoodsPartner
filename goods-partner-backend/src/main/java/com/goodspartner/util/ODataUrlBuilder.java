package com.goodspartner.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
@NoArgsConstructor
public class ODataUrlBuilder {
    private List<ODataUrlBuilder.Segment> segments = new ArrayList<>(1);
    private Map<String, String> queryOptions = new LinkedHashMap<>(1);

    public ODataUrlBuilder appendEntitySetSegment(String segmentValue) {
        this.addSegment(URLOptionType.ENTITYSET, segmentValue);
        return this;
    }

    public ODataUrlBuilder baseUrl(String segmentValue) {
        this.addSegment(URLOptionType.SERVICEROOT, segmentValue);
        return this;
    }

    private void addSegment(URLOptionType segmetType, String value) {
        this.segments.add(new ODataUrlBuilder.Segment(segmetType, value));
    }

    public ODataUrlBuilder expand(String... expandItems) {
        return this.addQueryOption(URLOptionType.EXPAND, StringUtils.join(expandItems, ","));
    }

    public ODataUrlBuilder format(String format) {
        return this.addQueryOption(URLOptionType.FORMAT, format);
    }

    public ODataUrlBuilder filter(String filter) {
        return this.addQueryOption(URLOptionType.FILTER, filter);
    }

    public ODataUrlBuilder select(String select) {
        return this.addQueryOption(URLOptionType.SELECT, select);
    }

    public ODataUrlBuilder top(int top) {
        return this.addQueryOption(URLOptionType.TOP, String.valueOf(top));
    }

    ODataUrlBuilder addQueryOption(URLOptionType option, String value) {
        this.queryOptions.put(option.toString(), value);
        return this;
    }

    String encodeQueryParameter(String value) {
        String encode;
        try {
            encode = URLEncoder.encode(value, StandardCharsets.UTF_8.toString())
                    .replace("+", "%20");
        } catch (UnsupportedEncodingException exception) {
            throw new RuntimeException("Error during encode query parameter in url - ", exception);
        }

        return encode;
    }

    public URI build() {
        StringBuilder urlBuilder = new StringBuilder();

        if (segments.size() != 0) {
            StringJoiner segmentJoiner = new StringJoiner("/");
            for (Segment segment : segments) {
                String value = segment.getValue();
                if (value.charAt(value.length() -1) == '/') {
                    value = value.substring(0, value.length() - 1);
                }
                segmentJoiner.add(value);
            }

            urlBuilder.append(segmentJoiner);
        }

        if (queryOptions.size() !=0) {
            StringJoiner queryJoiner = new StringJoiner("&", "?", "");
            for (Map.Entry<String, String> entry : queryOptions.entrySet()) {
                String newElement = encodeQueryParameter("$" + entry.getKey()) + "=" + encodeQueryParameter(entry.getValue());
                queryJoiner.add(newElement);
            }

            urlBuilder.append(queryJoiner);
        }

        segments = new ArrayList<>(1);
        queryOptions = new LinkedHashMap<>(1);

        return URI.create(urlBuilder.toString());
    }

    public String toString() {
        return this.build().toASCIIString();
    }

    @AllArgsConstructor
    @Getter
    private static class Segment {
        private final URLOptionType type;
        private final String value;
    }

    @NoArgsConstructor
    private enum URLOptionType {
        SERVICEROOT,
        ENTITYSET,
        EXPAND,
        FORMAT,
        SELECT,
        TOP,
        FILTER;

        public String toString() {
            return this.name().toLowerCase();
        }
    }
}