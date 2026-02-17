package pcy.study.server.service.command;

import pcy.study.server.domain.File;

public record FileSaveCommand(
        String path,
        String name,
        String extension
) {

    public File toDomain() {
        return new File(
                path,
                name,
                extension
        );
    }
}
