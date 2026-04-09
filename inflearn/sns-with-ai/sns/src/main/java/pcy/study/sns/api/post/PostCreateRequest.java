package pcy.study.sns.api.post;

import java.util.List;

public record PostCreateRequest(
        String content,
        List<Long> mediaIds
) {
}
