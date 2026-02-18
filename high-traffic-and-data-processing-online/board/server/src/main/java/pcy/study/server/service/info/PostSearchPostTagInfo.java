package pcy.study.server.service.info;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostSearchPostTagInfo {

    private Long id;

    private Long tagId;

    private String name;

    private String url;
}
