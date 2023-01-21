package com.goodspartner.service.impl;

import com.goodspartner.dto.UserDto;
import com.goodspartner.entity.User;
import com.goodspartner.event.LiveEvent;
import com.goodspartner.exception.SubscriberNotFoundException;
import com.goodspartner.mapper.UserMapper;
import com.goodspartner.service.LiveEventService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
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

    private static final int TIME_TO_LIVE = 60000;
    private static final String DRIVER_ROLE = "DRIVER";

    private final Map<UserDto, Subscriber> listeners = new ConcurrentHashMap<>();
    private final UserMapper userMapper;

    @Override
    public void subscribe(OAuth2AuthenticationToken authenticationToken, Consumer<LiveEvent> consumer) {
        if (authenticationToken != null) {
            UserDto user = userMapper.mapAuthentication(authenticationToken);
            listeners.put(user, new Subscriber(consumer));
            log.info("New subscriber added: {}, total count: {}", user.getEmail(), listeners.size());
        } else {
            log.warn("Unable to subscribe consumer without authentication");
        }
    }

    @Override
    public void publishHeartBeat(OAuth2AuthenticationToken authentication, LiveEvent event) {
        Subscriber subscriber = Optional.ofNullable(listeners.get(userMapper.mapAuthentication(authentication)))
                .orElseThrow(SubscriberNotFoundException::new);
        subscriber.setLastAccessed(System.currentTimeMillis());
        subscriber.getConsumer().accept(event);
        log.debug("Received heartbeat for subscriber: {}", authentication.getPrincipal().getName());
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
        @Setter
        private long lastAccessed;
        private final Consumer<LiveEvent> consumer;

        public Subscriber(Consumer<LiveEvent> consumer) {
            this.lastAccessed = System.currentTimeMillis();
            this.consumer = consumer;
        }
    }
}