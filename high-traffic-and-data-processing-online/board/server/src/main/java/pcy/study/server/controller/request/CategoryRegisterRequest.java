package pcy.study.server.controller.request;

import pcy.study.server.service.command.CategorySortStatus;
import pcy.study.server.service.command.CategorySaveCommand;

public record CategoryRegisterRequest(
        String name,
        String sortStatus,
        int searchCount,
        int pagingStartOffset
) {

    public CategorySaveCommand toCommand(Long userId) {
        return new  CategorySaveCommand(
                userId,
                name,
                CategorySortStatus.valueOf(sortStatus.toUpperCase()),
                searchCount,
                pagingStartOffset
        );
    }
}
