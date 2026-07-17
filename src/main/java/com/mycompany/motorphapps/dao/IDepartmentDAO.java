package com.mycompany.motorphapps.dao;

import java.util.List;

/**
 * Contract for department data access operations.
 */
public interface IDepartmentDAO {

    List<String> getDepartmentNames();

    List<String[]> getAllDepartments();

    List<String[]> getEmployeesByDepartment(String departmentName);

    boolean renameDepartment(String oldName, String newName);

    boolean deleteDepartment(String departmentName);
}
