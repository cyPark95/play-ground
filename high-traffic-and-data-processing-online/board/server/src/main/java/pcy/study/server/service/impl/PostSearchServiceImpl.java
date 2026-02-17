package pcy.study.server.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pcy.study.server.mapper.PostSearchMapper;
import pcy.study.server.service.PostSearchService;
import pcy.study.server.service.info.PostSearchInfo;
import pcy.study.server.service.query.PostSearchQuery;

import java.util.List;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class PostSearchServiceImpl implements PostSearchService {

    private final PostSearchMapper postSearchMapper;

    @Override
    @Cacheable(value = "getPosts", key = "'getPosts' + #searchQuery.name() + '_' + #searchQuery.categoryId() + '_' + #searchQuery.sortStatus()")
    public List<PostSearchInfo> searchPosts(PostSearchQuery searchQuery) {
        try {
            return postSearchMapper.selectPosts(searchQuery);
        } catch (RuntimeException e) {
            log.error("searchPosts 메서드 실패", e);
            throw new RuntimeException(e);
        }
    }
}
