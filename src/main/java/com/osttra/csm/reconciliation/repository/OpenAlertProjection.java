package com.osttra.csm.reconciliation.repository;

public interface OpenAlertProjection { // Choose a meaningful name for your projection


    String getId(); // Maps to 'id' alias
    String getCompType(); // Maps to 'compType' alias
    String getRule();     // Maps to 'rule' alias
    Boolean getIsEnabled(); // Maps to 'isEnable' alias (Note: Boolean object for nullability)
    String getSystemType(); // Maps to 'systemType' alias
    String getComponentAddress(); // Maps to 'componentAddress' alias
    String getRedTime();   // Maps to 'redTime' alias
    String getAmberTime(); // Maps to 'amberTime' alias
}
