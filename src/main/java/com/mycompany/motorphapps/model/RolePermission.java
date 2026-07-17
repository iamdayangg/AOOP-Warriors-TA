package com.mycompany.motorphapps.model;

public interface RolePermission {

    boolean canViewEmployees();
    boolean canEditEmployees();

    boolean canViewAttendance();
    boolean canDeleteAttendance();

    boolean canViewLeave();
    boolean canManageLeave();

    boolean canViewPayroll();
    boolean canProcessPayroll();

    boolean canManageAccounts();

    String getRoleName();
}
