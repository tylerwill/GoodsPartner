package com.goodspartner.service.document.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
public class TempPdfFilesGC {
    private static final Path TEMP_PDF_FILES = Paths.get("temp_pdf_files");

    @PostConstruct
    void createTempFolder() throws IOException {
        Files.createDirectories(TEMP_PDF_FILES);
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void deleteFilesScheduledTask() throws IOException {
        FileUtils.cleanDirectory(TEMP_PDF_FILES.toFile());
    }
}
