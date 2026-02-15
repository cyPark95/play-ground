package pcy.study.server.controller.request;

import pcy.study.server.service.command.CategoryUpdateCommand;

public record CategoryModifyRequest(
        String name
) {

    public CategoryUpdateCommand toCommand(Long userId, Long categoryId) {
        return new CategoryUpdateCommand(
                userId,
                categoryId,
                name
        );
    }
}
