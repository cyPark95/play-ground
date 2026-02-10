package pcy.study.server.controller.request;

import pcy.study.server.service.command.UserUpdatePasswordCommand;

public record UserChangePasswordRequest(
        String beforePassword,
        String afterPassword
) {

    public UserUpdatePasswordCommand toCommand(Long id) {
        return new UserUpdatePasswordCommand(
                id,
                beforePassword,
                afterPassword
        );
    }
}
