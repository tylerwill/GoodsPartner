package com.goodspartner.web.controller;

import com.goodspartner.util.FileReader;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/mock1CoData", produces = MediaType.APPLICATION_JSON_VALUE)
public class MockedODataController {
    private static final String FILE_PATH = "mock1CoData/";
    private final FileReader fileReader;

    @GetMapping("{file}")
    public String get1CoData(@PathVariable String file) {
        return fileReader.readFileAsString(FILE_PATH + file);
    }
}