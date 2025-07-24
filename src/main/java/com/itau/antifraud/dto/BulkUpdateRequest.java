package com.itau.antifraud.dto;

import java.util.List;

public class BulkUpdateRequest {
    private List<Long> ids;
    private boolean isSpam;

    // Constructors
    public BulkUpdateRequest() {}

    public BulkUpdateRequest(List<Long> ids, boolean isSpam) {
        this.ids = ids;
        this.isSpam = isSpam;
    }

    // Getters and Setters
    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public boolean getIsSpam() {
        return isSpam;
    }

    public void setIsSpam(boolean isSpam) {
        this.isSpam = isSpam;
    }
}
