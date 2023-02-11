package com.goodspartner.web.controller;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.goodspartner.AbstractWebITest;
import com.goodspartner.config.TestSecurityDisableConfig;
import com.goodspartner.service.SettingsCache;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Objects;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DBRider
@AutoConfigureMockMvc(addFilters = false)
@Import({TestSecurityDisableConfig.class})
class SettingsControllerITest extends AbstractWebITest {

    private static final String SETTINGS_ENDPOINT = "/api/v1/settings";
    private static final String SETTINGS_DATASET = "datasets/settings/settings-dataset.json";
    private static final String SETTINGS_RESPONSE = "datasets/settings/response-settings-dto.json";

    @Test
    @DataSet(value = SETTINGS_DATASET,
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    void shouldResponseValidSettingsDtoOnRequestFromFe() throws Exception {
        var context = mockMvc.getDispatcherServlet().getWebApplicationContext();
        Objects.requireNonNull(context);
        var cacheBean = context.getBean(SettingsCache.class);
        cacheBean.setUpCache();

        mockMvc
                .perform(MockMvcRequestBuilders
                        .get(SETTINGS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseAsString(SETTINGS_RESPONSE)));
    }

    @Test
    @DataSet(value = SETTINGS_DATASET,
            cleanAfter = true, cleanBefore = true, skipCleaningFor = "flyway_schema_history")
    void shouldAddSettingsThatWereProvidedFromFe() throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders
                        .put(SETTINGS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(getResponseAsString(SETTINGS_RESPONSE)))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseAsString(SETTINGS_RESPONSE)));
    }
}