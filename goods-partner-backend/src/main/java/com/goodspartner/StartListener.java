package com.goodspartner;

import com.goodspartner.service.SettingsCache;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class StartListener {

    private final SettingsCache settingsCache;

    @EventListener(ApplicationReadyEvent.class)
    public void onReadyEvent() {
        settingsCache.setUpCache();
    }

}
