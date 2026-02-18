package pcy.study.server.controller.request;

import pcy.study.server.service.command.TagSaveCommand;

public record TagRegisterRequest(
        String name,
        String url
) {

    public TagSaveCommand toCommand(Long userId) {
        return new TagSaveCommand(
                userId,
                name,
                url
        );
    }
}
