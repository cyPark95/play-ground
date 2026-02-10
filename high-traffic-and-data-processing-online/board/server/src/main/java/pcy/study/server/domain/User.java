package pcy.study.server.domain;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class User {

    private Long id;

    private final String userId;

    private String password;

    private String nickname;

    private boolean isWithDraw;

    private boolean isAdmin;

    private final LocalDateTime createdAt;

    public User(Long id, String userId, String password, String nickname, boolean isWithDraw, boolean isAdmin, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.password = password;
        this.nickname = nickname;
        this.isWithDraw = isWithDraw;
        this.isAdmin = isAdmin;
        this.createdAt = createdAt;
    }

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
