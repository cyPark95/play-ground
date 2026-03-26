package pcy.study.sns.api;

import pcy.study.sns.api.auth.AuthSessionInfo;
import pcy.study.sns.api.auth.AuthSessionsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final SessionRegistry sessionRegistry;

    @GetMapping("/api/v1/sessions")
    public ResponseEntity<AuthSessionsResponse> getAllSessions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.ok(new AuthSessionsResponse(0, List.of()));
        }

        List<AuthSessionInfo> sessions = sessionRegistry.getAllSessions(authentication.getPrincipal(), false).stream()
                .map(sessionInfo -> AuthSessionInfo.from(
                        sessionInfo.getSessionId(),
                        sessionInfo.getPrincipal(),
                        sessionInfo.getLastRequest(),
                        sessionInfo.isExpired()
                ))
                .toList();

        return ResponseEntity.ok(new AuthSessionsResponse(sessions.size(), sessions));
    }
}
