package pcy.study.server.domain;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostTag {

    private Long id;

    private Long tagId;

    public PostTag(Long tagId) {
        this.tagId = tagId;
    }
}
