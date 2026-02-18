package pcy.study.server.service;

import pcy.study.server.service.command.*;
import pcy.study.server.service.info.PostInfo;

import java.util.List;

public interface PostService {

    void save(PostSaveCommand saveCommand);

    List<PostInfo> getMyPosts(Long userId);

    void update(PostUpdateCommand updateCommand);

    void delete(Long userId, Long postId);

    void saveComment(CommentSaveCommand saveCommand);

    void updateComment(CommentUpdateCommand updateCommand);

    void deleteComment(Long userId, Long commentId);

    void saveTag(TagSaveCommand saveCommend);

    void updateTag(TagUpdateCommand updateCommand);

    void deleteTag(Long userId, Long tagId);
}
