package com.itau.antifraud.listener;

import com.itau.antifraud.model.Alert;
import com.itau.antifraud.service.DataExtractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.persistence.*;

@Component
public class AlertChangeListener {
    
    @Autowired
    private DataExtractionService dataExtractionService;
    
    @PostPersist
    public void afterCreate(Alert alert) {
        dataExtractionService.exportAlertChange(alert, "CREATE");
    }
    
    @PostUpdate
    public void afterUpdate(Alert alert) {
        dataExtractionService.exportAlertChange(alert, "UPDATE");
    }
    
    @PostRemove
    public void afterDelete(Alert alert) {
        dataExtractionService.exportAlertChange(alert, "DELETE");
    }
}
