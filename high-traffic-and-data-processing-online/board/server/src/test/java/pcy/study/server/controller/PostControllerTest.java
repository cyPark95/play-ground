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
import pcy.study.server.controller.request.FileRegisterRequest;
import pcy.study.server.controller.request.PostModifyRequest;
import pcy.study.server.controller.request.PostRegisterRequest;
import pcy.study.server.domain.Category;
import pcy.study.server.domain.File;
import pcy.study.server.domain.Post;
import pcy.study.server.domain.User;
import pcy.study.server.mapper.CategoryMapper;
import pcy.study.server.mapper.FileMapper;
import pcy.study.server.mapper.PostMapper;
import pcy.study.server.mapper.UserMapper;
import pcy.study.server.utils.SHA256Util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private FileMapper fileMapper;

    private MockHttpSession userSession;
    private Post post;
    private File file;
    private User user;
    private Category category;

    @BeforeEach
    void setUp() {
        userSession = new MockHttpSession();

        String password = "password";
        String encryptedPassword = SHA256Util.encryptSHA256(password);
        user = new User("user_id", encryptedPassword, "nickname", false, false);
        userMapper.insertUser(user);

        userSession.setAttribute("LOGIN_USER_ID", user.getId());

        category = new Category("Category");
        categoryMapper.insertCategory(category);

        file = new File("/path", "old1.txt", "txt");
        post = new Post("Title", "Contents", false, user.getId(), category.getId(), file);
        postMapper.insertPost(post);
        fileMapper.insertFile(post);
    }

    @Test
    @DisplayName("파일과 함께 게시글 등록 성공")
    void registerPostSuccessWithMultipleFiles() throws Exception {
        // given
        var fileRegisterRequest = new FileRegisterRequest("/new Path", "new File", "txt");
        var registerRequest = new PostRegisterRequest("new Title", "new Contents", false, category.getId(), fileRegisterRequest);

        // when
        mockMvc.perform(post("/posts")
                        .session(userSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("게시글 등록 실패 - 미인증")
    void registerPostFailWithNoAuth() throws Exception {
        // given
        var registerRequest = new PostRegisterRequest("Title", "Contents", false, category.getId(), null);

        // when & then
        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("내 게시글 목록 조회 성공")
    void getMyPostsSuccess() throws Exception {
        // when & then
        mockMvc.perform(get("/posts/my")
                        .session(userSession))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(post.getName()))
                .andExpect(jsonPath("$[0].contents").value(post.getContents()))
                .andExpect(jsonPath("$[0].isAdmin").value(post.isAdmin()))
                .andExpect(jsonPath("$[0].views").value(post.getViews()))
                .andExpect(jsonPath("$[0].userId").value(user.getId()))
                .andExpect(jsonPath("$[0].userNickname").value(user.getNickname()))
                .andExpect(jsonPath("$[0].categoryId").value(category.getId()))
                .andExpect(jsonPath("$[0].categoryName").value(category.getName()))
                .andExpect(jsonPath("$[0].fileId").value(file.getId()))
                .andExpect(jsonPath("$[0].filePath").value(file.getPath()))
                .andExpect(jsonPath("$[0].fileName").value(file.getName()))
                .andExpect(jsonPath("$[0].fileExtension").value(file.getExtension()));
    }

    @Test
    @DisplayName("게시글 수정 성공")
    void modifyPostSuccess() throws Exception {
        // given
        var fileRegisterRequest = new FileRegisterRequest("/new Path", "new File", "txt");
        var modifyRequest = new PostModifyRequest("change Title", "change Contents", false, fileRegisterRequest);

        // when & then
        mockMvc.perform(patch("/posts/{postId}", post.getId())
                        .session(userSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modifyRequest)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("게시글 삭제 성공 - 첨부파일 포함")
    void removePostSuccess() throws Exception {
        // when
        mockMvc.perform(delete("/posts/{postId}", post.getId())
                        .session(userSession))
                .andDo(print())
                .andExpect(status().isOk());

        // then
        Post deletedPost = postMapper.findById(post.getId());
        assertThat(deletedPost).isNull();
    }
}
