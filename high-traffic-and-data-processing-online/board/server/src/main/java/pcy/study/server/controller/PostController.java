package pcy.study.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pcy.study.server.aop.LoginCheck;
import pcy.study.server.controller.request.*;
import pcy.study.server.controller.response.PostResponse;
import pcy.study.server.service.PostService;
import pcy.study.server.service.command.*;
import pcy.study.server.service.info.PostInfo;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @LoginCheck
    public void register(Long userId, @RequestBody PostRegisterRequest registerRequest) {
        PostSaveCommand saveCommand = registerRequest.toCommand(userId);
        postService.save(saveCommand);
    }

    @GetMapping("/my")
    @LoginCheck
    public List<PostResponse> getMyPosts(Long userId) {
        List<PostInfo> posts = postService.getMyPosts(userId);
        return posts.stream()
                .map(PostResponse::from)
                .toList();
    }

    @PatchMapping("/{postId}")
    @LoginCheck
    public void modify(
            Long userId,
            @PathVariable Long postId,
            @RequestBody PostModifyRequest modifyRequest
    ) {
        PostUpdateCommand updateCommand = modifyRequest.toCommand(postId, userId);
        postService.update(updateCommand);
    }

    @DeleteMapping("/{postId}")
    @LoginCheck
    public void remove(Long userId, @PathVariable Long postId) {
        postService.delete(userId, postId);
    }

    @PostMapping("/{postId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    @LoginCheck
    public void commentRegister(
            Long userId,
            @PathVariable Long postId,
            @RequestBody CommentRegisterRequest registerRequest
    ) {
        CommentSaveCommand saveCommand = registerRequest.toCommand(userId, postId);
        postService.saveComment(saveCommand);
    }

    @PatchMapping("/comments/{commentId}")
    @LoginCheck
    public void commentModify(
            Long userId,
            @PathVariable Long commentId,
            @RequestBody CommentModifyRequest modifyRequest
    ) {
        CommentUpdateCommand command = modifyRequest.toCommand(userId, commentId);
        postService.updateComment(command);
    }

    @DeleteMapping("/comments/{commentId}")
    @LoginCheck
    public void commentRemove(Long userId, @PathVariable Long commentId) {
        postService.deleteComment(userId, commentId);
    }

    @PostMapping("/tags")
    @ResponseStatus(HttpStatus.CREATED)
    @LoginCheck
    public void tagRegister(Long userId, @RequestBody TagRegisterRequest registerRequest) {
        TagSaveCommand saveCommand = registerRequest.toCommand(userId);
        postService.saveTag(saveCommand);
    }

    @PatchMapping("/tags/{tagId}")
    @LoginCheck
    public void tagModify(
            Long userId,
            @PathVariable Long tagId,
            @RequestBody TagModifyRequest modifyRequest
    ) {
        TagUpdateCommand command = modifyRequest.toCommand(userId, tagId);
        postService.updateTag(command);
    }

    @DeleteMapping("/tags/{tagId}")
    @LoginCheck
    public void tagRemove(Long userId, @PathVariable Long tagId) {
        postService.deleteTag(userId, tagId);
    }
}
