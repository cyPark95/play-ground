package pcy.study.server.domain;

import lombok.Getter;
import pcy.study.server.service.command.PostUpdateCommand;

import java.time.LocalDateTime;

@Getter
public class Post {

    private Long id;

    private String name;

    private String contents;

    private boolean isAdmin;

    private int views;

    private Long userId;

    private Long categoryId;

    private Long fileId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Post(Long id, String name, String contents, boolean isAdmin, int views, Long userId, Long categoryId, Long fileId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.contents = contents;
        this.isAdmin = isAdmin;
        this.views = views;
        this.userId = userId;
        this.categoryId = categoryId;
        this.fileId = fileId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Post(String name, String contents, boolean isAdmin, Long userId, Long categoryId, Long fileId) {
        this.name = name;
        this.contents = contents;
        this.isAdmin = isAdmin;
        this.userId = userId;
        this.categoryId = categoryId;
        this.fileId = fileId;

        this.views = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void change(PostUpdateCommand updateCommand) {
        this.name = updateCommand.name();
        this.contents = updateCommand.contents();
        this.isAdmin = updateCommand.isAdmin();
        this.views = updateCommand.views();
        this.userId = updateCommand.userId();
        this.fileId = updateCommand.fileId();

        this.updatedAt = LocalDateTime.now();
    }

    public boolean canBeDeletedBy(Long userId) {
        return this.userId.equals(userId);
    }
}
