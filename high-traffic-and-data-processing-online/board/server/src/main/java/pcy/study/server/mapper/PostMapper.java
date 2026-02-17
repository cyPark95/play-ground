package pcy.study.server.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pcy.study.server.domain.Post;

import java.util.List;

@Mapper
public interface PostMapper {

    void insertPost(Post post);

    List<Post> findByUserId(@Param("userId") Long userId);

    Post findById(@Param("id") Long id);

    void updatePost(Post post);

    void deletePost(@Param("id") Long id);
}
