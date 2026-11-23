package Swing;

import javax.swing.AbstractCellEditor;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.DefaultFormatter;
import java.awt.Component;
import java.text.ParseException;


public class SpinnerEditor extends AbstractCellEditor implements TableCellEditor, TableCellRenderer {


    private final JSpinner renderSpinner = new JSpinner();
    private final JSpinner editSpinner = new JSpinner();

    private final int qtyColumn;

    private JTable editingTable;
    private int editingMax;

    public SpinnerEditor(int qtyColumn) {
        this.qtyColumn = qtyColumn;
    }


    private int prepare(JSpinner spinner, JTable table, Object value, int row) {
        int modelRow = table.convertRowIndexToModel(row);
        int available = (Integer) table.getModel().getValueAt(modelRow, qtyColumn);
        int current = (value == null) ? 0 : (Integer) value;
        spinner.setModel(new SpinnerNumberModel(Math.min(current, available), 0, available, 1));
        return available;
    }


    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        prepare(renderSpinner, table, value, row);
        return renderSpinner;
    }


    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
                                                 int row, int column) {
        editingTable = table;
        editingMax = prepare(editSpinner, table, value, row);
        watchTyping();
        return editSpinner;
    }


    private void watchTyping() {
        JFormattedTextField field = ((JSpinner.DefaultEditor) editSpinner.getEditor()).getTextField();


        ((DefaultFormatter) field.getFormatter()).setAllowsInvalid(true);

        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkTyped(field);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkTyped(field);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkTyped(field);
            }
        });
    }


    private void checkTyped(JFormattedTextField field) {
        String text = field.getText().trim();
        if (text.isEmpty() || text.equals("-")) {
            return;
        }
        try {
            int typed = Integer.parseInt(text);
            if (typed < 0 || typed > editingMax) {

                SwingUtilities.invokeLater(() -> {
                    showError("You cannot take more than what is available.\n"
                            + "Please enter a number from 0 to " + editingMax + ".");
                    field.setValue(editSpinner.getValue()); // back to the last valid number
                });
            }
        } catch (NumberFormatException e) {

        }
    }


    protected void showError(String message) {
        JOptionPane.showMessageDialog(editingTable, message, "Invalid Quantity", JOptionPane.ERROR_MESSAGE);
    }

    //comment
    @Override
    public Object getCellEditorValue() {
        return editSpinner.getValue();
    }


    @Override
    public boolean stopCellEditing() {
        try {
            editSpinner.commitEdit();
        } catch (ParseException e) {

            showError("Please enter a whole number from 0 to " + editingMax + ".");
            return false;
        }
        return super.stopCellEditing();
    }
}