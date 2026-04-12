package com.parksense.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RecommendationExplanationService {

    public String generateExplanation(double availability, double predictedPrice, double distanceMeters) {
        List<String> reasons = new ArrayList<>();

        if (availability >= 0.75) {
            reasons.add("high availability");
        } else if (availability <= 0.40) {
            reasons.add("limited availability");
        }

        if (predictedPrice <= 12.00) {
            reasons.add("low price");
        } else if (predictedPrice >= 18.00) {
            reasons.add("higher price");
        }

        if (distanceMeters <= 300.0) {
            reasons.add("short walking distance");
        } else if (distanceMeters >= 1000.0) {
            reasons.add("longer walking distance");
        }

        if (reasons.isEmpty()) {
            return "Balanced option with moderate availability, price, and distance";
        }

        return buildExplanation(reasons);
    }

    private String buildExplanation(List<String> reasons) {
        if (reasons.size() == 1) {
            return capitalize(reasons.get(0)) + " for your selected arrival time";
        }

        if (reasons.size() == 2) {
            return capitalize(reasons.get(0)) + " and " + reasons.get(1);
        }

        return capitalize(reasons.get(0)) + ", " + reasons.get(1) + ", and " + reasons.get(2);
    }

    private String capitalize(String value) {
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }
}
