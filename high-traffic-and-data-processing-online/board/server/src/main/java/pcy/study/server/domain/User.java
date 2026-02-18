package pcy.study.server.domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    private Long id;

    private String userId;

    private String password;

    private String nickname;

    private boolean isWithDraw;

    private boolean isAdmin;

    private LocalDateTime createdAt;

    public User(String userId, String password, String nickname, boolean isWithDraw, boolean isAdmin) {
        this.userId = userId;
        this.password = password;
        this.nickname = nickname;
        this.isWithDraw = isWithDraw;
        this.isAdmin = isAdmin;
        this.createdAt = LocalDateTime.now();
    }

    public void changePassword(String encryptedAfterPassword) {
        this.password = encryptedAfterPassword;
    }
}
