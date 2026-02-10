package pcy.study.server.controller.request;

import pcy.study.server.service.command.UserUpdatePasswordCommand;

public record UserChangePasswordRequest(
        String userId,
        String beforePassword,
        String afterPassword
) {

    public UserUpdatePasswordCommand toCommand() {
        return new UserUpdatePasswordCommand(
                userId,
                beforePassword,
                afterPassword
        );
    }
}
