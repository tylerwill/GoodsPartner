package com.goodspartner.web.localization.abstracts;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.io.IOException;
import java.util.Locale;

public abstract class AbstractLocaleSerializer<T> extends JsonSerializer<T> {

    protected static ResourceBundleMessageSource messageSource;

    @Autowired
    public void setMessageSource(ResourceBundleMessageSource messageSource)  {
        AbstractLocaleSerializer.messageSource = messageSource;
    }
    public void serialize(T deliveryType, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
        Locale locale = getLocaleFromContext();
        String valueOfDeliveryTypeConsideringLocale = messageSource.getMessage(String.valueOf(deliveryType), null, locale);

        jsonGenerator.writeString(valueOfDeliveryTypeConsideringLocale);
    }
    protected Locale getLocaleFromContext() {
        return LocaleContextHolder.getLocale();
    }
}
