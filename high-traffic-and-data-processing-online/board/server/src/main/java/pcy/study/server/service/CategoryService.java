package pcy.study.server.service;

import pcy.study.server.service.command.CategorySaveCommand;
import pcy.study.server.service.command.CategoryUpdateCommand;

public interface CategoryService {

    void save(CategorySaveCommand saveCommand);

    void update(CategoryUpdateCommand updateCommand);

    void delete(Long categoryId);
}
