package ru.test.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import lombok.*;

@Service
@RequiredArgsConstructor
public class UserEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendUserCreated(String email) {

        UserEvent event = new UserEvent();
        event.setEmail(email);
        event.setState(Operation.CREATE);
        kafkaTemplate.send("user-events", event);
    }

    public void sendUserDeleted(String email) {
        UserEvent event = new UserEvent();
        event.setEmail(email);
        event.setState(Operation.DELETE);
        kafkaTemplate.send("user-events", event);
    }
}
