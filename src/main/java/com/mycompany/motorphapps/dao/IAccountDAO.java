package com.mycompany.motorphapps.dao;

import java.util.List;

/**
 * Contract for account (credential) data access operations.
 */
public interface IAccountDAO {

    List<String[]> getAllAccounts();

    void addAccount(String employeeId, String password, int roleId);

    void updateAccount(String employeeId, String newPassword, int newRoleId);

    void deleteAccount(String employeeId);
}
