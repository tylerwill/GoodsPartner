package com.goodspartner.util;

import com.goodspartner.dto.OrderDto;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class DeliveryTimeRangeParser {

    // Regexes
    private static final String TIME_RANGE_COMPOUND_REGEX =
            "(?<!обід.{1,5})(?<=( с | з | ))\\d{1,2}:\\d{2}(-| - | до | по )\\d{1,2}:\\d{2}(?!\\sобід)";
    private static final String TIME_IN_RANGE_REGEX = "\\d{1,2}(?=:\\d{2})";
    private static final String TIME_AFTER_REGEX = "(?<=( с | з | після | не раніше ))(\\d{1,2}(?=:\\d{2})|\\d{1,2})";
    private static final String TIME_BEFORE_REGEX = "(?<=( до | по ))(\\d{1,2}(?=:\\d{2})|\\d{1,2})";

    // Patterns
    private static final Pattern AFTER_TIME_PATTERN = Pattern.compile(TIME_AFTER_REGEX);
    private static final Pattern BEFORE_TIME_PATTERN = Pattern.compile(TIME_BEFORE_REGEX);
    private static final Pattern TIME_RANGE_PATTERN = Pattern.compile(TIME_RANGE_COMPOUND_REGEX);
    private static final Pattern TIME_IN_RANGE_PATTERN = Pattern.compile(TIME_IN_RANGE_REGEX);

    // --- Delivery Time Parsers ---

    public static void parseDeliveryTimeFromComment(OrderDto orderDto) {
        if (Objects.isNull(orderDto.getComment())) {
            return;
        }

        String input = orderDto.getComment()
                .toLowerCase() // ignore case
                .replaceAll("\\s+"," "); // Collapse multiple spaces

        Matcher rangeMatcher = TIME_RANGE_PATTERN.matcher(input);
        if (rangeMatcher.find()) {
            String range = rangeMatcher.group();
            Matcher timeInRangeMatcher = TIME_IN_RANGE_PATTERN.matcher(range);
            List<String> hours = timeInRangeMatcher.results().map(MatchResult::group).toList();
            updateOrderDeliveryTime(orderDto, hours);
            log.debug("Range: [{} <> {}] - {}", orderDto.getDeliveryStart(), orderDto.getDeliveryFinish(), orderDto.getComment());

        } else if (input.contains("обід") || input.contains("обед")) {
            log.debug("Detected delivery range time with lunch time only: {}", orderDto.getComment());

        } else {
            boolean matchedAfterTime = false;
            boolean matchedBeforeTime = false;

            String afterTime = null;
            Matcher afterTimeMatcher = AFTER_TIME_PATTERN.matcher(input);
            if (afterTimeMatcher.find()) {
                afterTime = afterTimeMatcher.group();
                matchedAfterTime = true;
            }

            String beforeTime = null;
            Matcher beforeTimeMatcher = BEFORE_TIME_PATTERN.matcher(input);
            if (beforeTimeMatcher.find()) {
                beforeTime = beforeTimeMatcher.group();
                matchedBeforeTime = true;
            }

            if (matchedAfterTime || matchedBeforeTime) {
                updateOrderDeliveryTime(orderDto, afterTime, beforeTime);
                log.debug("Bound: [{} <> {}] - {}", orderDto.getDeliveryStart(), orderDto.getDeliveryFinish(), orderDto.getComment());
            } else {
                log.trace("Order comment has no delivery range: {}", orderDto.getComment());
            }
        }
    }

    private static void updateOrderDeliveryTime(OrderDto orderDto, List<String> hours) {
        if (hours.size() == 2) {
            String afterTime = hours.get(0);
            String beforeTime = hours.get(1);
            updateOrderDeliveryTime(orderDto, afterTime, beforeTime);
        } else {
            log.warn("Invalid range match time: {}", orderDto.getComment());
        }
    }

    private static void updateOrderDeliveryTime(OrderDto orderDto, String afterTime, String beforeTime) {
        try {
            // TODO: Retrieve default times from properties
            LocalTime defaultStartTime = LocalTime.of(9, 0);
            LocalTime defaultFinishTime = LocalTime.of(18, 0);

            LocalTime deliveryStartTime = afterTime == null ? defaultStartTime : LocalTime.of(Integer.parseInt(afterTime), 0);
            LocalTime deliveryFinishTime = beforeTime == null ? defaultFinishTime : LocalTime.of(Integer.parseInt(beforeTime), 0);

            if (defaultStartTime.isAfter(deliveryStartTime)
                    || defaultFinishTime.isBefore(deliveryStartTime)) {
                log.warn("Invalid start time parsed: {} for order comment: {}", deliveryStartTime, orderDto.getComment());
                deliveryStartTime = defaultStartTime;
            }

            if (defaultStartTime.isAfter(deliveryFinishTime)
                    || defaultFinishTime.isBefore(deliveryFinishTime)) {
                log.warn("Invalid finish time parsed: {} for order comment: {}", deliveryFinishTime, orderDto.getComment());
                deliveryFinishTime = defaultFinishTime;
            }

            if (deliveryStartTime.isAfter(deliveryFinishTime)) {
                log.warn("AfterTime: {} is greater then BeforeTime: {} for order comment: {}",
                        deliveryStartTime, deliveryFinishTime, orderDto.getComment());
                deliveryFinishTime = defaultFinishTime;
            }

            orderDto.setDeliveryStart(deliveryStartTime);
            orderDto.setDeliveryFinish(deliveryFinishTime);
        } catch (Exception e) {
            log.warn("Comment: {} parsing result with exception: {}", orderDto.getComment(), e.getMessage());
        }
    }
}
