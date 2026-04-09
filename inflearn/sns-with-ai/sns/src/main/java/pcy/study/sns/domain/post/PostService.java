package pcy.study.sns.domain.post;

import pcy.study.sns.domain.base.DomainException;
import pcy.study.sns.domain.base.ErrorCode;
import pcy.study.sns.domain.like.Like;
import pcy.study.sns.domain.like.LikeRepository;
import pcy.study.sns.domain.media.Media;
import pcy.study.sns.domain.media.MediaRepository;
import pcy.study.sns.domain.postview.PostViewService;
import pcy.study.sns.domain.timeline.TimelineService;
import pcy.study.sns.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostViewService postViewService;
    private final MediaRepository mediaRepository;
    private final TimelineService timelineService;
    private final LikeRepository likeRepository;

    public Post createPost(String content, List<Long> mediaIds, User user) {
        if (mediaIds != null && !mediaIds.isEmpty()) {
            List<Media> mediaList = mediaRepository.findAllById(mediaIds);

            if (mediaList.size() != mediaIds.size()) {
                throw new DomainException(ErrorCode.POST_SOME_MEDIA_NOT_FOUND);
            }

            mediaList.forEach(media -> {
                if (!media.getUserId().equals(user.getId())) {
                    throw new DomainException(ErrorCode.POST_UNAUTHORIZED_MEDIA_USE);
                }
            });
        }

        Post post = Post.create(content, user, mediaIds);
        Post savedPost = postRepository.save(post);

        timelineService.fanOutToFollowers(savedPost.getId(), user);

        return savedPost;
    }

    public List<PostWithViewCount> getAllPosts() {
        List<Post> posts = postRepository.findAllByDeletedAtIsNullOrderByCreatedAtDesc();
        return enrichWithViewCount(posts);
    }

    public Post getPostById(Long id) {
        return postRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new DomainException(ErrorCode.POST_NOT_FOUND));
    }

    public PostWithViewCount getPostByIdWithPostViewIncrement(Long id) {
        Post post = getPostById(id);
        postViewService.incrementPostView(id);
        Long viewCount = postViewService.getPostView(id);
        return new PostWithViewCount(post, viewCount);
    }

    @Transactional
    public Post updatePost(Long id, String content, User user) {
        Post post = getPostById(id);

        if (!post.getUser().getId().equals(user.getId())) {
            throw new DomainException(ErrorCode.POST_UNAUTHORIZED_UPDATE);
        }

        if (post.isEditExpired()) {
            throw new DomainException(ErrorCode.POST_EDIT_EXPIRED);
        }

        post.updateContent(content);
        return post;
    }

    public void deletePost(Long id, User user) {
        Post post = getPostById(id);

        if (!post.getUser().getId().equals(user.getId())) {
            throw new DomainException(ErrorCode.POST_UNAUTHORIZED_DELETE);
        }

        post.delete();
        postRepository.save(post);
    }

    public List<PostWithViewCount> getPostsByUserId(Long userId) {
        List<Post> posts = postRepository.findByUserIdAndParentIdIsNullAndDeletedAtIsNullOrderByCreatedAtDesc(userId);
        return enrichWithViewCount(posts);
    }

    public List<PostWithViewCount> getRepliesByUserId(Long userId) {
        List<Post> posts = postRepository.findByUserIdAndParentIdIsNotNullAndDeletedAtIsNullOrderByCreatedAtDesc(userId);
        return enrichWithViewCount(posts);
    }

    public PostWithViewCount enrichWithViewCount(Post post) {
        Long viewCount = postViewService.getPostView(post.getId());
        return new PostWithViewCount(post, viewCount);
    }

    public List<PostWithViewCount> enrichWithViewCount(List<Post> posts) {
        return posts.stream()
                .map(this::enrichWithViewCount)
                .toList();
    }

    public PostWithUserContext enrichWithUserContext(Post post, User user) {
        return enrichWithUserContext(List.of(post), user).get(0);
    }

    public List<PostWithUserContext> enrichWithUserContext(List<Post> posts, User user) {
        if (posts.isEmpty()) {
            return List.of();
        }

        List<Long> postIds = posts.stream().map(Post::getId).toList();

        Set<Long> relatedPostIds = posts.stream()
                .flatMap(p -> java.util.stream.Stream.of(p.getRepostId(), p.getQuoteId(), p.getParentId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, Post> relatedPostsMap = relatedPostIds.isEmpty()
                ? Map.of()
                : postRepository.findAllByIdInAndDeletedAtIsNull(new ArrayList<>(relatedPostIds)).stream()
                        .collect(Collectors.toMap(Post::getId, p -> p));

        Map<Long, Like> likesMap = likeRepository.findByUserIdAndPostIdInAndDeletedAtIsNull(user.getId(), postIds).stream()
                .collect(Collectors.toMap(l -> l.getPost().getId(), l -> l));

        Map<Long, Post> repostsMap = postRepository.findByUserIdAndRepostIdInAndDeletedAtIsNull(user.getId(), postIds).stream()
                .collect(Collectors.toMap(Post::getRepostId, p -> p));

        return posts.stream()
                .map(post -> buildPostWithUserContext(post, relatedPostsMap, likesMap, repostsMap))
                .toList();
    }

    private PostWithUserContext buildPostWithUserContext(
            Post post,
            Map<Long, Post> relatedPostsMap,
            Map<Long, Like> likesMap,
            Map<Long, Post> repostsMap
    ) {
        Long viewCount = postViewService.getPostView(post.getId());

        Like like = likesMap.get(post.getId());
        Post userRepost = repostsMap.get(post.getId());

        boolean isRepostOrQuote = post.getRepostId() != null || post.getQuoteId() != null;

        return new PostWithUserContext(
                post,
                viewCount,
                like != null,
                like != null ? like.getId() : null,
                userRepost != null,
                userRepost != null ? userRepost.getId() : null,
                post.getRepostId() != null ? relatedPostsMap.get(post.getRepostId()) : null,
                post.getQuoteId() != null ? relatedPostsMap.get(post.getQuoteId()) : null,
                post.getParentId() != null ? relatedPostsMap.get(post.getParentId()) : null,
                isRepostOrQuote ? post.getUser().getId() : null,
                isRepostOrQuote ? post.getUser().getUsername() : null
        );
    }
}
