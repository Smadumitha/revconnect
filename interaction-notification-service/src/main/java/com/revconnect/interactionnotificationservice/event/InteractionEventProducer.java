package com.revconnect.interactionnotificationservice.event;

import com.revconnect.interactionnotificationservice.dto.InteractionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InteractionEventProducer {

    private final ApplicationEventPublisher eventPublisher;

    public void sendInteractionEvent(InteractionEvent event) {
        eventPublisher.publishEvent(event);
    }
}
