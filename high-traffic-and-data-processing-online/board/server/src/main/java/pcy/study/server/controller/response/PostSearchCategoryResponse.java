package pcy.study.server.controller.response;

import pcy.study.server.service.info.PostSearchCategoryInfo;

public record PostSearchCategoryResponse(
        Long id,
        String name
) {

    public static PostSearchCategoryResponse from(PostSearchCategoryInfo info) {
        return new PostSearchCategoryResponse(
                info.getId(),
                info.getName()
        );
    }
}
