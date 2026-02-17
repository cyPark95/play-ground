package pcy.study.server.controller.request;

import pcy.study.server.service.command.PostUpdateCommand;

public record PostModifyRequest(
        String name,
        String contents,
        boolean isAdmin,
        FileRegisterRequest fileRegisterRequest
) {

    public PostUpdateCommand toCommand(Long postId, Long userId) {
        return new PostUpdateCommand(
                postId,
                name,
                contents,
                isAdmin,
                userId,
                fileRegisterRequest.toCommand()
        );
    }
}
