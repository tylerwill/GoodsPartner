package com.goodspartner;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;


@AutoConfigureMockMvc
public class AbstractWebITest extends AbstractBaseITest {

    @Autowired
    protected MockMvc mockMvc;

}
