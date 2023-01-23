package com.goodspartner;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class TestUtil {

    public static String getResourceAsString(String jsonPath) {
        URL resource = TestUtil.class.getClassLoader().getResource(jsonPath);
        try {
            return FileUtils.readFileToString(new File(resource.toURI()), StandardCharsets.UTF_8);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("Unable to find file: " + jsonPath);
        }
    }

}
