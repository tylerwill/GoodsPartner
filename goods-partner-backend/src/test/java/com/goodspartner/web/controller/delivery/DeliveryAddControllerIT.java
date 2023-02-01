package com.goodspartner.web.controller.delivery;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.AbstractWebITest;
import com.goodspartner.config.TestConfigurationToCountAllQueries;
import com.goodspartner.config.TestSecurityEnableConfig;
import com.goodspartner.dto.DeliveryDto;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.entity.AddressExternal;
import com.goodspartner.entity.AddressStatus;
import com.goodspartner.entity.Delivery;
import com.goodspartner.entity.DeliveryFormationStatus;
import com.goodspartner.entity.DeliveryHistoryTemplate;
import com.goodspartner.entity.DeliveryStatus;
import com.goodspartner.entity.DeliveryType;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.event.EventType;
import com.goodspartner.event.LiveEvent;
import com.goodspartner.repository.AddressExternalRepository;
import com.goodspartner.repository.DeliveryRepository;
import com.goodspartner.repository.OrderExternalRepository;
import com.goodspartner.service.EventService;
import com.goodspartner.service.IntegrationService;
import com.goodspartner.service.client.GoogleClient;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.Geometry;
import com.google.maps.model.LatLng;
import com.google.maps.model.LocationType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.testcontainers.shaded.org.awaitility.Durations;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.goodspartner.entity.AddressStatus.AUTOVALIDATED;
import static com.goodspartner.entity.AddressStatus.KNOWN;
import static com.goodspartner.entity.DeliveryFormationStatus.ORDERS_LOADED;
import static com.goodspartner.entity.DeliveryFormationStatus.ORDERS_LOADING;
import static com.goodspartner.entity.DeliveryFormationStatus.READY_FOR_CALCULATION;
import static com.goodspartner.entity.DeliveryStatus.DRAFT;
import static com.goodspartner.entity.DeliveryType.REGULAR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@Slf4j
@DBRider
@Import({
        TestSecurityEnableConfig.class,
        TestConfigurationToCountAllQueries.class
})
@AutoConfigureMockMvc
public class DeliveryAddControllerIT extends AbstractWebITest {

    private static final String INTEGRATION_SERVICE_VARIOUS_ORDERS_MOCK_PATH = "mock/integration/mock-integration-service-return-various-orders.json";
    private static final String INTEGRATION_SERVICE_KNOWN_ORDERS_MOCK_PATH = "mock/integration/mock-integration-service-return-known-orders.json";
    private static final LocalDate SHIPPING_DATE = LocalDate.parse("2022-07-10");

    private static final double AUTOVALIDATED_ADDRESS_LATITUDE = 50.51;
    private static final double AUTOVALIDATED_ADDRESS_LONGITUDE = 30.79;
    private static final String AUTOVALIDATED_ADDRESS = "Бровари, Марії Лагунової, 11";
    private static final String FORMATTED_AUTOVALIDATED_ADDRESS = "вулиця Марії Лагунової, 11, Бровари, Київська обл., Україна, 07400";

    private static final double OUT_OF_REGION_ADDRESS_LATITUDE = 50.25;
    private static final double OUT_OF_REGION_ADDRESS_LONGITUDE = 28.69;
    private static final String OUT_OF_REGION_ADDRESS = "м. Житомир, вул Корольова, 1";

    private static final String PRE_PACKING_ADDRESS = "вулиця Київська, 34, Фастів, Київська обл., Україна, 08500";
    private static final String POSTAL_ADDRESS = "вулиця Київська, 30, Фастів, Київська обл., Україна, 08500";

    private static final String UNKNOWN_ADDRESS = "вул. Невідома 8667";

    @MockBean
    private IntegrationService integrationService;
    @MockBean
    private GoogleClient googleClient;
    @MockBean
    private EventService eventService;

    @Autowired
    private DeliveryRepository deliveryRepository;
    @Autowired
    private AddressExternalRepository addressExternalRepository;
    @Autowired
    private OrderExternalRepository orderExternalRepository;

    @Captor
    private ArgumentCaptor<LiveEvent> liveEventArgumentCaptor;

