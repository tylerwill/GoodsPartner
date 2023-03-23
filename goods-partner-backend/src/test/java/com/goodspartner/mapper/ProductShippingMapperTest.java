package com.goodspartner.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.goodspartner.dto.ProductLoadDto;
import com.goodspartner.dto.ProductMeasureDetails;
import com.goodspartner.dto.ProductShippingDto;
import com.goodspartner.entity.Car;
import com.goodspartner.entity.CarLoad;
import com.goodspartner.entity.OrderExternal;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
class ProductShippingMapperTest {

    private static final String MOCK_ORDERS_PATH = "mock/product-shipping-mapper-mocks/orders.json";
    private static final String MOCK_CARS_PATH = "mock/product-shipping-mapper-mocks/cars.json";
    private static final String MOCK_PRODUCT_SHIPPING_PATH = "mock/product-shipping-mapper-mocks/expected_product_shipping_list.json";

    private final ProductShippingMapper productShippingMapper = new ProductShippingMapper();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private List<CarLoad> carLoads;
    private Car car;
    private Map<String, List<ProductLoadDto>> productMap;
    private List<ProductShippingDto> expectedProductShipping;

    @BeforeAll
    void before() throws IOException {
        objectMapper.registerModule(new JavaTimeModule());

        List<OrderExternal> orders = Arrays.asList(
                objectMapper.readValue(getResponseAsString(MOCK_ORDERS_PATH), OrderExternal[].class));
        List<Car> cars = Arrays.asList(
                objectMapper.readValue(getResponseAsString(MOCK_CARS_PATH), Car[].class));
        expectedProductShipping = Arrays.asList(
                objectMapper.readValue(getResponseAsString(MOCK_PRODUCT_SHIPPING_PATH), ProductShippingDto[].class));

        carLoads = List.of(
                new CarLoad(null, cars.get(0), orders, null),
                new CarLoad(null, cars.get(1), orders, null)
        );
        car = cars.get(1);

        ProductMeasureDetails productUnit = new ProductMeasureDetails("кг", 1.0, 10.0);
        ProductMeasureDetails productPackaging = new ProductMeasureDetails("ящ", 10.0, 1.0);

        List<ProductLoadDto> flour = List.of(
                new ProductLoadDto("986453", "Mercedes Vito (AA 2222 CT)", 9, 50, 450, productUnit, productPackaging),
                new ProductLoadDto("986453", "Mercedes Sprinter (AA 3333 CT)", 9, 50, 450, productUnit, productPackaging)
        );
        List<ProductLoadDto> dye = List.of(
                new ProductLoadDto("432565", "Mercedes Vito (AA 2222 CT)", 5, 10, 50, productUnit, productPackaging),
                new ProductLoadDto("426457", "Mercedes Vito (AA 2222 CT)", 10, 10, 100, productUnit, productPackaging),
                new ProductLoadDto("432565", "Mercedes Sprinter (AA 3333 CT)", 5, 10, 50, productUnit, productPackaging),
                new ProductLoadDto("426457", "Mercedes Sprinter (AA 3333 CT)", 10, 10, 100, productUnit, productPackaging)
        );
        List<ProductLoadDto> oil = List.of(
                new ProductLoadDto("432565", "Mercedes Vito (AA 2222 CT)", 10, 25, 250, productUnit, productPackaging),
                new ProductLoadDto("986453", "Mercedes Vito (AA 2222 CT)", 1, 25, 25, productUnit, productPackaging),
                new ProductLoadDto("432565", "Mercedes Sprinter (AA 3333 CT)", 10, 25, 250, productUnit, productPackaging),
                new ProductLoadDto("986453", "Mercedes Sprinter (AA 3333 CT)", 1, 25, 25, productUnit, productPackaging)
        );

        productMap = Map.of(
                "8795 Мука екстра", flour,
                "4695 Фарба харчова зелена", dye,
                "8452 Масло 1й гатунок", oil
        );

    }

    @Test
    void testGetCarloadByProduct() {
        List<ProductShippingDto> actualProductShipping = productShippingMapper.getCarloadByProduct(carLoads);

        assertEquals(expectedProductShipping, actualProductShipping);

    }

    @Test
    void testGetProductLoadMap() {
        Map<String, List<ProductLoadDto>> actualProductLoadMap = productShippingMapper.getProductLoadMap(carLoads);

        assertEquals(productMap, actualProductLoadMap);
    }

    @Test
    void testGetProductShippingList() {
        List<ProductShippingDto> actualProductShipping = productShippingMapper.getProductShippingList(productMap);

        assertTrue(CollectionUtils.isEqualCollection(expectedProductShipping, actualProductShipping));
    }

    @Test
    void testGetCarNameAndLicencePlate() {
        String carNameAndLicencePlate = productShippingMapper.getCarNameAndLicencePlate(car);
        assertEquals("Mercedes Sprinter (AA 3333 CT)", carNameAndLicencePlate);
    }

    private String getResponseAsString(String jsonPath) {
        URL resource = getClass().getClassLoader().getResource(jsonPath);
        try {
            return FileUtils.readFileToString(new File(resource.toURI()), StandardCharsets.UTF_8);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("Unable to find file: " + jsonPath);
        }
    }
}