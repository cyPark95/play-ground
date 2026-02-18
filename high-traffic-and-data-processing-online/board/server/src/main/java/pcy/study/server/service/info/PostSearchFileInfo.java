package pcy.study.server.service.info;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostSearchFileInfo {

    private Long id;

    private String path;

    private String name;

    private String extension;
}
