package com.goodspartner.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "settings")
public class Setting {

    @EmbeddedId
    private SettingKey settingKey;
    @Column
    private String properties;

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Embeddable
    @Getter
    @Setter
    public static class SettingKey implements Serializable {
        @Enumerated(value = EnumType.STRING)
        @Column(name = "group_id")
        private SettingsGroup group;
        @Enumerated(value = EnumType.STRING)
        @Column(name = "category_id")
        private SettingsCategory category;

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            SettingKey that = (SettingKey) obj;
            return Objects.equals(group, that.getGroup()) &&
                    Objects.equals(category, that.getCategory());
        }

        @Override
        public int hashCode() {
            return Objects.hash(group, category);
        }

        @Override
        public String toString() {
            return "SettingKey{" +
                    "group=" + group +
                    ", category=" + category +
                    '}';
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Setting that = (Setting) o;
        return Objects.equals(settingKey, that.settingKey) &&
                Objects.equals(properties, that.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(settingKey, properties);
    }

    @Override
    public String toString() {
        return "Setting{" +
                "settingKey=" + settingKey +
                ", properties='" + properties +
                '}';
    }
}
