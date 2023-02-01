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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
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

    private final Map<UserDto, List<Subscriber>> listeners = new ConcurrentHashMap<>();
    private final UserService userService;

    @Override
    public void subscribe(Consumer<LiveEvent> consumer) {
        UserDto user = userService.getAuthenticatedUserDto();
        Subscriber newSubscriber = new Subscriber(consumer);
        listeners.computeIfAbsent(user, k -> new ArrayList<>()).add(newSubscriber);
        log.info("{} added for account: {}, total account subscribers: {}. Total accounts: {}",
                newSubscriber, user.getEmail(), listeners.get(user).size(), listeners.size());
    }

    @Override
    public void publishHeartBeat() {
        UserDto authenticatedUser = userService.getAuthenticatedUserDto();
        List<Subscriber> subscribers = Optional.ofNullable(listeners.get(authenticatedUser))
                .orElseThrow(() -> new SubscriberNotFoundException(authenticatedUser.getUserName()));
        subscribers.forEach(subscriber -> {
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
                                    TIME_TO_LIVE, entry.getKey().getEmail());
                        }
                        return expired;
                    });
                    // Remove full group
                    boolean groupEmpty = subscribers.isEmpty();
                    if (groupEmpty) {
                        log.info("Removing empty subscribers group for: {}",  entry.getKey().getEmail());
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
        return user -> !user.getRole().equals(DRIVER_ROLE) || driver.getEmail().equals(user.getEmail());
    }

    @NotNull
    private Predicate<UserDto> isAdminOrLogistician() {
        return user -> !user.getRole().equals(DRIVER_ROLE);
    }

    @Getter
    public static class Subscriber {
        private final Consumer<LiveEvent> consumer;
        private final UUID id;

        @Setter
        private long lastAccessed;

        public Subscriber(Consumer<LiveEvent> consumer) {
            this.lastAccessed = System.currentTimeMillis();
            this.consumer = consumer;
            this.id = UUID.randomUUID();
        }

        @Override
        public String toString() {
            return "Subscriber{" +
                    "id=" + id +
                    ", lastAccessed=" + lastAccessed +
                    '}';
        }
    }
}