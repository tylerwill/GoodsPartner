package com.goodspartner.web.localization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;

import com.goodspartner.entity.DeliveryType;
import com.goodspartner.web.localization.abstracts.AbstractLocaleDeserializer;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Locale;
@Service
public class DeliveryTypeDeserializer extends AbstractLocaleDeserializer<DeliveryType> {
    @Override
    public DeliveryType deserialize(JsonParser jsonParser, DeserializationContext deSerialContext) throws IOException {
        Locale locale = getLocaleFromContext();
        String deliveryTypeFromJson = jsonParser.getText();
        String valueOfDeliveryTypeConsideringLocale = messageSource.getMessage(deliveryTypeFromJson, null, locale);

        return Enum.valueOf(DeliveryType.class, valueOfDeliveryTypeConsideringLocale);
    }
}
