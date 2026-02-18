package pcy.study.server.controller.response;

import pcy.study.server.service.info.PostSearchFileInfo;

public record PostSearchFileResponse(
        Long id,
        String path,
        String name,
        String extension
) {

    public static PostSearchFileResponse from(PostSearchFileInfo info) {
        return new PostSearchFileResponse(
                info.getId(),
                info.getPath(),
                info.getName(),
                info.getExtension()
        );
    }
}
