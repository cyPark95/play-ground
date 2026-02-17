package pcy.study.server.controller.request;

import pcy.study.server.service.command.FileSaveCommand;

public record FileRegisterRequest(
    String path,
    String name,
    String extension
) {

    public FileSaveCommand toCommand() {
        return new FileSaveCommand(
                path,
                name,
                extension
        );
    }
}
