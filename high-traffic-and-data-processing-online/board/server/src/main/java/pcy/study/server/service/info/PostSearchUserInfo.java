package pcy.study.server.service.info;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostSearchUserInfo {

    private Long id;

    private String userId;

    private String nickname;

    private boolean isWithDraw;

    private boolean isAdmin;
}
