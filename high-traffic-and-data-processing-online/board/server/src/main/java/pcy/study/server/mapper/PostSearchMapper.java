package pcy.study.server.mapper;

import org.apache.ibatis.annotations.Mapper;
import pcy.study.server.service.info.PostSearchInfo;
import pcy.study.server.service.query.PostSearchQuery;

import java.util.List;

@Mapper
public interface PostSearchMapper {

    List<PostSearchInfo> selectPosts(PostSearchQuery query);
}
