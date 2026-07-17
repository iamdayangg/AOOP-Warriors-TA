package com.mycompany.motorphapps.dao;

import java.util.List;

/**
 * Contract for employee data access operations.
 */
public interface IEmployeeDAO {

    List<String[]> getAllEmployees();

    String[] getEmployeeById(String employeeId);

    void addEmployee(String[] data);

    void updateEmployee(String[] data);

    void deleteEmployee(String id);

    boolean employeeExists(String id);
}
