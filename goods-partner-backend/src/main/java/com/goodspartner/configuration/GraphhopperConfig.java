package com.goodspartner.configuration;

import com.goodspartner.configuration.properties.GraphhopperProperties;
import com.graphhopper.GraphHopper;
import com.graphhopper.config.Profile;
import com.graphhopper.json.Statement;
import com.graphhopper.routing.weighting.custom.CustomProfile;
import com.graphhopper.util.CustomModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Configuration
@Slf4j
public class GraphhopperConfig {
    @Autowired
    private GraphhopperProperties properties;

    @Bean
    @ConditionalOnProperty(
            prefix = "graphhopper",
            name = "enabled",
            havingValue = "true",
            matchIfMissing = true)
    GraphHopper getHopper() throws IOException {
        Path path = Paths.get(properties.getOsm().getFile());

        if (!Files.exists(path)) {
            log.info("File {} does not exist", path.getFileName());
            download(path);
        }
        GraphHopper hopper = new GraphHopper();
        hopper.setOSMFile(properties.getOsm().getFile());
        hopper.setGraphHopperLocation(properties.getOsm().getGraph());

        Profile profile = new Profile(properties.getProfiles().getName())
                .setVehicle(properties.getProfiles().getVehicle())
                .setWeighting(properties.getProfiles().getWeighting())
                .setTurnCosts(false);

        CustomModel customModel = new CustomModel();
        customModel.addToSpeed(Statement.If("urban_density == CITY", Statement.Op.LIMIT, "30"));

        hopper.setProfiles(new CustomProfile(profile).setCustomModel(customModel));
        hopper.setUrbanDensityCalculation(300, 60, 2000, 30, 5);

        hopper.importOrLoad();

        return hopper;
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "graphhopper",
            name = "enabled",
            havingValue = "false")
    GraphHopper getTestHopper() {
        return new GraphHopper();
    }

    private void download(Path path) throws IOException {
        log.info("Downloading: {}", properties.getOsm().getUrl());
        Files.createDirectory(path.getParent());
        WebClient webClient = WebClient.builder()
                .baseUrl(properties.getOsm().getUrl())
                .build();
        Flux<DataBuffer> dataBuffers = webClient
                .get()
                .retrieve()
                .bodyToFlux(DataBuffer.class);
        DataBufferUtils.write(dataBuffers, path, StandardOpenOption.CREATE_NEW).block();
    }
}
