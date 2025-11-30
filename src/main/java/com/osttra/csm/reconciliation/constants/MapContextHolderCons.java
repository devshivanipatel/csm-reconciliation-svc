package com.osttra.csm.reconciliation.constants;

import lombok.Data;

@Data
public final class MapContextHolderCons {

    public static final String JWT_TOKEN = "jwtToken";
    public static final String USER_PERMISSIONS_HEADER = "userRoles";
    public static final String USER_ROLES_HEADER = "roles";
    public static final String ORG_ID_HEADER = "ORG-ID";
    public static final String USER_ID_HEADER = "USER-ID";
    public static final String USER_ROLES_TYPE_HEADER = "roleType";
    public static final String COGNITO_TOKEN = "cognitoToken";

    //adding private constructor to hide public one
    private MapContextHolderCons() {

    }
}
