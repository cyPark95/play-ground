package pcy.study.server.controller.request;

import pcy.study.server.service.command.PostSaveCommand;

import java.util.List;

public record PostRegisterRequest(
        String name,
        String contents,
        boolean isAdmin,
        Long categoryId,
        FileRegisterRequest fileRegisterRequest,
        List<Long> tagIds
) {

    public PostSaveCommand toCommand(Long userId) {
        return new PostSaveCommand(
                name,
                contents,
                isAdmin,
                userId,
                categoryId,
                fileRegisterRequest != null ? fileRegisterRequest.toCommand() : null,
                tagIds
        );
    }
}
