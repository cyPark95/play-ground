package pcy.study.server.controller.request;

import pcy.study.server.service.command.PostSaveCommand;

public record PostRegisterRequest(
        String name,
        String contents,
        boolean isAdmin,
        Long categoryId,
        FileRegisterRequest fileRegisterRequest
) {

    public PostSaveCommand toCommand(Long userId) {
        return new PostSaveCommand(
                name,
                contents,
                isAdmin,
                userId,
                categoryId,
                fileRegisterRequest.toCommand()
        );
    }
}
