package com.mycompany.motorphapps.model;

/**
 *
 * @author DAYANG GWAPA
 */
public class Finance extends Employee {
 
    public Finance(String employeeId, String firstName, String lastName) {
        super(employeeId, firstName, lastName);
    }
 
    @Override
    public String getRole() {
        return "FINANCE";
    }
 
    @Override
    public Role getPermissions() {
        return Role.FINANCE;
    }
}
 