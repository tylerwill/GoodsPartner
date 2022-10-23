package com.goodspartner.action;

import com.goodspartner.entity.Route;
import com.goodspartner.entity.RouteStatus;
import com.goodspartner.exception.InvalidActionType;
import com.goodspartner.exception.RouteInWrongState;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;

public enum RouteAction {

    START {
        @Override
        public void perform(Route route) {

            if (route.getStatus() != RouteStatus.APPROVED) {
                throw new RouteInWrongState(route);
            }

            route.setStatus(RouteStatus.INPROGRESS);
            route.setStartTime(LocalDateTime.now());
        }
    },

    COMPLETE {
        @Override
        public void perform(Route route) {

            if (route.getStatus() != RouteStatus.INPROGRESS) {
                throw new RouteInWrongState(route);
            }

            route.setStatus(RouteStatus.COMPLETED);
            route.setFinishTime(LocalDateTime.now());

            Duration duration = Duration.between(route.getStartTime(), route.getFinishTime());
            route.setSpentTime(duration.toMinutes());
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
