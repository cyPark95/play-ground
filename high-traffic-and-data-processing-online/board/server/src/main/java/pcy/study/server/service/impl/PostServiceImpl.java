package pcy.study.server.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pcy.study.server.domain.Category;
import pcy.study.server.domain.Post;
import pcy.study.server.domain.User;
import pcy.study.server.exception.AccessDeniedException;
import pcy.study.server.mapper.CategoryMapper;
import pcy.study.server.mapper.PostMapper;
import pcy.study.server.mapper.UserMapper;
import pcy.study.server.service.PostService;
import pcy.study.server.service.command.PostSaveCommand;
import pcy.study.server.service.command.PostUpdateCommand;
import pcy.study.server.service.info.PostInfo;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostMapper postMapper;
    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;

    @Override
    public void save(PostSaveCommand saveCommand) {
        if(saveCommand.userId() == null) {
            throw new AccessDeniedException("게시글 등록 사용자 로그인 실패했습니다.");
        }

        Post post = saveCommand.toDomain();
        postMapper.insertPost(post);
    }

    @Override
    public List<PostInfo> getMyPosts(Long userId) {
        User user = userMapper.findById(userId);
        List<Post> posts = postMapper.findByUserId(userId);
        return posts.stream()
                .map(post -> {
                    Category category = categoryMapper.findById(post.getCategoryId());
                    return PostInfo.from(post, user, category);
                })
                .toList();
    }

    @Override
    public void updatePost(PostUpdateCommand updateCommand) {
        if(updateCommand.userId() == null) {
            throw new AccessDeniedException("게시글 수정 사용자 로그인 실패했습니다.");
        }

        Post post = postMapper.findById(updateCommand.id());
        if(post == null) {
            throw new IllegalArgumentException("게시글 수정에 실패했습니다.\n Params: %s".formatted(updateCommand));
        }

        post.change(updateCommand);
        postMapper.updatePost(post);
    }

    @Override
    public void delete(Long userId, Long postId) {
        Post post = postMapper.findById(postId);
        if(!post.canBeDeletedBy(userId)) {
            throw new AccessDeniedException("게시글 삭제에 실패했습니다.\n Params: %d".formatted(userId));
        }

        postMapper.deletePost(postId);
    }
}
