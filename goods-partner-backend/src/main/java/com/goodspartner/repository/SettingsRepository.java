package com.goodspartner.repository;

import com.goodspartner.entity.Setting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingsRepository extends JpaRepository<Setting, Setting.SettingKey> {
}
