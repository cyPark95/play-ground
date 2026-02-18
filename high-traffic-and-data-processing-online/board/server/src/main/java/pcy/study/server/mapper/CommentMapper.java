package pcy.study.server.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pcy.study.server.domain.Comment;

@Mapper
public interface CommentMapper {

    void insertComment(Comment comment);

    Comment findById(@Param("id") Long id);

    void updateComment(Comment comment);

    void deleteComment(@Param("id") Long id);

    void deleteCommentByPostId(@Param("postId") Long postId);
}
