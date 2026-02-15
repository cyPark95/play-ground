package pcy.study.server.service.command;

import pcy.study.server.domain.Category;

public record CategorySaveCommand(
        Long userId,
        String name,
        CategorySortStatus sortStatus,
        int searchCount,
        int pagingStartOffset
) {

    public Category toDomain() {
        return new Category(name);
    }
}
