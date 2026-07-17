package com.mycompany.motorphapps.model;

public class Admin extends Employee {

    public Admin(String employeeId, String firstName, String lastName) {
        super(employeeId, firstName, lastName);
    }

    @Override
    public String getRole() { return "ADMIN"; }

    @Override
    public Role getPermissions() { return Role.ADMIN; }
}
