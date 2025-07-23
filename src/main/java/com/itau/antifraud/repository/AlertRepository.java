package com.itau.antifraud.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itau.antifraud.model.Alert;

import java.time.LocalDateTime;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    
    // Métodos existentes que podem estar faltando
    Iterable<Alert> findByIsSpamTrue();
    Iterable<Alert> findByIsSpamFalse();
    Long countByIsSpamTrue();
    Long countByIsSpamFalse();
    
    // Novos métodos para os endpoints adicionais
    Iterable<Alert> findByConfidenceGreaterThan(double confidence);
    Iterable<Alert> findByConfidenceBetween(double min, double max);
    Iterable<Alert> findByTimeDetectedAfter(LocalDateTime dateTime);
}
