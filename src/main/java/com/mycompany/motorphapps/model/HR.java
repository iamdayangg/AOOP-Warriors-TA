
package com.mycompany.motorphapps.model;
/**
 *
 * @author DAYANG GWAPA
 */
public class HR extends Employee {
 
    public HR(String employeeId, String firstName, String lastName) {
        super(employeeId, firstName, lastName);
    }
 
    @Override
    public String getRole() {
        return "HR";
    }
 
    @Override
    public Role getPermissions() {
        return Role.HR;
    }
}
 