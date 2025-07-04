package com.osttra.csm.reconciliation.repository;


import com.osttra.csm.reconciliation.entity.AlertConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecRepository extends JpaRepository<AlertConfigEntity, String> {

    @Query(value = "SELECT " +
            "    ac.id as id,ac.comp_type AS compType, " +
            "    ac.\"rule\" AS rule, " +
            "    ac.is_enable AS isEnable, " +
            "    ac.system_type AS systemType, " +
            "    at2.component_address AS componentAddress, " +
            "    at2.red_time AS redTime, " +
            "    at2.amber_time AS amberTime " +
            "FROM " +
            "    alert_transactions at2 " +
            "JOIN " +
            "    alert_config ac ON ac.id = at2.alert_config_id " ,
//            +
//            "WHERE " +
//            "    at2.green_time IS NULL",
            nativeQuery = true)
    List<OpenAlertProjection>  findActiveAlertDetails();

}

