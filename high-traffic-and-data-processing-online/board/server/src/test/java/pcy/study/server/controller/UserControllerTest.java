package pcy.study.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pcy.study.server.controller.request.LoginRequest;
import pcy.study.server.controller.request.SignUpRequest;
import pcy.study.server.controller.request.UserChangePasswordRequest;
import pcy.study.server.controller.request.UserRemoveRequest;
import pcy.study.server.domain.User;
import pcy.study.server.mapper.UserMapper;
import pcy.study.server.utils.SHA256Util;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserMapper userMapper;

    private MockHttpSession session;

    private User user;
    private String password;

    @BeforeEach
    void setUp() {
        session = new MockHttpSession();

        password = "password";
        String encryptedPassword = SHA256Util.encryptSHA256(password);
        user = new User("user_id", encryptedPassword, "nickname", false, false);
        userMapper.insertUser(user);
    }

    @Test
    @DisplayName("회원가입 성공")
    void signUpSuccess() throws Exception {
        // given
        var signUpRequest = new SignUpRequest("new_user_id", "password", "nickname", false, false);

        // when & then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("회원가입 실패 - 중복된 아이디")
    void signUpFailWithDuplicateId() throws Exception {
        // given
        var signUpRequest = new SignUpRequest(user.getUserId(), "password", "nickname", false, false);

        // when & then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("중복된 아이디입니다."));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("회원가입 실패 - 유효성 검사 실패 (빈 아이디 or null)")
    void signUpFailWithInvalidInput(String invalid) throws Exception {
        // given
        var signUpRequest = new SignUpRequest(invalid, "password", "nickname", false, false);

        // when & then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("아이디를 입력해주세요."));
    }

    @Test
    @DisplayName("로그인 성공")
    void loginSuccess() throws Exception {
        // given
        var loginRequest = new LoginRequest(user.getUserId(), password);

        // when & then
        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user_id"))
                .andExpect(jsonPath("$.nickname").value("nickname"));
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 사용자")
    void loginFailWithNonExistentUser() throws Exception {
        // given
        var loginRequest = new LoginRequest("nonExistentUser", password);

        // when & then
        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("사용자 정보 조회 성공")
    void getUserInfoSuccess() throws Exception {
        // given
        session.setAttribute("LOGIN_USER_ID", user.getId());

        // when & then
        mockMvc.perform(get("/users")
                        .session(session))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user_id"))
                .andExpect(jsonPath("$.nickname").value("nickname"));
    }

    @Test
    @DisplayName("비밀번호 변경 성공")
    void changePasswordSuccess() throws Exception {
        // given
        var changePasswordRequest = new UserChangePasswordRequest(password, "changePassword");
        session.setAttribute("LOGIN_USER_ID", user.getId());

        // when & then
        mockMvc.perform(patch("/users/password")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("user_id"))
                .andExpect(jsonPath("$.nickname").value("nickname"));
    }

    @Test
    @DisplayName("회원 탈퇴 성공")
    void removeUserSuccess() throws Exception {
        // given
        session.setAttribute("LOGIN_USER_ID", user.getId());

        var removeRequest = new UserRemoveRequest("password");

        // when & then
        mockMvc.perform(delete("/users")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(removeRequest)))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
