package pcy.study.server.service.command;

import pcy.study.server.domain.Tag;

public record TagSaveCommand(
        Long userId,
        String name,
        String url
) {

    public Tag toDomain() {
        return new Tag(
                name,
                url
        );
    }
}
