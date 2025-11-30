package com.osttra.csm.reconciliation.config;


import com.osttra.csm.reconciliation.constants.MapContextHolderCons;
import com.osttra.csm.reconciliation.constants.RoleType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is created to store JWT context for a particular thread
 */
@Slf4j
public final class MapContextHolder {

    private static final ThreadLocal<Map<String, String>> mapContextHolderStore = new ThreadLocal<>();

    //adding private constructor to hide public one
    private MapContextHolder() {

    }

    public static void clearContext() {
        mapContextHolderStore.remove();
    }

    public static String getJwtToken() {
        return mapContextHolderStore.get().get(MapContextHolderCons.JWT_TOKEN);
    }

    public static String getUserPermissions() {
        return mapContextHolderStore.get().get(MapContextHolderCons.USER_PERMISSIONS_HEADER);
    }

    public static String getUserRole() {
        return mapContextHolderStore.get().get(MapContextHolderCons.USER_ROLES_HEADER);
    }

    public static String getUserRolesType() {
        return mapContextHolderStore.get().get(MapContextHolderCons.USER_ROLES_TYPE_HEADER);
    }

    public static String getOrgId() {
        return mapContextHolderStore.get().get(MapContextHolderCons.ORG_ID_HEADER);
    }

    public static String getUserId() {
        return mapContextHolderStore.get().get(MapContextHolderCons.USER_ID_HEADER);
    }

    public static void addContext(String key, String value) {
        if (key != null && value != null) {
            Map<String, String> context = new HashMap<>();
            if (mapContextHolderStore.get() != null) {
                context = mapContextHolderStore.get();
            }
            context.put(key, value);
            mapContextHolderStore.set(context);
        }
    }

    //this may return false, in case this is a system generated event ie. Kafka
    public static Boolean isSet() {
        return mapContextHolderStore.get() != null && !mapContextHolderStore.get().isEmpty();
    }

    public static boolean isRoleType(RoleType roleType) {
        var isAdmin = false;
        try {
            String roleTypes = mapContextHolderStore.get().get(MapContextHolderCons.USER_ROLES_TYPE_HEADER);
            isAdmin = roleType.toString().equalsIgnoreCase(roleTypes);
        } catch (Exception e) {
            log.error("Exception while determining isRoleType {}?", roleType, e);
            throw new AccessDeniedException("Unable to determine user role ", e);
        }
        return isAdmin;
    }

    public static String getCognitoToken() {
        return mapContextHolderStore.get().get(MapContextHolderCons.COGNITO_TOKEN);
    }
}
