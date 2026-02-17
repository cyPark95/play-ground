package pcy.study.server.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pcy.study.server.domain.File;
import pcy.study.server.domain.Post;

@Mapper
public interface FileMapper {

    void insertFile(Post post);

    File findByPostId(@Param("postId") Long postId);

    void deleteFileByPostId(Long postId);
}
