package Swing;

import Swing.Foods.FoodItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Dimension;
import java.util.List;

public class App extends JFrame {

    private static final String APP_TITLE = "Pay It Forkward";
    private static final String PLACEHOLDER_USER = "supplier1";
    private static final String ROLE_SUPPLIER = "Supplier";

    private static final Dimension SIZE_DEFAULT = new Dimension(600, 400);
    private static final Dimension SIZE_DASHBOARD = new Dimension(780, 520);

    private static final String[] FOOD_TYPES = {"Perishable", "Non-perishable"};
    private static final String[] DISPATCH_STRATEGIES = {
            "Urgency first (shortest expiry)",
            "Volume first (highest quantity)"
    };

    private static final String[] FEED_COLUMNS = {"ID", "Food", "Qty", "Type", "Hrs left", "Donor"};
    private static final String[] ACTIVE_COLUMNS = {"ID", "Food", "Qty", "Recipient", "Volunteer", "Status"};
    private static final String[] RECIPIENT_COLUMNS =
            {"ID", "Food", "Qty", "Type", "Hrs left", "Donor", "Select"};

    private static final int[] FEED_WIDTHS = {50, 150, 50, 110, 70, 170};
    private static final int[] ACTIVE_WIDTHS = {50, 150, 50, 170, 120, 90};

    private static final int COL_ID = 0;
    private static final int COL_FOOD = 1;
    private static final int COL_QTY = 2;
    private static final int COL_ACTIVE_STATUS = 5;
    private static final int COL_SELECT = 6;

    private JPanel pnlMain;
    private JPanel selectWindow;
    private JButton SelectSupplier;
    private JButton SelectRecipient;

    private JPanel supplierWindow;
    private JComboBox<String> supplierCB;
    private JButton supplierConfirm;
    private JButton supplierBack;

    private JPanel recipientWindow;
    private JComboBox<String> recipientCB;
    private JButton recipientConfirm;
    private JButton recipientBack;

    private JPanel mainRecipientWindow;
    private JComboBox<String> recipientTableCB;
    private JButton ClaimButton;
    private JPanel urgencyTablePanel;
    private JPanel volumeTablePanel;
    private JTable urgencyTable;
    private JTable volumeTable;

    private JPanel dashboardWindow;
    private JTabbedPane dashboardTabs;
    private JButton btnBackToMenu;

    private JComboBox<String> foodTypeCB;
    private JTextField txtFoodName;
    private JTextField txtQuantity;
    private JLabel lblExpiry;
    private JTextField txtExpiry;
    private JCheckBox chkFlag;
    private JButton btnAddSurplus;

    private JComboBox<String> strategyCB;
    private JTable feedTable;
    private JButton btnClaim;

    private JTable activeTable;
    private JTextField txtVolunteer;
    private JButton btnAssign;
    private JButton btnDelivered;
    private JTextField CAPasswordTF;
    private JButton createAccBtn;
    private JPanel loginWindow;
    private JTextField CAUsernameTF;
    private JPanel createAccountWindow;
    private JTextField LogPasswordBtn;
    private JButton LogInBtn;
    private JButton SignUpBtn;
    private JTextField LogUsernameBtn;

    private final DispatchManager dispatchManager = new DispatchManager();
    private final PanelManager PM = new PanelManager();
    private final PanelManager recipientTablePM = new PanelManager();
    private final DefaultTableModel feedModel = createReadOnlyModel(FEED_COLUMNS);
    private final DefaultTableModel activeModel = createReadOnlyModel(ACTIVE_COLUMNS);

    private String establishment = "";
    private String donorName = PLACEHOLDER_USER;

    App() {
        setTitle(APP_TITLE);
        setContentPane(pnlMain);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        registerWindows();
        initMenuNavigation();
        initDonorPortal();
        initDispatchFeed();
        initActiveDeliveries();
        initRecipientSection();

        showMenu();
        setVisible(true);
    }

    private void registerWindows() {
        PM.add(selectWindow);
        PM.add(supplierWindow);
        PM.add(recipientWindow);
        PM.add(mainRecipientWindow);
        PM.add(dashboardWindow);
        PM.add(createAccountWindow);
        PM.add(loginWindow);
    }

    private void initMenuNavigation() {
        SelectSupplier.addActionListener(e -> showSupplierEstablishment());
        SelectRecipient.addActionListener(e -> showRecipientEstablishment());
        supplierBack.addActionListener(e -> showMenu());
        recipientBack.addActionListener(e -> showMenu());
        btnBackToMenu.addActionListener(e -> showMenu());
        supplierConfirm.addActionListener(e -> openDashboard());
        recipientConfirm.addActionListener(e -> showRecipientHome());
    }

