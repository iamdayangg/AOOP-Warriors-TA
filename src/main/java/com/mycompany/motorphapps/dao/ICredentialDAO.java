package com.mycompany.motorphapps.dao;

import java.util.List;

/**
 * Contract for credential data access operations.
 */
public interface ICredentialDAO {

    boolean createUser(String employeeId, String password, int roleId);

    boolean updatePassword(String employeeId, String newPassword);

    boolean updateRole(String employeeId, int newRoleId);

    boolean deleteUser(String employeeId);

    boolean exists(String employeeId);

    List<String[]> getAllCredentials();
}
