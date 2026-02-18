package pcy.study.server.domain;

import lombok.*;
import pcy.study.server.service.command.TagUpdateCommand;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tag {

    private Long id;

    private String name;

    private String url;

    public Tag(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public void change(TagUpdateCommand updateCommand) {
        this.name = updateCommand.name();
        this.url = updateCommand.url();
    }
}
