package pcy.study.sns.api.auth;

import java.util.List;

public record AuthSessionsResponse(
        int totalSessions,
        List<AuthSessionInfo> sessions
) {
}
