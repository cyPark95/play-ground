package pcy.study.server.service.info;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostSearchInfo {

    private Long id;

    private String name;

    private String contents;

    private boolean isAdmin;

    private int views;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private PostSearchUserInfo userInfo;

    private PostSearchCategoryInfo categoryInfo;

    private PostSearchFileInfo fileInfoInfo;

    private List<PostSearchPostTagInfo> postTagInfos;
}
