package ru.test.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import lombok.*;

@Service
@RequiredArgsConstructor
public class UserEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${topic.user-events}")
    private String topic;

    public void sendUserCreated(String email) {
        UserEvent event = new UserEvent();
        event.setEmail(email);
        event.setState(Operation.CREATE);
        kafkaTemplate.send(topic, event);
    }

    public void sendUserDeleted(String email) {
        UserEvent event = new UserEvent();
        event.setEmail(email);
        event.setState(Operation.DELETE);
        kafkaTemplate.send(topic, event);
    }
}
