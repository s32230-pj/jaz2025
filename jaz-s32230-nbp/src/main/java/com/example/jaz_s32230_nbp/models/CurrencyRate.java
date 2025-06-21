package com.example.jaz_s32230_nbp.models;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Schema(description = "Szczegóły dotyczące obliczonego kursu waluty")
public class CurrencyRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = ".", example = "1")
    private Long id;

    @Schema(description = "Kod waluty", example = "USD")
    private String currency;

    @Schema(description = "Data rozpoczęcia przedziału", example = "2024-01-01")
    private LocalDate startDate;

    @Schema(description = "Data zakończenia przedziału", example = "2024-01-10")
    private LocalDate endDate;

    @Schema(description = "Średni kurs waluty", example = "4.83")
    private double averageRate;

    @Schema(description = "Data i godzina wykonania zapytania", example = "2025-06-20T10:00:00")
    private LocalDateTime timestamp;

    public CurrencyRate() {
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }


    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }


    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }


    public void setAverageRate(double averageRate) {
        this.averageRate = averageRate;
    }


    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCurrency() {
        return currency;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public double getAverageRate() {
        return averageRate;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}