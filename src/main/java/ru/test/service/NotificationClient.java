package ru.test.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NotificationClient {

    private final RestTemplate restTemplate;

    public NotificationClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "notificationService", fallbackMethod = "fallback")
    public void sendUserCreated(String email) {
        restTemplate.postForObject(
                "http://notification-service/notifications/created?email=" + email,
                null,
                Void.class
        );
    }

    @CircuitBreaker(name = "notificationService", fallbackMethod = "fallback")
    public void sendUserDeleted(String email) {
        restTemplate.postForObject(
                "http://notification-service/notifications/deleted?email=" + email,
                null,
                Void.class
        );
    }

    public void fallback(String email, Exception e) {
        System.out.println("Notification service is down. Email: " + email);
    }
}