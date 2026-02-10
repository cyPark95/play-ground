package pcy.study.server.service.command;

import pcy.study.server.domain.User;

public record UserSaveCommand(
        String userId,
        String password,
        String nickname,
        boolean isWithDraw,
        boolean isAdmin
) {

    public User toDomain(String encryptPassword) {
        return new User(
                userId,
                encryptPassword,
                nickname,
                isWithDraw,
                isAdmin
        );
    }
}
