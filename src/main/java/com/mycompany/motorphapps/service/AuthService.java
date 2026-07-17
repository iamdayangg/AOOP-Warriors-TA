package com.mycompany.motorphapps.service;

import com.mycompany.motorphapps.dao.Authenticator;
import com.mycompany.motorphapps.model.Person;
import com.mycompany.motorphapps.model.Role;
import com.mycompany.motorphapps.security.OTPService;
import java.util.ArrayList;
import java.util.List;

public class AuthService {

    private final Authenticator authenticator = new Authenticator();
    private final OTPService    otpService    = new OTPService();
    private Person pendingUser;

    // ── Simple login (direct, no 2FA) ────────────────────────────────────────
    public Person login(String username, String password) {
        return authenticator.login(username, password);
    }

    // ── Two-factor: Step 1 ───────────────────────────────────────────────────
    public boolean loginStep1(String username, String password) {
        Person user = authenticator.login(username, password);
        if (user != null) {
            pendingUser = user;
            otpService.generateOTP(username);
            return true;
        }
        return false;
    }

    // ── Two-factor: Step 2 ───────────────────────────────────────────────────
    public Person loginStep2(String username, String otp) {
        if (pendingUser != null && otpService.verifyOTP(username, otp)) {
            Person user = pendingUser;
            pendingUser = null;
            return user;
        }
        return null;
    }

    // ── Menu visibility per role ─────────────────────────────────────────────
    public List<String> getAllowedMenus(String roleStr) {
        Role role = Role.fromString(roleStr);
        List<String> menus = new ArrayList<>();

        if (role.canViewAttendance())  menus.add("attendance");
        if (role.canViewEmployees())   menus.add("employees");
        if (role.canManageAccounts())  menus.add("accounts");
        if (role.canViewLeave())       menus.add("leave");

        if (role.canProcessPayroll()) {
            menus.add("payroll");       // Finance / Admin: full payroll module
        } else if (role.canViewPayroll()) {
            menus.add("payslip");       // Everyone else: own payslip only
        }

        return menus;
    }
}