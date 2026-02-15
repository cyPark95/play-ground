package pcy.study.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pcy.study.server.aop.LoginCheck;
import pcy.study.server.aop.UserType;
import pcy.study.server.controller.request.CategoryRegisterRequest;
import pcy.study.server.controller.request.CategoryModifyRequest;
import pcy.study.server.service.CategoryService;
import pcy.study.server.service.command.CategorySaveCommand;
import pcy.study.server.service.command.CategoryUpdateCommand;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @LoginCheck(type = UserType.ADMIN)
    public void register(
            Long userId,
            @RequestBody CategoryRegisterRequest registerRequest
    ) {
        CategorySaveCommand registerCommand = registerRequest.toCommand(userId);
        categoryService.save(registerCommand);
    }

    @PatchMapping("/{categoryId}")
    @LoginCheck(type = UserType.ADMIN)
    public void modify(
            Long userId,
            @PathVariable Long categoryId,
            @RequestBody CategoryModifyRequest modifyRequest
    ) {
        CategoryUpdateCommand updateCommand = modifyRequest.toCommand(userId, categoryId);
        categoryService.update(updateCommand);
    }

    @DeleteMapping("/{categoryId}")
    public void remove(@PathVariable Long categoryId) {
        categoryService.delete(categoryId);
    }
}
