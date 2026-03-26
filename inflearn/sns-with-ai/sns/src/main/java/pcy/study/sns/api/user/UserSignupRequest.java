package pcy.study.sns.api.user;

public record UserSignupRequest(
        String username,
        String password
) {
}
