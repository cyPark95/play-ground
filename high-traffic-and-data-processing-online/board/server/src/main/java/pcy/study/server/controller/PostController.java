package pcy.study.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pcy.study.server.aop.LoginCheck;
import pcy.study.server.controller.request.PostModifyRequest;
import pcy.study.server.controller.request.PostRegisterRequest;
import pcy.study.server.controller.response.PostResponse;
import pcy.study.server.service.PostService;
import pcy.study.server.service.command.PostSaveCommand;
import pcy.study.server.service.command.PostUpdateCommand;
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
        postService.updatePost(updateCommand);
    }

    @DeleteMapping("/{postId}")
    @LoginCheck
    public void remove(Long userId, @PathVariable Long postId) {
        postService.delete(userId, postId);
    }
}
