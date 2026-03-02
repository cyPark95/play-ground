package pcy.study.flow.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpCookie;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.server.ServerWebExchange;
import pcy.study.flow.exception.ApplicationException;
import pcy.study.flow.service.UserQueueService;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class WaitingRoomController {

    private final UserQueueService userQueueService;

    @GetMapping("/waiting-room")
    public Mono<Rendering> waitingRoomPage(
            @RequestParam(name = "queue", defaultValue = "default") String queue,
            @RequestParam(name = "user_id") Long userId,
            @RequestParam(name = "redirect_url") String redirectUrl,
            ServerWebExchange exchange
    ) {
        var key = "user-queue-%s-token".formatted(queue);
        var token = Optional.ofNullable(exchange.getRequest().getCookies().getFirst(key))
                .map(HttpCookie::getValue)
                .orElse("");

        return userQueueService.isAllowed(queue, userId, token)
                .filter(allowed -> allowed)
                .map(allowed -> Rendering.redirectTo(redirectUrl).build())
                .switchIfEmpty(Mono.defer(() ->
                        userQueueService.registerWaitQueue(queue, userId)
                                .onErrorResume(ApplicationException.class, ex -> userQueueService.getRank(queue, userId))
                                .map(rank -> Rendering.view("waiting-room")
                                        .modelAttribute("number", rank)
                                        .modelAttribute("userId", userId)
                                        .modelAttribute("queue", queue)
                                        .build()
                                )
                ));
    }
}
