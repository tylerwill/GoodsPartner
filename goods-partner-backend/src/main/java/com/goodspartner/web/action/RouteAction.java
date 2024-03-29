package com.goodspartner.web.action;

import com.goodspartner.entity.Route;
import com.goodspartner.entity.RouteStatus;
import com.goodspartner.exception.InvalidActionType;
import com.goodspartner.exception.route.IllegalRouteStatusForCompletion;
import com.goodspartner.exception.route.IllegalRouteStatusForStart;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;

import static com.goodspartner.entity.RoutePointStatus.PENDING;
import static com.goodspartner.entity.RoutePointStatus.SKIPPED;

public enum RouteAction {

    START {
        @Override
        public void perform(Route route) {

            if (route.getStatus() != RouteStatus.APPROVED) {
                throw new IllegalRouteStatusForStart();
            }

            route.setStatus(RouteStatus.INPROGRESS);
            route.setStartTime(LocalDateTime.now());
        }
    },

    COMPLETE {
        @Override
        public void perform(Route route) {

            if (route.getStatus() != RouteStatus.INPROGRESS) {
                throw new IllegalRouteStatusForCompletion();
            }

            route.setStatus(RouteStatus.COMPLETED);
            route.setFinishTime(LocalDateTime.now());

            Duration duration = Duration.between(route.getStartTime(), route.getFinishTime());
            route.setSpentTime(duration.toMinutes());

            route.getRoutePoints().stream()
                    .filter(routePoint -> PENDING.equals(routePoint.getStatus()))
                    .forEach(routePoint -> routePoint.setStatus(SKIPPED));
        }
    };

    public static RouteAction of(String action) {
        return Arrays.stream(values())
                .filter(value -> value.name().equalsIgnoreCase(action))
                .findFirst()
                .orElseThrow(() -> new InvalidActionType(action));
    }

    public abstract void perform(Route routePoint);
}
