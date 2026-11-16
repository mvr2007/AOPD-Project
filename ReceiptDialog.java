import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * ReceiptDialog — styled digital receipt for FleetHub Management.
 * Shows line items, grand total, and allows saving as HTML or TXT.
 */
public class ReceiptDialog extends JDialog {

    // ── CozyTheme colours (duplicated so this file compiles standalone) ──
    private static final Color WARM_CREAM  = CozyTheme.WARM_CREAM;
    private static final Color SEC_PANEL   = CozyTheme.SEC_PANEL;
    private static final Color SAGE_GREEN  = CozyTheme.SAGE_GREEN;
    private static final Color TERRACOTTA  = CozyTheme.TERRACOTTA;
    private static final Color DARK_SLATE  = CozyTheme.DARK_SLATE;

    private final String                dialogTitle;
    private final java.util.Map<String,String> lineItems;
    private final double                grandTotal;
    private final boolean               isReturn;
    private final String                dateTimeStr;

    // ────────────────────────────────────────────────────────────────────────
    public ReceiptDialog(JFrame parent,
                         String title,
                         java.util.Map<String,String> lineItems,
                         double grandTotal,
                         boolean isReturn) {
        super(parent, title, true);
        this.dialogTitle = title;
        this.lineItems   = lineItems;
        this.grandTotal  = grandTotal;
        this.isReturn    = isReturn;
        this.dateTimeStr = new SimpleDateFormat("dd MMM yyyy  HH:mm").format(new Date());

        buildUI();
        setSize(480, 620);
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    // ── UI construction ──────────────────────────────────────────────────────
    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(WARM_CREAM);

        root.add(buildTopSection(),    BorderLayout.NORTH);
        root.add(buildMiddleSection(), BorderLayout.CENTER);
        root.add(buildBottomSection(), BorderLayout.SOUTH);

        setContentPane(root);
    }

    // ── TOP SECTION ──────────────────────────────────────────────────────────
    private JPanel buildTopSection() {
        JPanel top = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);

                // Background
                g2.setColor(DARK_SLATE);
                g2.fillRect(0, 0, getWidth(), getHeight());

                int cx = getWidth() / 2;

