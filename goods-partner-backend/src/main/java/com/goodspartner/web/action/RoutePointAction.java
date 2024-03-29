package com.goodspartner.web.action;

import com.goodspartner.entity.RoutePoint;
import com.goodspartner.exception.InvalidActionType;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.Arrays;

import static com.goodspartner.entity.RoutePointStatus.DONE;
import static com.goodspartner.entity.RoutePointStatus.PENDING;
import static com.goodspartner.entity.RoutePointStatus.SKIPPED;

@AllArgsConstructor
public enum RoutePointAction {

    RESET {
        @Override
        public void perform(RoutePoint routePoint) {
            routePoint.setStatus(PENDING);
            routePoint.setCompletedAt(null);
        }
    },

    COMPLETE {
        @Override
        public void perform(RoutePoint routePoint) {
            routePoint.setStatus(DONE);
            routePoint.setCompletedAt(LocalDateTime.now());
        }
    },

    SKIP {
        @Override
        public void perform(RoutePoint routePoint) {
            routePoint.setStatus(SKIPPED);
            routePoint.setExpectedArrival(null);
            routePoint.setExpectedCompletion(null);
        }
    };

    public static RoutePointAction of(String action) {
        return Arrays.stream(values())
                .filter(value -> value.name().equalsIgnoreCase(action))
                .findFirst()
                .orElseThrow(() -> new InvalidActionType(action));
    }

    public abstract void perform(RoutePoint routePoint);
}