    private void showMenu() {
        display(selectWindow, APP_TITLE, SIZE_DEFAULT);
    }

    private void showSupplierEstablishment() {
        display(supplierWindow, APP_TITLE + " - Supplier", SIZE_DEFAULT);
    }

    private void showRecipientEstablishment() {
        display(recipientWindow, APP_TITLE + " - Recipient", SIZE_DEFAULT);
    }

    private void showRecipientHome() {
        display(mainRecipientWindow, APP_TITLE + " - Recipient", SIZE_DEFAULT);
    }

    private void openDashboard() {
        Object selected = supplierCB.getSelectedItem();
        if (selected == null) {
            showWarning("No Selection", "Please select your establishment first.");
            return;
        }
        establishment = selected.toString();
        donorName = PLACEHOLDER_USER + " (" + establishment + ")";

        clearDonorForm();
        dashboardTabs.setSelectedIndex(0);
        refreshTables();
        display(dashboardWindow,
                APP_TITLE + " - Dashboard (" + PLACEHOLDER_USER + " | " + ROLE_SUPPLIER + " | " + establishment + ")",
                SIZE_DASHBOARD);
    }

    private void display(JPanel target, String title, Dimension size) {
        PM.show(target);
        setTitle(title);
        if (!getSize().equals(size)) {
            setSize(size);
            setLocationRelativeTo(null);
        }
        pnlMain.revalidate();
        pnlMain.repaint();
    }

    private void initDonorPortal() {
        foodTypeCB.setModel(new DefaultComboBoxModel<>(FOOD_TYPES));
        foodTypeCB.addActionListener(e -> updateDonorLabels());
        updateDonorLabels();

        btnAddSurplus.addActionListener(e -> guard(() -> {
            dispatchManager.addFoodItem(isPerishableSelected(), txtFoodName.getText(),
                    txtQuantity.getText(), txtExpiry.getText(), chkFlag.isSelected(), donorName);
            showInfo("Success", "Surplus added to the dispatch feed.");
            clearDonorForm();
            refreshTables();
        }));
    }

    private boolean isPerishableSelected() {
        return foodTypeCB.getSelectedIndex() == 0;
    }

    private void updateDonorLabels() {
        boolean perishable = isPerishableSelected();
        lblExpiry.setText(perishable ? "Hours until expiration" : "Days until best-before");
        chkFlag.setText(perishable ? "Requires refrigeration" : "Packaging intact");
        chkFlag.setSelected(!perishable);
    }

    private void clearDonorForm() {
        foodTypeCB.setSelectedIndex(0);
        txtFoodName.setText("");
        txtQuantity.setText("");
        txtExpiry.setText("");
        updateDonorLabels();
    }

    private void initDispatchFeed() {
        configureTable(feedTable, feedModel, FEED_WIDTHS);
        strategyCB.setModel(new DefaultComboBoxModel<>(DISPATCH_STRATEGIES));

        strategyCB.addActionListener(e -> {
            dispatchManager.setDispatchStrategy(strategyCB.getSelectedIndex() == 0
                    ? new UrgencyFirstStrategy()
                    : new VolumeFirstStrategy());
            refreshTables();
        });

        btnClaim.addActionListener(e -> guard(() -> {
            int row = feedTable.getSelectedRow();
            if (row < 0) {
                showWarning("No Selection", "Select an available batch first.");
                return;
            }
            String id = (String) feedModel.getValueAt(row, COL_ID);
            dispatchManager.claim(id, donorName);
            refreshTables();
            showInfo("Claimed", "Batch " + id + " claimed. See Active Deliveries.");
        }));
    }

    private void initActiveDeliveries() {
        configureTable(activeTable, activeModel, ACTIVE_WIDTHS);

        btnAssign.addActionListener(e -> guard(() -> {
            int row = activeTable.getSelectedRow();
            if (row < 0 || !FoodItem.CLAIMED.equals(activeModel.getValueAt(row, COL_ACTIVE_STATUS))) {
                showWarning("No Selection", "Select a CLAIMED batch first.");
                return;
            }
            dispatchManager.assignVolunteer((String) activeModel.getValueAt(row, COL_ID),
                    txtVolunteer.getText());
            txtVolunteer.setText("");
            refreshTables();
        }));

        btnDelivered.addActionListener(e -> guard(() -> {
            int row = activeTable.getSelectedRow();
            if (row < 0 || !FoodItem.ON_ROUTE.equals(activeModel.getValueAt(row, COL_ACTIVE_STATUS))) {
                showWarning("No Selection", "Select an ON ROUTE delivery.");
                return;
            }
            dispatchManager.markDelivered((String) activeModel.getValueAt(row, COL_ID));
            refreshTables();
        }));
    }

