package pcy.study.server.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import pcy.study.server.exception.SnsException;
import pcy.study.server.service.SnsService;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class AwsSnsService implements SnsService {

    @Value("${aws.sns.accessKey}")
    private String accessKey;

    @Value("${aws.sns.secretKey}")
    private String secretKey;

    @Value("${aws.sns.region}")
    private String region;

    @Override
    public String createTopic(String topicName) {
        final CreateTopicRequest topicRequest = CreateTopicRequest.builder()
                .name(topicName)
                .build();

        try (SnsClient snsClient = getSnsClient()) {
            var topicResponse = snsClient.createTopic(topicRequest);
            if (!topicResponse.sdkHttpResponse().isSuccessful()) {
                log.error("create-topic failed: {}", topicResponse.sdkHttpResponse().statusText().orElse("Unknown error"));
                throw new SnsException("Failed to create SNS topic", HttpStatus.valueOf(topicResponse.sdkHttpResponse().statusCode()));
            }

            log.info("topic-name: {}", topicResponse.topicArn());
            return topicResponse.topicArn();
        } catch (software.amazon.awssdk.services.sns.model.SnsException e) {
            log.error("AWS SNS SDK error during createTopic: {}", e.getMessage());
            throw new SnsException("AWS SNS Service error", e);
        }
    }

    @Override
    public void subscribe(String endpoint, String topicArn) {
        var subscribeRequest = SubscribeRequest.builder()
                .protocol("https")
                .topicArn(topicArn)
                .endpoint(endpoint)
                .build();

        try (SnsClient snsClient = getSnsClient()) {
            SubscribeResponse subscribeResponse = snsClient.subscribe(subscribeRequest);
            if (!subscribeResponse.sdkHttpResponse().isSuccessful()) {
                log.error("subscribe failed: {}", subscribeResponse.sdkHttpResponse().statusText().orElse("Unknown error"));
                throw new SnsException("Failed to subscribe to SNS topic", HttpStatus.valueOf(subscribeResponse.sdkHttpResponse().statusCode()));
            }

            log.info("topic ARN to subscribe: {}", topicArn);
        } catch (software.amazon.awssdk.services.sns.model.SnsException e) {
            log.error("AWS SNS SDK error during subscribe: {}", e.getMessage());
            throw new SnsException("AWS SNS Service error", e);
        }
    }

    @Override
    public void publish(String topicArn, String message) {
        var publishRequest = PublishRequest.builder()
                .topicArn(topicArn)
                .subject("HTTP ENDPOINT MESSAGE")
                .message(message)
                .build();

        try (SnsClient snsClient = getSnsClient()) {
            PublishResponse publishResponse = snsClient.publish(publishRequest);
            if (!publishResponse.sdkHttpResponse().isSuccessful()) {
                log.error("publish failed: {}", publishResponse.sdkHttpResponse().statusText().orElse("Unknown error"));
                throw new SnsException("Failed to publish message to SNS", HttpStatus.valueOf(publishResponse.sdkHttpResponse().statusCode()));
            }

            log.info("message status: {}", publishResponse.sdkHttpResponse().statusCode());
        } catch (software.amazon.awssdk.services.sns.model.SnsException e) {
            log.error("AWS SNS SDK error during publish: {}", e.getMessage());
            throw new SnsException("AWS SNS Service error", e);
        }
    }

    private SnsClient getSnsClient() {
        return SnsClient.builder()
                .credentialsProvider(getAwsCredentials(accessKey, secretKey))
                .region(Region.of(region))
                .build();
    }

    private AwsCredentialsProvider getAwsCredentials(String accessKey, String secretKey) {
        AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(accessKey, secretKey);
        return () -> awsBasicCredentials;
    }
}
