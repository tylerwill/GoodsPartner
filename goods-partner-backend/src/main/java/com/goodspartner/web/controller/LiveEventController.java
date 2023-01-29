package com.goodspartner.web.controller;

import com.goodspartner.service.LiveEventService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class LiveEventController {

    private final LiveEventService liveEventService;

    @GetMapping(path = "/keep-alive")
    public void keepAlive() {
        liveEventService.publishHeartBeat();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER', 'LOGISTICIAN')")
    @ApiOperation(
            value = "Live server-sent events",
            notes = "Stream of events"
    )
    @GetMapping(path = "/live-event", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<Object>> consumer() {
        return Flux.create(sink -> liveEventService.subscribe(sink::next))
                .map(liveEvent -> ServerSentEvent.builder()
                        .data(liveEvent)
                        .build()
                );
    }
}
