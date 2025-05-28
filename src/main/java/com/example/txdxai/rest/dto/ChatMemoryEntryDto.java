package com.example.txdxai.rest.dto;

import java.time.Instant;
import java.time.LocalDateTime;

public record ChatMemoryEntryDto(
        Long    id,
        String  message,
        String  sender,
        LocalDateTime timestamp
) { }