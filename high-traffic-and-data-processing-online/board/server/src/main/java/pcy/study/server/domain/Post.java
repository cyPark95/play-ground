package pcy.study.server.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pcy.study.server.service.command.PostUpdateCommand;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    private Long id;

    private String name;

    private String contents;

    private boolean isAdmin;

    private int views;

    private File file;

    private Long userId;

    private Long categoryId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private boolean isChangeFile;

    public Post(String name, String contents, boolean isAdmin, Long userId, Long categoryId, File file) {
        this.name = name;
        this.contents = contents;
        this.isAdmin = isAdmin;
        this.userId = userId;
        this.categoryId = categoryId;
        this.file = file;

        this.views = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void change(PostUpdateCommand updateCommand) {
        this.name = updateCommand.name();
        this.contents = updateCommand.contents();
        this.isAdmin = updateCommand.isAdmin();
        this.updatedAt = LocalDateTime.now();

        if (updateCommand.fileSaveCommand() != null) {
            this.file = updateCommand.fileSaveCommand().toDomain();
            this.isChangeFile = true;
        }
    }

    public boolean canBeDeletedBy(Long userId) {
        return this.userId.equals(userId);
    }

    public boolean hasFile() {
        return this.file != null;
    }
}
