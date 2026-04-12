package com.parksense.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "search_history")
public class SearchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Column(nullable = false)
    private LocalDateTime arrivalTime;

    @Column(nullable = false)
    private LocalDateTime searchedAt;

    @Column(nullable = false, length = 255)
    private String bestOptionSummary;

    protected SearchHistory() {
    }

    public SearchHistory(
            double latitude,
            double longitude,
            LocalDateTime arrivalTime,
            LocalDateTime searchedAt,
            String bestOptionSummary
    ) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.arrivalTime = arrivalTime;
        this.searchedAt = searchedAt;
        this.bestOptionSummary = bestOptionSummary;
    }

    public Long getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public LocalDateTime getSearchedAt() {
        return searchedAt;
    }

    public String getBestOptionSummary() {
        return bestOptionSummary;
    }
}
