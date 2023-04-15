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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.goodspartner.entity.User.UserRole.DRIVER;

@Slf4j
@Service
@AllArgsConstructor
public class DefaultLiveEventService implements LiveEventService {

    private static final LiveEvent HEAR_BEAT_EVENT =
            LiveEvent.builder()
                    .message("Keep alive")
                    .type(EventType.HEARTBEAT)
                    .build();

    private static final int TIME_TO_LIVE = 60000 * 10; // 10 min keep active sessions before removal from system

    private final Map<UserDto, List<Subscriber>> listeners = new ConcurrentHashMap<>();
    private final UserService userService;

    @Override
    public void subscribe(Consumer<LiveEvent> consumer, UUID heartbeatId) {
        UserDto user = userService.getUserDto(heartbeatId);
        Subscriber newSubscriber = new Subscriber(consumer, heartbeatId);
        listeners.computeIfAbsent(user, k -> new ArrayList<>()).add(newSubscriber);
        log.info("{} added for account: {}, total account subscribers: {}. Total accounts: {}",
                newSubscriber, user.getUserName(), listeners.get(user).size(), listeners.size());
    }

    @Override
    public void publishHeartBeat(UUID heartbeatId) {
        UserDto authenticatedUser = userService.getUserDto(heartbeatId);
        List<Subscriber> subscribers = Optional.ofNullable(listeners.get(authenticatedUser))
                .orElseThrow(() -> new SubscriberNotFoundException(authenticatedUser.getUserName()));
        subscribers.stream()
                .filter(subscriber -> subscriber.getHeartbeatId().equals(heartbeatId))
                .forEach(subscriber -> {
                    subscriber.setLastAccessed(System.currentTimeMillis());
                    subscriber.getConsumer().accept(HEAR_BEAT_EVENT);
                    log.debug("Received heartbeat for {} subscriber: {}", authenticatedUser.getUserName(), subscriber);
                });
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
                    // Remove specific subscribers
                    List<Subscriber> subscribers = entry.getValue();
                    subscribers.removeIf(subscriber -> {
                        long staleTime = System.currentTimeMillis() - subscriber.getLastAccessed();
                        boolean expired = staleTime > TIME_TO_LIVE;
                        log.debug("{} staleTime: {}, expired: {}", subscriber, staleTime, expired);
                        if (expired) {
                            log.info("Subscriber has been inactive for more than {} milliseconds. Removing from group for: {}",
                                    TIME_TO_LIVE, entry.getKey().getUserName());
                            userService.removeUserDto(subscriber.heartbeatId);
                        }
                        return expired;
                    });
                    // Remove full group
                    boolean groupEmpty = subscribers.isEmpty();
                    if (groupEmpty) {
                        log.info("Removing empty subscribers group for: {}", entry.getKey().getUserName());
                    }
                    return groupEmpty;
                });
    }

    private void publishEvent(LiveEvent event, Predicate<UserDto> predicate) {
        listeners.entrySet()
                .stream()
                .filter(entry -> predicate.test(entry.getKey()))
                .forEach(entry -> entry.getValue().forEach(subscriber -> subscriber.getConsumer().accept(event)));
    }

    @NotNull
    private Predicate<UserDto> isAdminOrLogisticianOrResponsibleDriver(User driver) {
        return user -> !user.getRole().equals(DRIVER.name()) || driver.getUserName().equals(user.getUserName());
    }

    @NotNull
    private Predicate<UserDto> isAdminOrLogistician() {
        return user -> !user.getRole().equals(DRIVER.name());
    }

    @Getter
    public static class Subscriber {
        private final Consumer<LiveEvent> consumer;
        private final UUID heartbeatId;

        @Setter
        private long lastAccessed;

        public Subscriber(Consumer<LiveEvent> consumer, UUID heartbeatId) {
            this.lastAccessed = System.currentTimeMillis();
            this.consumer = consumer;
            this.heartbeatId = heartbeatId;
        }

        @Override
        public String toString() {
            return "Subscriber{" +
                    "id=" + heartbeatId +
                    ", lastAccessed=" + lastAccessed +
                    '}';
        }
    }
}