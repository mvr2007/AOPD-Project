import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class MainAppFrame extends JFrame {
    private FleetDAO fleetDAO;
    private JTabbedPane tabbedPane;
    private JTable inventoryTable;
    private JTable rentTable;
    private JTable activeRentalsTable;
    private JLabel revenueLabel;

    public MainAppFrame() {
        fleetDAO = new FleetDAO();
        
        setTitle("FleetWise Hub: Smart Vehicle Rental & Fleet Management System");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(CozyTheme.WARM_CREAM);
        
        UIManager.put("TabbedPane.background", CozyTheme.WARM_CREAM);
        UIManager.put("TabbedPane.foreground", CozyTheme.DARK_SLATE);
        UIManager.put("TabbedPane.selected", CozyTheme.SEC_PANEL);
        UIManager.put("Panel.background", CozyTheme.WARM_CREAM);
        
        initUI();
    }
    
    private void initUI() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(CozyTheme.HEADER_FONT);
        
        tabbedPane.addTab("Fleet Inventory", createInventoryPanel());
        tabbedPane.addTab("Rent Vehicle", createRentPanel());
        tabbedPane.addTab("Active Rentals", createActiveRentalsPanel());
        tabbedPane.addTab("Analytics/Billing", createAnalyticsPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private void styleTable(JTable table) {
        table.setFont(CozyTheme.MAIN_FONT);
        table.setRowHeight(30);
        table.setBackground(Color.WHITE);
        table.setForeground(CozyTheme.DARK_SLATE);
        table.setSelectionBackground(CozyTheme.SAGE_GREEN);
        table.setSelectionForeground(Color.WHITE);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setShowGrid(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(CozyTheme.SEC_PANEL);
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for(int x=0; x<table.getColumnCount(); x++){
            table.getColumnModel().getColumn(x).setCellRenderer(centerRenderer);
        }

        JTableHeader header = table.getTableHeader();
        header.setBackground(CozyTheme.SAGE_GREEN);
        header.setForeground(Color.WHITE);
        header.setFont(CozyTheme.HEADER_FONT);
        header.setOpaque(false);
    }
    
    private JPanel createInventoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        inventoryTable = new JTable(fleetDAO.getAllVehicles());
        styleTable(inventoryTable);
        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        scrollPane.getViewport().setBackground(CozyTheme.WARM_CREAM);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(CozyTheme.SEC_PANEL);
        JButton btnAdd = new JButton("Add Vehicle");
        styleButton(btnAdd, CozyTheme.SAGE_GREEN);
        btnAdd.addActionListener(e -> showAddVehicleModal());
        JButton btnRefresh = new JButton("Refresh");
        styleButton(btnRefresh, CozyTheme.TERRACOTTA);
        btnRefresh.addActionListener(e -> refreshTables());
        
        actionPanel.add(btnAdd);
        actionPanel.add(btnRefresh);
        panel.add(actionPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createRentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        rentTable = new JTable(fleetDAO.getAvailableVehicles());
        styleTable(rentTable);
        JScrollPane scrollPane = new JScrollPane(rentTable);
        scrollPane.getViewport().setBackground(CozyTheme.WARM_CREAM);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(CozyTheme.SEC_PANEL);
        JButton btnRent = new JButton("Rent Selected Vehicle");
        styleButton(btnRent, CozyTheme.SAGE_GREEN);
        btnRent.addActionListener(e -> showRentModal());
        
        actionPanel.add(btnRent);
        panel.add(actionPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createActiveRentalsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        activeRentalsTable = new JTable(fleetDAO.getActiveRentals());
        styleTable(activeRentalsTable);
        JScrollPane scrollPane = new JScrollPane(activeRentalsTable);
        scrollPane.getViewport().setBackground(CozyTheme.WARM_CREAM);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(CozyTheme.SEC_PANEL);
        JButton btnReturn = new JButton("Return Vehicle");
        styleButton(btnReturn, CozyTheme.TERRACOTTA);
        btnReturn.addActionListener(e -> processReturn());
        
        actionPanel.add(btnReturn);
        panel.add(actionPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createAnalyticsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(CozyTheme.WARM_CREAM);
        
        revenueLabel = new JLabel("Total Revenue: $" + fleetDAO.calculateTotalRevenue());
        revenueLabel.setFont(CozyTheme.TITLE_FONT);
        revenueLabel.setForeground(CozyTheme.DARK_SLATE);
        
        JButton refreshBtn = new JButton("Update Analytics");
        styleButton(refreshBtn, CozyTheme.SAGE_GREEN);
        refreshBtn.addActionListener(e -> {
            revenueLabel.setText("Total Revenue: $" + fleetDAO.calculateTotalRevenue());
        });
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(10,10,20,10);
        panel.add(revenueLabel, gbc);
        gbc.gridy = 1;
        panel.add(refreshBtn, gbc);
        
        return panel;
    }
    
    private void styleButton(JButton btn, Color bgColor) {
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFont(CozyTheme.HEADER_FONT);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(8, 15, 8, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    private void showAddVehicleModal() {
        JDialog dialog = new JDialog(this, "Add New Vehicle", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(CozyTheme.WARM_CREAM);
        
        JTextField tfModel = new JTextField();
        JTextField tfCat = new JTextField();
        JTextField tfRate = new JTextField();
        JComboBox<String> cbStatus = new JComboBox<>(new String[]{"Available", "Maintenance"});
        JTextField tfFuel = new JTextField();
        
        panel.add(new JLabel("Model Name:")); panel.add(tfModel);
        panel.add(new JLabel("Category:")); panel.add(tfCat);
        panel.add(new JLabel("Daily Rate:")); panel.add(tfRate);
        panel.add(new JLabel("Status:")); panel.add(cbStatus);
        panel.add(new JLabel("Fuel Type:")); panel.add(tfFuel);
        
        JButton btnSave = new JButton("Save");
        styleButton(btnSave, CozyTheme.SAGE_GREEN);
        btnSave.addActionListener(e -> {
            try {
                double rate = Double.parseDouble(tfRate.getText());
                if(fleetDAO.addVehicle(tfModel.getText(), tfCat.getText(), rate, (String)cbStatus.getSelectedItem(), tfFuel.getText())) {
                    JOptionPane.showMessageDialog(dialog, "Vehicle added successfully!");
                    refreshTables();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to add vehicle.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch(NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid Daily Rate.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        panel.add(new JLabel()); panel.add(btnSave);
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void showRentModal() {
        int row = rentTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an available vehicle to rent.");
            return;
        }
        
        int vehicleId = (int) rentTable.getValueAt(row, 0);
        double rate = (double) rentTable.getValueAt(row, 3);
        
        JDialog dialog = new JDialog(this, "Rent Vehicle", true);
        dialog.setSize(300, 250);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(CozyTheme.WARM_CREAM);
        
        JTextField tfCustomer = new JTextField("1");
        JTextField tfDays = new JTextField();
        
        panel.add(new JLabel("Customer ID:")); panel.add(tfCustomer);
        panel.add(new JLabel("Rental Days:")); panel.add(tfDays);
        
        JButton btnProcess = new JButton("Process Rental");
        styleButton(btnProcess, CozyTheme.SAGE_GREEN);
        btnProcess.addActionListener(e -> {
            try {
                int cid = Integer.parseInt(tfCustomer.getText());
                int days = Integer.parseInt(tfDays.getText());
                double total = rate * days;
                if(fleetDAO.rentVehicle(vehicleId, cid, days, total)) {
                    JOptionPane.showMessageDialog(dialog, "Rental successful! Total: $" + total);
                    refreshTables();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Rental failed.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid Input.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        panel.add(new JLabel()); panel.add(btnProcess);
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void processReturn() {
        int row = activeRentalsTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an active rental to return.");
            return;
        }
        
        int rentalId = (int) activeRentalsTable.getValueAt(row, 0);
        int vehicleId = (int) activeRentalsTable.getValueAt(row, 1);
        
        if (fleetDAO.returnVehicle(rentalId, vehicleId)) {
            JOptionPane.showMessageDialog(this, "Vehicle returned successfully!");
            refreshTables();
            revenueLabel.setText("Total Revenue: $" + fleetDAO.calculateTotalRevenue());
        } else {
            JOptionPane.showMessageDialog(this, "Failed to return vehicle.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void refreshTables() {
        inventoryTable.setModel(fleetDAO.getAllVehicles());
        rentTable.setModel(fleetDAO.getAvailableVehicles());
        activeRentalsTable.setModel(fleetDAO.getActiveRentals());
        styleTable(inventoryTable);
        styleTable(rentTable);
        styleTable(activeRentalsTable);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {}
        
        SwingUtilities.invokeLater(() -> {
            new MainAppFrame().setVisible(true);
        });
    }
}
