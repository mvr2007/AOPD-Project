import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class LoginDialog extends JDialog {

    public String loggedInUser = null;
    public String loggedInRole = null;

    private final FleetDAO fleetDAO = new FleetDAO();

    // -----------------------------------------------------------------------
    // bypass constructor — for CaptureGUIScreenshot
    // -----------------------------------------------------------------------
    public LoginDialog(boolean bypass) {
        if (bypass) {
            loggedInUser = "admin";
            loggedInRole = "ADMIN";
            dispose();
        }
    }

    // -----------------------------------------------------------------------
    // Normal constructor
    // -----------------------------------------------------------------------
    public LoginDialog(Frame owner) {
        super(owner, "Login", true);
        setUndecorated(true);
        setSize(820, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Subtle diagonal line pattern on WARM_CREAM background
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CozyTheme.WARM_CREAM);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(0xE0, 0xD8, 0xCC, 60));
                g2.setStroke(new BasicStroke(1f));
                int gap = 18;
                for (int i = -getHeight(); i < getWidth() + getHeight(); i += gap) {
                    g2.drawLine(i, 0, i + getHeight(), getHeight());
                }
                g2.dispose();
            }
        };
        root.setOpaque(false);

        // Split panels
        int leftW  = (int)(820 * 0.40);
        int rightW = 820 - leftW;

        JPanel leftPanel  = buildLeftPanel(leftW);
        JPanel rightPanel = buildRightPanel();

        root.add(leftPanel,  BorderLayout.WEST);
        root.add(rightPanel, BorderLayout.CENTER);

        setContentPane(root);
    }

    // -----------------------------------------------------------------------
    // LEFT panel
    // -----------------------------------------------------------------------
    private JPanel buildLeftPanel(int width) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Background
                g2.setColor(CozyTheme.DARK_SLATE);
                g2.fillRect(0, 0, w, h);

                // Decorative geometric shapes (background accents)
                g2.setColor(new Color(0xFF, 0xFF, 0xFF, 18));
                g2.fill(new Ellipse2D.Float(-40, -40, 180, 180));
                g2.setColor(new Color(0xFF, 0xFF, 0xFF, 12));
                g2.fill(new Ellipse2D.Float(w - 90, h - 110, 180, 180));

                // --- Vehicle / Hub Emblem ---
                int ex = w / 2 - 40;
                int ey = 90;

                // Truck body (rounded rect)
                g2.setColor(CozyTheme.SAGE_GREEN);
                g2.fill(new RoundRectangle2D.Float(ex, ey, 80, 44, 12, 12));

                // Cabin (smaller rounded rect on right side)
                g2.setColor(new Color(0x6A, 0x87, 0x5E));
                g2.fill(new RoundRectangle2D.Float(ex + 54, ey + 4, 32, 36, 8, 8));

                // Windshield
                g2.setColor(new Color(0xC8, 0xE6, 0xC9, 180));
                g2.fill(new RoundRectangle2D.Float(ex + 58, ey + 8, 20, 18, 4, 4));

                // Wheels (4 circles)
                g2.setColor(CozyTheme.DARK_SLATE);
                g2.fillOval(ex + 8,  ey + 38, 18, 18);
                g2.fillOval(ex + 34, ey + 38, 18, 18);
                g2.fillOval(ex + 60, ey + 38, 16, 16);
                g2.fillOval(ex + 76, ey + 38, 16, 16);

                // Wheel rims
                g2.setColor(Color.WHITE);
                g2.fillOval(ex + 12, ey + 42, 10, 10);
                g2.fillOval(ex + 38, ey + 42, 10, 10);
                g2.fillOval(ex + 63, ey + 42, 9, 9);
                g2.fillOval(ex + 79, ey + 42, 9, 9);

                // Hub accent lines on body
                g2.setColor(new Color(0xFF, 0xFF, 0xFF, 50));
                g2.setStroke(new BasicStroke(2f));
                g2.drawLine(ex + 8, ey + 14, ex + 50, ey + 14);
                g2.drawLine(ex + 8, ey + 24, ex + 50, ey + 24);

                // "FleetHub" title
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 28));
                FontMetrics fm = g2.getFontMetrics();
                String title = "FleetHub";
                g2.drawString(title, (w - fm.stringWidth(title)) / 2, ey + 86);

                // Subtitle
                g2.setColor(CozyTheme.SAGE_GREEN);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                fm = g2.getFontMetrics();
                String sub = "Fleet Lifecycle Console";
                g2.drawString(sub, (w - fm.stringWidth(sub)) / 2, ey + 110);

                g2.dispose();
            }
        };
        panel.setPreferredSize(new Dimension(width, 500));
        panel.setOpaque(false);
        return panel;
    }

    // -----------------------------------------------------------------------
    // RIGHT panel
    // -----------------------------------------------------------------------
    private JTextField  usernameField;
    private JPasswordField passwordField;
    private JLabel errorLabel;

    private JPanel buildRightPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(CozyTheme.WARM_CREAM);
        panel.setOpaque(true);

        int pw = 492; // 820 * 0.60
        int ph = 500;
        panel.setPreferredSize(new Dimension(pw, ph));

        int formW = 340;
        int startX = (pw - formW) / 2;
        int y = 80;

        // "Welcome Back" heading
        JLabel heading = new JLabel("Welcome Back");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 22));
        heading.setForeground(CozyTheme.DARK_SLATE);
        heading.setBounds(startX, y, formW, 36);
        panel.add(heading);
        y += 42;

        // Subtitle
        JLabel subtitle = new JLabel("Sign in to your fleet console");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(new Color(0x88, 0x88, 0x88));
        subtitle.setBounds(startX, y, formW, 22);
        panel.add(subtitle);
        y += 36;

        // Username field
        usernameField = createPlaceholderTextField("Username");
        usernameField.setBounds(startX, y, formW, 44);
        panel.add(usernameField);
        y += 56;

        // Password field
        passwordField = createPlaceholderPasswordField("Password");
        passwordField.setBounds(startX, y, formW, 44);
        panel.add(passwordField);
        y += 56;

        // Error label
        errorLabel = new JLabel("");
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errorLabel.setForeground(CozyTheme.TERRACOTTA);
        errorLabel.setBounds(startX, y, formW, 20);
        panel.add(errorLabel);
        y += 24;

        // Sign In button
        ModernComponents.ModernButton signInBtn =
                new ModernComponents.ModernButton("Sign In", CozyTheme.SAGE_GREEN, Color.WHITE);
        signInBtn.setBounds(startX, y, formW, 48);
        signInBtn.addActionListener(e -> doSignIn());
        panel.add(signInBtn);
        y += 60;

        // Demo Admin Login button (smaller pill)
        ModernComponents.ModernButton demoBtn =
                new ModernComponents.ModernButton("Demo Admin Login", CozyTheme.DARK_SLATE, Color.WHITE);
        demoBtn.setBounds(startX + formW / 4, y, formW / 2, 34);
        demoBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        demoBtn.addActionListener(e -> {
            usernameField.setText("admin");
            passwordField.setText("admin123");
            doSignIn();
        });
        panel.add(demoBtn);

        // Allow Enter key on password field to trigger sign-in
        passwordField.addActionListener(e -> doSignIn());
        usernameField.addActionListener(e -> passwordField.requestFocus());

        return panel;
    }

    // -----------------------------------------------------------------------
    // Authentication
    // -----------------------------------------------------------------------
    private void doSignIn() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword()).trim();

        if (user.isEmpty() || pass.isEmpty()) {
            errorLabel.setText("Please enter username and password.");
            return;
        }

        String role = fleetDAO.authenticateUser(user, pass);
        if (role != null) {
            loggedInUser = user;
            loggedInRole = role;
            dispose();
        } else {
            errorLabel.setText("Invalid credentials. Please try again.");
            passwordField.setText("");
        }
    }

    // -----------------------------------------------------------------------
    // Styled text field helpers
    // -----------------------------------------------------------------------
    private JTextField createPlaceholderTextField(String placeholder) {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !hasFocus()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(new Color(0xAA, 0xAA, 0xAA));
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    Insets ins = getInsets();
                    g2.drawString(placeholder, ins.left, getHeight() / 2 + g2.getFontMetrics().getAscent() / 2 - 2);
                    g2.dispose();
                }
            }
        };
        styleTextField(field);
        return field;
    }

    private JPasswordField createPlaceholderPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getPassword().length == 0 && !hasFocus()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(new Color(0xAA, 0xAA, 0xAA));
                    g2.setFont(new Font("Segoe UI", Font.ITALIC, 14));
                    Insets ins = getInsets();
                    g2.drawString(placeholder, ins.left, getHeight() / 2 + g2.getFontMetrics().getAscent() / 2 - 2);
                    g2.dispose();
                }
            }
        };
        styleTextField(field);
        return field;
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(Color.WHITE);
        field.setForeground(CozyTheme.DARK_SLATE);
        field.setCaretColor(CozyTheme.DARK_SLATE);
        field.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(10, new Color(0xCC, 0xC6, 0xBC)),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        field.setOpaque(true);

        // Focus highlight
        field.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    new RoundedBorder(10, CozyTheme.SAGE_GREEN),
                    BorderFactory.createEmptyBorder(12, 12, 12, 12)
                ));
                field.repaint();
            }
            @Override public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    new RoundedBorder(10, new Color(0xCC, 0xC6, 0xBC)),
                    BorderFactory.createEmptyBorder(12, 12, 12, 12)
                ));
                field.repaint();
            }
        });
    }

    // -----------------------------------------------------------------------
    // RoundedBorder helper (inner class)
    // -----------------------------------------------------------------------
    private static class RoundedBorder extends AbstractBorder {
        private final int radius;
        private final Color color;

        RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.5f));
            g2.draw(new RoundRectangle2D.Float(x, y, w - 1, h - 1, radius, radius));
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) { return new Insets(4, 4, 4, 4); }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.set(4, 4, 4, 4);
            return insets;
        }
    }
}
