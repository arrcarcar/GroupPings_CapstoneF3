package Swing;

import javax.swing.*;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class PanelManager {
    private ArrayList<JPanel> panels;
    public PanelManager(ArrayList<JPanel> panels){
        this.panels = panels;
    }

    public PanelManager(){
        this.panels = new ArrayList<JPanel>();
    }

    public void add(JPanel newPanel){
        panels.add(newPanel);
    }

    public void hideAll(){
        for(JPanel p : panels){
            p.setVisible(false);
        }
    }
}
