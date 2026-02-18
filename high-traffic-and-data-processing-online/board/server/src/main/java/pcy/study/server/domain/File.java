package pcy.study.server.domain;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class File {

    private Long id;

    private String path;

    private String name;

    private String extension;

    public File(String path, String name, String extension) {
        this.path = path;
        this.name = name;
        this.extension = extension;
    }
}
