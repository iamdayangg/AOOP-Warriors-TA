package com.mycompany.motorphapps.dao;

import com.mycompany.motorphapps.config.DBConnection;
import com.mycompany.motorphapps.payroll.GovernmentContributionCalculator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompensationDAO implements ICompensationDAO {

    public List<String[]> getBenefitsForEmployee(String employeeId) {

        List<String[]> list = new ArrayList<>();

        String sql =
            "SELECT b.benefit_name, cd.amount " +
            "FROM compensation c " +
            "JOIN compensation_details cd " +
            "ON c.compensation_id = cd.compensation_id " +
            "JOIN benefits b " +
            "ON cd.benefit_id = b.benefit_id " +
            "WHERE c.employee_id = ? " +
            "ORDER BY b.benefit_id";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, employeeId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    list.add(new String[] {
                        rs.getString("benefit_name"),
                        rs.getString("amount")
                    });
                }
            }

        } catch (Exception e) {

            System.err.println(
                "[CompensationDAO] getBenefitsForEmployee failed: "
                + e.getMessage()
            );

            e.printStackTrace();
        }

        return list;
    }


    public List<String[]> getAllCompensation() {

        List<String[]> list = new ArrayList<>();

        String sql =
            "SELECT c.employee_id, " +
            "       CONCAT(e.first_name, ' ', e.last_name) AS full_name, " +
            "       c.basic_salary, " +
            "       c.semi_monthly_rate, " +
            "       c.hourly_rate, " +
            "       MAX(CASE WHEN b.benefit_name = 'Rice Subsidy' THEN cd.amount ELSE 0 END) AS rice, " +
            "       MAX(CASE WHEN b.benefit_name = 'Phone Allowance' THEN cd.amount ELSE 0 END) AS phone, " +
            "       MAX(CASE WHEN b.benefit_name = 'Clothing Allowance' THEN cd.amount ELSE 0 END) AS clothing " +
            "FROM compensation c " +
            "JOIN employees e " +
            "ON c.employee_id = e.employee_id " +
            "LEFT JOIN compensation_details cd " +
            "ON c.compensation_id = cd.compensation_id " +
            "LEFT JOIN benefits b " +
            "ON cd.benefit_id = b.benefit_id " +
            "GROUP BY c.compensation_id " +
            "ORDER BY c.employee_id";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                list.add(new String[] {
                    rs.getString("employee_id"),
                    rs.getString("full_name"),
                    rs.getString("basic_salary"),
                    rs.getString("semi_monthly_rate"),
                    rs.getString("hourly_rate"),
                    rs.getString("rice"),
                    rs.getString("phone"),
                    rs.getString("clothing")
                });
            }

        } catch (Exception e) {

            System.err.println(
                "[CompensationDAO] getAllCompensation failed: "
                + e.getMessage()
            );

            e.printStackTrace();
        }

        return list;
    }


    public void syncFromEmployee(
            String employeeId,
            double basicSalary,
            double semiMonthly,
            double hourlyRate,
            double rice,
            double phone,
            double clothing
    ) {

        try (Connection conn = DBConnection.getConnection()) {

            int compensationId =
                getCompensationId(conn, employeeId);

            if (compensationId == -1) {

                String insertSql =
                    "INSERT INTO compensation " +
                    "(employee_id, basic_salary, semi_monthly_rate, hourly_rate) " +
                    "VALUES (?, ?, ?, ?)";

                try (
                    PreparedStatement ps =
                        conn.prepareStatement(
                            insertSql,
                            Statement.RETURN_GENERATED_KEYS
                        )
                ) {

                    ps.setString(1, employeeId);
                    ps.setDouble(2, basicSalary);
                    ps.setDouble(3, semiMonthly);
                    ps.setDouble(4, hourlyRate);

                    ps.executeUpdate();

                    try (ResultSet keys = ps.getGeneratedKeys()) {

                        if (keys.next()) {
                            compensationId = keys.getInt(1);
                        }
                    }
                }

            } else {


                String updateSql =
                    "UPDATE compensation " +
                    "SET basic_salary = ?, " +
                    "    semi_monthly_rate = ?, " +
                    "    hourly_rate = ? " +
                    "WHERE compensation_id = ?";

                try (
                    PreparedStatement ps =
                        conn.prepareStatement(updateSql)
                ) {

                    ps.setDouble(1, basicSalary);
                    ps.setDouble(2, semiMonthly);
                    ps.setDouble(3, hourlyRate);
                    ps.setInt(4, compensationId);

                    ps.executeUpdate();
                }
            }



            upsertDetail(conn, compensationId, 1, rice);


            upsertDetail(conn, compensationId, 2, phone);


            upsertDetail(conn, compensationId, 3, clothing);

            System.out.println(
                "[CompensationDAO] syncFromEmployee success: "
                + employeeId
            );

        } catch (Exception e) {

            System.err.println(
                "[CompensationDAO] syncFromEmployee failed: "
                + e.getMessage()
            );

            e.printStackTrace();
        }
    }

    public double getSSSContribution(double monthlySalary) {
        return GovernmentContributionCalculator.calculateSSS(monthlySalary);
    }

    public double getPhilHealthContribution(double monthlySalary) {
        return GovernmentContributionCalculator.calculatePhilHealth(monthlySalary);
    }

    public double getPagibigContribution(double monthlySalary) {
        return GovernmentContributionCalculator.calculatePagibig(monthlySalary);
    }


    private int getCompensationId(
            Connection conn,
            String employeeId
    ) throws SQLException {

        String sql =
            "SELECT compensation_id " +
            "FROM compensation " +
            "WHERE employee_id = ? " +
            "LIMIT 1";

        try (
            PreparedStatement ps =
                conn.prepareStatement(sql)
        ) {

            ps.setString(1, employeeId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt("compensation_id");
                }
            }
        }

        return -1;
    }


    private void upsertDetail(
            Connection conn,
            int compensationId,
            int benefitId,
            double amount
    ) throws SQLException {

        String sql =
            "INSERT INTO compensation_details " +
            "(compensation_id, benefit_id, amount) " +
            "VALUES (?, ?, ?) " +
            "ON DUPLICATE KEY UPDATE amount = VALUES(amount)";

        try (
            PreparedStatement ps =
                conn.prepareStatement(sql)
        ) {

            ps.setInt(1, compensationId);
            ps.setInt(2, benefitId);
            ps.setDouble(3, amount);

            ps.executeUpdate();
        }
    }
}