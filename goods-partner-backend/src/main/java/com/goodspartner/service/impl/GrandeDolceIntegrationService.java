package com.goodspartner.service.impl;

import com.goodspartner.configuration.properties.ClientProperties;
import com.goodspartner.dto.InvoiceDto;
import com.goodspartner.dto.InvoiceProduct;
import com.goodspartner.dto.OrderDto;
import com.goodspartner.exception.EmptyIntegrationCallResult;
import com.goodspartner.mapper.InvoiceProductMapper;
import com.goodspartner.mapper.ODataInvoiceMapper;
import com.goodspartner.mapper.ODataOrderMapper;
import com.goodspartner.mapper.ProductMapper;
import com.goodspartner.service.IntegrationService;
import com.goodspartner.service.dto.external.grandedolce.*;
import com.goodspartner.util.ODataUrlBuilder;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GrandeDolceIntegrationService implements IntegrationService {
    // Order exclude reasons
    private static final String DELETED_ORDER_EXCLUDE_REASON = "Замовлення: %s має флаг видалення в 1С";
    private static final String INVOICE_MISSED_EXCLUDE_REASON = "Відсутня або видалена видаткова в 1С для замовлення: %s";
    // 1C data fetch configuration // TODO move to properties.yml and later to configuration
    private static final int PARTITION_SIZE = 100;
    private static final int INVOICE_PARTITION_SIZE = 25;
    private static final int RETRY_ATTEMPTS = 3;
    private static final int RETRY_DELAY = 5;
    private static final int ORDER_FETCH_LIMIT = Integer.MAX_VALUE; // API Key limit
    // Generic fetching
    private static final String PREFIX_ODATA = "/odata/standard.odata/";
    private static final String FORMAT = "json";
    private static final String REF_KEY_FILTER = "Ref_Key eq guid'%s'";
    private static final String DEAL_CAST_REQUEST = "Сделка eq cast(guid'%s','%s')";
    private static final String OBJECT_CAST_REQUEST = "Объект eq cast(guid'%s','%s')";
    private static final String ORGANISATION_CATALOG = "Catalog_Организации";
    private static final String ADDRESS_CONTACT_TYPE = "Адрес";
    private static final String PHONE_CONTACT_TYPE = "Телефон";
    private static final String SHOULD_LOAD_TO_BUH_BASE = "Вивантажувати в бух. базу";
    // Order fetching
    private static final String DATE_FILTER = "ДатаОтгрузки eq datetime'%s'";
    private static final String ORDER_ENTRY_SET_NAME = "Document_ЗаказПокупателя";
    private static final String ORDER_SELECT_FIELDS = "Ref_Key,Number,ДатаОтгрузки,Date,DeletionMark,АдресДоставки,Комментарий,Контрагент/Description,Ответственный/Code";
    private static final String ORDER_EXPAND_FIELDS = "Ответственный/ФизЛицо,Контрагент";
    // Invoice fetching
    private static final String INVOICE_ENTRY_SET_NAME = "Document_РеализацияТоваровУслуг";
    private static final String INVOICE_SELECT_FIELDS = "Ref_Key,Организация_Key,Сделка,DeletionMark,Number,Date,Posted,СуммаДокумента,АдресДоставки,ДоговорКонтрагента/Description,Контрагент/НаименованиеПолное,БанковскийСчетОрганизации/НомерСчета,БанковскийСчетОрганизации/Банк/Description,БанковскийСчетОрганизации/Банк/Code,Склад/Местонахождение,Организация/НаименованиеПолное,Ответственный/ФизЛицо/Description";
    private static final String INVOICE_EXPAND_FIELDS = "Организация,БанковскийСчетОрганизации/Банк,Контрагент,Склад,ДоговорКонтрагента,Ответственный/ФизЛицо";
    // Invoice product fetching
    private static final String INVOICE_PRODUCT_ENTRY_SET_NAME = "Document_РеализацияТоваровУслуг_Товары";
    private static final String INVOICE_PRODUCT_SELECT_FIELDS = "Ref_Key,LineNumber,Количество,КоличествоМест,Коэффициент,ЕдиницаИзмерения/Description,Номенклатура/Description,Номенклатура/Ref_Key,Номенклатура/НоменклатураГТД,Номенклатура/ЕдиницаХраненияОстатков/Description,Номенклатура/ЕдиницаХраненияОстатков/Коэффициент,Номенклатура/ЕдиницаИзмеренияМест/Description,Номенклатура/ЕдиницаИзмеренияМест/Коэффициент,Сумма,СуммаНДС,Цена,СерияНоменклатуры/СертификатФайл";
    private static final String INVOICE_PRODUCT_EXPAND_FIELDS = "СерияНоменклатуры,Номенклатура/*,ЕдиницаИзмерения";
    // Product GTD
    private static final String PRODUCT_GTD_ENTRY_SET_NAME = "Catalog_НоменклатураГТД";
    private static final String PRODUCT_GTD_SELECT_FIELDS = "Ref_Key,Owner_Key,КодУКТВЭД_Индекс";
    // Information register - organisation codes
    private static final String ORGANISATION_CODES_ENTRY_SET_NAME = "InformationRegister_КодыОрганизации/SliceLast()";
    private static final String ORGANISATION_CODES_SELECT_FIELDS = "Организация_Key,КодПоЕДРПОУ,ИНН,НомерСвидетельства";
    // Information register - organisation contacts
    private static final String ORGANISATION_CONTACTS_ENTRY_SET_NAME = "InformationRegister_КонтактнаяИнформация";
    private static final String ORGANISATION_CONTACTS_SELECT_FIELDS = "Объект,Тип,Вид,Представление";
    // Information register - object properties
    private static final String INVOICE_PROPERTIES_ENTRY_SET_NAME = "InformationRegister_ЗначенияСвойствОбъектов";
    private static final String INVOICE_PROPERTIES_SELECT_FIELDS = "Объект,Свойство_Key,Значение,Свойство/Description";
    private static final String INVOICE_PROPERTIES_EXPAND_FIELDS = "Свойство";
    //Catalog - contacts type
    private static final String CONTACTS_TYPE_ENTRY_SET_NAME = "Catalog_ВидыКонтактнойИнформации";
    private static final String CONTACTS_TYPE_SELECT_FIELDS = "Ref_Key,Description";
    private static final List<String> ORGANISATION_CONTACTS = List.of("Юридична адреса організації", "Телефон організації");

    // Services
    private final WebClient webClient;
    private final ODataOrderMapper odataOrderMapper;
    private final ProductMapper productMapper;
    private final InvoiceProductMapper invoiceProductMapper;
    private final ODataInvoiceMapper odataInvoiceMapper;
    // Props
    private final ClientProperties properties;

    @Override
    public List<InvoiceDto> getInvoicesByOrderRefKeys(List<String> orderRefKeys) {
        List<ODataInvoiceDto> oDataInvoiceDtos = getValidInvoicesForOrders(orderRefKeys);

        List<ODataInvoiceProductDto> oDataInvoiceProductDtos = getODataInvoiceProductsPartitioned(oDataInvoiceDtos);

        List<List<String>> productsGTDKeys = getPartitionOfProductsGTDKeys(oDataInvoiceProductDtos);
        List<ODataProductGtdDto> oDataProductGTD = getODataProductGTD(productsGTDKeys);

        Map<String, String> uktzedCodeMap = groupUktzedCodeByProductGtdRefKey(oDataProductGTD);
        addUktzedCodesToInvoiceProducts(oDataInvoiceProductDtos, uktzedCodeMap);

        Map<String, List<ODataInvoiceProductDto>> groupedInvoiceProducts = groupInvoiceProductsByInvoiceRefKey(oDataInvoiceProductDtos);
        addInvoiceProductsToInvoices(oDataInvoiceDtos, groupedInvoiceProducts);

        URI orderUri = buildOrderUri(createFilter(REF_KEY_FILTER, orderRefKeys));
        ODataWrapperDto<ODataOrderDto> oDataWrappedOrderDtos = getOrdersByRefKeys(orderUri);
        List<ODataOrderDto> oDataOrderDtos = oDataWrappedOrderDtos.getValue();

        Map<String, ODataOrderDto> groupedOrder = groupOrderByRefKey(oDataOrderDtos);
        addODataOrderDtoToInvoices(oDataInvoiceDtos, groupedOrder);

        Set<String> organisationsRefKeys = getOrganisationsRefKeys(oDataInvoiceDtos);
        List<ODataOrganisationCodesDto> organisationCodesDtos = getODataOrganisationCodes(organisationsRefKeys);

        Map<String, ODataOrganisationCodesDto> groupedOrganisationCodes = groupCodesByRefKey(organisationCodesDtos);
        addOrganisationCodesToInvoices(oDataInvoiceDtos, groupedOrganisationCodes);

        List<ODataContactsTypeDto> oDataContactsType = getODataContactsType(organisationsRefKeys);
        oDataContactsType = filterContactsType(oDataContactsType);
        List<String> contactsTypeKeys = getContactsTypeKeys(oDataContactsType);

        List<String> organisationKeysList = organisationsRefKeys.stream().toList();
        URI organisationContactsUri = buildOrganisationContactsUri(createCastFilterByRefKeys(organisationKeysList, OBJECT_CAST_REQUEST, ORGANISATION_CATALOG));
        ODataWrapperDto<ODataOrganisationContactsDto> organisationContacts = getOrganisationContacts(organisationContactsUri);
        List<ODataOrganisationContactsDto> contactValues = organisationContacts.getValue();

        List<ODataOrganisationContactsDto> oDataOrganisationContactsDtos = filterContactValue(contactValues, contactsTypeKeys);
        Map<String, String> contactMap = getContactMap(oDataOrganisationContactsDtos);
        addOrganisationContactsToInvoices(oDataInvoiceDtos, contactMap);

        List<List<String>> invoicesKeys = getInvoicesKeys(oDataInvoiceDtos);
        List<ODataInvoicePropertiesDto> rawInvoiceProperties = getODataInvoicePropertiesDto(invoicesKeys);
        List<ODataInvoicePropertiesDto> invoiceProperties = filterBuhBaseProperties(rawInvoiceProperties);
        Map<String, Boolean> buhBasePropertiesMap = getBuhBasePropertiesMap(invoiceProperties);
        addBuhBasePropertyToInvoices(oDataInvoiceDtos, buhBasePropertiesMap);

        return odataInvoiceMapper.mapList(oDataInvoiceDtos);
    }

    @Override
    public List<OrderDto> findAllByShippingDate(LocalDate deliveryDate) {
        log.info("Start fetching orders for date: {}", deliveryDate);
        long startTime = System.currentTimeMillis();

        URI orderUri = buildOrderUri(buildFilter(DATE_FILTER, deliveryDate.atStartOfDay().toString()));
        ODataWrapperDto<ODataOrderDto> oDataWrappedOrderDtos = getOrdersByShippingDate(orderUri);
        List<ODataOrderDto> oDataOrderDtosList = oDataWrappedOrderDtos.getValue();

        List<String> orderRefKeys = getOrderRefKeys(oDataOrderDtosList);
        List<ODataInvoiceDto> validOrdersInvoices = getValidInvoicesForOrders(orderRefKeys);

        List<String> validOrdersRefKeys = getRelevantOrderRefKeys(validOrdersInvoices);
        excludingInvalidOrders(validOrdersRefKeys, oDataOrderDtosList);

        List<ODataInvoiceProductDto> oDataInvoiceProductDtos = getODataInvoiceProductsPartitioned(validOrdersInvoices);
        Map<String, List<ODataInvoiceProductDto>> groupedInvoiceProducts = groupInvoiceProductsByInvoiceRefKey(oDataInvoiceProductDtos);
        addInvoiceProductsToInvoices(validOrdersInvoices, groupedInvoiceProducts);
        Map<String, List<InvoiceProduct>> invoiceProductGroupedByOrderRefKey = groupInvoiceProductByOrderRefKey(validOrdersInvoices);

        enrichOrdersByInvoiceProduct(oDataOrderDtosList, invoiceProductGroupedByOrderRefKey);

        log.info("{} Orders has been fetched from 1C for date: {} in {}",
                oDataOrderDtosList.size(), deliveryDate, System.currentTimeMillis() - startTime);

        return odataOrderMapper.toOrderDtosList(oDataOrderDtosList);
    }

    private List<ODataInvoicePropertiesDto> filterBuhBaseProperties(List<ODataInvoicePropertiesDto> source) {
        return source.stream()
                .filter(property -> SHOULD_LOAD_TO_BUH_BASE.equals(property.getPropertyName()))
                .collect(Collectors.toList());
    }

    private void excludingInvalidOrders(List<String> relevantOrderRefKeys, List<ODataOrderDto> source) {
        for (ODataOrderDto oDataOrderDto : source) {
            if (oDataOrderDto.getDeletionMark()) {
                log.info(String.format("Order has been excluded due to deletionMark is true. Number of order %s, shipping date - %s, client - %s",
                        oDataOrderDto.getOrderNumber(),
                        oDataOrderDto.getShippingDate(),
                        oDataOrderDto.getClientName()));
                excludeOrder(oDataOrderDto, String.format(DELETED_ORDER_EXCLUDE_REASON, oDataOrderDto.getOrderNumber()));
            } else if (!relevantOrderRefKeys.contains(oDataOrderDto.getRefKey())) {
                log.info(String.format("Order with number %s, shipping date %s and client %s has been excluded because it does not apply to any invoices",
                        oDataOrderDto.getOrderNumber(),
                        oDataOrderDto.getShippingDate(),
                        oDataOrderDto.getClientName()));
                excludeOrder(oDataOrderDto, String.format(INVOICE_MISSED_EXCLUDE_REASON, oDataOrderDto.getOrderNumber()));
            }
        }
    }

    private void excludeOrder(ODataOrderDto oDataOrderDto, String reason) {
        oDataOrderDto.setExcluded(true);
        oDataOrderDto.setExcludeReason(reason);
    }

    private List<String> getRelevantOrderRefKeys(List<ODataInvoiceDto> oDataInvoiceDtos) {
        return oDataInvoiceDtos.stream()
                .map(ODataInvoiceDto::getOrderRefKey)
                .collect(Collectors.toList());
    }

    private List<String> getOrderRefKeys(List<ODataOrderDto> orders) {
        return orders.stream()
                .map(ODataOrderDto::getRefKey)
                .collect(Collectors.toList());
    }

    private List<ODataInvoiceProductDto> getODataInvoiceProductsPartitioned(List<ODataInvoiceDto> oDataInvoiceDtos) {
        List<List<String>> invoiceKeys = getPartitionOfInvoiceKeys(oDataInvoiceDtos);
        return getODataInvoiceProducts(invoiceKeys);
    }

    private List<ODataInvoiceDto> getValidInvoicesForOrders(List<String> orderRefKeys) {
        return Lists.partition(orderRefKeys, INVOICE_PARTITION_SIZE)
                .stream()
                .map(orderKeysPart -> buildInvoiceUri(createCastFilterByRefKeys(orderKeysPart, DEAL_CAST_REQUEST, ORDER_ENTRY_SET_NAME)))
                .map(this::getInvoices)
                .map(ODataWrapperDto::getValue)
                .map(this::filterValidInvoices)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<ODataInvoiceDto> filterValidInvoices(List<ODataInvoiceDto> source) {
        List<ODataInvoiceDto> oDataInvoiceDtos = new ArrayList<>();
        for (ODataInvoiceDto oDataInvoiceDto : source) {
            if (oDataInvoiceDto.getDeletionMark()) {
                log.info("Ignored invoice due to deletionMark is {}. Number of invoice {}, date - {}, client - {}",
                        oDataInvoiceDto.getDeletionMark(),
                        oDataInvoiceDto.getNumber(),
                        oDataInvoiceDto.getDocumentDate(),
                        oDataInvoiceDto.getClientName());
            } else {
                oDataInvoiceDtos.add(oDataInvoiceDto);
            }
        }
        return oDataInvoiceDtos;
    }

    private Boolean isAddresContactType(ODataOrganisationContactsDto oDataOrganisationContactsDto) {
        return ADDRESS_CONTACT_TYPE.equalsIgnoreCase(oDataOrganisationContactsDto.getType());
    }

    private Boolean isPhoneContactType(ODataOrganisationContactsDto oDataOrganisationContactsDto) {
        return PHONE_CONTACT_TYPE.equalsIgnoreCase(oDataOrganisationContactsDto.getType());
    }

    private List<ODataOrganisationContactsDto> filterContactValue(List<ODataOrganisationContactsDto> contactsValue, List<String> contactsTypeKeys) {
        return contactsValue.stream()
                .filter(contact -> contactsTypeKeys.contains(contact.getView()))
                .collect(Collectors.toList());
    }

    private List<ODataContactsTypeDto> filterContactsType(List<ODataContactsTypeDto> oDataContactsType) {
        return oDataContactsType.stream()
                .filter(contactsType -> ORGANISATION_CONTACTS.contains(contactsType.getDescription()))
                .collect(Collectors.toList());
    }

    private Set<String> getOrganisationsRefKeys(List<ODataInvoiceDto> oDataInvoiceDtos) {
        return oDataInvoiceDtos.stream()
                .map(ODataInvoiceDto::getOrganisationRefKey)
                .collect(Collectors.toSet());
    }

    /**
     * Get orders from 1C
     */
    private ODataWrapperDto<ODataOrderDto> getOrdersByRefKeys(URI orderUriByRefKeys) {
        log.info("Orders by refKeys uri: {}", URLDecoder.decode(orderUriByRefKeys.toString(), StandardCharsets.UTF_8));
        return Optional.ofNullable(
                        webClient.get()
                                .uri(orderUriByRefKeys)
                                .headers(httpHeaders -> httpHeaders.setBasicAuth(properties.getLogin(), properties.getPassword()))
                                .retrieve()
                                .bodyToMono(new ParameterizedTypeReference<ODataWrapperDto<ODataOrderDto>>() {
                                })
                                .retryWhen(Retry.backoff(RETRY_ATTEMPTS, Duration.ofSeconds(RETRY_DELAY)))
                                .block())
                .orElseThrow(EmptyIntegrationCallResult::new);
    }

    private ODataWrapperDto<ODataOrderDto> getOrdersByShippingDate(URI orderUriByShippingDate) {
        log.info("Orders by shippingDate uri: {}", URLDecoder.decode(orderUriByShippingDate.toString(), StandardCharsets.UTF_8));
        return Optional.ofNullable(
                        webClient.get()
                                .uri(orderUriByShippingDate)
                                .headers(httpHeaders -> httpHeaders.setBasicAuth(properties.getLogin(), properties.getPassword()))
                                .retrieve()
                                .bodyToMono(new ParameterizedTypeReference<ODataWrapperDto<ODataOrderDto>>() {
                                })
                                .retryWhen(Retry.backoff(RETRY_ATTEMPTS, Duration.ofSeconds(RETRY_DELAY)))
                                .block())
                .orElseThrow(EmptyIntegrationCallResult::new);
    }

    private ODataWrapperDto<ODataInvoiceProductDto> getODataInvoiceProducts(URI productUri) {
        log.info("InvoiceProduct uri: {}", URLDecoder.decode(productUri.toString(), StandardCharsets.UTF_8));
        return Optional.ofNullable(
                        webClient.get()
                                .uri(productUri)
                                .headers(httpHeaders -> httpHeaders.setBasicAuth(properties.getLogin(), properties.getPassword()))
                                .retrieve()
                                .bodyToMono(new ParameterizedTypeReference<ODataWrapperDto<ODataInvoiceProductDto>>() {
                                })
                                .retryWhen(Retry.backoff(RETRY_ATTEMPTS, Duration.ofSeconds(RETRY_DELAY)))
                                .block())
                .orElseThrow(EmptyIntegrationCallResult::new);
    }

    private ODataWrapperDto<ODataInvoiceDto> getInvoices(URI uri) {
        log.info("Invoice uri: {}", URLDecoder.decode(uri.toString(), StandardCharsets.UTF_8));
        return Optional.ofNullable(
                        webClient.get()
                                .uri(uri)
                                .headers(httpHeaders -> httpHeaders.setBasicAuth(properties.getLogin(), properties.getPassword()))
                                .retrieve()
                                .bodyToMono(new ParameterizedTypeReference<ODataWrapperDto<ODataInvoiceDto>>() {
                                })
                                .retryWhen(Retry.backoff(RETRY_ATTEMPTS, Duration.ofSeconds(RETRY_DELAY)))
                                .block())
                .orElseThrow(EmptyIntegrationCallResult::new);
    }

    private ODataWrapperDto<ODataOrganisationContactsDto> getOrganisationContacts(URI uri) {
        log.info("Organisation contracts uri: {}", URLDecoder.decode(uri.toString(), StandardCharsets.UTF_8));
        return Optional.ofNullable(
                        webClient.get()
                                .uri(uri)
                                .headers(httpHeaders -> httpHeaders.setBasicAuth(properties.getLogin(), properties.getPassword()))
                                .retrieve()
                                .bodyToMono(new ParameterizedTypeReference<ODataWrapperDto<ODataOrganisationContactsDto>>() {
                                })
                                .retryWhen(Retry.backoff(RETRY_ATTEMPTS, Duration.ofSeconds(RETRY_DELAY)))
                                .block())
                .orElseThrow(EmptyIntegrationCallResult::new);
    }

    private ODataWrapperDto<ODataInvoicePropertiesDto> getInvoiceProperties(URI uri) {
        log.info("Invoice properties uri: {}", URLDecoder.decode(uri.toString(), StandardCharsets.UTF_8));
        return Optional.ofNullable(
                        webClient.get()
                                .uri(uri)
                                .headers(httpHeaders -> httpHeaders.setBasicAuth(properties.getLogin(), properties.getPassword()))
                                .retrieve()
                                .bodyToMono(new ParameterizedTypeReference<ODataWrapperDto<ODataInvoicePropertiesDto>>() {
                                })
                                .retryWhen(Retry.backoff(RETRY_ATTEMPTS, Duration.ofSeconds(RETRY_DELAY)))
                                .block())
                .orElseThrow(EmptyIntegrationCallResult::new);
    }

    private ODataWrapperDto<ODataProductGtdDto> getODataProductGtd(URI productUri) {
        log.info("Product gtd uri: {}", URLDecoder.decode(productUri.toString(), StandardCharsets.UTF_8));
        return Optional.ofNullable(
                        webClient.get()
                                .uri(productUri)
                                .headers(httpHeaders -> httpHeaders.setBasicAuth(properties.getLogin(), properties.getPassword()))
                                .retrieve()
                                .bodyToMono(new ParameterizedTypeReference<ODataWrapperDto<ODataProductGtdDto>>() {
                                })
                                .retryWhen(Retry.backoff(RETRY_ATTEMPTS, Duration.ofSeconds(RETRY_DELAY)))
                                .block())
                .orElseThrow(EmptyIntegrationCallResult::new);
    }

    private ODataWrapperDto<ODataOrganisationCodesDto> getODataOrganisationCodes(URI organisationCodesUri) {
        log.info("Organisation codes uri: {}", URLDecoder.decode(organisationCodesUri.toString(), StandardCharsets.UTF_8));
        return Optional.ofNullable(
                        webClient.get()
                                .uri(organisationCodesUri)
                                .headers(httpHeaders -> httpHeaders.setBasicAuth(properties.getLogin(), properties.getPassword()))
                                .retrieve()
                                .bodyToMono(new ParameterizedTypeReference<ODataWrapperDto<ODataOrganisationCodesDto>>() {
                                })
                                .retryWhen(Retry.backoff(RETRY_ATTEMPTS, Duration.ofSeconds(RETRY_DELAY)))
                                .block())
                .orElseThrow(EmptyIntegrationCallResult::new);
    }

    private ODataWrapperDto<ODataContactsTypeDto> getODataContactsTypes(URI organisationCodesUri) {
        log.info("Contract types uri: {}", URLDecoder.decode(organisationCodesUri.toString(), StandardCharsets.UTF_8));
        return Optional.ofNullable(
                        webClient.get()
                                .uri(organisationCodesUri)
                                .headers(httpHeaders -> httpHeaders.setBasicAuth(properties.getLogin(), properties.getPassword()))
                                .retrieve()
                                .bodyToMono(new ParameterizedTypeReference<ODataWrapperDto<ODataContactsTypeDto>>() {
                                })
                                .retryWhen(Retry.backoff(RETRY_ATTEMPTS, Duration.ofSeconds(RETRY_DELAY)))
                                .block())
                .orElseThrow(EmptyIntegrationCallResult::new);
    }

    /*URI*/

    private URI buildOrderUri(String filter) {
        return new ODataUrlBuilder()
                .baseUrl(properties.getClientServerURL())
                .hostPrefix(properties.getServer1CUriPrefix() + PREFIX_ODATA)
                .appendEntitySetSegment(ORDER_ENTRY_SET_NAME)
                .filter(filter)
                .expand(ORDER_EXPAND_FIELDS)
                .select(ORDER_SELECT_FIELDS)
                .format(FORMAT)
                .top(ORDER_FETCH_LIMIT)
                .build();
    }

    private URI buildInvoiceUri(String filter) {
        return new ODataUrlBuilder()
                .baseUrl(properties.getClientServerURL())
                .hostPrefix(properties.getServer1CUriPrefix() + PREFIX_ODATA)
                .appendEntitySetSegment(INVOICE_ENTRY_SET_NAME)
                .filter(filter)
                .expand(INVOICE_EXPAND_FIELDS)
                .select(INVOICE_SELECT_FIELDS)
                .format(FORMAT)
                .build();
    }

    private URI buildOrganisationContactsUri(String filter) {
        return new ODataUrlBuilder()
                .baseUrl(properties.getClientServerURL())
                .hostPrefix(properties.getServer1CUriPrefix() + PREFIX_ODATA)
                .appendEntitySetSegment(ORGANISATION_CONTACTS_ENTRY_SET_NAME)
                .filter(filter)
                .select(ORGANISATION_CONTACTS_SELECT_FIELDS)
                .format(FORMAT)
                .build();
    }

    private URI buildInvoicePropertiesUri(String filter) {
        return new ODataUrlBuilder()
                .baseUrl(properties.getClientServerURL())
                .hostPrefix(properties.getServer1CUriPrefix() + PREFIX_ODATA)
                .appendEntitySetSegment(INVOICE_PROPERTIES_ENTRY_SET_NAME)
                .filter(filter)
                .expand(INVOICE_PROPERTIES_EXPAND_FIELDS)
                .select(INVOICE_PROPERTIES_SELECT_FIELDS)
                .format(FORMAT)
                .build();
    }

    private URI buildInvoiceProductUri(String filter) {
        return new ODataUrlBuilder()
                .baseUrl(properties.getClientServerURL())
                .hostPrefix(properties.getServer1CUriPrefix() + PREFIX_ODATA)
                .appendEntitySetSegment(INVOICE_PRODUCT_ENTRY_SET_NAME)
                .filter(filter)
                .expand(INVOICE_PRODUCT_EXPAND_FIELDS)
                .select(INVOICE_PRODUCT_SELECT_FIELDS)
                .format(FORMAT)
                .build();
    }

    private URI buildProductGTDUri(String filter) {
        return new ODataUrlBuilder()
                .baseUrl(properties.getClientServerURL())
                .hostPrefix(properties.getServer1CUriPrefix() + PREFIX_ODATA)
                .appendEntitySetSegment(PRODUCT_GTD_ENTRY_SET_NAME)
                .filter(filter)
                .select(PRODUCT_GTD_SELECT_FIELDS)
                .format(FORMAT)
                .build();
    }

    private URI buildOrganisationCodesUri() {
        return new ODataUrlBuilder()
                .baseUrl(properties.getClientServerURL())
                .hostPrefix(properties.getServer1CUriPrefix() + PREFIX_ODATA)
                .appendEntitySetSegment(ORGANISATION_CODES_ENTRY_SET_NAME)
                .select(ORGANISATION_CODES_SELECT_FIELDS)
                .format(FORMAT)
                .build();
    }

    private URI buildContactsTypeUri() {
        return new ODataUrlBuilder()
                .baseUrl(properties.getClientServerURL())
                .hostPrefix(properties.getServer1CUriPrefix() + PREFIX_ODATA)
                .appendEntitySetSegment(CONTACTS_TYPE_ENTRY_SET_NAME)
                .select(CONTACTS_TYPE_SELECT_FIELDS)
                .format(FORMAT)
                .build();
    }

    /**
     * If amount of orders would be huge, for possibility to send request for retrieving products -
     * split on partition of refKeys list for further creation filter for OData request
     */
    private List<List<String>> getPartitionOfInvoiceKeys(List<ODataInvoiceDto> invoices) {
        List<String> listRefKeys = invoices.stream()
                .map(ODataInvoiceDto::getRefKey)
                .collect(Collectors.toList());
        return Lists.partition(listRefKeys, INVOICE_PARTITION_SIZE);
    }

    private List<List<String>> getPartitionOfProductsGTDKeys(List<ODataInvoiceProductDto> invoiceProducts) {
        List<String> listRefKeys = invoiceProducts.stream()
                .map(ODataInvoiceProductDto::getProductGTDRefKey)
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toList());
        return Lists.partition(listRefKeys, PARTITION_SIZE);
    }

    private List<List<String>> getInvoicesKeys(List<ODataInvoiceDto> oDataInvoiceDtos) {
        return Lists.partition(oDataInvoiceDtos.stream()
                .map(ODataInvoiceDto::getRefKey)
                .collect(Collectors.toList()), INVOICE_PARTITION_SIZE);
    }

    private List<String> getContactsTypeKeys(List<ODataContactsTypeDto> invoiceProductsStream) {
        return invoiceProductsStream.stream()
                .map(ODataContactsTypeDto::getRefKey)
                .collect(Collectors.toList());
    }

    /**
     * - From partitions (list of refKeys) create filter
     * - build appropriate URI
     * - get response and parse it to ODataWrapperDto<ODataProductDto>
     * - collect to Map<RefKey, List<ProductDto>>
     */
    private List<ODataInvoiceProductDto> getODataInvoiceProducts(List<List<String>> partition) {
        return partition.stream()
                .map(part -> createFilter(REF_KEY_FILTER, part))
                .map(this::buildInvoiceProductUri)
                .map(this::getODataInvoiceProducts)
                .map(ODataWrapperDto::getValue)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<ODataProductGtdDto> getODataProductGTD(List<List<String>> productGTDRefKeys) {
        return productGTDRefKeys.stream()
                .map(productGTDRefKey -> createFilter(REF_KEY_FILTER, productGTDRefKey))
                .map(this::buildProductGTDUri)
                .map(this::getODataProductGtd)
                .map(ODataWrapperDto::getValue)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<ODataInvoicePropertiesDto> getODataInvoicePropertiesDto(List<List<String>> invoiceRefKeys) {
        return invoiceRefKeys.stream()
                .map(invoicesRefKey -> createCastFilterByRefKeys(invoicesRefKey, OBJECT_CAST_REQUEST, INVOICE_ENTRY_SET_NAME))
                .map(this::buildInvoicePropertiesUri)
                .map(this::getInvoiceProperties)
                .map(ODataWrapperDto::getValue)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<ODataOrganisationCodesDto> getODataOrganisationCodes(Set<String> organisationRefKeys) {
        URI organisationCodesUri = buildOrganisationCodesUri();
        return organisationRefKeys.stream()
                .map(organisationRefKey -> getODataOrganisationCodes(organisationCodesUri))
                .map(ODataWrapperDto::getValue)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<ODataContactsTypeDto> getODataContactsType(Set<String> organisationRefKeys) {
        URI organisationCodesUri = buildContactsTypeUri();
        return organisationRefKeys.stream()
                .map(organisationRefKey -> getODataContactsTypes(organisationCodesUri))
                .map(ODataWrapperDto::getValue)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private Map<String, List<ODataInvoiceProductDto>> groupInvoiceProductsByInvoiceRefKey(List<ODataInvoiceProductDto> oDataInvoice) {
        return oDataInvoice.stream()
                .collect(Collectors.groupingBy(ODataInvoiceProductDto::getRefKey, Collectors.toList()));
    }

    private Map<String, List<InvoiceProduct>> groupInvoiceProductByOrderRefKey(List<ODataInvoiceDto> invoices) {
        return invoices.stream()
                .collect(Collectors.toMap(ODataInvoiceDto::getOrderRefKey, ODataInvoiceDto::getProducts));
    }

    private Map<String, ODataOrderDto> groupOrderByRefKey(List<ODataOrderDto> oDataOrderDtosList) {
        return oDataOrderDtosList.stream()
                .collect(Collectors.toMap(ODataOrderDto::getRefKey, Function.identity()));
    }

    private Map<String, String> groupUktzedCodeByProductGtdRefKey(List<ODataProductGtdDto> oDataProductGTD) {
        return oDataProductGTD.stream()
                .collect(Collectors.toMap(ODataProductGtdDto::getRefKey,
                        ODataProductGtdDto::getUktzedCode,
                        (key1, key2) -> key1));
    }

    private Map<String, ODataOrganisationCodesDto> groupCodesByRefKey(List<ODataOrganisationCodesDto> organisationCodesDtos) {
        return organisationCodesDtos.stream()
                .collect(Collectors.toMap(ODataOrganisationCodesDto::getOrganisationRefKey, Function.identity()));
    }

    private Map<String, String> getContactMap(List<ODataOrganisationContactsDto> oDataOrganisationContactsDtos) {
        Map<String, String> contactMap = new HashMap<>();
        for (ODataOrganisationContactsDto oDataOrganisationContactsDto : oDataOrganisationContactsDtos) {
            if (isAddresContactType(oDataOrganisationContactsDto)) {
                contactMap.put(ADDRESS_CONTACT_TYPE, oDataOrganisationContactsDto.getContact());
            }
            if (isPhoneContactType(oDataOrganisationContactsDto)) {
                contactMap.put(PHONE_CONTACT_TYPE, oDataOrganisationContactsDto.getContact());
            }
        }
        return contactMap;
    }

    private Map<String, Boolean> getBuhBasePropertiesMap(List<ODataInvoicePropertiesDto> invoiceProperties) {
        return invoiceProperties.stream()
                .collect(Collectors.toMap(
                        ODataInvoicePropertiesDto::getInvoiceRefKey,
                        ODataInvoicePropertiesDto::getPropertyValue
                ));
    }

    /**
     * Insert products into orders
     */

    void enrichOrdersByInvoiceProduct(List<ODataOrderDto> orders,
                                      Map<String, List<InvoiceProduct>> invoiceProductGroupedByOrderRefKey) {
        for (ODataOrderDto order : orders) {
            if (!order.isExcluded()) {
                String refKey = order.getRefKey();
                List<InvoiceProduct> products = invoiceProductGroupedByOrderRefKey.get(refKey);
                order.setProducts(productMapper.invoiceProductToProductList(products));
                order.setOrderWeight(getTotalOrderWeightFromInvoiceProduct(products)); // TODO probably totalOrderWeight is not a part for integration
            } else {
                order.setProducts(new ArrayList<>());
            }
        }
    }

    void addInvoiceProductsToInvoices(List<ODataInvoiceDto> oDataInvoiceDtos, Map<String, List<ODataInvoiceProductDto>> allInvoiceProducts) {
        for (ODataInvoiceDto invoice : oDataInvoiceDtos) {
            String refKey = invoice.getRefKey();
            List<ODataInvoiceProductDto> products = allInvoiceProducts.get(refKey);
            invoice.setProducts(invoiceProductMapper.toInvoiceProductList(products));
        }
    }

    private void addUktzedCodesToInvoiceProducts(List<ODataInvoiceProductDto> oDataProductGTD, Map<String, String> productGtdMap) {
        for (ODataInvoiceProductDto oDataProductGtdDto : oDataProductGTD) {
            String productGtdDtoRefKey = oDataProductGtdDto.getProductGTDRefKey();
            Optional.ofNullable(productGtdMap.get(productGtdDtoRefKey))
                    .ifPresent(oDataProductGtdDto::setUktzedCode);
        }
    }

    private void addOrganisationCodesToInvoices(List<ODataInvoiceDto> oDataInvoiceDtos, Map<String, ODataOrganisationCodesDto> organisationCodesMap) {
        for (ODataInvoiceDto oDataInvoiceDto : oDataInvoiceDtos) {
            String organisationRefKey = oDataInvoiceDto.getOrganisationRefKey();
            ODataOrganisationCodesDto oDataOrganisationCodesDto = organisationCodesMap.get(organisationRefKey);
            oDataInvoiceDto.setOrganisationCodes(oDataOrganisationCodesDto);
        }
    }

    private void addOrganisationContactsToInvoices(List<ODataInvoiceDto> oDataInvoiceDtos, Map<String, String> contactMap) {
        for (ODataInvoiceDto oDataInvoiceDto : oDataInvoiceDtos) {
            oDataInvoiceDto.setAddress(contactMap.get(ADDRESS_CONTACT_TYPE));
            oDataInvoiceDto.setPhone(contactMap.get(PHONE_CONTACT_TYPE));
        }
    }

    private void addBuhBasePropertyToInvoices(List<ODataInvoiceDto> oDataInvoiceDtos, Map<String, Boolean> propertyMap) {
        for (ODataInvoiceDto oDataInvoiceDto : oDataInvoiceDtos) {
            Boolean buhBaseProperty = propertyMap.get(oDataInvoiceDto.getRefKey());
            oDataInvoiceDto.setBuhBaseProperty(buhBaseProperty != null ? buhBaseProperty : false);
        }
    }

    private void addODataOrderDtoToInvoices(List<ODataInvoiceDto> oDataInvoiceDtos, Map<String, ODataOrderDto> oDataGroupedByOrder) {
        for (ODataInvoiceDto oDataInvoiceDto : oDataInvoiceDtos) {
            String orderRefKey = oDataInvoiceDto.getOrderRefKey();
            ODataOrderDto oDataOrderDto = oDataGroupedByOrder.get(orderRefKey);
            oDataInvoiceDto.setOrder(oDataOrderDto);
        }
    }

    double getTotalOrderWeight(List<ODataProductDto> products) {
        return products.stream()
                .mapToDouble(ODataProductDto::getTotalProductWeight)
                .sum();
    }

    double getTotalOrderWeightFromInvoiceProduct(List<InvoiceProduct> products) {
        return products.stream()
                .mapToDouble(InvoiceProduct::getTotalProductWeight)
                .sum();
    }

    /**
     * Filters
     */
    String createFilter(String filter, List<String> refKeys) {
        StringJoiner stringJoiner = new StringJoiner(" or ");
        refKeys.forEach(
                key -> stringJoiner.add(buildFilter(filter, key))
        );
        return stringJoiner.toString();
    }

    String buildFilter(String filter, String key) {
        return String.format(filter, key);
    }

    private String createFilterByRefKey(String refKey, String castRequest, String entrySet) {
        return String.format(castRequest, refKey, entrySet);
    }

    private String createCastFilterByRefKeys(List<String> orderRefKeys, String castRequest, String entrySet) {
        StringJoiner stringJoiner = new StringJoiner(" or ", "", "");
        orderRefKeys.forEach(refKey -> stringJoiner.add(createFilterByRefKey(refKey, castRequest, entrySet)));
        return stringJoiner.toString();
    }
}