package com.mycompany.motorphapps.ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import javax.imageio.ImageIO;
import com.mycompany.motorphapps.service.AuthService;
import com.mycompany.motorphapps.model.Person;

/**
 * Login Screen — professional navy blue theme.
 * Background image preserved; form panel uses UIStyle tokens.
 *
 * @author DAYANG GWAPA
 */
public class LoginScreen extends JFrame {

    private JTextField     usernameField;
    private JPasswordField passwordField;
    private BufferedImage  bgImage;
    private BufferedImage  logoImage;
    private JPanel         backgroundPanel;
    private JButton        loginButton;

    public LoginScreen() {
        setTitle("MotorPH — Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 540);
        setMinimumSize(new Dimension(750, 460));
        setLocationRelativeTo(null);
        setResizable(true);

        loadBackgroundImage();
        loadLogoImage();
        backgroundPanel = createBackgroundPanel();
        setContentPane(backgroundPanel);

        buildUI();
        setVisible(true);
    }

    // ── Background ────────────────────────────────────────────────────────────
    private void loadBackgroundImage() {
        try {
            bgImage = ImageIO.read(
                    getClass().getResource("/images/MotorPH Login Design.jpg"));
        } catch (Exception e) {
            System.out.println("Background image not found — using gradient fallback.");
        }
    }

    // ── Logo (same image used in the JasperReports header) ─────────────────────
    private void loadLogoImage() {
        try {
            logoImage = ImageIO.read(
                    getClass().getResource("/images/MTPH.png"));
        } catch (Exception e) {
            System.out.println("Logo image not found — falling back to text-only logo.");
        }
    }

    private JPanel createBackgroundPanel() {
        return new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bgImage != null) {
                    g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    // Fallback: navy gradient
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setPaint(new GradientPaint(
                            0, 0, UIStyle.PRIMARY_DARK,
                            getWidth(), getHeight(), UIStyle.PRIMARY_LIGHT));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
    }

    // ── Form ──────────────────────────────────────────────────────────────────
    private void buildUI() {
        // Glass card
        JPanel card = new JPanel(new BorderLayout(0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 230));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(380, 420));

        // ── Card header (navy band) ───────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout(12, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UIStyle.PRIMARY);
                // Only round top corners
                g2.fillRoundRect(0, 0, getWidth(), getHeight() + 20, 16, 16);
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 100));
        header.setBorder(new EmptyBorder(24, 30, 24, 30));

        // ── Logo image (same MTPH.png used in the payslip/report header) ──
        if (logoImage != null) {
            ImageIcon scaledLogo = new ImageIcon(
                    logoImage.getScaledInstance(62, 52, Image.SCALE_SMOOTH));
            JLabel logoIcon = new JLabel(scaledLogo);
            logoIcon.setBorder(new EmptyBorder(0, 0, 0, 0));
            header.add(logoIcon, BorderLayout.WEST);
        }

        JLabel logo = new JLabel("MotorPH");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        logo.setForeground(Color.WHITE);

        JLabel tagline = new JLabel("Management System");
        tagline.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tagline.setForeground(new Color(180, 210, 255));

        JPanel logoStack = new JPanel();
        logoStack.setOpaque(false);
        logoStack.setLayout(new BoxLayout(logoStack, BoxLayout.Y_AXIS));
        logoStack.add(logo);
        logoStack.add(tagline);
        header.add(logoStack, BorderLayout.CENTER);
        card.add(header, BorderLayout.NORTH);

        // ── Form body ────────────────────────────────────────────────────────
        JPanel body = new JPanel(new GridBagLayout());
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(28, 32, 28, 32));

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(0, 0, 16, 0);
        gc.weightx = 1.0;

        // Username
        gc.gridx = 0; gc.gridy = 0;
        body.add(buildFieldLabel("Employee ID / Username"), gc);

        gc.gridy = 1;
        gc.insets = new Insets(0, 0, 20, 0);
        usernameField = buildTextField();
        body.add(usernameField, gc);

        // Password
        gc.gridy = 2;
        gc.insets = new Insets(0, 0, 16, 0);
        body.add(buildFieldLabel("Password"), gc);

        gc.gridy = 3;
        gc.insets = new Insets(0, 0, 28, 0);
        passwordField = new JPasswordField();
        styleInputField(passwordField);
        // Allow Enter key to submit
        passwordField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) authenticate();
            }
        });
        body.add(passwordField, gc);

        // Login button
        gc.gridy = 4;
        gc.insets = new Insets(0, 0, 0, 0);
        loginButton = new JButton("Sign In") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed()  ? UIStyle.PRIMARY_DARK
                          : getModel().isRollover() ? UIStyle.PRIMARY_LIGHT
                          : UIStyle.PRIMARY);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        loginButton.setForeground(Color.WHITE);
        loginButton.setContentAreaFilled(false);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setBorder(new EmptyBorder(12, 0, 12, 0));
        loginButton.setPreferredSize(new Dimension(0, 46));
        loginButton.addActionListener(e -> authenticate());
        // Enter key on username field
        usernameField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) passwordField.requestFocus();
            }
        });
        body.add(loginButton, gc);

        card.add(body, BorderLayout.CENTER);

        // ── Footer note ───────────────────────────────────────────────────────
        JLabel footer = new JLabel("© 2025 MotorPH. All rights reserved.", JLabel.CENTER);
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footer.setForeground(UIStyle.TEXT_SECONDARY);
        footer.setBorder(new EmptyBorder(0, 0, 6, 0));
        card.add(footer, BorderLayout.SOUTH);

        backgroundPanel.add(card);
    }

    private JLabel buildFieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(UIStyle.TEXT_SECONDARY);
        return lbl;
    }

    private JTextField buildTextField() {
        JTextField f = new JTextField();
        styleInputField(f);
        return f;
    }

    private void styleInputField(JTextField f) {
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setBackground(new Color(245, 248, 255));
        f.setForeground(UIStyle.TEXT_PRIMARY);
        f.setCaretColor(UIStyle.PRIMARY);
        f.setBorder(new CompoundBorder(
                new LineBorder(UIStyle.BORDER, 1, true),
                new EmptyBorder(10, 14, 10, 14)));
        f.setPreferredSize(new Dimension(0, 44));

        // Focus highlight
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                f.setBorder(new CompoundBorder(
                        new LineBorder(UIStyle.PRIMARY_LIGHT, 2, true),
                        new EmptyBorder(9, 13, 9, 13)));
            }
            public void focusLost(FocusEvent e) {
                f.setBorder(new CompoundBorder(
                        new LineBorder(UIStyle.BORDER, 1, true),
                        new EmptyBorder(10, 14, 10, 14)));
            }
        });
    }

    // ── Auth ──────────────────────────────────────────────────────────────────
    private void authenticate() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both username and password.",
                    "Login Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        loginButton.setText("Signing in...");
        loginButton.setEnabled(false);

        AuthService authService = new AuthService();

        // ✅ STEP 1: Check username/password
        boolean valid = authService.loginStep1(username, password);

        if (!valid) {
            JOptionPane.showMessageDialog(this,
                    "Invalid username or password.",
                    "Login Failed", JOptionPane.ERROR_MESSAGE);

            passwordField.setText("");
            loginButton.setText("Sign In");
            loginButton.setEnabled(true);
            return;
        }

        // ✅ STEP 2: Ask for OTP
        String otp = JOptionPane.showInputDialog(this,
                "Enter the 6-digit OTP sent to your account:");

        if (otp == null || otp.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "OTP is required.",
                    "Error", JOptionPane.WARNING_MESSAGE);

            loginButton.setText("Sign In");
            loginButton.setEnabled(true);
            return;
        }

        Person user = authService.loginStep2(username, otp);

        if (user != null) {
            JOptionPane.showMessageDialog(this,
                    "Login successful with 2FA!");

            dispose();
            new Dashboard(user);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Invalid OTP.",
                    "2FA Failed", JOptionPane.ERROR_MESSAGE);

            loginButton.setText("Sign In");
            loginButton.setEnabled(true);
        }
    }
}