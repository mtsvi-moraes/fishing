package com.itau.antifraud.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/data-extraction")
@Tag(name = "Data Extraction", description = "APIs para gerenciamento de arquivos de extração de dados")
public class DataExtractionController {
    
    private static final String EXPORT_DIR = "data-exports";
    
    @GetMapping("/files")
    @Operation(summary = "Listar arquivos de extração", description = "Lista todos os arquivos de mudanças exportados")
    public ResponseEntity<List<String>> listExportedFiles(
            @Parameter(description = "Filtrar por tipo de operação (CREATE, UPDATE, DELETE)")
            @RequestParam(value = "operation", required = false) String operation) {
        
        File directory = new File(EXPORT_DIR);
        if (!directory.exists() || !directory.isDirectory()) {
            return ResponseEntity.ok(List.of());
        }
        
        List<String> files = Arrays.stream(directory.listFiles())
                .filter(File::isFile)
                .map(File::getName)
                .filter(filename -> operation == null || filename.contains("_" + operation.toUpperCase() + "_"))
                .sorted()
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(files);
    }
    
    @GetMapping("/files/count")
    @Operation(summary = "Contar arquivos de extração", description = "Conta arquivos por tipo de operação")
    public ResponseEntity<Object> countExportedFiles(
            @Parameter(description = "Tipo de operação para contar (CREATE, UPDATE, DELETE)")
            @RequestParam(value = "operation", required = false) String operation) {
        
        File directory = new File(EXPORT_DIR);
        if (!directory.exists() || !directory.isDirectory()) {
            return ResponseEntity.ok(0);
        }
        
        long count = Arrays.stream(directory.listFiles())
                .filter(File::isFile)
                .filter(file -> operation == null || file.getName().contains("_" + operation.toUpperCase() + "_"))
                .count();
        
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/files/{filename}")
    @Operation(summary = "Baixar arquivo específico", description = "Baixa o conteúdo de um arquivo de extração específico")
    public ResponseEntity<String> downloadFile(
            @Parameter(description = "Nome do arquivo para download")
            @PathVariable String filename) {
        
        try {
            Path filePath = Paths.get(EXPORT_DIR, filename);
            
            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }
            
            String content = Files.readString(filePath);
            return ResponseEntity.ok()
                    .header("Content-Type", "application/json")
                    .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                    .body(content);
                    
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Erro ao ler arquivo: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/files")
    @Operation(summary = "Limpar arquivos de extração", description = "Remove arquivos antigos de extração")
    public ResponseEntity<String> cleanupFiles(
            @Parameter(description = "Tipo de operação para limpar (CREATE, UPDATE, DELETE)")
            @RequestParam(value = "operation", required = false) String operation,
            @Parameter(description = "Manter apenas os N arquivos mais recentes")
            @RequestParam(value = "keep", defaultValue = "50") int keepCount) {
        
        File directory = new File(EXPORT_DIR);
        if (!directory.exists() || !directory.isDirectory()) {
            return ResponseEntity.ok("Diretório não existe");
        }
        
        List<File> files = Arrays.stream(directory.listFiles())
                .filter(File::isFile)
                .filter(file -> operation == null || file.getName().contains("_" + operation.toUpperCase() + "_"))
                .sorted((f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified())) // Mais recentes primeiro
                .collect(Collectors.toList());
        
        int deletedCount = 0;
        for (int i = keepCount; i < files.size(); i++) {
            if (files.get(i).delete()) {
                deletedCount++;
            }
        }
        
        return ResponseEntity.ok("Removidos " + deletedCount + " arquivos. Mantidos " + Math.min(keepCount, files.size()) + " mais recentes.");
    }
    
    @GetMapping("/status")
    @Operation(summary = "Status do sistema de extração", description = "Verifica status e estatísticas do sistema de extração")
    public ResponseEntity<Object> getExtractionStatus() {
        File directory = new File(EXPORT_DIR);
        
        if (!directory.exists()) {
            return ResponseEntity.ok(new Object() {
                public final String status = "DISABLED";
                public final String message = "Diretório de extração não existe";
                public final int totalFiles = 0;
            });
        }
        
        File[] files = directory.listFiles();
        long createCount = Arrays.stream(files).filter(f -> f.getName().contains("_CREATE_")).count();
        long updateCount = Arrays.stream(files).filter(f -> f.getName().contains("_UPDATE_")).count();
        long deleteCount = Arrays.stream(files).filter(f -> f.getName().contains("_DELETE_")).count();
        
        return ResponseEntity.ok(new Object() {
            public final String status = "ACTIVE";
            public final String exportDirectory = EXPORT_DIR;
            public final int totalFiles = files.length;
            public final long createOperations = createCount;
            public final long updateOperations = updateCount;
            public final long deleteOperations = deleteCount;
        });
    }
}
