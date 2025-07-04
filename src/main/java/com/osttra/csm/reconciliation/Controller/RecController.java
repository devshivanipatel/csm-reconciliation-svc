package com.osttra.csm.reconciliation.Controller;

import com.osttra.csm.reconciliation.Dto.openAlertDTO;
import com.osttra.csm.reconciliation.client.RouteClient;
import com.osttra.csm.reconciliation.entity.CacheStatusResult;
import com.osttra.csm.reconciliation.repository.OpenAlertProjection;
import com.osttra.csm.reconciliation.service.RecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reconciliation/v1/")
//@Slf4j
public class RecController {


    @GetMapping("/hi")
    public String getCacheKeysStatus1() {
        return "hello";
    }
    @Autowired
    private RouteClient routeClient;
    @Autowired
    private RecService recService;

    /*@Operation(summary = "Get Cache Keys Metrics", description = "Input format - metricId_address" +
            "e.g. 53cd81aa-3db6-4d8a-a70e-74f6d3d0000a_ue1u-sh-ic1.uat-trm.us")
    @PreAuthorize("hasAnyAuthority('it.dashboard','mobo.dashboard','csm_service_permission')")*/

   @GetMapping("/cache/keys/status")
    public List<CacheStatusResult> getCacheKeysStatus(@RequestParam List<String> keys) {

        routeClient.getCacheKeysStatus(keys);
       return null;
    }


    @GetMapping("/allOpenAlerts")
    public List<OpenAlertProjection> getAllOpenAlerts() {
        return recService.getAllOpenAlerts();
    }
}
