package com.revconnect.interactionnotificationservice.event;

import com.revconnect.interactionnotificationservice.dto.InteractionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InteractionEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendInteractionEvent(InteractionEvent event) {
        kafkaTemplate.send("interaction-events", event);
    }
}
