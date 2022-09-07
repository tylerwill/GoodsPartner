package com.goodspartner.configuration.properties;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@ToString
@Configuration
@ConfigurationProperties("graphhopper")
public class GraphhopperProperties {

    @NestedConfigurationProperty
    private Osm osm;

    @NestedConfigurationProperty
    private Profiles profiles;

    @Data
    public static class Osm {
        private String file;
        private String graph;
        private String url;
    }

    @Data
    public static class Profiles{
        private String name;
        private String vehicle;
        private String weighting;
    }
}
