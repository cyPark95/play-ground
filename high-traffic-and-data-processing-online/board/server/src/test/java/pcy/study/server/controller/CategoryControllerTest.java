package pcy.study.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pcy.study.server.controller.request.CategoryModifyRequest;
import pcy.study.server.controller.request.CategoryRegisterRequest;
import pcy.study.server.domain.Category;
import pcy.study.server.domain.User;
import pcy.study.server.mapper.CategoryMapper;
import pcy.study.server.mapper.UserMapper;
import pcy.study.server.utils.SHA256Util;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    private MockHttpSession session;

    private Category category;

    @BeforeEach
    void setUp() {
        session = new MockHttpSession();

        category = new Category("Category");
        categoryMapper.insertCategory(category);

        String password = "password";
        String encryptedPassword = SHA256Util.encryptSHA256(password);
        User admin = new User("admin_id", encryptedPassword, "admin", false, true);
        userMapper.insertUser(admin);

        session.setAttribute("LOGIN_ADMIN_ID", admin.getId());
    }

    @Test
    @DisplayName("카테고리 등록 성공")
    void registerCategorySuccess() throws Exception {
        // given
        var registerRequest = new CategoryRegisterRequest("Category", "CATEGORIES", 10, 0);

        // when & then
        mockMvc.perform(post("/categories")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("카테고리 등록 실패 - 권한 없음")
    void registerCategoryFailWithNoAuth() throws Exception {
        // given
        var registerRequest = new CategoryRegisterRequest("Category", "CATEGORIES", 10, 0);

        // when & then
        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("카테고리 수정 성공")
    void modifyCategorySuccess() throws Exception {
        // given
        var modifyRequest = new CategoryModifyRequest("Modify Category");

        // when & then
        mockMvc.perform(patch("/categories/{categoryId}", category.getId())
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modifyRequest)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("카테고리 수정 실패 - 권한 없음")
    void modifyCategoryFailWithNoAuth() throws Exception {
        // given
        var modifyRequest = new CategoryModifyRequest("Modify Category");

        // when & then
        mockMvc.perform(patch("/categories/{categoryId}", category.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modifyRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    @DisplayName("카테고리 삭제 성공")
    void removeCategorySuccess() throws Exception {
        // when & then
        mockMvc.perform(delete("/categories/{categoryId}", category.getId()))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
