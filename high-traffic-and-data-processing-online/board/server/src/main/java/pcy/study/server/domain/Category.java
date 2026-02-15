package pcy.study.server.domain;

import lombok.Getter;
import pcy.study.server.service.command.CategoryUpdateCommand;

@Getter
public class Category {

    private Long id;

    private String name;

    public Category(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Category(String name) {
        this.name = name;
    }

    public void change(CategoryUpdateCommand command) {
        this.name = command.name();
    }

}
