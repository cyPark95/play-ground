package pcy.study.server.service;

import pcy.study.server.service.info.PostSearchInfo;
import pcy.study.server.service.query.PostSearchQuery;

import java.util.List;

public interface PostSearchService {

    List<PostSearchInfo> searchPosts(PostSearchQuery searchQuery);

    List<PostSearchInfo> getPostsByTagName(String tagName);
}
