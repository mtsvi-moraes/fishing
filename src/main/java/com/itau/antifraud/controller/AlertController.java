package com.itau.antifraud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.itau.antifraud.dto.AlertDTO;
import com.itau.antifraud.model.Alert;
import com.itau.antifraud.repository.AlertRepository;

import org.springframework.web.client.RestClientException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    @Autowired
    private AlertRepository alertRepository;

    @PostMapping("/fetch")
    public ResponseEntity<?> fetchFromPythonAndSave() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            
            // Use the Docker service name instead of localhost
            String pythonServiceUrl = "http://localhost:5000/";

            
            AlertDTO dto = restTemplate.getForObject(pythonServiceUrl, AlertDTO.class);
            
            if (dto == null) {
                return ResponseEntity.badRequest().body("Failed to get response from Python service");
            }

            Alert alert = convertToAlert(dto);
            Alert saved = alertRepository.save(alert);
            
            return ResponseEntity.ok(saved);
        } catch (RestClientException e) {
            return ResponseEntity.status(500).body("Error calling Python service: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error saving alert: " + e.getMessage());
        }
    }
    
    @PostMapping("/analyze")
    public ResponseEntity<?> analyzeEmailAndSave(@RequestBody String emailContent) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            
            // Use the analyze endpoint with POST
            String pythonServiceUrl = "http://python-backend:5000/analyze";
            AlertDTO dto = restTemplate.postForObject(pythonServiceUrl, emailContent, AlertDTO.class);
            
            if (dto == null) {
                return ResponseEntity.badRequest().body("Failed to analyze email");
            }

            Alert alert = convertToAlert(dto);
            Alert saved = alertRepository.save(alert);
            
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error analyzing email: " + e.getMessage());
        }
    }
    
    @GetMapping
    public ResponseEntity<Iterable<Alert>> getAllAlerts() {
        return ResponseEntity.ok(alertRepository.findAll());
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("AlertController is working!");
    }
    
    private Alert convertToAlert(AlertDTO dto) {
        Alert alert = new Alert();
        alert.setConfidence(dto.getConfidence());
        alert.setIsSpam(dto.is_is_spam());
        alert.setSubject(dto.getSubject());
        alert.setEmailContent(dto.getEmail_content()); // Add this line
        
        // Better date parsing
        try {
            String timeStr = dto.getTime_detected();
            if (timeStr != null) {
                // Handle different date formats
                LocalDateTime dateTime;
                if (timeStr.endsWith("Z")) {
                    dateTime = LocalDateTime.parse(timeStr.replace("Z", ""));
                } else if (timeStr.contains("T")) {
                    dateTime = LocalDateTime.parse(timeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                } else {
                    dateTime = LocalDateTime.now(); // fallback
                }
                alert.setTimeDetected(dateTime);
            } else {
                alert.setTimeDetected(LocalDateTime.now());
            }
        } catch (DateTimeParseException e) {
            alert.setTimeDetected(LocalDateTime.now());
        }
        
        return alert;
    }
}
