package pcy.study.server.service.info;

import pcy.study.server.domain.Category;
import pcy.study.server.domain.File;
import pcy.study.server.domain.Post;
import pcy.study.server.domain.User;

import java.time.LocalDateTime;

public record PostInfo(
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

    public static PostInfo from(Post post, User user, Category category, File file) {
        return new PostInfo(
                post.getId(),
                post.getName(),
                post.getContents(),
                post.isAdmin(),
                post.getViews(),
                user.getId(),
                user.getNickname(),
                category.getId(),
                category.getName(),
                file.getId(),
                file.getPath(),
                file.getName(),
                file.getExtension(),
                post.getCreatedAt()
        );
    }
}
