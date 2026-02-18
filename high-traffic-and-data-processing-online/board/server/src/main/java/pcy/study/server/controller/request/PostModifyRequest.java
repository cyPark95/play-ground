package pcy.study.server.controller.request;

import pcy.study.server.service.command.PostUpdateCommand;

import java.util.List;

public record PostModifyRequest(
        String name,
        String contents,
        boolean isAdmin,
        FileRegisterRequest fileRegisterRequest,
        List<Long> tagIds
) {

    public PostUpdateCommand toCommand(Long postId, Long userId) {
        return new PostUpdateCommand(
                postId,
                name,
                contents,
                isAdmin,
                userId,
                fileRegisterRequest != null ? fileRegisterRequest.toCommand() : null,
                tagIds
        );
    }
}
