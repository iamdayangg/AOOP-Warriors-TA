/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorphapps.ui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Central UI style registry for MotorPH Management System.
 * Navy blue professional theme.
 *
 * @author DAYANG GWAPA
 */
public class UIStyle {

    // ── Palette ──────────────────────────────────────────────────────────────
    public static final Color PRIMARY        = new Color(25, 85, 155);
    public static final Color PRIMARY_DARK   = new Color(15, 60, 120);
    public static final Color PRIMARY_LIGHT  = new Color(52, 120, 200);
    public static final Color ACCENT         = new Color(0, 170, 210);      // teal highlight
    public static final Color DANGER         = new Color(192, 38, 38);
    public static final Color SUCCESS        = new Color(22, 130, 80);
    public static final Color WARNING        = new Color(200, 140, 0);

    public static final Color BACKGROUND     = new Color(242, 245, 251);
    public static final Color SURFACE        = Color.WHITE;
    public static final Color BORDER         = new Color(210, 218, 232);
    public static final Color ROW_ALT        = new Color(245, 248, 255);

    public static final Color TEXT_PRIMARY   = new Color(22, 34, 57);
    public static final Color TEXT_SECONDARY = new Color(100, 115, 140);
    public static final Color TEXT_DISABLED  = new Color(170, 180, 200);

    // legacy aliases so existing code compiles unchanged
    public static Color SECONDARY  = PRIMARY_LIGHT;
    public static Color CARD       = SURFACE;
    public static Color TEXT_DARK  = TEXT_PRIMARY;
    public static Color TEXT_LIGHT = TEXT_SECONDARY;

    // ── Fonts ─────────────────────────────────────────────────────────────────
    public static final Font FONT_TITLE    = new Font("Segoe UI", Font.BOLD,  22);
    public static final Font FONT_HEADING  = new Font("Segoe UI", Font.BOLD,  16);
    public static final Font FONT_BODY     = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL    = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BUTTON   = new Font("Segoe UI", Font.BOLD,  13);

    // legacy aliases
    public static Font TITLE    = FONT_TITLE;
    public static Font SUBTITLE = FONT_BODY;

    // ── Card panel ───────────────────────────────────────────────────────────
    public static JPanel createCard() {
        JPanel card = new JPanel();
        card.setBackground(SURFACE);
        card.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(16, 16, 16, 16)));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        return card;
    }

    /** Elevated card with soft drop shadow illusion via layered borders. */
    public static JPanel createElevatedCard() {
        JPanel card = new JPanel();
        card.setBackground(SURFACE);
        card.setBorder(new CompoundBorder(
                new CompoundBorder(
                        new LineBorder(new Color(200, 210, 230), 1, true),
                        new LineBorder(new Color(230, 236, 248), 3, true)),
                new EmptyBorder(18, 18, 18, 18)));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        return card;
    }

    // ── Sidebar button ───────────────────────────────────────────────────────
    public static JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setForeground(new Color(200, 218, 255));
        btn.setBackground(PRIMARY);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(11, 18, 11, 18));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(PRIMARY_LIGHT);
                btn.setForeground(Color.WHITE);
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(PRIMARY);
                btn.setForeground(new Color(200, 218, 255));
            }
        });
        return btn;
    }

    // ── Primary button (filled navy) ─────────────────────────────────────────
    public static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color base = getModel().isPressed()  ? PRIMARY_DARK
                           : getModel().isRollover() ? PRIMARY_LIGHT
                           : PRIMARY;
                g2.setColor(base);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        styleButton(btn, Color.WHITE);
        return btn;
    }

    /** Danger / delete button — red filled. */
    public static JButton createDangerButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color base = getModel().isPressed()  ? DANGER.darker()
                           : getModel().isRollover() ? new Color(220, 50, 50)
                           : DANGER;
                g2.setColor(base);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        styleButton(btn, Color.WHITE);
        return btn;
    }

    // ── Secondary / outline button ───────────────────────────────────────────
    public static JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isPressed()  ? new Color(230, 236, 250)
                         : getModel().isRollover() ? new Color(240, 244, 255)
                         : SURFACE;
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(PRIMARY);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        styleButton(btn, PRIMARY);
        return btn;
    }

    private static void styleButton(JButton btn, Color fg) {
        btn.setFont(FONT_BUTTON);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 20, 8, 20));
    }

    // ── Table helpers ─────────────────────────────────────────────────────────
    public static void styleTable(JTable table) {
        table.setRowHeight(30);
        table.setFont(FONT_BODY);
        table.setGridColor(new Color(230, 235, 245));
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionBackground(PRIMARY_LIGHT);
        table.setSelectionForeground(Color.WHITE);
        table.setFillsViewportHeight(true);
        styleTableHeader(table);
        table.setDefaultRenderer(Object.class, createAlternatingRowRenderer());
    }

    public static void styleTableHeader(JTable table) {
        JTableHeader h = table.getTableHeader();
        h.setFont(new Font("Segoe UI", Font.BOLD, 13));
        h.setBackground(PRIMARY);
        h.setForeground(Color.WHITE);
        h.setPreferredSize(new Dimension(h.getWidth(), 36));
        h.setReorderingAllowed(false);
        ((DefaultTableCellRenderer) h.getDefaultRenderer())
                .setHorizontalAlignment(JLabel.LEFT);
    }

    public static DefaultTableCellRenderer createAlternatingRowRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object value, boolean sel,
                    boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(
                        t, value, sel, focus, row, col);
                if (!sel) {
                    c.setBackground(row % 2 == 0 ? SURFACE : ROW_ALT);
                    c.setForeground(TEXT_PRIMARY);
                }
                setBorder(new EmptyBorder(0, 8, 0, 8));
                return c;
            }
        };
    }

    // ── Styled text field ─────────────────────────────────────────────────────
    public static JTextField createTextField(int cols) {
        JTextField f = new JTextField(cols);
        f.setFont(FONT_BODY);
        f.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(6, 10, 6, 10)));
        f.setBackground(SURFACE);
        f.setForeground(TEXT_PRIMARY);
        return f;
    }

    // ── Section divider label ─────────────────────────────────────────────────
    public static JLabel createSectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(TEXT_SECONDARY);
        lbl.setBorder(new EmptyBorder(10, 0, 4, 0));
        return lbl;
    }

    // ── Separator ─────────────────────────────────────────────────────────────
    public static JSeparator createSeparator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }

    // ── Badge label (pill chip) ───────────────────────────────────────────────
    public static JLabel createBadge(String text, Color bg, Color fg) {
        JLabel lbl = new JLabel(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lbl.setOpaque(false);
        lbl.setBackground(bg);
        lbl.setForeground(fg);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setBorder(new EmptyBorder(3, 10, 3, 10));
        return lbl;
    }
}