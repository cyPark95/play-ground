package pcy.study.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import pcy.study.server.service.SnsService;
import pcy.study.server.service.SlackService;

import java.util.Map;

@RestController
@Log4j2
@RequiredArgsConstructor
public class SnsController {

    private final SnsService snsService;
    private final SlackService slackService;

    @PostMapping("/create-topic")
    public String createTopic(@RequestParam("topicName") String topicName) {
        String topicArn = snsService.createTopic(topicName);
        return "Create Topic Name: " + topicArn;
    }

    @PostMapping("/subscribe")
    public String subscribe(@RequestParam("endpoint") String endpoint, @RequestParam("topicArn") String topicArn) {
        snsService.subscribe(endpoint, topicArn);
        return "Subscribe Topic ARN: " + topicArn;
    }

    @PostMapping("/publish")
    public String publish(@RequestParam(name = "topicArn") String topicArn, @RequestBody Map<String, Object> message) {
        snsService.publish(topicArn, message.toString());
        return "Sent message to topic: " + topicArn;
    }

    @GetMapping("/slack/error")
    public void error() {
        log.info("슬랙 error 채널 테스트");
        slackService.sendSlackMessage("슬랙 에러 테스트", "error");
    }
}
