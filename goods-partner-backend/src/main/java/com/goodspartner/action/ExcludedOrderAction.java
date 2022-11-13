package com.goodspartner.action;

import com.goodspartner.dto.RescheduleOrdersDto;
import com.goodspartner.entity.OrderExternal;
import com.goodspartner.exception.InvalidActionType;

import java.time.LocalDate;
import java.util.Arrays;

public enum ExcludedOrderAction {

    RESCHEDULE {
        @Override
        public void perform(OrderExternal order,
                            RescheduleOrdersDto rescheduleOrdersDto) {
            order.setRescheduleDate(rescheduleOrdersDto.getRescheduleDate());
        }
    },

    REMOVE {
        @Override
        public void perform(OrderExternal order,
                            RescheduleOrdersDto rescheduleOrdersDto) {
            order.setRescheduleDate(LocalDate.EPOCH); // Set 1970 1 1
        }
    };

    public static ExcludedOrderAction of(String action) {
        return Arrays.stream(values())
                .filter(value -> value.name().equalsIgnoreCase(action))
                .findFirst()
                .orElseThrow(() -> new InvalidActionType(action));
    }

    public abstract void perform(OrderExternal order, RescheduleOrdersDto rescheduleOrdersDto);
}
