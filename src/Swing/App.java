package Swing;

import javax.swing.*;
import java.util.Scanner;

public class App extends JFrame{
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
    private JPanel dashboardWindow;
    private JTabbedPane dashboardTabs;
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
    private JTable activeTable;
    private JTextField txtVolunteer;
    private JButton btnAssign;
    private JButton btnDelivered;
    private JButton btnBackToMenu;

    App(){
        this.setContentPane(pnlMain);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(600,400);
        this.setVisible(true);
        this.PM = new PanelManager();

        PM.add(selectWindow);
        PM.add(supplierWindow);
        PM.add(recipientWindow);
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
        supplierConfirm.addActionListener(e -> {
            PM.show(dashboardWindow);
        });
    }

    public static void main(String[] args) {
        App app = new App();
        // ONLY FOR DEBUGGING/TESTING {{{{
        Scanner sc = new Scanner(System.in);
        while(true) {
            System.out.print("Op: ");
            String consoleInput = sc.nextLine();
            switch (consoleInput){
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
        // }}}}
    }
}
