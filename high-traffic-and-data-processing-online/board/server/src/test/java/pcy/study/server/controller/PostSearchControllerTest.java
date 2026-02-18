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
import pcy.study.server.domain.*;
import pcy.study.server.mapper.*;
import pcy.study.server.service.command.CategorySortStatus;
import pcy.study.server.utils.SHA256Util;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    private CommentMapper commentMapper;

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private TagMapper tagMapper;

    private User user;
    private Category category;
    private Post post;
    private Comment comment;
    private File file;
    private Tag tag;
    private PostTag postTag;

    @BeforeEach
    void setUp() {
        String password = "password";
        String encryptedPassword = SHA256Util.encryptSHA256(password);
        user = new User("user_id", encryptedPassword, "nickname", false, false);
        userMapper.insertUser(user);

        category = new Category("Category");
        categoryMapper.insertCategory(category);

        tag = new Tag("new", "/tags/new");
        tagMapper.insertTag(tag);

        file = new File("/path", "file.txt", "txt");
        postTag = new PostTag(tag.getId());
        List<PostTag> postTags = List.of(postTag);
        post = new Post("new Title", "new Contents", false, user.getId(), category.getId(), file, postTags);
        postMapper.insertPost(post);
        fileMapper.insertFile(post);
        tagMapper.insertPostTags(post);

        comment = new Comment(post.getId(), "contents", null);
        commentMapper.insertComment(comment);

        var post1 = new Post("Title1", "Contents1", false, user.getId(), category.getId(), null, null);
        postMapper.insertPost(post1);
        var post2 = new Post("Title2", "Contents2", false, user.getId(), category.getId(), null, null);
        postMapper.insertPost(post2);
    }

    @Test
    @DisplayName("게시글 검색 성공")
    void searchPostsSuccess() throws Exception {
        // given
        var searchRequest = new SearchPostRequest("new", null, null, null, null);

        // when & then
        mockMvc.perform(post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(post.getId()))
                .andExpect(jsonPath("$[0].name").value(post.getName()))
                .andExpect(jsonPath("$[0].contents").value(post.getContents()))
                .andExpect(jsonPath("$[0].views").value(post.getViews()))
                .andExpect(jsonPath("$[0].user.id").value(user.getId()))
                .andExpect(jsonPath("$[0].user.userId").value(user.getUserId()))
                .andExpect(jsonPath("$[0].user.nickname").value(user.getNickname()))
                .andExpect(jsonPath("$[0].user.isWithDraw").value(user.isWithDraw()))
                .andExpect(jsonPath("$[0].user.isAdmin").value(user.isAdmin()))
                .andExpect(jsonPath("$[0].category.id").value(category.getId()))
                .andExpect(jsonPath("$[0].category.name").value(category.getName()))
                .andExpect(jsonPath("$[0].file.id").value(file.getId()))
                .andExpect(jsonPath("$[0].file.path").value(file.getPath()))
                .andExpect(jsonPath("$[0].file.name").value(file.getName()))
                .andExpect(jsonPath("$[0].file.extension").value(file.getExtension()))
                .andExpect(jsonPath("$[0].postTags[0].tagId").value(tag.getId()))
                .andExpect(jsonPath("$[0].postTags[0].name").value(tag.getName()))
                .andExpect(jsonPath("$[0].postTags[0].url").value(tag.getUrl()));
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
                .andExpect(jsonPath("$[2].name").value("new Title"));
    }

    @Test
    @DisplayName("게시글 검색 성공 - 오래된 순 정렬")
    void searchPostsSuccessWithOldestSort() throws Exception {
        // given
        var searchRequest = new SearchPostRequest(null, null, null, null, CategorySortStatus.OLDEST.name());

        // when & then
        mockMvc.perform(post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("new Title"))
                .andExpect(jsonPath("$[1].name").value("Title1"))
                .andExpect(jsonPath("$[2].name").value("Title2"));
    }

    @Test
    @DisplayName("태그 이름으로 게시글 검색 성공")
    void searchPostsByTagNameSuccess() throws Exception {
        // when & then
        mockMvc.perform(get("/search/tag")
                        .param("tagName", "new"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(post.getId()))
                .andExpect(jsonPath("$[0].name").value(post.getName()))
                .andExpect(jsonPath("$[0].contents").value(post.getContents()))
                .andExpect(jsonPath("$[0].views").value(post.getViews()))
                .andExpect(jsonPath("$[0].user.id").value(user.getId()))
                .andExpect(jsonPath("$[0].user.userId").value(user.getUserId()))
                .andExpect(jsonPath("$[0].user.nickname").value(user.getNickname()))
                .andExpect(jsonPath("$[0].user.isWithDraw").value(user.isWithDraw()))
                .andExpect(jsonPath("$[0].user.isAdmin").value(user.isAdmin()))
                .andExpect(jsonPath("$[0].category.id").value(category.getId()))
                .andExpect(jsonPath("$[0].category.name").value(category.getName()))
                .andExpect(jsonPath("$[0].file.id").value(file.getId()))
                .andExpect(jsonPath("$[0].file.path").value(file.getPath()))
                .andExpect(jsonPath("$[0].file.name").value(file.getName()))
                .andExpect(jsonPath("$[0].file.extension").value(file.getExtension()))
                .andExpect(jsonPath("$[0].postTags[0].tagId").value(tag.getId()))
                .andExpect(jsonPath("$[0].postTags[0].name").value(tag.getName()))
                .andExpect(jsonPath("$[0].postTags[0].url").value(tag.getUrl()));
    }
}
