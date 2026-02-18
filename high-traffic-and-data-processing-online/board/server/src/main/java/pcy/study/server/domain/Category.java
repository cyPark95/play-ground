package pcy.study.server.domain;

import lombok.*;
import pcy.study.server.service.command.CategoryUpdateCommand;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {

    private Long id;

    private String name;

    public Category(String name) {
        this.name = name;
    }

    public void change(CategoryUpdateCommand command) {
        this.name = command.name();
    }

}
