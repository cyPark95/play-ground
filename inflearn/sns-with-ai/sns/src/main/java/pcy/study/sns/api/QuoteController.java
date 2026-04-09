package pcy.study.sns.api;

import pcy.study.sns.api.quote.QuoteCreateRequest;
import pcy.study.sns.api.quote.QuoteResponse;
import pcy.study.sns.config.auth.AuthUser;
import pcy.study.sns.domain.post.Post;
import pcy.study.sns.domain.quote.QuoteService;
import pcy.study.sns.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class QuoteController {

    private final QuoteService quoteService;

    @PostMapping("/api/v1/posts/{postId}/quotes")
    public ResponseEntity<QuoteResponse> createQuote(
            @PathVariable Long postId,
            @RequestBody QuoteCreateRequest request,
            @AuthUser User user
    ) {
        Post quote = quoteService.createQuote(postId, request.content(), user);
        return ResponseEntity.status(HttpStatus.CREATED).body(QuoteResponse.from(quote));
    }

    @GetMapping("/api/v1/quotes")
    public ResponseEntity<List<QuoteResponse>> getAllQuotes() {
        List<QuoteResponse> quotes = quoteService.getAllQuotes().stream()
                .map(QuoteResponse::from)
                .toList();
        return ResponseEntity.ok(quotes);
    }

    @GetMapping("/api/v1/quotes/{id}")
    public ResponseEntity<QuoteResponse> getQuoteById(@PathVariable Long id) {
        Post quote = quoteService.getQuoteById(id);
        return ResponseEntity.ok(QuoteResponse.from(quote));
    }

    @DeleteMapping("/api/v1/quotes/{id}")
    public ResponseEntity<Void> deleteQuote(
            @PathVariable Long id,
            @AuthUser User user
    ) {
        quoteService.deleteQuote(id, user);
        return ResponseEntity.noContent().build();
    }
}
