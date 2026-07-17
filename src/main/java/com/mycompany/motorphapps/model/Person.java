package com.mycompany.motorphapps.model;

/**
 *
 * @author DAYANG GWAPA
 */
public abstract class Person {

    private String employeeId;
    private String firstName;
    private String lastName;


    public Person(String employeeId, String firstName, String lastName) {
        this.employeeId = employeeId;
        this.firstName  = firstName;
        this.lastName   = lastName;
    }

    public Person(String employeeId) {
        this(employeeId, "", "");
    }

    public abstract String getRole();

    public abstract Role getPermissions();

    public String getDisplayName() {
        return lastName + ", " + firstName;
    }

    public String getDisplayName(boolean useFirstNameFirst) {
        return useFirstNameFirst
                ? firstName + " " + lastName
                : getDisplayName();
    }


    public String getEmployeeId() { return employeeId; }
    public void   setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getFirstName() { return firstName; }
    public void   setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void   setLastName(String lastName) { this.lastName = lastName; }

    @Override
    public String toString() {
        return "[" + getRole() + "] " + getDisplayName() + " (ID: " + employeeId + ")";
    }
}
