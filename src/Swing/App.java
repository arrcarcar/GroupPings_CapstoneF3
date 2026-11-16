///
package Swing;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.Scanner;

public class App extends JFrame {
    private PanelManager PM;
    private JPanel pnlMain;
    private JButton SelectSupplier;
    private JButton SelectRecipient;
    private JPanel selectWindow;
    private JPanel supplierWindow;
    private JComboBox supplierCB;
    private JPanel recipientWindow;
    private JComboBox recipientCB;
    private JButton supplierConfirm;
    private JButton recipientConfirm;
    private JButton supplierBack;
    private JButton recipientBack;
    private JComboBox recipientTableCB;
    private JButton ClaimButton;
    private JPanel mainRecipientWindow;
    private JPanel urgencyTablePanel;
    private JPanel volumeTablePanel;
    private JPanel dashboardWindow;
    private JTabbedPane dashboardTabs;
    private JButton btnBackToMenu;
    private JComboBox foodTypeCB;
    private JTextField txtFoodName;
    private JTextField txtQuantity;
    private JLabel lblExpiry;
    private JTextField txtExpiry;
    private JCheckBox chkFlag;
    private JButton btnAddSurplus;
    private JComboBox strategyCB;
    private JTable feedTable;
    private JButton btnClaim;
    private JTextField txtVolunteer;
    private JButton btnAssign;
    private JButton btnDelivered;
    private JTable urgencyTable;
    private JTable volumeTable;
    private JTable activeTable;

    App() {
        this.setContentPane(pnlMain);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(600, 400);
        this.setVisible(true);
        this.PM = new PanelManager();

        PM.add(selectWindow);
        PM.add(supplierWindow);
        PM.add(recipientWindow);
        PM.add(mainRecipientWindow);
        PM.add(dashboardWindow);
        PM.setFirstPanel(selectWindow);

        PM.hideAll();
        PM.show(selectWindow);

        SelectSupplier.addActionListener(e -> {
            PM.show(supplierWindow);
        });
        SelectRecipient.addActionListener(e -> {
            PM.show(recipientWindow);
        });
        supplierBack.addActionListener(e -> {
            PM.show(selectWindow);
        });
        recipientBack.addActionListener(e -> {
            PM.show(selectWindow);
        });
        recipientConfirm.addActionListener(e -> {
            PM.show(mainRecipientWindow);
        });
        supplierConfirm.addActionListener(e->{
            PM.show(dashboardWindow);
        });

        setupTables();

        PanelManager tablePM = new PanelManager(urgencyTablePanel);
        tablePM.add(volumeTablePanel);
        tablePM.hideAll();
        tablePM.show(urgencyTablePanel);

        recipientTableCB.addActionListener(e -> {
            if (recipientTableCB.getSelectedIndex() == 0) {
                tablePM.show(urgencyTablePanel);
            } else {
                tablePM.show(volumeTablePanel);
            }
        });

        // 2. Action listener for the "Claim Selected" button (button1)
        ClaimButton.addActionListener(e -> claimCheckedItems());

        this.setVisible(true);
    }

    private void setupTables() {
        recipientTableCB.removeAllItems();
        recipientTableCB.addItem("Urgency first (shortest expiry)");
        recipientTableCB.addItem("Volume first (highest quantity)");

        // Added 7th column "Select" at index 6 for the checkbox
        String[] columns = {"ID", "Food", "Qty", "Type", "Hrs left", "Donor", "Select"};

        // Added 'false' at the end of every row for the unchecked checkbox
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

        urgencyTable.setModel(createCheckboxTableModel(urgencyData, columns));
        volumeTable.setModel(createCheckboxTableModel(volumeData, columns));
    }

    // Helper method that tells JTable to render column 6 ("Select") as a Checkbox
    private DefaultTableModel createCheckboxTableModel(Object[][] data, String[] columns) {
        return new DefaultTableModel(data, columns) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 6) {
                    return Boolean.class; // Turns column 6 into a JCheckBox
                }
                return super.getColumnClass(columnIndex);
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Only allow clicking the checkbox, lock text cells
            }
        };
    }

    // Checks which rows are ticked and shows a dialog box
    private void claimCheckedItems() {
        // Check which table is currently visible based on the dropdown
        JTable activeTable = (recipientTableCB.getSelectedIndex() == 0) ? urgencyTable : volumeTable;

        StringBuilder claimedList = new StringBuilder();
        int count = 0;

        for (int i = 0; i < activeTable.getRowCount(); i++) {
            Boolean isChecked = (Boolean) activeTable.getValueAt(i, 6);
            if (Boolean.TRUE.equals(isChecked)) {
                String id = activeTable.getValueAt(i, 0).toString();
                String food = activeTable.getValueAt(i, 1).toString();
                String qty = activeTable.getValueAt(i, 2).toString();

                claimedList.append("- ").append(food)
                        .append(" (ID: ").append(id)
                        .append(", Qty: ").append(qty).append(")\n");
                count++;

                // Optional: Uncheck the box after claiming
                activeTable.setValueAt(false, i, 6);
            }
        }

        if (count == 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please check at least one food item to claim.",
                    "No Item Selected",
                    JOptionPane.WARNING_MESSAGE
            );
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Successfully claimed:\n" + claimedList,
                    "Claim Confirmed",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }


    public static void main(String[] args) {
        App app = new App();
        // ONLY FOR DEBUGGING/TESTING {{{{
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print("Op: ");
            String consoleInput = sc.nextLine();
            switch (consoleInput) {
                case "hideAll":
                    app.PM.hideAll();
                    break;
                case "show":
                    System.out.print("Window to show: ");
                    consoleInput = sc.nextLine();
                    app.PM.show(app.PM.getPanelObj(Integer.parseInt(consoleInput)));
                    break;
                default:
                    break;
            }
        }
    }
}
