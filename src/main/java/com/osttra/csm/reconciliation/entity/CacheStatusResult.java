package com.osttra.csm.reconciliation.entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
//move to parent model
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CacheStatusResult {

    private String key;
    private Object value;

}
