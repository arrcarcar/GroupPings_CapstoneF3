package Swing;

import javax.swing.*;
import java.awt.*;
import java.util.Scanner;

public class App extends JFrame{
    private JPanel lastActiveWindow;
    private PanelManager PM;
    private JPanel pnlMain;
    private JButton SelectSupplier;
    private JButton SelectRecipient;
    private JPanel selectWindow;
    private JPanel supplierWindow;
    private JComboBox supplierCB;
    private JPanel recipientWindow;
    private JComboBox recipientCB;

    App(){
        this.setContentPane(pnlMain);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(400,300);
        this.setVisible(true);
        this.PM = new PanelManager();
        lastActiveWindow = selectWindow;

        PM.add(selectWindow);
        PM.add(supplierWindow);
        PM.add(recipientWindow);

        SelectSupplier.addActionListener(e -> {
            lastActiveWindow.setVisible(false);
            lastActiveWindow = selectWindow;
            supplierWindow.setVisible(true);
        });

        SelectRecipient.addActionListener(e -> {
            lastActiveWindow.setVisible(false);
            lastActiveWindow = selectWindow;
            recipientWindow.setVisible(true);
        });
    }

    public void showWindow(String windowName){

    }

    public static void main(String[] args) {
        App app = new App();
        Scanner sc = new Scanner(System.in);
        while(true) {
            System.out.println("Op: ");
            String consoleInput = sc.nextLine();
            switch (consoleInput){
                case "hideAll":
                    app.PM.hideAll();
                    break;
                default:
                    break;
            }
        }
    }


}
