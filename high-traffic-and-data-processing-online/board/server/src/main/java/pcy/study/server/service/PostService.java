package pcy.study.server.service;

import pcy.study.server.service.command.PostSaveCommand;
import pcy.study.server.service.command.PostUpdateCommand;
import pcy.study.server.service.info.PostInfo;

import java.util.List;

public interface PostService {

    void save(PostSaveCommand saveCommand);

    List<PostInfo> getMyPosts(Long userId);

    void updatePost(PostUpdateCommand updateCommand);

    void delete(Long userId, Long postId);
}
