package pcy.study.sns.domain.base;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum ErrorCode {

    CANNOT_FOLLOW_SELF(HttpStatus.BAD_REQUEST, "FOLLOW_001", "Cannot follow yourself"),
    ALREADY_FOLLOWING(HttpStatus.CONFLICT, "FOLLOW_002", "Already following this user"),
    NOT_FOLLOWING(HttpStatus.BAD_REQUEST, "FOLLOW_003", "Not following this user"),
    FOLLOW_COUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "FOLLOW_004", "Follow count not found"),

    ALREADY_LIKED(HttpStatus.CONFLICT, "LIKE_001", "You have already liked this post"),
    LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "LIKE_002", "Like not found"),
    LIKE_UNAUTHORIZED_DELETE(HttpStatus.FORBIDDEN, "LIKE_003", "You are not authorized to delete this like"),

    MEDIA_NOT_FOUND(HttpStatus.NOT_FOUND, "MEDIA_001", "Media not found"),
    MEDIA_INVALID_STATUS(HttpStatus.BAD_REQUEST, "MEDIA_002", "Media is not in INIT status"),
    MEDIA_UNAUTHORIZED_UPDATE(HttpStatus.FORBIDDEN, "MEDIA_003", "You are not authorized to update this media"),
    MEDIA_UNAUTHORIZED_DELETE(HttpStatus.FORBIDDEN, "MEDIA_004", "You are not authorized to delete this media"),

    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "POST_001", "Post not found"),
    POST_SOME_MEDIA_NOT_FOUND(HttpStatus.NOT_FOUND, "POST_002", "Some media not found"),
    POST_UNAUTHORIZED_MEDIA_USE(HttpStatus.FORBIDDEN, "POST_003", "You are not authorized to use this media"),
    POST_UNAUTHORIZED_UPDATE(HttpStatus.FORBIDDEN, "POST_004", "You are not authorized to update this post"),
    POST_EDIT_EXPIRED(HttpStatus.BAD_REQUEST, "POST_005", "Post can only be edited within 1 hour of creation"),
    POST_UNAUTHORIZED_DELETE(HttpStatus.FORBIDDEN, "POST_006", "You are not authorized to delete this post"),

    ORIGINAL_POST_NOT_FOUND_FOR_QUOTE(HttpStatus.NOT_FOUND, "QUOTE_001", "Post for quote not found"),
    ALREADY_QUOTED(HttpStatus.CONFLICT, "QUOTE_002", "You have already quoted this post"),
    QUOTE_NOT_FOUND(HttpStatus.NOT_FOUND, "QUOTE_003", "Quote not found"),
    NOT_A_QUOTE(HttpStatus.BAD_REQUEST, "QUOTE_004", "This is not a quote"),
    QUOTE_UNAUTHORIZED_DELETE(HttpStatus.FORBIDDEN, "QUOTE_005", "You are not authorized to delete this quote"),

    PARENT_POST_NOT_FOUND(HttpStatus.NOT_FOUND, "REPLY_001", "Parent post not found"),
    REPLY_NOT_FOUND(HttpStatus.NOT_FOUND, "REPLY_002", "Reply not found"),
    NOT_A_REPLY(HttpStatus.BAD_REQUEST, "REPLY_003", "This is not a reply"),
    REPLY_UNAUTHORIZED_UPDATE(HttpStatus.FORBIDDEN, "REPLY_004", "You are not authorized to update this reply"),
    REPLY_EDIT_EXPIRED(HttpStatus.BAD_REQUEST, "REPLY_005", "Reply can only be edited within 1 hour of creation"),
    REPLY_UNAUTHORIZED_DELETE(HttpStatus.FORBIDDEN, "REPLY_006", "You are not authorized to delete this reply"),

    ORIGINAL_POST_NOT_FOUND_FOR_REPOST(HttpStatus.NOT_FOUND, "REPOST_001", "Post for repost not found"),
    ALREADY_REPOSTED(HttpStatus.CONFLICT, "REPOST_002", "You have already reposted this post"),
    REPOST_NOT_FOUND(HttpStatus.NOT_FOUND, "REPOST_003", "Repost not found"),
    NOT_A_REPOST(HttpStatus.BAD_REQUEST, "REPOST_004", "This is not a repost"),
    REPOST_UNAUTHORIZED_DELETE(HttpStatus.FORBIDDEN, "REPOST_005", "You are not authorized to delete this repost"),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_001", "User not found");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
