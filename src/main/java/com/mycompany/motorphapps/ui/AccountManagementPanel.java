/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorphapps.ui;

import com.mycompany.motorphapps.service.AccountService;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class AccountManagementPanel extends JPanel {

    private DefaultTableModel model;
    private JTable            table;
    private JTextField        searchField;
    private AccountService    accountService;

    private static final String SEARCH_PLACEHOLDER = "Search accounts...";


    private static final String[] ROLES = {
        "Employee",
        "System Administrator",
        "HR Personnel",
        "Finance Personnel",
        "IT Personnel"
    };

    public AccountManagementPanel() {
        accountService = new AccountService();
        setLayout(new BorderLayout(0, 0));
        setBackground(UIStyle.BACKGROUND);
        add(buildTopBar(),    BorderLayout.NORTH);
        add(buildTableArea(), BorderLayout.CENTER);
        add(buildButtonBar(), BorderLayout.SOUTH);
    }


    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(UIStyle.PRIMARY);
        bar.setBorder(new EmptyBorder(16, 24, 16, 24));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);
        JLabel icon  = new JLabel("\uD83D\uDDC2");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        icon.setForeground(Color.WHITE);
        JLabel title = new JLabel("Account Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        left.add(icon);
        left.add(title);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        JLabel searchIcon = new JLabel("\uD83D\uDD0D");
        searchIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        searchIcon.setForeground(new Color(180, 210, 255));

        searchField = new JTextField(18);
        searchField.setFont(UIStyle.FONT_BODY);
        searchField.setBackground(Color.WHITE);
        searchField.setForeground(UIStyle.TEXT_SECONDARY);
        searchField.setCaretColor(UIStyle.TEXT_PRIMARY);
        searchField.setBorder(new CompoundBorder(
                new LineBorder(new Color(255, 255, 255, 120), 1, true),
                new EmptyBorder(6, 10, 6, 10)));
        searchField.setText(SEARCH_PLACEHOLDER);

        searchField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals(SEARCH_PLACEHOLDER)) {
                    searchField.setText("");
                    searchField.setForeground(UIStyle.TEXT_PRIMARY);
                }
            }
            public void focusLost(FocusEvent e) {
                if (searchField.getText().trim().isEmpty()) {
                    searchField.setText(SEARCH_PLACEHOLDER);
                    searchField.setForeground(UIStyle.TEXT_SECONDARY);
                }
            }
        });

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { filter(); }
            public void removeUpdate(DocumentEvent e)  { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
        });

        right.add(searchIcon);
        right.add(searchField);
        bar.add(left,  BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }


    private JPanel buildTableArea() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(UIStyle.BACKGROUND);
        wrapper.setBorder(new EmptyBorder(16, 24, 8, 24));
        wrapper.add(buildStatsStrip(), BorderLayout.NORTH);

        model = new DefaultTableModel(
                new Object[]{"Employee ID", "Role"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        UIStyle.styleTable(table);


        table.getColumnModel().getColumn(1).setCellRenderer(
                new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object value, boolean sel,
                    boolean focus, int row, int col) {

                JPanel cell = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
                cell.setOpaque(true);
                cell.setBackground(sel ? UIStyle.PRIMARY_LIGHT
                        : row % 2 == 0 ? UIStyle.SURFACE : UIStyle.ROW_ALT);

                String role = value == null ? "" : value.toString();
                Color bg, fg = Color.WHITE;
                switch (role.toLowerCase()) {
                    case "system administrator": bg = new Color(220, 38,  38);  break;
                    case "hr personnel":         bg = new Color(37,  99,  235); break;
                    case "finance personnel":    bg = new Color(5,   150, 105); break;
                    case "it personnel":         bg = new Color(124, 58,  237); break;
                    case "employee":             bg = new Color(234, 88,  12);  break;
                    default:                     bg = new Color(75,  85,  99);  break;
                }

                JLabel badge = UIStyle.createBadge(role.toUpperCase(), bg, fg);
                cell.add(badge);
                return cell;
            }
        });

        table.getColumnModel().getColumn(0).setPreferredWidth(160);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);

        loadAccounts();

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new CompoundBorder(
                new LineBorder(UIStyle.BORDER, 1, true),
                new EmptyBorder(0, 0, 0, 0)));
        scroll.getViewport().setBackground(UIStyle.SURFACE);

        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }


    private JPanel buildStatsStrip() {
        JPanel strip = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        strip.setOpaque(false);
        strip.setBorder(new EmptyBorder(0, 0, 12, 0));

        List<String[]> accounts = accountService.getAllAccounts();

        long admins    = accounts.stream().filter(a ->
            a.length > 2 && "system administrator".equalsIgnoreCase(a[2])).count();
        long employees = accounts.stream().filter(a ->
            a.length > 2 && "employee".equalsIgnoreCase(a[2])).count();
        long hr        = accounts.stream().filter(a ->
            a.length > 2 && "hr personnel".equalsIgnoreCase(a[2])).count();
        long finance   = accounts.stream().filter(a ->
            a.length > 2 && "finance personnel".equalsIgnoreCase(a[2])).count();
        long it        = accounts.stream().filter(a ->
            a.length > 2 && "it personnel".equalsIgnoreCase(a[2])).count();

        strip.add(buildStatChip("Total Accounts",
                String.valueOf(accounts.size()), UIStyle.PRIMARY));

        if (admins    > 0) strip.add(buildStatChip("Admins",
                String.valueOf(admins),    new Color(192, 38,  38)));
        if (employees > 0) strip.add(buildStatChip("Employees",
                String.valueOf(employees), new Color(22,  130, 80)));
        if (hr        > 0) strip.add(buildStatChip("HR",
                String.valueOf(hr),        new Color(37,  99,  235)));
        if (finance   > 0) strip.add(buildStatChip("Finance",
                String.valueOf(finance),   new Color(5,   150, 105)));
        if (it        > 0) strip.add(buildStatChip("IT",
                String.valueOf(it),        new Color(124, 58,  237)));

        return strip;
    }

    private JPanel buildStatChip(String label, String value, Color color) {
        JPanel chip = new JPanel(new BorderLayout(4, 0));
        chip.setBackground(UIStyle.SURFACE);
        chip.setBorder(new CompoundBorder(
                new LineBorder(UIStyle.BORDER, 1, true),
                new EmptyBorder(8, 14, 8, 14)));

        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 18));
        val.setForeground(color);

        JLabel lbl = new JLabel("  " + label);
        lbl.setFont(UIStyle.FONT_SMALL);
        lbl.setForeground(UIStyle.TEXT_SECONDARY);

        chip.add(val, BorderLayout.WEST);
        chip.add(lbl, BorderLayout.EAST);
        return chip;
    }

    private JPanel buildButtonBar() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(UIStyle.BACKGROUND);
        outer.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, UIStyle.BORDER),
                new EmptyBorder(12, 24, 16, 24)));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setOpaque(false);

        JButton addBtn    = UIStyle.createPrimaryButton("+ Add Account");
        JButton editBtn   = UIStyle.createSecondaryButton(" Edit");
        JButton deleteBtn = UIStyle.createDangerButton(" Delete");

        addBtn.addActionListener(e    -> openAccountDialog(null));
        editBtn.addActionListener(e   -> editSelected());
        deleteBtn.addActionListener(e -> deleteSelected());

        btnRow.add(deleteBtn);
        btnRow.add(editBtn);
        btnRow.add(addBtn);

        outer.add(btnRow, BorderLayout.EAST);
        return outer;
    }


    private void loadAccounts() {
        model.setRowCount(0);
        for (String[] row : accountService.getAllAccounts()) {
            if (row.length >= 3)
                model.addRow(new Object[]{ row[0], row[2] });
        }
    }

    private void filter() {
        String text = searchField.getText();
        if (text.equals(SEARCH_PLACEHOLDER)) {
            table.setRowSorter(null);
            return;
        }
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        sorter.setRowFilter(
                text.isEmpty() ? null : RowFilter.regexFilter("(?i)" + text));
        table.setRowSorter(sorter);
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { showInfo("Select an account to edit."); return; }
        row = table.convertRowIndexToModel(row);
        String empId = model.getValueAt(row, 0).toString();
        for (String[] acc : accountService.getAllAccounts()) {
            if (acc[0].equals(empId)) { openAccountDialog(acc); return; }
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { showInfo("Select an account to delete."); return; }
        row = table.convertRowIndexToModel(row);
        String empId = model.getValueAt(row, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete account for Employee ID: " + empId + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            accountService.deleteAccount(empId);
            loadAccounts();
            JOptionPane.showMessageDialog(this,
                    "Account deleted successfully.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }


    private void openAccountDialog(String[] data) {
        boolean isEdit = data != null;

        JTextField empIdField = new JTextField(data != null ? data[0] : "", 20);
        empIdField.setFont(UIStyle.FONT_BODY);
        empIdField.setForeground(UIStyle.TEXT_PRIMARY);
        empIdField.setBackground(Color.WHITE);
        empIdField.setCaretColor(UIStyle.TEXT_PRIMARY);
        if (isEdit) {
            empIdField.setEditable(false);
            empIdField.setBackground(new Color(240, 242, 248));
            empIdField.setForeground(UIStyle.TEXT_SECONDARY);
        }

        JPasswordField passField = new JPasswordField(data != null ? data[1] : "", 20);
        passField.setFont(UIStyle.FONT_BODY);
        passField.setForeground(UIStyle.TEXT_PRIMARY);
        passField.setBackground(Color.WHITE);
        passField.setCaretColor(UIStyle.TEXT_PRIMARY);

        JComboBox<String> roleBox = new JComboBox<>(ROLES);
        roleBox.setFont(UIStyle.FONT_BODY);

        if (isEdit && data[2] != null) {
            for (String r : ROLES) {
                if (r.equalsIgnoreCase(data[2])) {
                    roleBox.setSelectedItem(r);
                    break;
                }
            }
        }

        JLabel empIdErr = errorLabel();
        JLabel passErr  = errorLabel();

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UIStyle.SURFACE);
        form.setBorder(new EmptyBorder(8, 4, 8, 4));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 6, 0, 6);
        gc.fill   = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.WEST;

        addFormRow(form, gc, 0, "Employee ID *", empIdField);
        addErrorRow(form, gc, 1, empIdErr);
        addFormRow(form, gc, 2, "Password *",    passField);
        addErrorRow(form, gc, 3, passErr);
        addFormRow(form, gc, 4, "Role:",          roleBox);

        JLabel heading = new JLabel(isEdit ? "  Edit Account" : "+  Add New Account");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 15));
        heading.setForeground(UIStyle.PRIMARY);
        heading.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, UIStyle.BORDER),
                new EmptyBorder(0, 0, 10, 0)));

        JPanel wrapper = new JPanel(new BorderLayout(0, 12));
        wrapper.setBackground(UIStyle.SURFACE);
        wrapper.setBorder(new EmptyBorder(4, 4, 4, 4));
        wrapper.add(heading, BorderLayout.NORTH);
        wrapper.add(form,    BorderLayout.CENTER);

        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(
                parentWindow,
                isEdit ? "Edit Account" : "Add Account",
                Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JButton okBtn     = UIStyle.createPrimaryButton(isEdit ? "Save Changes" : "Add Account");
        JButton cancelBtn = UIStyle.createSecondaryButton("Cancel");

        cancelBtn.addActionListener(e -> dialog.dispose());

        okBtn.addActionListener(e -> {
            empIdErr.setText(" ");
            passErr.setText(" ");
            empIdField.setBorder(new CompoundBorder(
                    new LineBorder(UIStyle.BORDER, 1, true),
                    new EmptyBorder(6, 10, 6, 10)));
            passField.setBorder(new CompoundBorder(
                    new LineBorder(UIStyle.BORDER, 1, true),
                    new EmptyBorder(6, 10, 6, 10)));

            String empId = empIdField.getText().trim();
            String pass  = new String(passField.getPassword()).trim();
            String role  = roleBox.getSelectedItem().toString();

            boolean valid = true;

            if (empId.isEmpty()) {
                empIdErr.setText("⚠ Employee ID is required");
                empIdField.setBorder(new CompoundBorder(
                        new LineBorder(UIStyle.DANGER, 2, true),
                        new EmptyBorder(6, 10, 6, 10)));
                valid = false;
            }

            if (pass.isEmpty()) {
                passErr.setText("⚠ Password is required");
                passField.setBorder(new CompoundBorder(
                        new LineBorder(UIStyle.DANGER, 2, true),
                        new EmptyBorder(6, 10, 6, 10)));
                valid = false;
            }

            if (!valid) return;

            try {
                if (!isEdit) accountService.addAccount(empId, pass, role);
                else         accountService.updateAccount(empId, pass, role);
                loadAccounts();
                dialog.dispose();
                JOptionPane.showMessageDialog(
                        AccountManagementPanel.this,
                        "Account " + (isEdit ? "updated" : "added") + " successfully.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
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

    private void addFormRow(JPanel p, GridBagConstraints gc,
                            int row, String label, JComponent field) {
        gc.gridx = 0; gc.gridy = row; gc.weightx = 0.3;
        JLabel lbl = new JLabel(label);
        lbl.setFont(UIStyle.FONT_BODY);
        lbl.setForeground(UIStyle.TEXT_SECONDARY);
        p.add(lbl, gc);

        gc.gridx = 1; gc.weightx = 0.7;
        if (field instanceof JTextField || field instanceof JPasswordField) {
            field.setBorder(new CompoundBorder(
                    new LineBorder(UIStyle.BORDER, 1, true),
                    new EmptyBorder(6, 10, 6, 10)));
        }
        p.add(field, gc);
    }

    private void addErrorRow(JPanel p, GridBagConstraints gc,
                             int row, JLabel errLabel) {
        gc.gridx = 1; gc.gridy = row; gc.weightx = 0.7;
        p.add(errLabel, gc);
    }

    private JLabel errorLabel() {
        JLabel lbl = new JLabel(" ");
        lbl.setFont(UIStyle.FONT_SMALL);
        lbl.setForeground(UIStyle.DANGER);
        lbl.setBorder(new EmptyBorder(0, 2, 2, 0));
        return lbl;
    }

    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Info",
                JOptionPane.INFORMATION_MESSAGE);
    }
}