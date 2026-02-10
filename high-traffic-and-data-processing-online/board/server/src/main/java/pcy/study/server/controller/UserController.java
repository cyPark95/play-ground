package pcy.study.server.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pcy.study.server.aop.LoginCheck;
import pcy.study.server.controller.request.LoginRequest;
import pcy.study.server.controller.request.SignUpRequest;
import pcy.study.server.controller.request.UserChangePasswordRequest;
import pcy.study.server.controller.request.UserRemoveRequest;
import pcy.study.server.controller.response.UserResponse;
import pcy.study.server.service.UserService;
import pcy.study.server.service.command.UserSaveCommand;
import pcy.study.server.service.command.UserUpdatePasswordCommand;
import pcy.study.server.service.info.UserInfo;
import pcy.study.server.utils.SessionUtil;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        UserSaveCommand saveCommand = signUpRequest.toCommand();
        userService.save(saveCommand);
    }

    @PostMapping("/login")
    @ResponseBody
    public UserResponse login(@RequestBody LoginRequest loginRequest, HttpSession session) {
        UserInfo userInfo = userService.login(loginRequest.userId(), loginRequest.password());
        setSessionLoginId(session, userInfo);
        return UserResponse.from(userInfo);
    }

    @PostMapping("/logout")
    public void logout(HttpSession session) {
        SessionUtil.clear(session);
    }

    @GetMapping
    @LoginCheck
    public UserResponse userInfo(Long id) {
        UserInfo userInfo = userService.getUser(id);
        return UserResponse.from(userInfo);
    }

    @PatchMapping("/password")
    @LoginCheck
    @ResponseBody
    public UserResponse changePassword(Long id, @RequestBody UserChangePasswordRequest changePasswordRequest) {
        UserUpdatePasswordCommand updatePasswordCommand = changePasswordRequest.toCommand(id);
        UserInfo userInfo = userService.updatePassword(updatePasswordCommand);
        return UserResponse.from(userInfo);
    }

    @DeleteMapping
    @LoginCheck
    public void remove(Long id, @Valid @RequestBody UserRemoveRequest removeRequest) {
        userService.delete(id, removeRequest.password());
    }

    private void setSessionLoginId(HttpSession session, UserInfo userInfo) {
        if (userInfo.isAdmin()) {
            SessionUtil.setLoginAdminId(session, userInfo.id());
        } else {
            SessionUtil.setLoginUserId(session, userInfo.id());
        }
    }
}
