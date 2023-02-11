package com.goodspartner.annotations;

import com.goodspartner.entity.SettingsCategory;
import com.goodspartner.entity.SettingsGroup;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SettingsAllocation {
    SettingsGroup group();

    SettingsCategory category();
}
