package client.view.utility;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.EventObject;
import java.util.Vector;

public class TableButtonEdit extends JButton implements TableCellRenderer, TableCellEditor {
    private int selectedRow;
    private int selectedColumn;
    Vector<TableButtonListener> listener;
    public TableButtonEdit(String text) {
        super(text);
        setOpaque(true);
        listener = new Vector<>();
        addActionListener(e -> {
            for(TableButtonListener l : listener) {
                l.tableButtonClicked(selectedRow, selectedColumn);
            }
        });
    }

    public void addTableButtonListener( TableButtonListener l ) {
        listener.add(l);
    }

    public void removeTableButtonListener( TableButtonListener l ) {
        listener.remove(l);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
        return this;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int col) {
        selectedRow = row;
        selectedColumn = col;
        return this;
    }

    @Override
    public void addCellEditorListener(CellEditorListener arg0) {
    }

    @Override
    public Object getCellEditorValue() {
        return "";
    }

    @Override
    public boolean isCellEditable(EventObject arg0) {
        return true;
    }

    @Override
    public void removeCellEditorListener(CellEditorListener arg0) {
    }

    @Override
    public boolean shouldSelectCell(EventObject arg0) {
        return false;
    }

    @Override
    public boolean stopCellEditing() {
        return true;
    }

    @Override
    public void cancelCellEditing() {

    }
}
