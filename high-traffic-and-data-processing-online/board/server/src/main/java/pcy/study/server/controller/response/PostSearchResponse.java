package pcy.study.server.controller.response;

import pcy.study.server.service.info.PostSearchInfo;

import java.time.LocalDateTime;

public record PostSearchResponse(
        Long id,
        String name,
        String contents,
        boolean isAdmin,
        int views,
        Long userId,
        String userNickname,
        Long categoryId,
        String categoryName,
        Long fileId,
        String filePath,
        String fileName,
        String fileExtension,
        LocalDateTime createdAt
) {

    public static PostSearchResponse from(PostSearchInfo info) {
        return new PostSearchResponse(
                info.getId(),
                info.getName(),
                info.getContents(),
                info.isAdmin(),
                info.getViews(),
                info.getUserId(),
                info.getUserNickname(),
                info.getCategoryId(),
                info.getCategoryName(),
                info.getFileId(),
                info.getFilePath(),
                info.getFileName(),
                info.getFileExtension(),
                info.getCreatedAt()
        );
    }
}
