package com.goodspartner.web.localization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.goodspartner.entity.DeliveryType;
import com.goodspartner.web.localization.abstracts.AbstractLocaleSerializer;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class DeliveryTypeSerializer extends AbstractLocaleSerializer<DeliveryType> {

    public void serialize(DeliveryType deliveryType, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
        super.serialize(deliveryType, jsonGenerator, serializers);
    }
}
