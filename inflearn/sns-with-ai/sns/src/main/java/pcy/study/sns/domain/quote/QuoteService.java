package pcy.study.sns.domain.quote;

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
public class QuoteService {

    private final PostRepository postRepository;
    private final TimelineService timelineService;

    @Transactional
    public Post createQuote(Long quoteId, String content, User user) {
        postRepository.findByIdAndDeletedAtIsNull(quoteId)
                .orElseThrow(() -> new DomainException(ErrorCode.ORIGINAL_POST_NOT_FOUND_FOR_QUOTE));

        if (postRepository.existsByUserIdAndQuoteIdAndDeletedAtIsNull(user.getId(), quoteId)) {
            throw new DomainException(ErrorCode.ALREADY_QUOTED);
        }

        Post quote = Post.createQuote(content, user, quoteId);
        Post savedQuote = postRepository.save(quote);

        postRepository.incrementRepostCount(quoteId);

        timelineService.fanOutToFollowers(savedQuote.getId(), user);

        return savedQuote;
    }

    public List<Post> getAllQuotes() {
        return postRepository.findAllByDeletedAtIsNullOrderByCreatedAtDesc().stream()
                .filter(post -> post.getQuoteId() != null)
                .toList();
    }

    public Post getQuoteById(Long id) {
        Post post = postRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new DomainException(ErrorCode.QUOTE_NOT_FOUND));
        if (post.getQuoteId() == null) {
            throw new DomainException(ErrorCode.NOT_A_QUOTE);
        }
        return post;
    }

    @Transactional
    public void deleteQuote(Long id, User user) {
        Post quote = getQuoteById(id);

        if (!quote.getUser().getId().equals(user.getId())) {
            throw new DomainException(ErrorCode.QUOTE_UNAUTHORIZED_DELETE);
        }

        quote.delete();
        postRepository.decrementRepostCount(quote.getQuoteId());
    }
}
