package com.itau.antifraud.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AlertDTO {
    private double confidence;
    
    @JsonProperty("is_spam")
    private boolean is_spam;
    
    private String subject;
    
    @JsonProperty("time_detected")
    private String time_detected;
    
    @JsonProperty("email_content")
    private String email_content;

    // Getters and Setters
    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }

    public boolean is_is_spam() { return is_spam; }
    public void setIs_spam(boolean is_spam) { this.is_spam = is_spam; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getTime_detected() { return time_detected; }
    public void setTime_detected(String time_detected) { this.time_detected = time_detected; }
    
    public String getEmail_content() { return email_content; }
    public void setEmail_content(String email_content) { this.email_content = email_content; }
}
