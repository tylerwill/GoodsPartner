package com.goodspartner;

import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO test preparation in progress
public class TimeExtractionTest  {

    private static final String TIME_REGEX = "ДО.([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]";
    private static final String TIME_REGEX_2 = "до \\d{1,2}:\\d{2}";
    private static final Pattern PATTERN = Pattern.compile(TIME_REGEX_2, Pattern.CASE_INSENSITIVE);

    @Test
    public void testBeforeTimeUpperCase() {

        String input = "бн, ДО 16:00, + декларація безпеки вантажу, + якісні документи";

        Matcher m = PATTERN.matcher(input);

        while (m.find()) {
            System.out.println("Found value: " + m.group(0) );
        }
    }

    @Test
    public void testBeforeTimeLowerCase() {

        String input = "бн, до 16:00, + декларація безпеки вантажу, + якісні документи";

        Matcher m = PATTERN.matcher(input);

        while (m.find()) {
            System.out.println("Found value: " + m.group(0) );
        }
    }
}
