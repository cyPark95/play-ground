package pcy.study.server.service;

public interface SnsService {

    String createTopic(String topicName);

    void subscribe(String endpoint, String topicArn);

    void publish(String topicArn, String message);
}
