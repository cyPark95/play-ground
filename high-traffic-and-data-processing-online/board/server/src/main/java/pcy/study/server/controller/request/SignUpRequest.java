package pcy.study.server.controller.request;

import jakarta.validation.constraints.NotBlank;
import pcy.study.server.service.command.UserSaveCommand;

public record SignUpRequest(
        @NotBlank(message = "아이디를 입력해주세요.")
        String userId,
        @NotBlank(message = "비밀번호를 입력해주세요.")
        String password,
        @NotBlank(message = "닉네임을 입력해주세요.")
        String nickname,
        boolean isWithDraw,
        boolean isAdmin
) {

    public UserSaveCommand toCommand() {
        return new UserSaveCommand(
                userId,
                password,
                nickname,
                isWithDraw,
                isAdmin
        );
    }
}
