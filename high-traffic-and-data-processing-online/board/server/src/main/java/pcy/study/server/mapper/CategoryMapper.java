package pcy.study.server.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pcy.study.server.domain.Category;

@Mapper
public interface CategoryMapper {

    void insertCategory(Category category);

    Category findById(@Param("id") Long id);

    void updateCategory(Category category);

    void deleteCategory(@Param("id") Long id);
}
