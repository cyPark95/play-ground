package pcy.study.sns.config.auth;

public record AuthLoginResponse(
        String sessionId,
        String username
) {
}
