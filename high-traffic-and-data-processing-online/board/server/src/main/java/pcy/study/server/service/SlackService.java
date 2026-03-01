package pcy.study.server.service;

import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class SlackService {

    @Value("${slack.token}")
    private String slackToken;

    public void sendSlackMessage(String message, String channel) {
        if (channel.equals("error")) {
            channel = "#모니터링";
        }

        try {
            MethodsClient methodsClient = Slack.getInstance().methods(slackToken);
            ChatPostMessageRequest messageRequest = ChatPostMessageRequest.builder()
                    .channel(channel)
                    .text(message)
                    .build();
            methodsClient.chatPostMessage(messageRequest);
            log.info("Slack channel: {}", channel);
        } catch (SlackApiException | IOException e) {
            log.error("sendSlackMessage error message: {}", e.getMessage());
        }
    }
}
