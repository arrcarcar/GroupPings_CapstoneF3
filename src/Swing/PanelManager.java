package Swing;

import javax.swing.*;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class PanelManager {
    private ArrayList<JPanel> panels;
    private JPanel lastActivePanel;
    private JPanel currentActivePanel;

    public PanelManager(ArrayList<JPanel> panels) {
        this.panels = panels;
    }

    public PanelManager(JPanel p) {
        this.panels = new ArrayList<JPanel>();
        add(p);
        this.lastActivePanel = p;
    }

    public PanelManager() {
        this.panels = new ArrayList<JPanel>();
    }

    public void add(JPanel newPanel) {
        panels.add(newPanel);
    }

    public void hideAll() {
        for (JPanel p : panels) {
            p.setVisible(false);

        }
    }

    public void show(JPanel p) {
        p.setVisible(true);
        if (p != lastActivePanel) {
            lastActivePanel.setVisible(false);
        }
        lastActivePanel = p;

    }

    public void show() {
        show(lastActivePanel);
    }

    public void setFirstPanel(JPanel p) {
        this.lastActivePanel = p;
    }

    public JPanel getPanelObj(int index) {
        return panels.get(index);
    }
    ////
}
