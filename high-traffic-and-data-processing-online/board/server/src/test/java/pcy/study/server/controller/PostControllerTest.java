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
import pcy.study.server.controller.request.*;
import pcy.study.server.domain.*;
import pcy.study.server.mapper.*;
import pcy.study.server.utils.SHA256Util;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private CommentMapper commentMapper;

    private MockHttpSession userSession;
    private User user;
    private Category category;
    private Post post;
    private Comment comment;
    private Tag tag;

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

        File file = new File("/path", "file.txt", "txt");
        Tag firstTag = new Tag("spring", "/tags/spring");
        Tag secondTag = new Tag("java", "/tags/java");
        List<PostTag> postTags = List.of(new PostTag(firstTag.getId()), new PostTag(secondTag.getId()));
        post = new Post("Title", "Contents", false, user.getId(), category.getId(), file, postTags);
        postMapper.insertPost(post);
        fileMapper.insertFile(post);
        tagMapper.insertPostTags(post);

        comment = new Comment(post.getId(), "contents", null);
        commentMapper.insertComment(comment);

        tag = new Tag("new", "/tags/new");
        tagMapper.insertTag(tag);
    }

    @Test
    @DisplayName("게시글 등록 성공 - 파일과 태그 포함")
    void registerPostSuccess() throws Exception {
        // given
        var fileRequest = new FileRegisterRequest("/path", "file.txt", "txt");
        var tagIds = List.of(tag.getId());
        var registerRequest = new PostRegisterRequest("new Title", "new Contents", false, category.getId(), fileRequest, tagIds);

        // when & then
        mockMvc.perform(post("/posts")
                        .session(userSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("게시글 수정 성공 - 태그 변경")
    void modifyPostSuccess() throws Exception {
        // given
        var newTagIds = List.of(tag.getId());
        var modifyRequest = new PostModifyRequest("change Title", "change Contents", false, null, newTagIds);

        // when & then
        mockMvc.perform(patch("/posts/{postId}", post.getId())
                        .session(userSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modifyRequest)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("게시글 삭제 성공 - 파일 포함")
    void removePostSuccess() throws Exception {
        // when
        mockMvc.perform(delete("/posts/{postId}", post.getId())
                        .session(userSession))
                .andDo(print())
                .andExpect(status().isOk());

        // then
        Post deletedPost = postMapper.findById(post.getId());
        File deletedFile = fileMapper.findByPostId(post.getFile().getId());
        assertThat(deletedPost).isNull();
        assertThat(deletedFile).isNull();
    }

    @Test
    @DisplayName("댓글 등록 성공")
    void commentRegister_success() throws Exception {
        // given
        var request = new CommentRegisterRequest("new comment", null);

        // when & then
        mockMvc.perform(post("/posts/{postId}/comments", post.getId())
                        .session(userSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("댓글 수정 성공")
    void commentModify_success() throws Exception {
        // given
        var request = new CommentModifyRequest("changed contents");

        // when & then
        mockMvc.perform(patch("/posts/comments/{commentId}", comment.getId())
                        .session(userSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("댓글 삭제 성공")
    void commentRemove_success() throws Exception {
        // given
        Comment comment = new Comment(post.getId(), "contents", null);
        commentMapper.insertComment(comment);

        // when & then
        mockMvc.perform(delete("/posts/comments/{commentId}", comment.getId())
                        .session(userSession))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("태그 등록 성공")
    void tagRegister_success() throws Exception {
        // given
        var request = new TagRegisterRequest("new_tag", "/tags/new_tag");

        // when & then
        mockMvc.perform(post("/posts/tags")
                        .session(userSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("태그 수정 성공")
    void tagModify_success() throws Exception {
        // given
        var request = new TagModifyRequest("changed_name", "/tags/changed_name");

        // when & then
        mockMvc.perform(patch("/posts/tags/{tagId}", tag.getId())
                        .session(userSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("태그 삭제 성공")
    void tagRemove_success() throws Exception {
        // when & then
        mockMvc.perform(delete("/posts/tags/{tagId}", tag.getId())
                        .session(userSession))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
