import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class ModernComponents {

    // -------------------------------------------------------------------------
    // RoundedPanel
    // -------------------------------------------------------------------------
    public static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color bg;
        private static final Color BORDER_COLOR = new Color(0xE0, 0xD8, 0xCC);

        public RoundedPanel(int radius, Color bg) {
            this.radius = radius;
            this.bg = bg;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Drop shadow: 3 layers (alpha 30, 20, 10), slightly offset and larger
            int[] alphas = {30, 20, 10};
            for (int i = 0; i < alphas.length; i++) {
                int offset = i + 1;
                g2.setColor(new Color(0, 0, 0, alphas[i]));
                g2.fill(new RoundRectangle2D.Float(offset, offset, w - 1, h - 1, radius, radius));
            }

            // Fill background
            g2.setColor(bg);
            g2.fill(new RoundRectangle2D.Float(0, 0, w - 1, h - 1, radius, radius));

            // 1px border
            g2.setColor(BORDER_COLOR);
            g2.setStroke(new BasicStroke(1f));
            g2.draw(new RoundRectangle2D.Float(0, 0, w - 1, h - 1, radius, radius));

            g2.dispose();
        }
    }

    // -------------------------------------------------------------------------
    // ModernButton
    // -------------------------------------------------------------------------
    public static class ModernButton extends JButton {
        private final Color baseBg;
        private Color currentBg;
        private final Color fg;
        private static final int RADIUS = 10;

        public ModernButton(String text, Color bg, Color fg) {
            super(text);
            this.baseBg = bg;
            this.currentBg = bg;
            this.fg = fg;

            setContentAreaFilled(false);
            setOpaque(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setForeground(fg);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    currentBg = brighten(baseBg, 15);
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    currentBg = baseBg;
                    repaint();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    currentBg = darken(baseBg, 15);
                    repaint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    currentBg = baseBg;
                    repaint();
                }
            });
        }

        private Color brighten(Color c, int amt) {
            return new Color(
                Math.min(255, c.getRed()   + amt),
                Math.min(255, c.getGreen() + amt),
                Math.min(255, c.getBlue()  + amt)
            );
        }

        private Color darken(Color c, int amt) {
            return new Color(
                Math.max(0, c.getRed()   - amt),
                Math.max(0, c.getGreen() - amt),
                Math.max(0, c.getBlue()  - amt)
            );
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(currentBg);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, RADIUS, RADIUS));
            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        public void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(currentBg.darker());
            g2.setStroke(new BasicStroke(1f));
            g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, RADIUS, RADIUS));
            g2.dispose();
        }
    }

    // -------------------------------------------------------------------------
    // ToastNotification
    // -------------------------------------------------------------------------
    public static class ToastNotification {

        public static void show(Component parent, String message, boolean isSuccess) {
            Color toastColor = isSuccess ? CozyTheme.SAGE_GREEN : CozyTheme.TERRACOTTA;
            String icon = isSuccess ? "\u2713" : "\u2717";

            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            int toastW = 320;
            int toastH = 56;
            int x = screen.width  - toastW - 24;
            int y = screen.height - toastH - 48;

            JWindow window = new JWindow();
            window.setSize(toastW, toastH);
            window.setLocation(x, y);

            // We store alpha as array so lambda can mutate it
            float[] alpha = {1.0f};

            JPanel panel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha[0]));

                    // Rounded rect background
                    g2.setColor(toastColor);
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 16, 16));

                    // White circle for icon
                    int circleX = 14;
                    int circleY = (getHeight() - 28) / 2;
                    g2.setColor(Color.WHITE);
                    g2.fillOval(circleX, circleY, 28, 28);

                    // Icon inside circle
                    g2.setColor(toastColor);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
                    FontMetrics fm = g2.getFontMetrics();
                    int ix = circleX + (28 - fm.stringWidth(icon)) / 2;
                    int iy = circleY + (28 + fm.getAscent() - fm.getDescent()) / 2;
                    g2.drawString(icon, ix, iy);

                    // Message text
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    fm = g2.getFontMetrics();
                    int tx = circleX + 28 + 12;
                    int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                    g2.drawString(message, tx, ty);

                    g2.dispose();
                }
            };
            panel.setOpaque(false);
            window.setContentPane(panel);
            window.setBackground(new Color(0, 0, 0, 0));
            window.setVisible(true);

            // Auto-dismiss after 2500ms, then fade out
            Timer dismissTimer = new Timer(2500, null);
            dismissTimer.setRepeats(false);
            dismissTimer.addActionListener(e -> {
                dismissTimer.stop();
                // Fade out: reduce alpha from ~0.9 to 0 in 50ms steps
                float[] currentAlpha = {230f / 255f};
                Timer fadeTimer = new Timer(50, null);
                fadeTimer.addActionListener(fe -> {
                    currentAlpha[0] -= (230f / 255f) / (2000f / 50f);
                    alpha[0] = Math.max(0f, currentAlpha[0]);
                    panel.repaint();
                    if (alpha[0] <= 0f) {
                        ((Timer) fe.getSource()).stop();
                        window.dispose();
                    }
                });
                fadeTimer.start();
            });
            dismissTimer.start();
        }
    }

    // -------------------------------------------------------------------------
    // StatusBadgeRenderer
    // -------------------------------------------------------------------------
    public static class StatusBadgeRenderer implements TableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            String text = value != null ? value.toString() : "";
            Color[] colors = getBadgeColors(text);
            Color pillColor = colors[0];
            Color textColor = colors[1];

            JLabel label = new JLabel(text, SwingConstants.CENTER) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    int pw = getWidth();
                    int ph = getHeight();
                    int pillH = 24;
                    int pillW = Math.min(pw - 16, pw);
                    int px = (pw - pillW) / 2;
                    int py = (ph - pillH) / 2;

                    // Fill pill
                    g2.setColor(pillColor);
                    g2.fill(new RoundRectangle2D.Float(px, py, pillW, pillH, pillH, pillH));

                    // Draw text
                    g2.setColor(textColor);
                    g2.setFont(getFont());
                    FontMetrics fm = g2.getFontMetrics();
                    int tx = (pw - fm.stringWidth(text)) / 2;
                    int ty = py + (pillH + fm.getAscent() - fm.getDescent()) / 2;
                    g2.drawString(text, tx, ty);

                    g2.dispose();
                }
            };

            label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            label.setOpaque(false);

            if (isSelected) {
                label.setBackground(table.getSelectionBackground());
            } else {
                label.setBackground(row % 2 == 0 ? Color.WHITE : CozyTheme.SEC_PANEL);
            }

            return label;
        }

        private Color[] getBadgeColors(String text) {
            switch (text) {
                case "Available":    return new Color[]{new Color(0x2E, 0x7D, 0x32), Color.WHITE};
                case "Rented":       return new Color[]{CozyTheme.TERRACOTTA,         Color.WHITE};
                case "Maintenance":  return new Color[]{new Color(0xF5, 0xA6, 0x23),  new Color(0x33, 0x33, 0x33)};
                case "Low Fuel":     return new Color[]{new Color(0xFF, 0xED, 0xD5),  new Color(0xC2, 0x41, 0x0C)};
                case "Service Due":  return new Color[]{new Color(0xC6, 0x28, 0x28),  Color.WHITE};
                case "OVERDUE":      return new Color[]{new Color(0xC6, 0x28, 0x28),  Color.WHITE};
                case "ADMIN":        return new Color[]{CozyTheme.DARK_SLATE,         Color.WHITE};
                case "STAFF":        return new Color[]{CozyTheme.SAGE_GREEN,         Color.WHITE};
                default:             return new Color[]{new Color(0x9E, 0x9E, 0x9E),  Color.WHITE};
            }
        }
    }

    // -------------------------------------------------------------------------
    // StyledTableSetup
    // -------------------------------------------------------------------------
    public static class StyledTableSetup {

        public static void apply(JTable table) {
            table.setRowHeight(44);
            table.setShowGrid(false);
            table.setIntercellSpacing(new Dimension(0, 1));
            table.setGridColor(new Color(0xEC, 0xE6, 0xDC));
            table.setBackground(Color.WHITE);
            table.setSelectionBackground(new Color(0x88, 0xA4, 0x7C, 80));
            table.setSelectionForeground(CozyTheme.DARK_SLATE);
            table.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            // Header styling
            JTableHeader header = table.getTableHeader();
            header.setBackground(CozyTheme.DARK_SLATE);
            header.setForeground(Color.WHITE);
            header.setFont(new Font("Segoe UI", Font.BOLD, 14));
            header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));
            header.setReorderingAllowed(false);

            // Alternating row renderer with padding
            DefaultTableCellRenderer rowRenderer = new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(
                        JTable tbl, Object value, boolean isSelected,
                        boolean hasFocus, int row, int col) {
                    super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, col);
                    setHorizontalAlignment(SwingConstants.CENTER);
                    setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
                    if (!isSelected) {
                        setBackground(row % 2 == 0 ? Color.WHITE : CozyTheme.SEC_PANEL);
                        setForeground(CozyTheme.DARK_SLATE);
                    }
                    return this;
                }
            };

            // Apply renderer to all columns
            for (int i = 0; i < table.getColumnCount(); i++) {
                table.getColumnModel().getColumn(i).setCellRenderer(rowRenderer);
            }

            // Apply StatusBadgeRenderer to column index 4 (Status column)
            if (table.getColumnCount() > 4) {
                table.getColumnModel().getColumn(4).setCellRenderer(new StatusBadgeRenderer());
            }
        }
    }
}
