package pcy.study.server.domain;

import lombok.*;
import pcy.study.server.service.command.CommentUpdateCommand;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {

    private Long id;

    private Long postId;

    private String contents;

    private Long subCommentId;

    public Comment(Long postId, String contents, Long subCommentId) {
        this.postId = postId;
        this.contents = contents;
        this.subCommentId = subCommentId;
    }

    public void change(CommentUpdateCommand updateCommand) {
        this.contents = updateCommand.contents();
    }
}
