package com.mycompany.motorphapps.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mycompany.motorphapps.dao.PayrollDAO;

public class ReportDataSource implements JRDataSource {

    private final List<String[]> data;
    private final Map<String, String> deptMap;
    private final Map<String, String> nameMap;
    private final Map<String, String> positionMap; // employeeId -> position
    private final PayrollDAO payrollDAO = new PayrollDAO();

    private int index = -1;

    // Cache of employeeId -> deduction breakdown, computed lazily per row
    // to avoid re-querying getDeductionsByPayrollId() for every field access.
    private final Map<Integer, Map<String, Double>> deductionCache = new HashMap<>();

    public ReportDataSource(List<String[]> data,
                             Map<String, String> deptMap,
                             Map<String, String> nameMap,
                             Map<String, String> positionMap) {
        this.data = data;
        this.deptMap = deptMap;
        this.nameMap = nameMap;
        this.positionMap = positionMap;
    }

    @Override
    public boolean next() throws JRException {
        index++;
        return index < data.size();
    }

    @Override
    public Object getFieldValue(JRField jrField) throws JRException {
        String[] row = data.get(index);
        String empId = row[1]; // Employee ID
        String fieldName = jrField.getName();

        // 1. Text Properties Mapping
        if ("empNo".equals(fieldName)) {
            return empId;
        }
        if ("empName".equals(fieldName)) {
            return nameMap.getOrDefault(empId, "Unknown Employee");
        }
        if ("department".equals(fieldName)) {
            return deptMap.getOrDefault(empId, "Unassigned");
        }
        if ("position".equals(fieldName)) {
            return positionMap.getOrDefault(empId, "");
        }

        // 2. Financial Numeric Computations
        try {
            if ("grossIncome".equals(fieldName)) {
                return Double.parseDouble(row[6].trim());
            }
            if ("netPay".equals(fieldName)) {
                return Double.parseDouble(row[8].trim());
            }

            Map<String, Double> deductions = getDeductionsForRow(row);

            if ("sssContribution".equals(fieldName)) {
                return deductions.get("sss");
            }
            if ("philhealthContribution".equals(fieldName)) {
                return deductions.get("philhealth");
            }
            if ("pagibigContribution".equals(fieldName)) {
                return deductions.get("pagibig");
            }
            if ("withholdingTax".equals(fieldName)) {
                return deductions.get("tax");
            }
        } catch (Exception e) {
            return 0.0; // Fail-safe: empty/broken fields resolve to 0.0 instead of null
        }

        String[] govIds = payrollDAO.getGovernmentIds(empId);
        if (govIds != null && govIds.length >= 4) {
            if ("sssNo".equals(fieldName)) return govIds[0];
            if ("philhealthNo".equals(fieldName)) return govIds[1];
            if ("tin".equals(fieldName)) return govIds[2];
            if ("pagibigNo".equals(fieldName)) return govIds[3];
        }

        return null;
    }

    /**
     * Looks up the real deduction breakdown for this row's payroll_id,
     * caching per employee so repeated field lookups on the same row
     * don't re-hit the database. Mirrors ReportService.extractDeductions()
     * so Payslip and Department Summary always agree on the same numbers.
     */
    private Map<String, Double> getDeductionsForRow(String[] row) {

    int payrollId;

    try {
        payrollId = Integer.parseInt(row[0].trim());
    } catch (Exception e) {
        return ReportService.extractDeductions(null);
    }

    if (deductionCache.containsKey(payrollId)) {
        return deductionCache.get(payrollId);
    }

    Map<String, Double> result;

    try {
        List<String[]> rows = payrollDAO.getDeductionsByPayrollId(payrollId);
        result = ReportService.extractDeductions(rows);
    } catch (Exception e) {
        result = ReportService.extractDeductions(null);
    }

    deductionCache.put(payrollId, result);
    return result;
}
    
}