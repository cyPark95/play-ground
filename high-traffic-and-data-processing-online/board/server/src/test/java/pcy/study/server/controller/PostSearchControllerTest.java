package pcy.study.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pcy.study.server.controller.request.SearchPostRequest;
import pcy.study.server.domain.Category;
import pcy.study.server.domain.File;
import pcy.study.server.domain.Post;
import pcy.study.server.domain.User;
import pcy.study.server.mapper.CategoryMapper;
import pcy.study.server.mapper.FileMapper;
import pcy.study.server.mapper.PostMapper;
import pcy.study.server.mapper.UserMapper;
import pcy.study.server.service.command.CategorySortStatus;
import pcy.study.server.utils.SHA256Util;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PostSearchControllerTest {

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

    private File file;
    private Post post;
    private User user;
    private Category category;

    @BeforeEach
    void setUp() {
        String password = "password";
        String encryptedPassword = SHA256Util.encryptSHA256(password);
        user = new User("user_id", encryptedPassword, "nickname", false, false);
        userMapper.insertUser(user);

        category = new Category("Category");
        categoryMapper.insertCategory(category);

        file = new File("/path", "old1.txt", "txt");
        post = new Post("Title", "Contents", false, user.getId(), category.getId(), file);
        postMapper.insertPost(post);
        fileMapper.insertFile(post);

        var firstPost = new Post("Title1", "Contents1", false, user.getId(), category.getId(), null);
        postMapper.insertPost(firstPost);

        var secondPost = new Post("Title2", "Contents2", false, user.getId(), category.getId(), null);
        postMapper.insertPost(secondPost);
    }

    @Test
    @DisplayName("게시글 검색 성공")
    void searchPostsSuccess() throws Exception {
        // given
        var searchRequest = new SearchPostRequest("Title", null, null, null, null);

        // when & then
        mockMvc.perform(post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[2].name").value(post.getName()))
                .andExpect(jsonPath("$[2].contents").value(post.getContents()))
                .andExpect(jsonPath("$[2].isAdmin").value(post.isAdmin()))
                .andExpect(jsonPath("$[2].views").value(post.getViews()))
                .andExpect(jsonPath("$[2].userId").value(user.getId()))
                .andExpect(jsonPath("$[2].userNickname").value(user.getNickname()))
                .andExpect(jsonPath("$[2].categoryId").value(category.getId()))
                .andExpect(jsonPath("$[2].categoryName").value(category.getName()))
                .andExpect(jsonPath("$[2].fileId").value(file.getId()))
                .andExpect(jsonPath("$[2].filePath").value(file.getPath()))
                .andExpect(jsonPath("$[2].fileName").value(file.getName()))
                .andExpect(jsonPath("$[2].fileExtension").value(file.getExtension()))
                .andExpect(jsonPath("$[2].createdAt").isNotEmpty())
        ;
    }

    @Test
    @DisplayName("게시글 검색 성공 - 결과 없음")
    void searchPostsSuccessWithNoResult() throws Exception {
        // given
        var searchRequest = new SearchPostRequest("NoResult", null, null, null, CategorySortStatus.OLDEST.name());

        // when & then
        mockMvc.perform(post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("게시글 검색 성공 - 최신순 정렬")
    void searchPostsSuccessWithNewestSort() throws Exception {
        // given
        var searchRequest = new SearchPostRequest(null, null, null, null, CategorySortStatus.NEWEST.name());

        // when & then
        mockMvc.perform(post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Title2"))
                .andExpect(jsonPath("$[1].name").value("Title1"))
                .andExpect(jsonPath("$[2].name").value("Title"));
    }

    @Test
    @DisplayName("게시글 검색 성공 -오래된 순 정렬")
    void searchPostsSuccessWithOldestSort() throws Exception {
        // given
        var searchRequest = new SearchPostRequest("Title", null, null, null, CategorySortStatus.OLDEST.name());

        // when & then
        mockMvc.perform(post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Title"))
                .andExpect(jsonPath("$[1].name").value("Title1"))
                .andExpect(jsonPath("$[2].name").value("Title2"));
    }
}
