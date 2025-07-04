package com.osttra.csm.reconciliation.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "alert_config")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertConfigEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "name")
    private String name;

    @Column(name = "comp_type")
    private String compType;

    @Column(name = "rule")
    private String rule;

    @Column(name = "is_enable")
    private Boolean isEnable;

    @Column(name = "system_type")
    private String systemType;

    @Column(name = "system_name")
    private String systemName;


}
