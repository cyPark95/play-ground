package pcy.study.server.domain;

import lombok.*;
import pcy.study.server.service.command.PostUpdateCommand;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    private Long id;

    private String name;

    private String contents;

    private boolean isAdmin;

    private int views;

    private Long userId;

    private Long categoryId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private File file;

    private boolean isChangeFile;

    private List<PostTag> postTags;

    private boolean isChangePostTag;

    public Post(String name, String contents, boolean isAdmin, Long userId, Long categoryId, File file, List<PostTag> postTags) {
        this.name = name;
        this.contents = contents;
        this.isAdmin = isAdmin;
        this.userId = userId;
        this.categoryId = categoryId;
        this.file = file;
        this.postTags = postTags;

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

        if (updateCommand.tagIds() != null && !updateCommand.tagIds().isEmpty()) {
            this.postTags = updateCommand.tagIds().stream()
                    .map(PostTag::new)
                    .toList();
            this.isChangePostTag = true;
        }
    }

    public boolean canBeDeletedBy(Long userId) {
        return this.userId.equals(userId);
    }

    public boolean hasFile() {
        return this.file != null;
    }

    public boolean hasPostTags() {
        return this.postTags != null && !this.postTags.isEmpty();
    }
}
