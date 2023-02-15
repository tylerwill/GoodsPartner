package com.goodspartner;

import com.goodspartner.service.SettingsCache;
import com.google.ortools.Loader;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class StartListener {

    private final SettingsCache settingsCache;

    @EventListener(ApplicationReadyEvent.class)
    public void onReadyEvent(@SuppressWarnings("unused") ApplicationReadyEvent applicationReadyEvent) {
        log.info("Initializing custom client settings");
        settingsCache.setUpCache();

        log.info("Initializing OR-Tools Native libraries");
        Loader.loadNativeLibraries();
    }

}
