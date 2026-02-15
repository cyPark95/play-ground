package pcy.study.server.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pcy.study.server.domain.Category;
import pcy.study.server.exception.AccessDeniedException;
import pcy.study.server.mapper.CategoryMapper;
import pcy.study.server.service.CategoryService;
import pcy.study.server.service.command.CategorySaveCommand;
import pcy.study.server.service.command.CategoryUpdateCommand;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    @Override
    public void save(CategorySaveCommand saveCommand) {
        if(saveCommand.userId() == null) {
            throw new AccessDeniedException("게시글 카테고리 등록 사용자 로그인 실패했습니다.");
        }

        Category category = saveCommand.toDomain();
        categoryMapper.insertCategory(category);
    }

    @Override
    public void update(CategoryUpdateCommand updateCommand) {
        if(updateCommand.userId() == null) {
            throw new AccessDeniedException("게시글 카테고리 수정 사용자 로그인 실패했습니다.");
        }

        Category category = categoryMapper.findById(updateCommand.categoryId());
        if(category == null) {
            throw new IllegalArgumentException("게시글 카테고리 수정에 실패했습니다.\n Params: %s".formatted(updateCommand));
        }

        category.change(updateCommand);
        categoryMapper.updateCategory(category);
    }

    @Override
    public void delete(Long categoryId) {
        categoryMapper.deleteCategory(categoryId);
    }
}
