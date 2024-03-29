package com.goodspartner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public class AbstractBaseITest {

    private static final PostgreSQLContainer<?> POSTGRES_SQL_CONTAINER;

    static {
        POSTGRES_SQL_CONTAINER =
                new PostgreSQLContainer<>("postgres:14.4")
                        .withDatabaseName("test")
                        .withUsername("test")
                        .withPassword("test");
        POSTGRES_SQL_CONTAINER.start();
    }

    @Autowired
    protected ObjectMapper objectMapper;

    @DynamicPropertySource
    public static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES_SQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES_SQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRES_SQL_CONTAINER::getPassword);
    }

    protected String getResponseAsString(String jsonPath) {
        URL resource = getClass().getClassLoader().getResource(jsonPath);
        try {
            return FileUtils.readFileToString(new File(resource.toURI()), StandardCharsets.UTF_8);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("Unable to find file: " + jsonPath);
        }
    }

    protected <T> List<T> getMockedListObjects(String mockPath, Class<T> contentClass) {
        URL resource = getClass().getClassLoader().getResource(mockPath);
        try {
            String fileContent = FileUtils.readFileToString(new File(resource.toURI()), StandardCharsets.UTF_8);
            return objectMapper.readValue(fileContent,
                    TypeFactory
                            .defaultInstance()
                            .constructParametricType(List.class, contentClass));
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("Unable to find file: " + mockPath);
        }
    }
}
