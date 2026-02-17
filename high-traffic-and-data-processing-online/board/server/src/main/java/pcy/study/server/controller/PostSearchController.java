package pcy.study.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pcy.study.server.controller.request.SearchPostRequest;
import pcy.study.server.controller.response.PostSearchResponse;
import pcy.study.server.service.PostSearchService;
import pcy.study.server.service.info.PostSearchInfo;
import pcy.study.server.service.query.PostSearchQuery;

import java.util.List;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class PostSearchController {

    private final PostSearchService postSearchService;

    @PostMapping
    public List<PostSearchResponse> searchPosts(@RequestBody SearchPostRequest searchPostRequest) {
        PostSearchQuery searchQuery = searchPostRequest.toQuery();
        List<PostSearchInfo> posts = postSearchService.searchPosts(searchQuery);
        return posts.stream()
                .map(PostSearchResponse::from)
                .toList();
    }
}
