package com.osttra.csm.reconciliation.service;

import com.osttra.csm.reconciliation.Dto.openAlertDTO;
import com.osttra.csm.reconciliation.entity.AlertConfigEntity;
import com.osttra.csm.reconciliation.repository.OpenAlertProjection;

import java.util.List;

public interface RecService {

    List<OpenAlertProjection> getAllOpenAlerts();
    List<AlertConfigEntity> getAll();
}
