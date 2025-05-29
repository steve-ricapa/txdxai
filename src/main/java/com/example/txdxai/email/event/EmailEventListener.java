package com.example.txdxai.email.event;

import com.example.txdxai.email.domain.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class EmailEventListener {

    private final EmailService emailService;

    @Autowired
    public EmailEventListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @EventListener
    @Async
    public void handleEmailEvent(EmailEvent event) {
        emailService.sendMessage(
                event.getTo(),
                event.getSubject(),
                event.getContent()
        );
    }
}