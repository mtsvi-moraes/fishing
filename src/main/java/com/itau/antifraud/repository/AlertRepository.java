package com.itau.antifraud.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itau.antifraud.model.Alert;

public interface AlertRepository extends JpaRepository<Alert, Long> {}