                // "FleetHub"
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 24));
                FontMetrics fm = g2.getFontMetrics();
                String brand = "FleetHub";
                g2.drawString(brand, cx - fm.stringWidth(brand) / 2, 42);

                // Receipt type subtitle (letter-spaced via manual loop)
                String sub = isReturn ? "RETURN  RECEIPT" : "RECEIPT";
                g2.setColor(SAGE_GREEN);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                fm = g2.getFontMetrics();
                int subW = fm.stringWidth(sub);
                g2.drawString(sub, cx - subW / 2, 65);

                // Date/time
                g2.setColor(new Color(0xCC, 0xC8, 0xC4));
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                fm = g2.getFontMetrics();
                g2.drawString(dateTimeStr, cx - fm.stringWidth(dateTimeStr) / 2, 90);

                // Thin decorative line at bottom
                g2.setColor(SAGE_GREEN);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawLine(40, 108, getWidth() - 40, 108);

                g2.dispose();
            }
        };
        top.setPreferredSize(new Dimension(480, 120));
        top.setOpaque(false);
        return top;
    }

    // ── MIDDLE SECTION (scrollable card) ─────────────────────────────────────
    private JScrollPane buildMiddleSection() {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth(), h = getHeight();
                // White rounded card
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, w, h, 16, 16));

                // Border
                g2.setColor(new Color(0xE0, 0xD8, 0xCC));
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0, 0, w - 1, h - 1, 16, 16));

                drawLineItems(g2, w);
                g2.dispose();
            }

            private void drawLineItems(Graphics2D g2, int w) {
                java.util.List<Map.Entry<String,String>> entries =
                        new ArrayList<>(lineItems.entrySet());
                int n = entries.size();
                int rowH = 36;
                int padX = 20;
                int y    = 28;

                // Index of dashed separator = 3rd from bottom, before grand total
                int dashIdx = n - 3;   // after this index draw dashed line

                for (int i = 0; i < n; i++) {
                    Map.Entry<String,String> e = entries.get(i);

                    // ── Dashed separator (before taxes / fees section) ──
                    if (i == Math.max(0, dashIdx)) {
                        g2.setColor(new Color(0xCC, 0xC6, 0xBC));
                        float[] dash = {4f, 4f};
                        g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_ROUND,
                                BasicStroke.JOIN_ROUND, 0f, dash, 0f));
                        g2.drawLine(padX, y - 6, w - padX, y - 6);
                        g2.setStroke(new BasicStroke(1f));
                        y += 4;
                    }

                    // ── Thick separator before last row (grand total) ──
                    if (i == n - 1) {
                        g2.setColor(DARK_SLATE);
                        g2.setStroke(new BasicStroke(2f));
                        g2.drawLine(padX, y - 6, w - padX, y - 6);
                        g2.setStroke(new BasicStroke(1f));
                        y += 6;
                    }

                    boolean isGrandTotal = (i == n - 1);

                    if (isGrandTotal) {
                        // Key: DARK_SLATE Bold 16
                        g2.setColor(DARK_SLATE);
                        g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
                        g2.drawString(e.getKey(), padX, y + 18);

                        // Value: TERRACOTTA Bold 20
                        g2.setColor(TERRACOTTA);
                        g2.setFont(new Font("Segoe UI", Font.BOLD, 20));
                        FontMetrics fm = g2.getFontMetrics();
                        g2.drawString(e.getValue(), w - padX - fm.stringWidth(e.getValue()), y + 20);
                        y += rowH + 8;
                    } else {
                        // Key: DARK_SLATE Plain 14
                        g2.setColor(DARK_SLATE);
                        g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                        g2.drawString(e.getKey(), padX, y + 18);

                        // Value: DARK_SLATE Bold 14
                        g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                        FontMetrics fm = g2.getFontMetrics();
                        g2.drawString(e.getValue(), w - padX - fm.stringWidth(e.getValue()), y + 18);
                        y += rowH;
                    }
                }
            }

            @Override public Dimension getPreferredSize() {
                int n   = lineItems.size();
                int est = 36 + n * 36 + 60; // header padding + rows + extra for separators / total
                return new Dimension(416, Math.max(200, est));
            }
        };
        card.setOpaque(false);

        JScrollPane scroll = new JScrollPane(card);
        scroll.setBorder(BorderFactory.createEmptyBorder(16, 16, 8, 16));
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        return scroll;
    }

    // ── BOTTOM SECTION ───────────────────────────────────────────────────────
    private JPanel buildBottomSection() {
        JPanel bottom = new JPanel();
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setBackground(WARM_CREAM);
        bottom.setBorder(BorderFactory.createEmptyBorder(4, 16, 16, 16));

        // Thank-you label
        JLabel thanks = new JLabel("Thank you for choosing FleetHub!", SwingConstants.CENTER);
        thanks.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        thanks.setForeground(SAGE_GREEN);
        thanks.setAlignmentX(Component.CENTER_ALIGNMENT);
        thanks.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        bottom.add(thanks);

        // Save-buttons row
        JPanel btnRow = new JPanel(new GridLayout(1, 2, 8, 0));
        btnRow.setOpaque(false);
        btnRow.setMaximumSize(new Dimension(448, 40));

        JButton btnHtml = makeButton("Save as HTML", SAGE_GREEN, Color.WHITE);
        JButton btnTxt  = makeButton("Save as TXT",  DARK_SLATE, Color.WHITE);
        btnRow.add(btnHtml);
        btnRow.add(btnTxt);
        bottom.add(btnRow);
        bottom.add(Box.createVerticalStrut(8));

        // Close button (full width)
        JButton btnClose = makeButton("Close", TERRACOTTA, Color.WHITE);
        btnClose.setMaximumSize(new Dimension(448, 40));
        btnClose.setAlignmentX(Component.CENTER_ALIGNMENT);
        bottom.add(btnClose);

        // ── Listeners ──
        btnHtml.addActionListener(e -> saveAsHtml());
        btnTxt .addActionListener(e -> saveAsTxt());
        btnClose.addActionListener(e -> dispose());

        return bottom;
    }

    private JButton makeButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? bg.darker() : bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(200, 38));
        return btn;
    }

    // ── Save helpers ─────────────────────────────────────────────────────────

    private void saveAsHtml() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Save Receipt as HTML");
        fc.setSelectedFile(new File("receipt.html"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File f = fc.getSelectedFile();
        if (!f.getName().toLowerCase().endsWith(".html")) {
            f = new File(f.getAbsolutePath() + ".html");
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
            pw.println(buildHtml());
            JOptionPane.showMessageDialog(this, "HTML saved:\n" + f.getAbsolutePath(),
                    "Saved", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveAsTxt() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Save Receipt as TXT");
        fc.setSelectedFile(new File("receipt.txt"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File f = fc.getSelectedFile();
        if (!f.getName().toLowerCase().endsWith(".txt")) {
            f = new File(f.getAbsolutePath() + ".txt");
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
            pw.println(buildTxt());
            JOptionPane.showMessageDialog(this, "TXT saved:\n" + f.getAbsolutePath(),
                    "Saved", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── HTML builder ─────────────────────────────────────────────────────────
    private String buildHtml() {
        java.util.List<Map.Entry<String,String>> entries = new ArrayList<>(lineItems.entrySet());
        int n = entries.size();
        int dashIdx = n - 3;

        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n")
          .append("<meta charset=\"UTF-8\">\n")
          .append("<title>FleetHub Receipt</title>\n")
          .append("<style>\n")
          .append("  body { font-family: 'Segoe UI', Arial, sans-serif; background:#FBF8F1; margin:0; padding:20px; }\n")
          .append("  .container { max-width:480px; margin:0 auto; background:#fff; border-radius:12px;")
          .append(" box-shadow:0 2px 12px rgba(0,0,0,.12); overflow:hidden; }\n")
          .append("  .header { background:#2C3531; color:#fff; padding:24px 20px 20px; text-align:center; }\n")
          .append("  .header h1 { margin:0; font-size:26px; font-weight:700; letter-spacing:1px; }\n")
          .append("  .header .sub { color:#88A47C; font-size:13px; font-weight:700; letter-spacing:3px; margin-top:4px; }\n")
          .append("  .header .dt  { color:#CCC; font-size:11px; margin-top:6px; }\n")
          .append("  .body { padding:20px; }\n")
          .append("  table { width:100%; border-collapse:collapse; }\n")
          .append("  td { padding:9px 4px; font-size:14px; color:#2C3531; }\n")
          .append("  td.val { text-align:right; font-weight:600; }\n")
          .append("  .dashed { border-top:1px dashed #CCC3B8; height:1px; margin:4px 0; }\n")
          .append("  .thick  { border-top:2px solid #2C3531; margin:6px 0; }\n")
          .append("  .gt-key { font-size:16px; font-weight:700; color:#2C3531; }\n")
          .append("  .gt-val { font-size:20px; font-weight:700; color:#E27355; text-align:right; }\n")
          .append("  .footer { text-align:center; padding:14px; color:#88A47C; font-style:italic; font-size:12px;")
          .append(" background:#FBF8F1; }\n")
          .append("</style>\n</head>\n<body>\n")
          .append("<div class=\"container\">\n")
          .append("  <div class=\"header\">\n")
          .append("    <h1>FleetHub</h1>\n")
          .append("    <div class=\"sub\">").append(isReturn ? "RETURN RECEIPT" : "RECEIPT").append("</div>\n")
          .append("    <div class=\"dt\">").append(esc(dateTimeStr)).append("</div>\n")
          .append("  </div>\n")
          .append("  <div class=\"body\">\n")
          .append("    <table>\n");

        for (int i = 0; i < n; i++) {
            Map.Entry<String,String> e = entries.get(i);

            if (i == Math.max(0, dashIdx)) {
                sb.append("    </table><div class=\"dashed\"></div><table>\n");
            }
            if (i == n - 1) {
                sb.append("    </table><div class=\"thick\"></div><table>\n");
                sb.append("      <tr><td class=\"gt-key\">").append(esc(e.getKey())).append("</td>")
                  .append("<td class=\"gt-val\">").append(esc(e.getValue())).append("</td></tr>\n");
            } else {
                sb.append("      <tr><td>").append(esc(e.getKey())).append("</td>")
                  .append("<td class=\"val\">").append(esc(e.getValue())).append("</td></tr>\n");
            }
        }

        sb.append("    </table>\n")
          .append("  </div>\n")
          .append("  <div class=\"footer\">Thank you for choosing FleetHub!</div>\n")
          .append("</div>\n</body>\n</html>");
        return sb.toString();
    }

    private static String esc(String s) {
        return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;");
    }

    // ── TXT builder ──────────────────────────────────────────────────────────
    private String buildTxt() {
        java.util.List<Map.Entry<String,String>> entries = new ArrayList<>(lineItems.entrySet());
        int n = entries.size();
        int dashIdx = n - 3;
        int W = 46;

        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(W)).append("\n");
        sb.append(centre("FleetHub", W)).append("\n");
        sb.append(centre(isReturn ? "RETURN RECEIPT" : "RECEIPT", W)).append("\n");
        sb.append(centre(dateTimeStr, W)).append("\n");
        sb.append("=".repeat(W)).append("\n\n");

        for (int i = 0; i < n; i++) {
            Map.Entry<String,String> e = entries.get(i);

            if (i == Math.max(0, dashIdx)) {
                sb.append("-".repeat(W)).append("\n");
            }
            if (i == n - 1) {
                sb.append("=".repeat(W)).append("\n");
                String k = e.getKey(), v = e.getValue();
                int spaces = W - k.length() - v.length();
                sb.append(k).append(" ".repeat(Math.max(1, spaces))).append(v).append("\n");
            } else {
                String k = e.getKey(), v = e.getValue();
                int spaces = W - k.length() - v.length();
                sb.append(k).append(" ".repeat(Math.max(1, spaces))).append(v).append("\n");
            }
        }

        sb.append("\n").append("=".repeat(W)).append("\n");
        sb.append(centre("Thank you for choosing FleetHub!", W)).append("\n");
        sb.append("=".repeat(W)).append("\n");
        return sb.toString();
    }

    private static String centre(String s, int w) {
        if (s.length() >= w) return s;
        int pad = (w - s.length()) / 2;
        return " ".repeat(pad) + s;
    }
}