    // Query count is not applicable in this test due to Async thread
    @Test
    @DataSet(value = "datasets/delivery/add_delivery_fetch_orders.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    public void addDelivery_OrdersFetchedAndPostProcessingExecuted() throws Exception {
        // Given
        verifyGivenAddresses();
        mockIntegrationService(INTEGRATION_SERVICE_VARIOUS_ORDERS_MOCK_PATH);
        mockGoogleGeocodeService();
        // When
        DeliveryDto addedDelivery = objectMapper.readValue(
                mockMvc.perform(post("/api/v1/deliveries")
                                .session(getLogistSession())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(DeliveryDto.builder()
                                        .deliveryDate(SHIPPING_DATE)
                                        .status(DRAFT)
                                        .build())))
                        .andExpect(status().isAccepted())
                        .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                DeliveryDto.class);
        // Then
        verifyOrdersFetchedDeliveryEnriched(addedDelivery, 7); // 6 Orders from 1C + 1 order rescheduled
        verifyDeliveryState(addedDelivery, ORDERS_LOADED);
        verifyRequiredEventsEmmitted(addedDelivery);
        verifyAddedAddresses();
        verifyOrdersState(addedDelivery);
    }

    @Test
    @DataSet(value = "datasets/delivery/add_delivery_fetch_orders.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    public void addDelivery_readyForCalculationStatusSet() throws Exception {
        // Given
        verifyGivenAddresses();
        mockIntegrationService(INTEGRATION_SERVICE_KNOWN_ORDERS_MOCK_PATH);
        mockAddressGeocoding(AUTOVALIDATED_ADDRESS_LATITUDE, AUTOVALIDATED_ADDRESS_LONGITUDE, FORMATTED_AUTOVALIDATED_ADDRESS, AUTOVALIDATED_ADDRESS);
        // When
        DeliveryDto addedDelivery = objectMapper.readValue(
                mockMvc.perform(post("/api/v1/deliveries")
                                .session(getLogistSession())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                        DeliveryDto.builder().deliveryDate(SHIPPING_DATE).build())))
                        .andExpect(status().isAccepted())
                        .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                DeliveryDto.class);
        // Then
        verifyOrdersFetchedDeliveryEnriched(addedDelivery, 2); // Rescheduled + Known one
        verifyDeliveryState(addedDelivery, READY_FOR_CALCULATION);
        verifyRequiredEventsEmmitted(addedDelivery);
    }

    @Test
    @DataSet(value = "datasets/delivery/add_delivery_fetch_orders.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    public void addDelivery_NoOrdersFrom1C_RescheduledOrderAttached() throws Exception {
        // Given
        when(integrationService.findAllByShippingDate(SHIPPING_DATE)).thenReturn(Collections.emptyList());
        // When
        DeliveryDto addedDelivery = objectMapper.readValue(
                mockMvc.perform(post("/api/v1/deliveries")
                                .session(getLogistSession())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                        DeliveryDto.builder().deliveryDate(SHIPPING_DATE).build())))
                        .andExpect(status().isAccepted())
                        .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                DeliveryDto.class);
        // Then
        verifyOrdersFetchedDeliveryEnriched(addedDelivery, 1); // Rescheduled + Known one
        verifyDeliveryState(addedDelivery, READY_FOR_CALCULATION);
        verifyRequiredEventsEmmitted(addedDelivery);
        verifyRescheduledOrder(addedDelivery);
    }

    @Test
    @DataSet(value = "datasets/delivery/add_delivery_fetch_orders.yml",
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    public void addDelivery_ExceptionWhileFetchingOrders() throws Exception {
        // Given
        when(integrationService.findAllByShippingDate(SHIPPING_DATE)).thenThrow(new RuntimeException("Oops"));
        // When
        DeliveryDto addedDelivery = objectMapper.readValue(
                mockMvc.perform(post("/api/v1/deliveries")
                                .session(getLogistSession())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                        DeliveryDto.builder().deliveryDate(SHIPPING_DATE).build())))
                        .andExpect(status().isAccepted())
                        .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                DeliveryDto.class);
        // Then
        Thread.sleep(2000); // Waiting while async thread completes
        verifyOrdersFetchedDeliveryEnriched(addedDelivery, 0); // Rescheduled + Known one
        verifyDeliveryState(addedDelivery, ORDERS_LOADING);

        Mockito.verify(eventService, times(1))
                .publishDeliveryEvent(DeliveryHistoryTemplate.DELIVERY_CREATED, addedDelivery.getId());
        Mockito.verify(eventService, times(1))
                .publishOrdersStatus(DeliveryHistoryTemplate.ORDERS_LOADING, addedDelivery.getId());
        Mockito.verify(eventService, times(1)).publishEvent(liveEventArgumentCaptor.capture());

        LiveEvent errorLiveEvent = liveEventArgumentCaptor.getValue();
        assertEquals(EventType.ERROR, errorLiveEvent.getType());
        assertEquals("Помилка під час вивантаження замовлень з 1С", errorLiveEvent.getMessage());
    }

    private void verifyDeliveryState(DeliveryDto addedDeliveryResponse,
                                     DeliveryFormationStatus updatedFormationStatus) {
        assertSame(DeliveryStatus.DRAFT, addedDeliveryResponse.getStatus());
        assertSame(ORDERS_LOADING, addedDeliveryResponse.getFormationStatus());

        Delivery delivery = deliveryRepository.findById(addedDeliveryResponse.getId()).get();
        assertSame(updatedFormationStatus, delivery.getFormationStatus());
    }

    private void verifyGivenAddresses() {
        List<AddressExternal> addressesBefore = addressExternalRepository.findAll();
        Map<AddressStatus, List<AddressExternal>> addressesMap = addressesBefore.stream()
                .collect(Collectors.groupingBy(AddressExternal::getStatus));
        assertEquals(2, addressesMap.size());

        assertEquals(1, addressesMap.get(AddressStatus.KNOWN).size());
        assertEquals(1, addressesMap.get(AUTOVALIDATED).size());
    }

    private void verifyAddedAddresses() {
        List<AddressExternal> addressesAfter = addressExternalRepository.findAll();
        assertEquals(8, addressesAfter.size());
        // Assert new address state
        assertTrue(verifyContains(addressesAfter, AUTOVALIDATED_ADDRESS, FORMATTED_AUTOVALIDATED_ADDRESS, AUTOVALIDATED));
        assertTrue(verifyContains(addressesAfter, OUT_OF_REGION_ADDRESS, null, AddressStatus.UNKNOWN));
        assertTrue(verifyContains(addressesAfter, UNKNOWN_ADDRESS, null, AddressStatus.UNKNOWN));
    }

    private boolean verifyContains(List<AddressExternal> addressesAfter,
                                   String orderAddress, String resolvedAddress, AddressStatus addressStatus) {
        log.info("Addresses: {}", addressesAfter);
        return addressesAfter.stream()
                .filter(addressExternal -> addressExternal.getOrderAddressId().getOrderAddress().equals(orderAddress))
                .filter(addressExternal ->
                        Objects.isNull(addressExternal.getValidAddress())
                                || addressExternal.getValidAddress().equals(resolvedAddress))
                .anyMatch(addressExternal -> addressExternal.getStatus().equals(addressStatus));
    }

    private void verifyOrdersFetchedDeliveryEnriched(DeliveryDto addedDelivery, int orderCount) {
        await()
                .atLeast(Durations.TWO_HUNDRED_MILLISECONDS)
                .atMost(Durations.TWO_SECONDS)
                .with()
                .pollInterval(Durations.TWO_HUNDRED_MILLISECONDS)
                .until(() -> deliveryRepository.findByIdWithOrders(addedDelivery.getId())
                        .map(Delivery::getOrders)
                        .map(List::size)
                        .map(size -> size == orderCount)
                        .orElse(false));
    }

    private void verifyRequiredEventsEmmitted(DeliveryDto addedDelivery) {
        Mockito.verify(eventService, times(1))
                .publishDeliveryEvent(DeliveryHistoryTemplate.DELIVERY_CREATED, addedDelivery.getId());

        Mockito.verify(eventService, times(1))
                .publishOrdersStatus(DeliveryHistoryTemplate.ORDERS_LOADING, addedDelivery.getId());

        Mockito.verify(eventService, times(1))
                .publishOrdersStatus(DeliveryHistoryTemplate.ORDERS_LOADED, addedDelivery.getId());
    }

    private void mockIntegrationService(String mockPath) {
        when(integrationService.findAllByShippingDate(SHIPPING_DATE))
                .thenReturn(getMockedListObjects(mockPath, OrderDto.class));
    }

    private void mockGoogleGeocodeService() {
        mockUnknownAddress();
        mockAddressGeocoding(AUTOVALIDATED_ADDRESS_LATITUDE, AUTOVALIDATED_ADDRESS_LONGITUDE, FORMATTED_AUTOVALIDATED_ADDRESS, AUTOVALIDATED_ADDRESS);
        mockAddressGeocoding(OUT_OF_REGION_ADDRESS_LATITUDE, OUT_OF_REGION_ADDRESS_LONGITUDE, OUT_OF_REGION_ADDRESS, OUT_OF_REGION_ADDRESS);
    }

    private void mockUnknownAddress() {
        when(googleClient.getGeocodingResults(UNKNOWN_ADDRESS)).thenReturn(new GeocodingResult[0]);
    }

    private void mockAddressGeocoding(double latitude, double longitude,
                                      String formattedAddress, String originalAddress) {
        LatLng latLngMockForAutovalidatedAddress = new LatLng(latitude, longitude);
        Geometry geometryMockForAutovalidatedAddress = new Geometry();
        geometryMockForAutovalidatedAddress.location = latLngMockForAutovalidatedAddress;
        geometryMockForAutovalidatedAddress.locationType = LocationType.ROOFTOP;

        GeocodingResult geocodingResultForAutovalidatedAddress = new GeocodingResult();
        geocodingResultForAutovalidatedAddress.formattedAddress = formattedAddress;
        geocodingResultForAutovalidatedAddress.geometry = geometryMockForAutovalidatedAddress;

        GeocodingResult[] mockedGeocodeResultsForAutovalidatedAddress =
                new GeocodingResult[]{geocodingResultForAutovalidatedAddress};

        when(googleClient.getGeocodingResults(originalAddress)).thenReturn(mockedGeocodeResultsForAutovalidatedAddress);
    }

    private void verifyOrdersState(DeliveryDto delivery) {
        List<OrderExternal> deliveryOrders = orderExternalRepository.findByDeliveryId(delivery.getId(), Sort.unsorted());
        Map<String, OrderExternal> ordersMap = deliveryOrders.stream()
                .collect(Collectors.toMap(OrderExternal::getRefKey, Function.identity()));
        assertEquals(7, ordersMap.size());

        // Check Orders
        OrderExternal order01 = ordersMap.get("01grande-0000-0000-0000-000000000000");
        assertFalse(order01.isFrozen());
        assertEquals(REGULAR, order01.getDeliveryType());
        assertSame(AUTOVALIDATED, order01.getAddressExternal().getStatus());
        assertEquals(FORMATTED_AUTOVALIDATED_ADDRESS, order01.getAddressExternal().getValidAddress());
        assertEquals(AUTOVALIDATED_ADDRESS, order01.getAddressExternal().getOrderAddressId().getOrderAddress());

        OrderExternal order02 = ordersMap.get("02grande-0000-0000-0000-000000000000");
        assertFalse(order02.isFrozen());
        assertEquals(REGULAR, order02.getDeliveryType());
        assertEquals(AddressStatus.UNKNOWN, order02.getAddressExternal().getStatus()); // Overide

        OrderExternal order03 = ordersMap.get("03grande-0000-0000-0000-000000000000");
        assertFalse(order03.isFrozen());
        assertEquals(DeliveryType.SELF_SERVICE, order03.getDeliveryType());
        assertEquals(AddressStatus.KNOWN, order03.getAddressExternal().getStatus());

        OrderExternal order04 = ordersMap.get("04grande-0000-0000-0000-000000000000");
        assertFalse(order04.isFrozen());
        assertEquals(REGULAR, order04.getDeliveryType());
        assertEquals(AddressStatus.UNKNOWN, order04.getAddressExternal().getStatus());

        OrderExternal order05 = ordersMap.get("05grande-0000-0000-0000-000000000000");
        assertTrue(order05.isFrozen());
        assertEquals(DeliveryType.POSTAL, order05.getDeliveryType());
        assertEquals(KNOWN, order05.getAddressExternal().getStatus());
        assertEquals(POSTAL_ADDRESS, order05.getAddressExternal().getOrderAddressId().getOrderAddress());
        assertEquals(POSTAL_ADDRESS, order05.getAddressExternal().getValidAddress());

        OrderExternal order06 = ordersMap.get("06grande-0000-0000-0000-000000000000");
        assertFalse(order06.isFrozen());
        assertEquals(DeliveryType.PRE_PACKING, order06.getDeliveryType());
        assertEquals(AddressStatus.KNOWN, order06.getAddressExternal().getStatus());
        assertEquals(PRE_PACKING_ADDRESS, order06.getAddressExternal().getValidAddress());
        assertEquals(PRE_PACKING_ADDRESS, order06.getAddressExternal().getOrderAddressId().getOrderAddress());

        OrderExternal rescheduled = ordersMap.get("f6f73d76-8005-11ec-b3ce-00155dd72305");
        assertFalse(rescheduled.isFrozen());
        assertEquals(REGULAR, rescheduled.getDeliveryType());
        assertEquals(AUTOVALIDATED, rescheduled.getAddressExternal().getStatus());
    }

    private void verifyRescheduledOrder(DeliveryDto delivery) {
        List<OrderExternal> deliveryOrders = orderExternalRepository.findByDeliveryId(delivery.getId(), Sort.unsorted());
        assertEquals(1, deliveryOrders.size());

        OrderExternal rescheduled = deliveryOrders.get(0);
        assertEquals(rescheduled.getRefKey(), "f6f73d76-8005-11ec-b3ce-00155dd72305");
        assertEquals(rescheduled.getDelivery().getId(), delivery.getId());
        assertFalse(rescheduled.isFrozen());
        assertEquals(REGULAR, rescheduled.getDeliveryType());
        assertEquals(AUTOVALIDATED, rescheduled.getAddressExternal().getStatus());
    }
}
