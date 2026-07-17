package com.mycompany.motorphapps.ui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import com.mycompany.motorphapps.service.EmployeeService;


public class AllEmployeesPanel extends JPanel {

    private DefaultTableModel model;
    private JTable            table;
    private JEditorPane       detailPane;
    private JTextField        searchField;
    private EmployeeService   employeeService;


    private final String[] fullColumns = {
        "Employee #",         
        "Last Name",           
        "First Name",         
        "Birthday",            
        "Address",             
        "Phone Number",       
        "SSS #",               
        "Philhealth #",        
        "TIN #",               
        "Pag-ibig #",         
        "Status",              
        "Position",            
        "Immediate Supervisor",
        "Basic Salary",       
        "Rice Subsidy",        
        "Phone Allowance",    
        "Clothing Allowance",  
        "Gross Semi-monthly Rate", 
        "Hourly Rate",         
        "Gender"              
    };

    private final String[] hints = {
        "5 digits e.g. 10001", "e.g. Dela Cruz", "e.g. Juan",
        "MM/DD/YYYY", "Full address", "09XXXXXXXXX",
        "##-#######-#", "####-####-####", "###-###-###",
        "####-####-####", "Regular / Probationary", "e.g. Software Engineer",
        "e.g. Garcia, Manuel III", "e.g. 30000", "e.g. 1500",
        "e.g. 2000", "e.g. 1000", "e.g. 15000", "e.g. 357.14",
        "Male / Female"
    };

    private final boolean[] required = {
        true, true, true, true, true, true, true, true, true, true,
        true, true, false, true, true, true, true, true, true, true
    };

