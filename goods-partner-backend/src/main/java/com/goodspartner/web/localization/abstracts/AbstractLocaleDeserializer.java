package com.goodspartner.web.localization.abstracts;

import com.fasterxml.jackson.databind.JsonDeserializer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

public abstract class AbstractLocaleDeserializer<T> extends JsonDeserializer<T> {

    protected static ResourceBundleMessageSource messageSource;

    @Autowired
    public void setMessageSource(ResourceBundleMessageSource messageSource)  {
        AbstractLocaleDeserializer.messageSource = messageSource;
    }
    protected Locale getLocaleFromContext() {
        return LocaleContextHolder.getLocale();
    }
}
