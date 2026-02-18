package pcy.study.server.controller.response;

import pcy.study.server.service.info.PostSearchPostTagInfo;

public record PostSearchPostTagResponse(
        Long id,
        Long tagId,
        String name,
        String url
) {

    public static PostSearchPostTagResponse from(PostSearchPostTagInfo info) {
        return new PostSearchPostTagResponse(
                info.getId(),
                info.getTagId(),
                info.getName(),
                info.getUrl()
        );
    }
}
