package com.osttra.csm.reconciliation.service;

import com.osttra.csm.reconciliation.Dto. openAlertDTO;
import com.osttra.csm.reconciliation.entity.AlertConfigEntity;
import com.osttra.csm.reconciliation.repository.OpenAlertProjection;
import com.osttra.csm.reconciliation.repository.RecRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class RecServiceImpl implements RecService {

    @Autowired
    private RecRepository recRepository;

    @Override
    public List<OpenAlertProjection> getAllOpenAlerts() {

//        List<Object[]> rawResults = recRepository.findActiveAlertDetails();
//        System.out.println("inside  service recRepository"+rawResults);
//        List<openAlertDTO> mappedResults = new ArrayList<>();
//        for (Object[] row : rawResults) {
//            openAlertDTO dto = new openAlertDTO();
//
//            dto.setId(Objects.toString(row[0], null));
//
//            dto.setCompType(Objects.toString(row[1], null));
//
//            dto.setRule(Objects.toString(row[2], null));
//
//            dto.setEnable((Boolean) row[3]);
//
//            dto.setSystemType(Objects.toString(row[4], null));
//
//            dto.setComponentAddress(Objects.toString(row[5], null));
//
//            dto.setRedTime(Objects.toString(row[6]) ); // Cast directly if DB maps to LocalDateTime
//
//            dto.setAmberTime(Objects.toString(row[7])); // Cast directly if DB maps to LocalDateTime
//
//            mappedResults.add(dto);
//        }

        return recRepository.findActiveAlertDetails();
    }

    @Override
    public List<AlertConfigEntity> getAll() {
        return List.of();
    }
}
