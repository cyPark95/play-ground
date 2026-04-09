package pcy.study.sns.api.reply;

import java.util.List;

public record ReplyCreateRequest(
        String content,
        List<Long> mediaIds
) {
}
