package com.osttra.csm.reconciliation.Dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class openAlertDTO {
    private String id;
    private String compType;
    private String rule;
    private boolean enable;
    private String systemType;
    private String componentAddress;
    private String redTime;
    private String amberTime;
}
