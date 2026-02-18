package pcy.study.server.controller.request;

import pcy.study.server.service.command.TagUpdateCommand;

public record TagModifyRequest(
        String name,
        String url
) {

    public TagUpdateCommand toCommand(Long userId, Long tagId) {
        return new TagUpdateCommand(
                userId,
                tagId,
                name,
                url
        );
    }
}
