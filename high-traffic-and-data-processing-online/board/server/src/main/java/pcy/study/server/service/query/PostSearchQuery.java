package pcy.study.server.service.query;

import pcy.study.server.service.command.CategorySortStatus;

public record PostSearchQuery(
        String name,
        String contents,
        Long categoryId,
        Long userId,
        CategorySortStatus sortStatus
) {
}
