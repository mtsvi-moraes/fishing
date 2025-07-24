package com.itau.antifraud.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itau.antifraud.model.Alert;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class DataExtractionService {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String EXPORT_DIR = "data-exports";
    
    public void exportAlertChange(Alert alert, String operation) {
        try {
            // Cria diretório se não existir
            File directory = new File(EXPORT_DIR);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
            // Cria objeto com dados da mudança
            Map<String, Object> changeData = new HashMap<>();
            changeData.put("operation", operation);
            changeData.put("timestamp", LocalDateTime.now().toString());
            changeData.put("alert_id", alert.getId());
            changeData.put("subject", alert.getSubject());
            changeData.put("is_spam", alert.getIsSpam());
            changeData.put("confidence", alert.getConfidence());
            changeData.put("created_date", alert.getCreatedDate());
            changeData.put("last_modified_date", alert.getLastModifiedDate());
            changeData.put("version", alert.getVersion());
            
            // Nome do arquivo com timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS"));
            String filename = String.format("%s/alert_change_%s_%s.json", EXPORT_DIR, operation, timestamp);
            
            // Escreve arquivo JSON
            try (FileWriter writer = new FileWriter(filename)) {
                objectMapper.writeValue(writer, changeData);
            }
            
            System.out.println("Mudança exportada para: " + filename);
            
        } catch (IOException e) {
            System.err.println("Erro ao exportar mudança: " + e.getMessage());
        }
    }
}
