package pcy.study.server.controller.request;

import jakarta.validation.constraints.NotBlank;

public record UserRemoveRequest(
        @NotBlank(message = "비밀번호를 입력해주세요.")
        String password
) {
}
