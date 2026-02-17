package pcy.study.server.service.info;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostSearchInfo {

    private final Long id;

    private final String name;

    private final String contents;

    private final boolean isAdmin;

    private final int views;

    private final Long userId;

    private final String userNickname;

    private final Long categoryId;

    private final String categoryName;

    private final Long fileId;

    private final LocalDateTime createdAt;

    public PostSearchInfo(Long id, String name, String contents, boolean isAdmin, int views, Long userId, String userNickname, Long categoryId, String categoryName, Long fileId, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.contents = contents;
        this.isAdmin = isAdmin;
        this.views = views;
        this.userId = userId;
        this.userNickname = userNickname;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.fileId = fileId;
        this.createdAt = createdAt;
    }
}
