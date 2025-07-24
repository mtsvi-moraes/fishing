package com.itau.antifraud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.itau.antifraud.dto.AlertDTO;
import com.itau.antifraud.dto.BulkUpdateRequest;
import com.itau.antifraud.model.Alert;
import com.itau.antifraud.repository.AlertRepository;

import org.springframework.web.client.RestClientException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/alerts")
@Tag(name = "Alert Management", description = "APIs para gerenciamento de alertas de spam/phishing")
public class AlertController {

    @Autowired
    private AlertRepository alertRepository;

    @PostMapping("/fetch")
    @Operation(summary = "Buscar análise de email de exemplo", description = "Faz uma requisição para o serviço Python para analisar um email de exemplo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Email analisado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Falha na comunicação com o serviço Python"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
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
    @Operation(summary = "Analisar conteúdo de email", description = "Analisa o conteúdo de um email fornecido para detectar spam/phishing")
    public ResponseEntity<?> analyzeEmailAndSave(@RequestBody String emailContent) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            
            // Use the analyze endpoint with POST
            String pythonServiceUrl = "http://localhost:5000/analyze";
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
    @Operation(summary = "Listar todos os alertas", description = "Retorna todos os alertas cadastrados no sistema")
    public ResponseEntity<Iterable<Alert>> getAllAlerts() {
        return ResponseEntity.ok(alertRepository.findAll());
    }
    
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Verifica se o controlador está funcionando")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("AlertController is working!");
    }
    
    @GetMapping("/spam")
    @Operation(summary = "Listar alertas de spam", description = "Retorna apenas os alertas identificados como spam")
    public ResponseEntity<Iterable<Alert>> getSpamAlerts() {
        return ResponseEntity.ok(alertRepository.findByIsSpamTrue());
    }
    
    // Novos endpoints GET
    @GetMapping("/legitimate")
    public ResponseEntity<Iterable<Alert>> getLegitimateAlerts() {
        return ResponseEntity.ok(alertRepository.findByIsSpamFalse());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Alert> getAlertById(@PathVariable Long id) {
        return alertRepository.findById(id)
            .map(alert -> ResponseEntity.ok(alert))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/high-confidence")
    public ResponseEntity<Iterable<Alert>> getHighConfidenceAlerts() {
        return ResponseEntity.ok(alertRepository.findByConfidenceGreaterThan(0.8));
    }
    
    @GetMapping("/count")
    public ResponseEntity<Long> getAlertsCount() {
        return ResponseEntity.ok(alertRepository.count());
    }
    
    @GetMapping("/count/spam")
    public ResponseEntity<Long> getSpamAlertsCount() {
        return ResponseEntity.ok(alertRepository.countByIsSpamTrue());
    }
    
    // Operações de UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Alert> updateAlert(@PathVariable Long id, @RequestBody Alert alertDetails) {
        return alertRepository.findById(id)
            .map(alert -> {
                alert.setConfidence(alertDetails.getConfidence());
                alert.setIsSpam(alertDetails.getIsSpam());
                alert.setSubject(alertDetails.getSubject());
                alert.setEmailContent(alertDetails.getEmailContent());
                // Não atualiza timeDetected - mantém o original
                
                Alert updatedAlert = alertRepository.save(alert);
                return ResponseEntity.ok(updatedAlert);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PatchMapping("/{id}/spam-status")
    public ResponseEntity<Alert> updateSpamStatus(@PathVariable Long id, @RequestBody boolean isSpam) {
        return alertRepository.findById(id)
            .map(alert -> {
                alert.setIsSpam(isSpam);
                Alert updatedAlert = alertRepository.save(alert);
                return ResponseEntity.ok(updatedAlert);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PatchMapping("/{id}/confidence")
    public ResponseEntity<Alert> updateConfidence(@PathVariable Long id, @RequestBody double confidence) {
        if (confidence < 0.0 || confidence > 1.0) {
            return ResponseEntity.badRequest().build();
        }
        
        return alertRepository.findById(id)
            .map(alert -> {
                alert.setConfidence(confidence);
                Alert updatedAlert = alertRepository.save(alert);
                return ResponseEntity.ok(updatedAlert);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PatchMapping("/{id}/subject")
    public ResponseEntity<Alert> updateSubject(@PathVariable Long id, @RequestBody String subject) {
        return alertRepository.findById(id)
            .map(alert -> {
                alert.setSubject(subject);
                Alert updatedAlert = alertRepository.save(alert);
                return ResponseEntity.ok(updatedAlert);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/bulk-update-spam")
    public ResponseEntity<String> bulkUpdateSpamStatus(@RequestBody BulkUpdateRequest request) {
        try {
            for (Long id : request.getIds()) {
                alertRepository.findById(id).ifPresent(alert -> {
                    alert.setIsSpam(request.getIsSpam());
                    alertRepository.save(alert);
                });
            }
            return ResponseEntity.ok("Updated " + request.getIds().size() + " alerts");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating alerts: " + e.getMessage());
        }
    }
    
    // Operações de DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAlert(@PathVariable Long id) {
        return alertRepository.findById(id)
            .map(alert -> {
                alertRepository.delete(alert);
                return ResponseEntity.ok("Alert with ID " + id + " deleted successfully");
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/bulk-delete")
    public ResponseEntity<String> bulkDeleteAlerts(@RequestBody BulkUpdateRequest request) {
        try {
            int deletedCount = 0;
            for (Long id : request.getIds()) {
                if (alertRepository.existsById(id)) {
                    alertRepository.deleteById(id);
                    deletedCount++;
                }
            }
            return ResponseEntity.ok("Deleted " + deletedCount + " alerts");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting alerts: " + e.getMessage());
        }
    }

    @DeleteMapping("/spam")
    public ResponseEntity<String> deleteAllSpamAlerts() {
        try {
            Iterable<Alert> spamAlerts = alertRepository.findByIsSpamTrue();
            int deletedCount = 0;
            for (Alert alert : spamAlerts) {
                alertRepository.delete(alert);
                deletedCount++;
            }
            return ResponseEntity.ok("Deleted " + deletedCount + " spam alerts");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting spam alerts: " + e.getMessage());
        }
    }

    @DeleteMapping("/low-confidence")
    public ResponseEntity<String> deleteLowConfidenceAlerts(@RequestParam(defaultValue = "0.3") double threshold) {
        try {
            Iterable<Alert> allAlerts = alertRepository.findAll();
            int deletedCount = 0;
            for (Alert alert : allAlerts) {
                if (alert.getConfidence() < threshold) {
                    alertRepository.delete(alert);
                    deletedCount++;
                }
            }
            return ResponseEntity.ok("Deleted " + deletedCount + " alerts with confidence below " + threshold);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting low confidence alerts: " + e.getMessage());
        }
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
