package com.example.txdxai.email.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EmailEvent extends ApplicationEvent {
    private final String to;
    private final String subject;
    private final String content;

    public EmailEvent(Object source, String to, String subject, String content) {
        super(source);
        this.to = to;
        this.subject = subject;
        this.content = content;
    }
}