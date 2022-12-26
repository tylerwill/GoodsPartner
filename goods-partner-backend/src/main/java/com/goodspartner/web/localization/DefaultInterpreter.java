package com.goodspartner.web.localization;

import com.goodspartner.entity.DeliveryType;
import com.goodspartner.web.localization.interfaces.Interpreter;
import org.mapstruct.Named;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class DefaultInterpreter implements Interpreter {
    private final ResourceBundleMessageSource messageSource;

    public DefaultInterpreter(ResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    @Named("enumToString")
    public String enumToString(DeliveryType deliveryTypeFromDb) {
        Locale locale = LocaleContextHolder.getLocale();

        return messageSource.getMessage(deliveryTypeFromDb.toString(), null, locale);
    }

    @Override
    @Named("stringToEnum")
    public DeliveryType stringToEnum(String deliveryTypeFromFrontend) {
        Locale locale = LocaleContextHolder.getLocale();
        String nameOfEnum = messageSource.getMessage(deliveryTypeFromFrontend, null, locale);

        return Enum.valueOf( DeliveryType.class, nameOfEnum);
    }
}
