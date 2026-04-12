package com.parksense.model;

import java.time.LocalDateTime;

public record SearchHistoryItem(
        Long id,
        double latitude,
        double longitude,
        LocalDateTime arrivalTime,
        LocalDateTime searchedAt,
        String bestOptionSummary
) {
}
