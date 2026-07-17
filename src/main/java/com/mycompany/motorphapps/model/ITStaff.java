package com.mycompany.motorphapps.model;

/**
 *
 * @author DAYANG GWAPA
 */
public class ITStaff extends Employee {

    public ITStaff(String employeeId, String firstName, String lastName) {
        super(employeeId, firstName, lastName);
    }

    @Override
    public String getRole() { return "IT"; }

    @Override
    public Role getPermissions() { return Role.IT; }
}
