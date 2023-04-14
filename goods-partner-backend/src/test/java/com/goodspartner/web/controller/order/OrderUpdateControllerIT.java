package com.goodspartner.web.controller.order;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.AbstractWebITest;
import com.goodspartner.config.TestConfigurationToCountAllQueries;
import com.goodspartner.dto.MapPoint;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.entity.AddressStatus;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.DeliveryFormationStatus;
import com.goodspartner.repository.DeliveryRepository;
import com.vladmihalcea.sql.SQLStatementCountValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.UUID;

import static com.goodspartner.entity.DeliveryType.POSTAL;
import static com.goodspartner.entity.DeliveryType.REGULAR;
import static com.vladmihalcea.sql.SQLStatementCountValidator.assertSelectCount;
import static com.vladmihalcea.sql.SQLStatementCountValidator.assertUpdateCount;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@DBRider
@Import({
        TestSecurityEnableConfig.class,
        TestConfigurationToCountAllQueries.class
})
@AutoConfigureMockMvc
public class OrderUpdateControllerIT extends AbstractWebITest {

    private static final String UPDATE_ORDER_ENDPOINT = "/api/v1/orders/%d";
    private static final UUID DELIVERY_ID = UUID.fromString("70574dfd-48a3-40c7-8b0c-3e5defe7d080");

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Test
    void givenOrderUpdateByDriver_thenForbiddenReturned() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put(String.format(UPDATE_ORDER_ENDPOINT, 251))
                        .contentType(MediaType.APPLICATION_JSON)
//                        .session(getDriverSession())
                        .content(objectMapper.writeValueAsString(buildRequestUpdateOrderDto())))
                .andExpect(status().isForbidden());
    }

    @Test
    @DataSet(value = "datasets/orders/update-order-dataset.json",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    void givenOrderUpdateByNonExistentId_thenBadRequestReturned() throws Exception {
        SQLStatementCountValidator.reset();
        mockMvc.perform(MockMvcRequestBuilders.put(String.format(UPDATE_ORDER_ENDPOINT, 9999999L))
                        .contentType(MediaType.APPLICATION_JSON)
//                        .session(getLogistSession())
                        .content(objectMapper.writeValueAsString(buildRequestUpdateOrderDto())))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"status\":\"NOT_FOUND\",\"message\":\"Не знайдено жодного замовлення з id: 9999999\"}"));
        assertSelectCount(1); // OrderById + Delivery
        assertUpdateCount(0); // Nothing updated
    }

    @Test
    @DataSet(value = "datasets/orders/update-order-with-approved-delivery-dataset.json",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    void givenOrderUpdateForApprovedDelivery_thenBadRequestReturned() throws Exception {
        SQLStatementCountValidator.reset();
        mockMvc.perform(MockMvcRequestBuilders.put(String.format(UPDATE_ORDER_ENDPOINT, 251))
                        .contentType(MediaType.APPLICATION_JSON)
//                        .session(getLogistSession())
                        .content(objectMapper.writeValueAsString(buildRequestUpdateOrderDto())))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"status\":\"BAD_REQUEST\",\"message\":\"Зміна замовлень можлива лише для доставки в статусі - Створена\"}\n"));
        assertSelectCount(1); // OrderById
        assertUpdateCount(0); // Nothing updated
    }

    @Test
    @DataSet(value = "datasets/orders/update-order-dataset.json",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    void givenOrderUpdateWithoutAddress_thenOnlyOrderUpdated() throws Exception {
        //Given
        Delivery deliveryBefore = deliveryRepository.findByIdWithOrders(DELIVERY_ID).get();
        assertEquals(2, deliveryBefore.getOrders().size());
        assertEquals(DeliveryFormationStatus.ORDERS_LOADED, deliveryBefore.getFormationStatus());

        OrderDto orderDto = buildRequestUpdateOrderDto();
        orderDto.setMapPoint(null);

        // When
        SQLStatementCountValidator.reset();
        mockMvc.perform(MockMvcRequestBuilders.put(String.format(UPDATE_ORDER_ENDPOINT, 251))
                        .contentType(MediaType.APPLICATION_JSON)
//                        .session(getLogistSession())
                        .content(objectMapper.writeValueAsString(orderDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseAsString("response/orders/update-order-no-address-response.json")));
        assertSelectCount(2); // OrderById + isAllOrdersValid verification + Delivery
        assertUpdateCount(1); // Update Orders + Delivery

        Delivery deliveryAfter = deliveryRepository.findByIdWithOrders(DELIVERY_ID).get();
        assertEquals(DeliveryFormationStatus.ORDERS_LOADED, deliveryAfter.getFormationStatus()); // AddressExternal is not updated, Delivery Updated
    }

    @Test
    @DataSet(value = "datasets/orders/update-order-dataset.json",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    void givenOrderUpdateWithoutAddressToPostalType_thenOrderUpdatedWithDelivery() throws Exception {
        //Given
        Delivery deliveryBefore = deliveryRepository.findByIdWithOrders(DELIVERY_ID).get();
        assertEquals(2, deliveryBefore.getOrders().size());
        assertEquals(DeliveryFormationStatus.ORDERS_LOADED, deliveryBefore.getFormationStatus());

        OrderDto orderDto = buildRequestUpdateOrderDto();
        orderDto.setMapPoint(null);
        orderDto.setDeliveryType(POSTAL);

        // When
        SQLStatementCountValidator.reset();
        mockMvc.perform(MockMvcRequestBuilders.put(String.format(UPDATE_ORDER_ENDPOINT, 251))
                        .contentType(MediaType.APPLICATION_JSON)
//                        .session(getLogistSession())
                        .content(objectMapper.writeValueAsString(orderDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseAsString("response/orders/update-order-delivery-type-postal-response.json")));
        assertSelectCount(4); // OrderById + isAllOrdersValid verification + Delivery
        assertUpdateCount(2); // Update Orders + Addresses + Delivery

        Delivery deliveryAfter = deliveryRepository.findByIdWithOrders(DELIVERY_ID).get();
        assertEquals(DeliveryFormationStatus.READY_FOR_CALCULATION, deliveryAfter.getFormationStatus());
    }

    @Test
    @DataSet(value = "datasets/orders/default-order-dataset.json",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    void whenOrderUpdateAddressOutOfReqion_BadRequestReturned() throws Exception {
        OrderDto payload = buildRequestUpdateOrderDto();
        MapPoint mapPoint = payload.getMapPoint();
        mapPoint.setLatitude(52.03); // Out of Kyiv region
        mapPoint.setLongitude(28.69);

        SQLStatementCountValidator.reset();
        mockMvc.perform(MockMvcRequestBuilders.put(String.format(UPDATE_ORDER_ENDPOINT, 251))
                        .contentType(MediaType.APPLICATION_JSON)
//                        .session(getLogistSession())
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"status\":\"BAD_REQUEST\",\"message\":\"Дана адреса знаходиться поза межами доступного регіону:\\n проспект Академіка Палладіна, 7А, Київ, Україна, 03179.\\n При необхідності змініть тип доставки\"}"));
        assertSelectCount(0); // OrderById + isAllOrdersValid verification
        assertUpdateCount(0); // Update Orders + Addresses + Delivery
    }

    @Test
    @DataSet(value = "datasets/orders/update-order-dataset.json",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    void givenOrderUpdateWithAddress_thenOrderUpdatedWithDelivery() throws Exception {
        Delivery deliveryBefore = deliveryRepository.findByIdWithOrders(DELIVERY_ID).get();
        assertEquals(2, deliveryBefore.getOrders().size());
        assertEquals(DeliveryFormationStatus.ORDERS_LOADED, deliveryBefore.getFormationStatus());

        SQLStatementCountValidator.reset();
        mockMvc.perform(MockMvcRequestBuilders.put(String.format(UPDATE_ORDER_ENDPOINT, 251))
                        .contentType(MediaType.APPLICATION_JSON)
//                        .session(getLogistSession())
                        .content(objectMapper.writeValueAsString(buildRequestUpdateOrderDto())))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseAsString("response/orders/update-order-response.json")));
        assertSelectCount(5); // OrderById + AddressExternal + InvalidOrders + DeliveryForUpdate + Event
        assertUpdateCount(3); // Update Orders + Addresses + Delivery

        Delivery deliveryAfter = deliveryRepository.findByIdWithOrders(DELIVERY_ID).get();
        assertEquals(DeliveryFormationStatus.READY_FOR_CALCULATION, deliveryAfter.getFormationStatus());
    }

    private OrderDto buildRequestUpdateOrderDto() {
        return OrderDto.builder()
                // Unmodifiable
                .id(251)
                .orderNumber("00000003639 - This value not modifiable")
                .managerFullName("Крамаренко Леся - This value not modifiable")
                .shippingDate(LocalDate.parse("2022-02-20")) // This value not modifiable
                .clientName("Новус Україна ТОВ - This value not modifiable")
                .address("м.Київ, пр. Академіка Палладіна, 7-А - This value not modifiable")
                .orderWeight(1500.00)
                .products(new ArrayList<>()) // TODO check
                .refKey("18ee46a5-8e41-11ec-b3ce-00155dd72305 - This value not modifiable")
                .comment("some-test-comment - This value not modifiable")
                .deliveryId(UUID.fromString("70574dfd-48a3-40c7-8b0c-3e5defe7d080")) // - This value not modifiable
                // Modifiable
                .deliveryType(REGULAR)
                .excluded(false)
                .dropped(false)
                .frozen(true)
                .deliveryStart(LocalTime.of(10, 0))
                .deliveryFinish(LocalTime.of(11, 0))
                .mapPoint(MapPoint.builder()
                        .status(AddressStatus.KNOWN) // Override status
                        .address("проспект Академіка Палладіна, 7А, Київ, Україна, 03179")
                        .longitude(31.3553835000000)
                        .latitude(51.4618259000000)
                        .build())
                .build();
    }
}
