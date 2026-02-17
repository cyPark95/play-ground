package pcy.study.server.controller.request;

import pcy.study.server.service.command.CategorySortStatus;
import pcy.study.server.service.query.PostSearchQuery;

public record SearchPostRequest(
        String name,
        String contents,
        Long categoryId,
        Long userId,
        String sortStatus
) {

    public PostSearchQuery toQuery() {
        return new PostSearchQuery(
                name,
                contents,
                categoryId,
                userId,
                sortStatus == null
                        ? CategorySortStatus.NEWEST
                        : CategorySortStatus.valueOf(sortStatus)
        );
    }
}