    public AllEmployeesPanel(String role) {
        employeeService = new EmployeeService();

        setLayout(new BorderLayout(0, 0));
        setBackground(UIStyle.BACKGROUND);

        add(buildTopBar(),          BorderLayout.NORTH);
        add(buildContentArea(role), BorderLayout.CENTER);
        add(buildButtonBar(role),   BorderLayout.SOUTH);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { filter(); }
            public void removeUpdate(DocumentEvent e)  { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
        });
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(UIStyle.PRIMARY);
        bar.setBorder(new EmptyBorder(16, 24, 16, 24));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);

        JLabel icon = new JLabel("👥");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        icon.setForeground(Color.WHITE);

        JLabel title = new JLabel("Employee Directory");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        left.add(icon);
        left.add(title);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);

        searchField = new JTextField(20);
        searchField.setFont(UIStyle.FONT_BODY);
        // Use a light background so typed text is visible
        searchField.setBackground(new Color(255, 255, 255));
        searchField.setForeground(UIStyle.TEXT_PRIMARY);
        searchField.setCaretColor(UIStyle.TEXT_PRIMARY);
        searchField.setBorder(new CompoundBorder(
                new LineBorder(new Color(255, 255, 255, 120), 1, true),
                new EmptyBorder(6, 10, 6, 10)));

        final String PLACEHOLDER = "Search employees...";
        searchField.setText(PLACEHOLDER);
        searchField.setForeground(UIStyle.TEXT_SECONDARY);

        searchField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals(PLACEHOLDER)) {
                    searchField.setText("");
                    searchField.setForeground(UIStyle.TEXT_PRIMARY);
                }
            }
            public void focusLost(FocusEvent e) {
                if (searchField.getText().trim().isEmpty()) {
                    searchField.setText(PLACEHOLDER);
                    searchField.setForeground(UIStyle.TEXT_SECONDARY);
                }
            }
        });

        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        searchIcon.setForeground(new Color(180, 210, 255));

        right.add(searchIcon);
        right.add(searchField);

        bar.add(left,  BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private JSplitPane buildContentArea(String role) {
        model = new DefaultTableModel(
                new Object[]{"Employee #", "Full Name", "Position", "Status"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        UIStyle.styleTable(table);

        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object value, boolean sel,
                    boolean focus, int row, int col) {

                JPanel cell = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 3));
                cell.setOpaque(true);
                cell.setBackground(sel ? UIStyle.PRIMARY_LIGHT
                        : row % 2 == 0 ? UIStyle.SURFACE : UIStyle.ROW_ALT);

                String status = value == null ? "" : value.toString();
                Color bg = "Regular".equalsIgnoreCase(status)
                        ? new Color(22, 130, 80) : new Color(200, 140, 0);
                cell.add(UIStyle.createBadge(status, bg, Color.WHITE));
                return cell;
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    row = table.convertRowIndexToModel(row);
                    showEmployeeDetails(getFullRowData(row));
                }
            }
        });

        loadEmployeeData();

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(new LineBorder(UIStyle.BORDER, 1, true));
        tableScroll.getViewport().setBackground(UIStyle.SURFACE);

        JPanel tablePanel = new JPanel(new BorderLayout(0, 8));
        tablePanel.setBackground(UIStyle.BACKGROUND);
        tablePanel.setBorder(new EmptyBorder(16, 24, 8, 12));
        tablePanel.add(tableScroll, BorderLayout.CENTER);

        detailPane = new JEditorPane();
        detailPane.setContentType("text/html");
        detailPane.setEditable(false);
        detailPane.setText(buildEmptyDetailHtml());

        JScrollPane detailScroll = new JScrollPane(detailPane);
        detailScroll.setBorder(new LineBorder(UIStyle.BORDER, 1, true));

        JPanel detailPanel = new JPanel(new BorderLayout(0, 8));
        detailPanel.setBackground(UIStyle.BACKGROUND);
        detailPanel.setBorder(new EmptyBorder(16, 12, 8, 24));

        JLabel detailTitle = new JLabel("Employee Profile");
        detailTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        detailTitle.setForeground(UIStyle.PRIMARY);
        detailTitle.setBorder(new EmptyBorder(0, 4, 8, 0));

        detailPanel.add(detailTitle,  BorderLayout.NORTH);
        detailPanel.add(detailScroll, BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT, tablePanel, detailPanel);
        split.setDividerLocation(500);
        split.setResizeWeight(0.55);
        split.setDividerSize(4);
        split.setBorder(null);
        split.setBackground(UIStyle.BACKGROUND);
        return split;
    }

    private JPanel buildButtonBar(String role) {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(UIStyle.BACKGROUND);
        outer.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, UIStyle.BORDER),
                new EmptyBorder(12, 24, 16, 24)));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);

        if (employeeService.canManageEmployees(role)) {
            JButton deleteBtn = UIStyle.createDangerButton(" Delete");
            JButton editBtn   = UIStyle.createSecondaryButton(" Edit");
            JButton addBtn    = UIStyle.createPrimaryButton("+ Add Employee");

            deleteBtn.addActionListener(e -> deleteSelected());
            editBtn.addActionListener(e   -> editSelected());
            addBtn.addActionListener(e    -> openEmployeeDialog(null));

            btnRow.add(deleteBtn);
            btnRow.add(editBtn);
            btnRow.add(addBtn);
        }

        outer.add(btnRow, BorderLayout.EAST);
        return outer;
    }

    private void loadEmployeeData() {
        model.setRowCount(0);
        for (String[] row : employeeService.getAllEmployees()) {
            if (row.length >= 3) {
                String id     = row[0];
                String name   = row[2] + " " + row[1];
                String pos    = row.length > 11 ? row[11] : "";
                String status = row.length > 10 ? row[10] : "";
                model.addRow(new Object[]{id, name, pos, status});
            }
        }
    }

    private void filter() {
        String text = searchField.getText();
        final String PLACEHOLDER = "Search employees...";
        if (text.equals(PLACEHOLDER)) {
            table.setRowSorter(null);
            return;
        }
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        sorter.setRowFilter(text.isEmpty() ? null : RowFilter.regexFilter("(?i)" + text));
        table.setRowSorter(sorter);
    }

    private String[] getFullRowData(int row) {
        String empId = model.getValueAt(row, 0).toString();
        for (String[] r : employeeService.getAllEmployees()) {
            if (r[0].equals(empId)) return r;
        }
        return new String[fullColumns.length];
    }

    private void showEmployeeDetails(String[] data) {
        StringBuilder html = new StringBuilder();
        html.append("<html><body style='font-family:Segoe UI,sans-serif;font-size:13px;")
            .append("color:#16223A;margin:10px;'>");

        String name = (data.length > 2 ? data[2] : "") + " " + (data.length > 1 ? data[1] : "");
        String pos  = data.length > 11 ? data[11] : "";
        html.append("<div style='background:#19558B;border-radius:8px;padding:14px 16px;margin-bottom:14px;'>")
            .append("<div style='color:white;font-size:16px;font-weight:bold;'>").append(name).append("</div>")
            .append("<div style='color:#99BBEE;font-size:12px;margin-top:4px;'>").append(pos).append("</div>")
            .append("</div>");

        appendSection(html, "Personal Information", new int[]{0,1,2,3,4,5},       data);
        appendSection(html, "Government IDs",       new int[]{6,7,8,9},           data);
        appendSection(html, "Employment",           new int[]{10,11,12,19},       data);
        appendSection(html, "Compensation",         new int[]{13,14,15,16,17,18}, data);

        html.append("</body></html>");
        detailPane.setText(html.toString());
        detailPane.setCaretPosition(0);
    }

    private void appendSection(StringBuilder html, String title,
                               int[] indices, String[] data) {
        html.append("<div style='margin-bottom:10px;'>")
            .append("<div style='font-size:11px;font-weight:bold;color:#6478A0;")
            .append("letter-spacing:1px;margin-bottom:6px;border-bottom:1px solid #E2E8F0;")
            .append("padding-bottom:4px;'>").append(title.toUpperCase()).append("</div>");

        html.append("<table style='width:100%;border-collapse:collapse;'>");
        for (int i : indices) {
            String label = i < fullColumns.length ? fullColumns[i] : "";
            String val   = i < data.length        ? data[i]        : "—";
            if (val == null || val.isBlank()) val = "—";
            html.append("<tr>")
                .append("<td style='color:#6478A0;font-size:12px;padding:3px 0;width:48%;'>")
                .append(label).append("</td>")
                .append("<td style='color:#16223A;font-size:12px;padding:3px 0;'>")
                .append(val).append("</td>")
                .append("</tr>");
        }
        html.append("</table></div>");
    }

    private String buildEmptyDetailHtml() {
        return "<html><body style='font-family:Segoe UI,sans-serif;color:#9AA3B8;"
             + "text-align:center;padding-top:60px;font-size:13px;'>"
             + "<div style='font-size:40px;'>👤</div>"
             + "<div style='margin-top:12px;'>Select an employee<br>to view their profile.</div>"
             + "</body></html>";
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an employee first.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        row = table.convertRowIndexToModel(row);
        openEmployeeDialog(getFullRowData(row));
    }

    private void deleteSelected() {
        System.out.println(">>> deleteSelected() called");
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an employee first.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        row = table.convertRowIndexToModel(row);
        String empID = model.getValueAt(row, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete employee record for ID: " + empID + "?\nThis action cannot be undone.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            System.out.println(">>> Confirmed delete for empID: " + empID);
            employeeService.deleteEmployee(empID);
            loadEmployeeData();
            detailPane.setText(buildEmptyDetailHtml());
            JOptionPane.showMessageDialog(this, "Employee record deleted.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            System.out.println(">>> Delete cancelled by user");
        }
    }

    private void openEmployeeDialog(String[] existingData) {
        boolean isEditing = existingData != null;

        JTextField[]      fields    = new JTextField[fullColumns.length];
        JLabel[]          errLabels = new JLabel[fullColumns.length];
        JComboBox<String> statusCombo = new JComboBox<>(
                new String[]{"Regular", "Probationary", "Contractual"});
        statusCombo.setFont(UIStyle.FONT_BODY);

        JComboBox<String> genderCombo = new JComboBox<>(
                new String[]{"Male", "Female", "Other"});
        genderCombo.setFont(UIStyle.FONT_BODY);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UIStyle.SURFACE);
        form.setBorder(new EmptyBorder(8, 8, 8, 8));

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(3, 8, 0, 8);

        int gRow = 0;
        for (int i = 0; i < fullColumns.length; i++) {
            // Label
            gc.gridx = 0; gc.gridy = gRow; gc.weightx = 0.35;
            JLabel lbl = new JLabel(fullColumns[i] + (required[i] ? " *" : ""));
            lbl.setFont(UIStyle.FONT_BODY);
            lbl.setForeground(required[i] ? UIStyle.TEXT_PRIMARY : UIStyle.TEXT_SECONDARY);
            form.add(lbl, gc);

            // Input
            gc.gridx = 1; gc.weightx = 0.65;
            String val = (existingData != null && i < existingData.length)
                    ? (existingData[i] != null ? existingData[i] : "")
                    : "";

            if (i == 10) {

                if (!val.isEmpty()) statusCombo.setSelectedItem(val);
                form.add(statusCombo, gc);
            } else if (i == 19) {

                if (!val.isEmpty()) genderCombo.setSelectedItem(val);
                form.add(genderCombo, gc);
            } else {
                fields[i] = new JTextField(val, 22);
                fields[i].setFont(UIStyle.FONT_BODY);
                fields[i].setForeground(UIStyle.TEXT_PRIMARY);
                fields[i].setBackground(Color.WHITE);
                fields[i].setCaretColor(UIStyle.TEXT_PRIMARY);
                fields[i].setToolTipText(hints[i]);
                fields[i].setBorder(new CompoundBorder(
                        new LineBorder(UIStyle.BORDER, 1, true),
                        new EmptyBorder(5, 8, 5, 8)));

                if (val.isEmpty()) {
                    fields[i].setForeground(UIStyle.TEXT_DISABLED);
                    fields[i].setText(hints[i]);
                    final int fi = i;
                    fields[i].addFocusListener(new FocusAdapter() {
                        public void focusGained(FocusEvent e) {
                            if (fields[fi].getText().equals(hints[fi])) {
                                fields[fi].setText("");
                                fields[fi].setForeground(UIStyle.TEXT_PRIMARY);
                            }

                            fields[fi].setBorder(new CompoundBorder(
                                    new LineBorder(UIStyle.BORDER, 1, true),
                                    new EmptyBorder(5, 8, 5, 8)));
                        }
                        public void focusLost(FocusEvent e) {
                            if (fields[fi].getText().trim().isEmpty()) {
                                fields[fi].setForeground(UIStyle.TEXT_DISABLED);
                                fields[fi].setText(hints[fi]);
                            }
                        }
                    });
                }

                if (i == 0 && isEditing) {
                    fields[i].setEditable(false);
                    fields[i].setBackground(new Color(240, 242, 248));
                    fields[i].setForeground(UIStyle.TEXT_SECONDARY);
                }

                form.add(fields[i], gc);
            }
            gRow++;

            gc.gridx = 1; gc.gridy = gRow; gc.weightx = 0.65;
            errLabels[i] = new JLabel(" ");
            errLabels[i].setFont(UIStyle.FONT_SMALL);
            errLabels[i].setForeground(UIStyle.DANGER);
            errLabels[i].setBorder(new EmptyBorder(0, 2, 2, 0));
            form.add(errLabels[i], gc);
            gRow++;
        }

        JScrollPane scroll = new JScrollPane(form);
        scroll.setPreferredSize(new Dimension(540, 500));
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        scroll.setBorder(null);

        JLabel heading = new JLabel(isEditing ? "  Edit Employee Record" : "+  Add New Employee");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 15));
        heading.setForeground(UIStyle.PRIMARY);
        heading.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, UIStyle.BORDER),
                new EmptyBorder(0, 0, 10, 0)));

        JPanel wrapper = new JPanel(new BorderLayout(0, 10));
        wrapper.setBackground(UIStyle.SURFACE);
        wrapper.setBorder(new EmptyBorder(4, 4, 4, 4));
        wrapper.add(heading, BorderLayout.NORTH);
        wrapper.add(scroll,  BorderLayout.CENTER);

        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(
                parentWindow,
                isEditing ? "Edit Employee" : "Add Employee",
                Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JButton okBtn     = UIStyle.createPrimaryButton(isEditing ? "Save Changes" : "Add Employee");
        JButton cancelBtn = UIStyle.createSecondaryButton("Cancel");

        cancelBtn.addActionListener(e -> dialog.dispose());

        okBtn.addActionListener(e -> {

            for (JLabel el : errLabels) {
                el.setText(" ");
                el.setForeground(UIStyle.DANGER);
            }

            boolean valid = true;
            String[] emp  = new String[fullColumns.length];

            for (int i = 0; i < fullColumns.length; i++) {
                if (i == 10) {
                    emp[i] = statusCombo.getSelectedItem().toString();
                } else if (i == 19) {
                    emp[i] = genderCombo.getSelectedItem().toString();
                } else {
                    String v = fields[i].getText().trim();
                    emp[i]   = v.equals(hints[i]) ? "" : v;
                }

                if (required[i] && emp[i].isEmpty()) {
                    errLabels[i].setText("⚠ This field is required");

                    if (i != 10 && i != 19 && fields[i] != null) {
                        fields[i].setBorder(new CompoundBorder(
                                new LineBorder(UIStyle.DANGER, 2, true),
                                new EmptyBorder(5, 8, 5, 8)));
                    }
                    valid = false;
                }

                if (!emp[i].isEmpty()) {
                    String err = validateField(i, emp[i]);
                    if (err != null) {
                        errLabels[i].setText("⚠ " + err);
                        if (i != 10 && i != 19 && fields[i] != null) {
                            fields[i].setBorder(new CompoundBorder(
                                    new LineBorder(UIStyle.DANGER, 2, true),
                                    new EmptyBorder(5, 8, 5, 8)));
                        }
                        valid = false;
                    }
                }
            }


            if (valid && !emp[3].isEmpty()) {
                try {
                    emp[3] = normalizeBirthday(emp[3]);
                } catch (DateTimeParseException ex) {
                    errLabels[3].setText("⚠ Use MM/DD/YYYY format");
                    if (fields[3] != null) {
                        fields[3].setBorder(new CompoundBorder(
                                new LineBorder(UIStyle.DANGER, 2, true),
                                new EmptyBorder(5, 8, 5, 8)));
                    }
                    valid = false;
                }
            }

            if (!valid) {
  
                scroll.getVerticalScrollBar().setValue(0);
                return; 
            }

            if (!isEditing && employeeService.employeeExists(emp[0])) {
                errLabels[0].setText("⚠ Employee number already exists");
                if (fields[0] != null) {
                    fields[0].setBorder(new CompoundBorder(
                            new LineBorder(UIStyle.DANGER, 2, true),
                            new EmptyBorder(5, 8, 5, 8)));
                }
                scroll.getVerticalScrollBar().setValue(0);
                return; 
            }

            try {
                if (!isEditing) {
                    employeeService.addEmployee(emp);
                } else {
                    employeeService.updateEmployee(emp);
                }
                loadEmployeeData();
                dialog.dispose(); 
                JOptionPane.showMessageDialog(
                        AllEmployeesPanel.this,
                        isEditing ? "Employee updated successfully." : "Employee added successfully.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {

                JOptionPane.showMessageDialog(dialog,
                        "Database error: " + ex.getMessage(),
                        "Save Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setBackground(UIStyle.SURFACE);
        btnRow.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, UIStyle.BORDER),
                new EmptyBorder(10, 8, 10, 8)));
        btnRow.add(cancelBtn);
        btnRow.add(okBtn);

        dialog.getContentPane().setLayout(new BorderLayout());
        dialog.getContentPane().add(wrapper, BorderLayout.CENTER);
        dialog.getContentPane().add(btnRow,  BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }


    private String validateField(int col, String value) {
        switch (col) {
            case 0: 
                if (!value.matches("\\d{5}"))
                    return "Must be exactly 5 digits (e.g. 10001)";
                break;
            case 3: 
                if (!value.matches("\\d{2}[/-]\\d{2}[/-]\\d{4}")
                        && !value.matches("\\d{4}-\\d{2}-\\d{2}"))
                    return "Use MM/DD/YYYY format";
                break;
            case 5: 
                if (!value.matches("[0-9\\-+() ]{7,15}"))
                    return "Invalid phone number format";
                break;
            case 13: case 14: case 15: case 16: case 17: case 18: 
                try { Double.parseDouble(value); }
                catch (NumberFormatException ex) { return "Must be a numeric value"; }
                break;
            default:
                break;
        }
        return null;
    }


    private String normalizeBirthday(String value) {
        
        if (value.matches("\\d{4}-\\d{2}-\\d{2}")) {
            
            LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
            return value;
        }


        String normalized = value.replace('/', '-');
        LocalDate date = LocalDate.parse(normalized, DateTimeFormatter.ofPattern("MM-dd-yyyy"));
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE); 
    }
}