    private void refreshTables() {
        feedModel.setRowCount(0);
        for (FoodItem item : dispatchManager.processMatching()) {
            feedModel.addRow(new Object[]{item.getBatchId(), item.getName(), item.getQuantity(),
                    item.getTypeName(), item.getHoursLeft(), item.getDonorName()});
        }
        activeModel.setRowCount(0);
        List<FoodItem> active = dispatchManager.getActiveDeliveries();
        for (FoodItem item : active) {
            activeModel.addRow(new Object[]{item.getBatchId(), item.getName(), item.getQuantity(),
                    item.getClaimedBy(), item.getVolunteer(), item.getStatus()});
        }
    }

    private void initRecipientSection() {
        recipientTablePM.add(urgencyTablePanel);
        recipientTablePM.add(volumeTablePanel);
        recipientTablePM.show(urgencyTablePanel);

        populateRecipientTables();

        recipientTableCB.addActionListener(e -> recipientTablePM.show(
                recipientTableCB.getSelectedIndex() == 0 ? urgencyTablePanel : volumeTablePanel));
        ClaimButton.addActionListener(e -> claimCheckedItems());
    }

    private void populateRecipientTables() {
        recipientTableCB.removeAllItems();
        recipientTableCB.addItem(DISPATCH_STRATEGIES[0]);
        recipientTableCB.addItem(DISPATCH_STRATEGIES[1]);

        Object[][] urgencyData = {
                {"B003", "Cooked rice", 25, "Perishable", 4, "Casa Resto", false},
                {"B001", "Bread rolls", 40, "Perishable", 6, "Sunrise Bakery", false},
                {"B002", "Canned sardines", 120, "Non-perishable", 720, "Metro Supermart", false},
                {"B004", "Instant noodles", 60, "Non-perishable", 2160, "Metro Supermart", false}
        };
        Object[][] volumeData = {
                {"B002", "Canned sardines", 120, "Non-perishable", 720, "Metro Supermart", false},
                {"B004", "Instant noodles", 60, "Non-perishable", 2160, "Metro Supermart", false},
                {"B001", "Bread rolls", 40, "Perishable", 6, "Sunrise Bakery", false},
                {"B003", "Cooked rice", 25, "Perishable", 4, "Casa Resto", false}
        };

        urgencyTable.setModel(createCheckboxTableModel(urgencyData));
        volumeTable.setModel(createCheckboxTableModel(volumeData));
    }

    private DefaultTableModel createCheckboxTableModel(Object[][] data) {
        return new DefaultTableModel(data, RECIPIENT_COLUMNS) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == COL_SELECT ? Boolean.class : super.getColumnClass(columnIndex);
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == COL_SELECT;
            }
        };
    }

    private void claimCheckedItems() {
        JTable visibleTable = recipientTableCB.getSelectedIndex() == 0 ? urgencyTable : volumeTable;

        StringBuilder claimedList = new StringBuilder();
        int count = 0;

        for (int row = 0; row < visibleTable.getRowCount(); row++) {
            if (Boolean.TRUE.equals(visibleTable.getValueAt(row, COL_SELECT))) {
                claimedList.append("- ").append(visibleTable.getValueAt(row, COL_FOOD))
                        .append(" (ID: ").append(visibleTable.getValueAt(row, COL_ID))
                        .append(", Qty: ").append(visibleTable.getValueAt(row, COL_QTY))
                        .append(")\n");
                visibleTable.setValueAt(false, row, COL_SELECT);
                count++;
            }
        }

        if (count == 0) {
            showWarning("No Item Selected", "Please check at least one food item to claim.");
        } else {
            showInfo("Claim Confirmed", "Successfully claimed:\n" + claimedList);
        }
    }

    private static DefaultTableModel createReadOnlyModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private static void configureTable(JTable table, DefaultTableModel model, int[] widths) {
        table.setModel(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setReorderingAllowed(false);
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    @FunctionalInterface
    private interface GuardedAction {
        void run() throws InvalidFoodInputException;
    }

    private void guard(GuardedAction action) {
        try {
            action.run();
        } catch (InvalidFoodInputException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Invalid Input", JOptionPane.ERROR_MESSAGE);
        } catch (InventoryStorageException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Storage Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showInfo(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    private void showWarning(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.WARNING_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.err.println("System look and feel unavailable: " + e.getMessage());
            }
            try {
                new App();
            } catch (InventoryStorageException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Storage Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}
