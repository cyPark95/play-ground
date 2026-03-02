package pcy.study.website;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.WebUtils;

import java.util.Arrays;

@SpringBootApplication
@Controller
public class WebsiteApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebsiteApplication.class, args);
    }

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${queue.server.url}")
    private String queueServerUrl;

    @Value("${target.server.url}")
    private String targetServerUrl;

    @GetMapping("/")
    public String index(
            @RequestParam(name = "queue", defaultValue = "default") String queue,
            @RequestParam(name = "user_id") Long userId,
            HttpServletRequest request
    ) {
        var cookieName = "user-queue-%s-token".formatted(queue);
        Cookie cookie = WebUtils.getCookie(request, cookieName);
        String token = cookie != null ? cookie.getValue() : "";

        boolean isAllowed = isUserAllowed(queue, userId, token);
        if (!isAllowed) {
            String redirectUrl = UriComponentsBuilder.fromUriString(targetServerUrl)
                    .queryParam("user_id", userId)
                    .build().toUriString();

            String finalRedirectUri = UriComponentsBuilder.fromUriString(queueServerUrl)
                    .path("/waiting-room")
                    .queryParam("user_id", userId)
                    .queryParam("redirect_url", redirectUrl)
                    .build().toUriString();

            return "redirect:" + finalRedirectUri;
        }

        return "index";
    }

    public boolean isUserAllowed(String queue, Long userId, String token) {
        var apiUri = UriComponentsBuilder
                .fromUriString(queueServerUrl)
                .path("/api/v1/queue/allowed")
                .queryParam("queue", queue)
                .queryParam("user_id", userId)
                .queryParam("token", token)
                .encode()
                .build()
                .toUri();

        try {
            ResponseEntity<AllowedUserResponse> response = restTemplate.getForEntity(apiUri, AllowedUserResponse.class);
            return response.getBody() != null && response.getBody().allowed();
        } catch (RestClientException e) {
            return false;
        }
    }
}
