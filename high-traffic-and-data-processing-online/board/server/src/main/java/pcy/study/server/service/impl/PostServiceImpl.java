package pcy.study.server.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pcy.study.server.domain.*;
import pcy.study.server.exception.AccessDeniedException;
import pcy.study.server.mapper.*;
import pcy.study.server.service.PostService;
import pcy.study.server.service.command.*;
import pcy.study.server.service.info.PostInfo;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostMapper postMapper;
    private final UserMapper userMapper;
    private final FileMapper fileMapper;
    private final CommentMapper commentMapper;
    private final TagMapper tagMapper;

    @Override
    public void save(PostSaveCommand saveCommand) {
        checkedLogin(saveCommand.userId());

        Post post = saveCommand.toDomain();
        postMapper.insertPost(post);

        if (post.hasFile()) {
            fileMapper.insertFile(post);
        }

        if (post.hasPostTags()) {
            tagMapper.insertPostTags(post);
        }
    }

    @Override
    public List<PostInfo> getMyPosts(Long userId) {
        checkedLogin(userId);

        List<Post> posts = postMapper.findByUserId(userId);
        return posts.stream()
                .map(PostInfo::from)
                .toList();
    }

    @Override
    public void update(PostUpdateCommand updateCommand) {
        checkedLogin(updateCommand.userId());

        Post post = getPost(updateCommand.postId());
        post.change(updateCommand);
        postMapper.updatePost(post);

        if (post.isChangeFile()) {
            fileMapper.deleteFileByPostId(post.getId());
            fileMapper.insertFile(post);
        }

        if (post.isChangePostTag()) {
            tagMapper.deletePostTagByPostId(post.getId());
            tagMapper.insertPostTags(post);
        }
    }

    @Override
    public void delete(Long userId, Long postId) {
        Post post = getPost(postId);

        if (!post.canBeDeletedBy(userId)) {
            throw new AccessDeniedException("게시글 삭제 사용자 인증에 실패했습니다.\n Params: %d".formatted(userId));
        }

        if(post.hasFile()) {
            fileMapper.deleteFileByPostId(postId);
        }

        if(post.hasPostTags()) {
            tagMapper.deletePostTagByPostId(post.getId());
        }

        commentMapper.deleteCommentByPostId(post.getId());
        postMapper.deletePost(postId);
    }

    @Override
    public void saveComment(CommentSaveCommand saveCommand) {
        checkedLogin(saveCommand.userId());

        Post post = getPost(saveCommand.postId());
        Comment comment = saveCommand.toDomain(post.getId());
        commentMapper.insertComment(comment);
    }

    @Override
    public void updateComment(CommentUpdateCommand updateCommand) {
        checkedLogin(updateCommand.userId());

        Comment comment = getComment(updateCommand.commentId());
        comment.change(updateCommand);
        commentMapper.updateComment(comment);
    }

    @Override
    public void deleteComment(Long userId, Long commentId) {
        checkedLogin(userId);

        Comment comment = getComment(commentId);
        commentMapper.deleteComment(comment.getId());
    }

    @Override
    public void saveTag(TagSaveCommand saveCommand) {
        checkedLogin(saveCommand.userId());

        Tag tag = saveCommand.toDomain();
        tagMapper.insertTag(tag);
    }

    @Override
    public void updateTag(TagUpdateCommand updateCommand) {
        checkedLogin(updateCommand.userId());

        Tag tag = getTag(updateCommand.tagId());
        tag.change(updateCommand);
        tagMapper.updateTag(tag);
    }

    @Override
    public void deleteTag(Long userId, Long tagId) {
        checkedLogin(userId);

        Tag tag = getTag(tagId);
        tagMapper.deleteTag(tag.getId());
    }

    private void checkedLogin(Long id) {
        User user = userMapper.findById(id);
        if (user == null) {
            throw new AccessDeniedException("사용자 로그인 실패했습니다.");
        }
    }

    private @NonNull Post getPost(Long id) {
        Post post = postMapper.findById(id);
        if(post == null) {
            throw new RuntimeException("게시글 조회에 실패했습니다.\n Params: %s".formatted(id));
        }
        return post;
    }

    private @NonNull Comment getComment(Long id) {
        Comment comment = commentMapper.findById(id);
        if(comment == null) {
            throw new RuntimeException("댓글 조회에 실패했습니다.\n Params: %s".formatted(id));
        }
        return comment;
    }

    private @NonNull Tag getTag(Long id) {
        Tag tag = tagMapper.findById(id);
        if(tag == null) {
            throw new RuntimeException("태그 조회에 실패했습니다.\n Params: %s".formatted(id));
        }
        return tag;
    }
}
