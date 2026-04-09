package pcy.study.sns.api;

import pcy.study.sns.api.reply.ReplyCreateRequest;
import pcy.study.sns.api.reply.ReplyResponse;
import pcy.study.sns.api.reply.ReplyUpdateRequest;
import pcy.study.sns.config.auth.AuthUser;
import pcy.study.sns.domain.post.Post;
import pcy.study.sns.domain.reply.ReplyService;
import pcy.study.sns.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService replyService;

    @PostMapping("/api/v1/posts/{postId}/replies")
    public ResponseEntity<ReplyResponse> createReply(
            @PathVariable Long postId,
            @RequestBody ReplyCreateRequest request,
            @AuthUser User user
    ) {
        Post reply = replyService.createReply(postId, request.content(), request.mediaIds(), user);
        return ResponseEntity.status(HttpStatus.CREATED).body(ReplyResponse.from(reply));
    }

    @GetMapping("/api/v1/posts/{postId}/replies")
    public ResponseEntity<List<ReplyResponse>> getReplies(@PathVariable Long postId) {
        List<ReplyResponse> replies = replyService.getRepliesByParentId(postId).stream()
                .map(ReplyResponse::from)
                .toList();
        return ResponseEntity.ok(replies);
    }

    @PutMapping("/api/v1/replies/{replyId}")
    public ResponseEntity<ReplyResponse> updateReply(
            @PathVariable Long replyId,
            @RequestBody ReplyUpdateRequest request,
            @AuthUser User user
    ) {
        Post reply = replyService.updateReply(replyId, request.content(), user);
        return ResponseEntity.ok(ReplyResponse.from(reply));
    }

    @DeleteMapping("/api/v1/replies/{replyId}")
    public ResponseEntity<Void> deleteReply(
            @PathVariable Long replyId,
            @AuthUser User user
    ) {
        replyService.deleteReply(replyId, user);
        return ResponseEntity.noContent().build();
    }
}
