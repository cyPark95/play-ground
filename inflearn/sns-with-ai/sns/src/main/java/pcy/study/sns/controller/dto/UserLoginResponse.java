package pcy.study.sns.controller.dto;

import pcy.study.sns.domain.user.User;

public record UserLoginResponse(Long id, String username) {

    public static UserLoginResponse from(User user) {
        return new UserLoginResponse(user.getId(), user.getUsername());
    }
}
