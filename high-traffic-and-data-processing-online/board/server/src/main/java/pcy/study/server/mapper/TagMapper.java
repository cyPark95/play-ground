package pcy.study.server.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pcy.study.server.domain.Post;
import pcy.study.server.domain.Tag;

@Mapper
public interface TagMapper {

    void insertTag(Tag tag);

    void insertPostTags(Post post);

    Tag findById(@Param("id") Long id);

    void updateTag(Tag tag);

    void deleteTag(@Param("id") Long id);

    void deletePostTagByPostId(@Param("postId") Long postId);
}
