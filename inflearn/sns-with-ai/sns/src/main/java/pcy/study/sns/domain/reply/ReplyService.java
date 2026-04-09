package pcy.study.sns.domain.reply;

import pcy.study.sns.domain.base.DomainException;
import pcy.study.sns.domain.base.ErrorCode;
import pcy.study.sns.domain.post.Post;
import pcy.study.sns.domain.post.PostRepository;
import pcy.study.sns.domain.timeline.TimelineService;
import pcy.study.sns.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReplyService {

    private final PostRepository postRepository;
    private final TimelineService timelineService;

    @Transactional
    public Post createReply(Long parentId, String content, List<Long> mediaIds, User user) {
        postRepository.findByIdAndDeletedAtIsNull(parentId)
                .orElseThrow(() -> new DomainException(ErrorCode.PARENT_POST_NOT_FOUND));

        Post reply = Post.createReply(content, user, parentId, mediaIds);
        Post savedReply = postRepository.save(reply);

        postRepository.incrementReplyCount(parentId);

        timelineService.fanOutToFollowers(savedReply.getId(), user);

        return savedReply;
    }

    public List<Post> getRepliesByParentId(Long parentId) {
        return postRepository.findByParentIdAndDeletedAtIsNullOrderByCreatedAtAsc(parentId);
    }

    @Transactional
    public Post updateReply(Long replyId, String content, User user) {
        Post reply = postRepository.findByIdAndDeletedAtIsNull(replyId)
                .orElseThrow(() -> new DomainException(ErrorCode.REPLY_NOT_FOUND));

        if (reply.getParentId() == null) {
            throw new DomainException(ErrorCode.NOT_A_REPLY);
        }

        if (!reply.getUser().getId().equals(user.getId())) {
            throw new DomainException(ErrorCode.REPLY_UNAUTHORIZED_UPDATE);
        }

        if (reply.isEditExpired()) {
            throw new DomainException(ErrorCode.REPLY_EDIT_EXPIRED);
        }

        reply.updateContent(content);
        return reply;
    }

    @Transactional
    public void deleteReply(Long replyId, User user) {
        Post reply = postRepository.findByIdAndDeletedAtIsNull(replyId)
                .orElseThrow(() -> new DomainException(ErrorCode.REPLY_NOT_FOUND));

        if (reply.getParentId() == null) {
            throw new DomainException(ErrorCode.NOT_A_REPLY);
        }

        if (!reply.getUser().getId().equals(user.getId())) {
            throw new DomainException(ErrorCode.REPLY_UNAUTHORIZED_DELETE);
        }

        reply.delete();
        postRepository.decrementReplyCount(reply.getParentId());
    }
}
