package com.mycompany.motorphapps.model;
 
public enum Role implements RolePermission {
 
    ADMIN {
        @Override public boolean canViewEmployees()    { return true; }
        @Override public boolean canEditEmployees()    { return true; }
        @Override public boolean canViewAttendance()   { return true; }
        @Override public boolean canDeleteAttendance() { return true; }
        @Override public boolean canViewLeave()        { return true; }
        @Override public boolean canManageLeave()      { return true; }
        @Override public boolean canViewPayroll()      { return true; }
        @Override public boolean canProcessPayroll()   { return true; }
        @Override public boolean canManageAccounts()   { return true; }
        @Override public String  getRoleName()         { return "ADMIN"; }
    },
 
    HR {
        @Override public boolean canViewEmployees()    { return true; }
        @Override public boolean canEditEmployees()    { return true; }
        @Override public boolean canViewAttendance()   { return true; }
        @Override public boolean canDeleteAttendance() { return false; }
        @Override public boolean canViewLeave()        { return true; }
        @Override public boolean canManageLeave()      { return true; }
        @Override public boolean canViewPayroll()      { return false; }
        @Override public boolean canProcessPayroll()   { return false; }
        @Override public boolean canManageAccounts()   { return false; }
        @Override public String  getRoleName()         { return "HR"; }
    },
 
    FINANCE {
        @Override public boolean canViewEmployees()    { return false; }
        @Override public boolean canEditEmployees()    { return false; }
        @Override public boolean canViewAttendance()   { return true; }
        @Override public boolean canDeleteAttendance() { return false; }
        @Override public boolean canViewLeave()        { return false; }
        @Override public boolean canManageLeave()      { return false; }
        @Override public boolean canViewPayroll()      { return true; }
        @Override public boolean canProcessPayroll()   { return true; }
        @Override public boolean canManageAccounts()   { return false; }
        @Override public String  getRoleName()         { return "FINANCE"; }
    },
 
    IT {
        @Override public boolean canViewEmployees()    { return true; }
        @Override public boolean canEditEmployees()    { return false; }
        @Override public boolean canViewAttendance()   { return true; }
        @Override public boolean canDeleteAttendance() { return false; }
        @Override public boolean canViewLeave()        { return false; }
        @Override public boolean canManageLeave()      { return false; }
        @Override public boolean canViewPayroll()      { return true; }
        @Override public boolean canProcessPayroll()   { return false; }
        @Override public boolean canManageAccounts()   { return true; }
        @Override public String  getRoleName()         { return "IT"; }
    },
 
    EMPLOYEE {
        @Override public boolean canViewEmployees()    { return false; }
        @Override public boolean canEditEmployees()    { return false; }
        @Override public boolean canViewAttendance()   { return true; }
        @Override public boolean canDeleteAttendance() { return false; }
        @Override public boolean canViewLeave()        { return true; }
        @Override public boolean canManageLeave()      { return false; }
        @Override public boolean canViewPayroll()      { return true; }
        @Override public boolean canProcessPayroll()   { return false; }
        @Override public boolean canManageAccounts()   { return false; }
        @Override public String  getRoleName()         { return "EMPLOYEE"; }
    };

    public static Role fromString(String roleStr) {
        if (roleStr == null) return EMPLOYEE;
        switch (roleStr.trim().toUpperCase()) {
            case "ADMIN":   return ADMIN;
            case "HR":      return HR;
            case "FINANCE": return FINANCE;
            case "IT":      return IT;
            default:        return EMPLOYEE;
        }
    }
}