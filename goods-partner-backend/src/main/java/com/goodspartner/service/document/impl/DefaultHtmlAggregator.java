package com.goodspartner.service.document.impl;

import com.goodspartner.service.document.HtmlAggregator;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.Objects;

public class DefaultHtmlAggregator implements HtmlAggregator {

    private static final String ENCODING_UTF_8 = "UTF-8";
    private static final Configuration CONFIG = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
    private static final String TEMPLATES_DIR = "/documents";

    static {
        setTemplateLoader();
        setEncoding();
    }

    public String getEnrichedHtml(Object incomeDto, String htmlTemplate) {
        try {
            requiredTemplateNameNotNullNotEmpty(htmlTemplate);
            requiredContentNotNull(incomeDto);

            return enrichHtmlTemplate(incomeDto, htmlTemplate);

        } catch (IOException | TemplateException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private String enrichHtmlTemplate(Object invoiceDto, String htmlTemplate) throws IOException, TemplateException {
        Map<String, Object> content = setContent(invoiceDto);

        StringWriter writer = new StringWriter();

        Template template = CONFIG.getTemplate(htmlTemplate);
        template.process(content, writer);

        return writer.toString();
    }

    private static void setTemplateLoader() {
        CONFIG.setClassForTemplateLoading(DefaultHtmlAggregator.class, TEMPLATES_DIR);
    }

    private static void setEncoding() {
        CONFIG.setDefaultEncoding(ENCODING_UTF_8);
    }

    private Map<String, Object> setContent(Object invoiceDto) {
        return Map.of(invoiceDto.getClass().getSimpleName(), invoiceDto);
    }

    private void requiredTemplateNameNotNullNotEmpty(String htmlTemplateName) {
        if (Objects.isNull(htmlTemplateName) || htmlTemplateName.isEmpty()) {
            throw new RuntimeException("The Html template name is null or empty");
        }
    }

    private void requiredContentNotNull(Object content) {
        if (Objects.isNull(content)) {
            throw new RuntimeException("The Content for Html template is null");
        }
    }
}
