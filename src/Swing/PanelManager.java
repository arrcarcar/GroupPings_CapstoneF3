package Swing;

import javax.swing.JPanel;
import java.util.ArrayList;
import java.util.List;

public class PanelManager {

    private final List<JPanel> panels = new ArrayList<>();
    private JPanel current;

    public void add(JPanel panel) {
        if (panel == null) {
            throw new IllegalArgumentException("Cannot register a null panel.");
        }
        if (!panels.contains(panel)) {
            panels.add(panel);
        }
    }
    //comment
    public void hideAll() {
        for (JPanel panel : panels) {
            panel.setVisible(false);
        }
        current = null;
    }

    public void show(JPanel target) {
        if (!panels.contains(target)) {
            throw new IllegalArgumentException("Panel is not registered with this PanelManager.");
        }
        for (JPanel panel : panels) {
            panel.setVisible(panel == target);
        }
        current = target;
    }

    public JPanel getCurrent() {
        return current;
    }
}
