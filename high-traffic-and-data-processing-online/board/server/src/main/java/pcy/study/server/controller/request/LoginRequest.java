package pcy.study.server.controller.request;

public record LoginRequest(
        String userId,
        String password
) {
}
