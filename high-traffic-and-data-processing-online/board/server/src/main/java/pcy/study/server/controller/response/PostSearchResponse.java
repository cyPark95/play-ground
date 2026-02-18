package pcy.study.server.controller.response;

import pcy.study.server.service.info.PostSearchInfo;

import java.time.LocalDateTime;
import java.util.List;

public record PostSearchResponse(
        Long id,
        String name,
        String contents,
        boolean isAdmin,
        int views,
        LocalDateTime createdAt,
        PostSearchUserResponse user,
        PostSearchCategoryResponse category,
        PostSearchFileResponse file,
        List<PostSearchPostTagResponse> postTags
) {

    public static PostSearchResponse from(PostSearchInfo info) {
        return new PostSearchResponse(
                info.getId(),
                info.getName(),
                info.getContents(),
                info.isAdmin(),
                info.getViews(),
                info.getCreatedAt(),
                info.getUserInfo() != null ? PostSearchUserResponse.from(info.getUserInfo()) : null,
                info.getCategoryInfo() != null ? PostSearchCategoryResponse.from(info.getCategoryInfo()) : null,
                info.getFileInfoInfo() != null ? PostSearchFileResponse.from(info.getFileInfoInfo()) : null,
                info.getPostTagInfos() != null ? info.getPostTagInfos().stream()
                        .map(PostSearchPostTagResponse::from)
                        .toList() : null
        );
    }
}
