package com.itau.antifraud.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.itau.antifraud.listener.AlertChangeListener;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@EntityListeners({AuditingEntityListener.class, AlertChangeListener.class})
@Table(name = "alerts")
public class Alert {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "confidence")
    private double confidence;
    
    @Column(name = "is_spam")
    private boolean isSpam;
    
    @Column(name = "subject")
    private String subject;
    
    @Column(name = "time_detected")
    private LocalDateTime timeDetected;
    
    @Column(name = "email_content", columnDefinition = "TEXT")
    private String emailContent;
    
    @CreatedDate
    @Column(name = "created_date")
    private LocalDateTime createdDate;
    
    @LastModifiedDate
    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;
    
    @Version
    private Long version;
    
    // Constructors
    public Alert() {}
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }
    
    public boolean getIsSpam() { return isSpam; }
    public void setIsSpam(boolean isSpam) { this.isSpam = isSpam; }
    
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    
    public LocalDateTime getTimeDetected() { return timeDetected; }
    public void setTimeDetected(LocalDateTime timeDetected) { this.timeDetected = timeDetected; }
    
    public String getEmailContent() { return emailContent; }
    public void setEmailContent(String emailContent) { this.emailContent = emailContent; }
    
    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
    
    public LocalDateTime getLastModifiedDate() { return lastModifiedDate; }
    public void setLastModifiedDate(LocalDateTime lastModifiedDate) { this.lastModifiedDate = lastModifiedDate; }
    
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
