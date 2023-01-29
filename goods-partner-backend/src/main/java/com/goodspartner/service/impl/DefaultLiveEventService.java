package com.goodspartner.service.impl;

import com.goodspartner.dto.UserDto;
import com.goodspartner.entity.User;
import com.goodspartner.event.EventType;
import com.goodspartner.event.LiveEvent;
import com.goodspartner.exception.SubscriberNotFoundException;
import com.goodspartner.service.LiveEventService;
import com.goodspartner.service.UserService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Slf4j
@Service
@AllArgsConstructor
public class DefaultLiveEventService implements LiveEventService {

    private static final LiveEvent HEAR_BEAT_EVENT = new LiveEvent("Keep alive", EventType.HEARTBEAT);

    private static final int TIME_TO_LIVE = 60000;
    private static final String DRIVER_ROLE = "DRIVER";

    private final Map<UserDto, Subscriber> listeners = new ConcurrentHashMap<>();
    private final UserService userService;

    @Override
    public void subscribe(Consumer<LiveEvent> consumer) {
        UserDto user = userService.getAuthenticatedUserDto();
        listeners.put(user, new Subscriber(consumer));
        log.info("New subscriber added: {}, total count: {}", user.getEmail(), listeners.size());
    }

    @Override
    public void publishHeartBeat() {
        UserDto authenticatedUser = userService.getAuthenticatedUserDto();
        Subscriber subscriber = Optional.ofNullable(listeners.get(authenticatedUser))
                .orElseThrow(() -> new SubscriberNotFoundException(authenticatedUser.getUserName()));
        subscriber.setLastAccessed(System.currentTimeMillis());
        subscriber.getConsumer().accept(HEAR_BEAT_EVENT);
        log.debug("Received heartbeat for subscriber: {}", authenticatedUser.getUserName());
    }

    @Override
    public void publishToDriver(LiveEvent event, User driver) {
        publishEvent(event, isAdminOrLogisticianOrResponsibleDriver(driver));
    }

    @Override
    public void publishToAdminAndLogistician(LiveEvent event) {
        publishEvent(event, isAdminOrLogistician());
    }

    @Scheduled(fixedRate = TIME_TO_LIVE)
    public void clearSubscribers() {
        log.debug("Clearing stale scan subscribers");

        listeners.entrySet()
                .removeIf(entry -> {
                    boolean expired = System.currentTimeMillis() - entry.getValue().getLastAccessed() > TIME_TO_LIVE;
                    if (expired) {
                        log.info("The subscriber: {} has been inactive for more than 60 seconds and will be removed" +
                                " from the list of subscriptions", entry.getKey().getEmail());
                    }
                    return expired;
                });
    }

    private void publishEvent(LiveEvent event, Predicate<UserDto> predicate) {
        listeners.entrySet()
                .stream()
                .filter(entry -> predicate.test(entry.getKey()))
                .forEach(entry -> entry.getValue().getConsumer().accept(event));
    }

    @NotNull
    private Predicate<UserDto> isAdminOrLogisticianOrResponsibleDriver(User driver) {
        return user -> !user.getRole().equals(DRIVER_ROLE) || driver.getEmail().equals(user.getEmail());
    }

    @NotNull
    private Predicate<UserDto> isAdminOrLogistician() {
        return user -> !user.getRole().equals(DRIVER_ROLE);
    }

    @Getter
    public static class Subscriber {
        private final Consumer<LiveEvent> consumer;
        @Setter
        private long lastAccessed;

        public Subscriber(Consumer<LiveEvent> consumer) {
            this.lastAccessed = System.currentTimeMillis();
            this.consumer = consumer;
        }
    }
}