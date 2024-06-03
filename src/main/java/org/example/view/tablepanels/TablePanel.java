package org.example.view.tablepanels;

import org.example.view.MainMenu;
import org.example.view.utility.TableButtonDelete;
import org.example.view.utility.TableButtonEdit;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class TablePanel {
    protected final int FETCH_COUNT = 42;
    protected String tableName;
    protected final MainMenu mainMenu;
    protected final Connection connection;
    protected JTable table;
    protected DefaultTableModel tableModel;

    protected JPanel oldP;
    protected JPanel p;
    protected String prefix;

    protected String tableInitSelectQuery;
    protected String currentSelectQuery;
    protected HashMap<Integer,Integer> rowId = new HashMap<>();
    protected final JPanel buttonsP = new JPanel(new FlowLayout());
    protected int tableColumnsCount;
    protected int visibleColumnsCount;
    protected int currentPage = 0;
    protected int pagesCount = 0;
    protected final ArrayList<String> tableColumnsNames = new ArrayList<>();
    protected final ArrayList<String> visibleColumnsNames= new ArrayList<>();
    protected final ArrayList<String> unprocessedVisibleColumnsNames= new ArrayList<>();
    private void setSize(JComponent jComponent,int width, int height){
        jComponent.setPreferredSize(new Dimension(width,height));
        jComponent.setMaximumSize(new Dimension(width,height));
        jComponent.setMinimumSize(new Dimension(width,height));
    }
    protected void deleteRow(int row) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("DELETE FROM "+ tableName.toUpperCase() + " WHERE " + prefix + "_ID = " + rowId.get(row) + ";");
        int r = statement.executeUpdate();
        connection.commit();
        System.out.println(r);
        statement.close();
        drawTable(tableInitSelectQuery);
    }
    protected JDialog createDialog(JTextField [] dTextFields){
        JDialog jDialog = new JDialog();
        jDialog.setSize(1000,150);
        jDialog.setLayout(new GridLayout(3, visibleColumnsCount));
        for (int i = 0; i < visibleColumnsCount; i++){
            JLabel jLabel = new JLabel(visibleColumnsNames.get(i));
            jDialog.add(jLabel);
        }
        for (int i = 0; i < visibleColumnsCount; i++){
            dTextFields[i] = new JTextField();
            jDialog.add(dTextFields[i]);
        }
        JButton exitB = new JButton("Назад");
        exitB.addActionListener(e -> {
            jDialog.dispose();
            try {
                drawTable(currentSelectQuery);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        jDialog.add(exitB);
        for (int i = 0; i < visibleColumnsCount-2;i++) {
            jDialog.add(new JLabel());
        }
        return jDialog;
    }
    protected void insertRow(){
        JTextField [] dTextFields = new JTextField[visibleColumnsCount];
        JDialog jDialog = createDialog(dTextFields);
        JButton editB = new JButton("✔");
        jDialog.add(editB);
        jDialog.setVisible(true);
        editB.addActionListener(e -> {
            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT MAX(" + prefix + "_id) FROM " + tableName);
                resultSet.next();
                int id = resultSet.getInt(1) + 1;
                statement.close();
                StringBuilder query = new StringBuilder("INSERT INTO " + tableName + " VALUES (" + id);
                for (int i = 0; i < visibleColumnsCount; i ++){
                    query.append(", '").append(dTextFields[i].getText()).append("'");
                }
                PreparedStatement preparedStatement = connection.prepareStatement(query +")");
                preparedStatement.executeUpdate();
                connection.commit();
                statement.close();
                jDialog.dispose();
                preparedStatement.close();
                drawTable(tableInitSelectQuery);
            } catch (SQLException ex) {
                System.out.println("Incorrect arguments");
            }
            catch (StringIndexOutOfBoundsException ex){
                try {
                    jDialog.dispose();
                    drawTable(tableInitSelectQuery);
                } catch (SQLException exc) {
                    throw new RuntimeException(exc);
                }
            }
        });
    }
    protected void findRows(){
        JTextField [] dTextFields = new JTextField[visibleColumnsCount];
        JDialog jDialog = createDialog(dTextFields);
        JButton editB = new JButton("✔");
        jDialog.add(editB);
        jDialog.setVisible(true);
        editB.addActionListener(e -> {
            try {
                StringBuilder query = new StringBuilder(tableInitSelectQuery);
                query.append(" WHERE ");
                for (int i = 0; i < visibleColumnsCount; i ++){
                    if(!Objects.equals(dTextFields[i].getText(), "")){
                        query.append(unprocessedVisibleColumnsNames.get(i+1)).append(" = ").append(dTextFields[i].getText()).append(" AND ");
                    }
                }
                query.delete(query.lastIndexOf("AND"),query.lastIndexOf("AND")+"AND".length());
                jDialog.dispose();
                drawTable(query.toString());
            } catch (SQLException ex) {
                System.out.println("Incorrect arguments");
            }
            catch (StringIndexOutOfBoundsException ex){
                try {
                    jDialog.dispose();
                    drawTable(tableInitSelectQuery);
                } catch (SQLException exc) {
                    throw new RuntimeException(exc);
                }
            }
        });
    }
    protected void editRow(int row) throws SQLException {
        JTextField [] dTextFields = new JTextField[visibleColumnsCount];
        JDialog jDialog = createDialog(dTextFields);
        JButton editB = new JButton("✔");
        jDialog.add(editB);
        jDialog.setVisible(true);
        editB.addActionListener(e -> {
            try {
                StringBuilder query = new StringBuilder("UPDATE " + tableName.toUpperCase() + " SET ");
                for (int i = 0; i < visibleColumnsCount; i ++){
                     if(!Objects.equals(dTextFields[i].getText(), "")){
                         query.append(unprocessedVisibleColumnsNames.get(i+1)).append(" = ").append(dTextFields[i].getText()).append(", ");
                     }
                }
                query.deleteCharAt(query.lastIndexOf(","));
                System.out.println(query + " WHERE " + unprocessedVisibleColumnsNames.get(0) + " = " +  rowId.get(row));
                PreparedStatement statement = connection.prepareStatement(query + " WHERE " + unprocessedVisibleColumnsNames.get(0) + " = " + rowId.get(row));
                statement.executeUpdate();
                connection.commit();
                statement.close();
                jDialog.dispose();
                drawTable(tableInitSelectQuery);
            } catch (SQLException ex) {
                System.out.println("Incorrect arguments");
            }
            catch (StringIndexOutOfBoundsException ex){
                jDialog.dispose();
            }
        });
    }
    protected void drawTable(String query) throws SQLException {
        Statement statement = connection.createStatement();
        statement.setFetchSize(FETCH_COUNT);
        ResultSet resultSet = statement.executeQuery(query);
        Object[][] data = new Object[FETCH_COUNT][visibleColumnsCount+2];
        for (int i = 0; i < currentPage*FETCH_COUNT; i++){
            resultSet.next();
        }
        for (int i = 0; i < FETCH_COUNT && resultSet.next(); i++) {
            rowId.put(i, Integer.valueOf(resultSet.getString(1)));
            for(int j = 0; j < visibleColumnsCount; j++){
                data[i][j] = resultSet.getString(j + 2);
            }
        }
        tableModel = new DefaultTableModel(data, visibleColumnsNames.toArray());
        table.setModel(tableModel);
        table.getTableHeader().setFont(new Font("",Font.BOLD,14));
        table.setFont(new Font("",Font.PLAIN,14));
        TableButtonDelete tableButtonDelete = new TableButtonDelete("X");//new ImageIcon(getClass().getResource("/img.png")));
        tableButtonDelete.addTableButtonListener((row, col) -> {
            try {
                deleteRow(row);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        TableButtonEdit tableButtonEdit = new TableButtonEdit("✎");
        tableButtonEdit.addTableButtonListener((row, col) -> {
            try {
                editRow(row);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        table.getColumn("Удалить").setCellRenderer(tableButtonDelete);
        table.getColumn("Удалить").setCellEditor(tableButtonDelete);
        table.getColumn("Удалить").setMaxWidth(80);
        table.getColumn("Изменить").setCellRenderer(tableButtonEdit);
        table.getColumn("Изменить").setCellEditor(tableButtonEdit);
        table.getColumn("Изменить").setMaxWidth(80);
        resultSet.close();
        statement.close();
    }

    public TablePanel(MainMenu mainMenu, JPanel oldP, String tableName, Connection connection){
        this.mainMenu = mainMenu;
        this.tableName = tableName.toUpperCase();
        this.connection = connection;
        this.oldP = oldP;
        oldP.setVisible(false);
        p = new JPanel();
        this.table = new JTable();
        p.add(table.getTableHeader());
        p.add(table);
        mainMenu.revalidate();
    }
    protected void setMetaInfo() throws SQLException {
        Statement statement = connection.createStatement();
        statement.setFetchSize(FETCH_COUNT);
        ResultSetMetaData metaData = statement.executeQuery("SELECT * FROM " + tableName).getMetaData();
        tableColumnsCount = metaData.getColumnCount();
        prefix = metaData.getColumnName(1).substring(0,metaData.getColumnName(1).indexOf("_"));
        for(int i = 0; i < tableColumnsCount; i++){
            tableColumnsNames.add(metaData.getColumnName(i+1));
        }
        ResultSetMetaData metaDataVisible = statement.executeQuery(tableInitSelectQuery).getMetaData();
        visibleColumnsCount = metaDataVisible.getColumnCount()-1;
        for(int i = 0; i < visibleColumnsCount+1; i++){
            String columnName = metaDataVisible.getColumnName(i+1);
            String processedColumnName = columnName.substring(columnName.indexOf('_') + 1).replace('_', ' ');
            unprocessedVisibleColumnsNames.add(columnName);
            if(!processedColumnName.equalsIgnoreCase("ID")){
                visibleColumnsNames.add(processedColumnName);
            }
        }
        visibleColumnsNames.add("Изменить");
        visibleColumnsNames.add("Удалить");
        ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM " + tableName);
        resultSet.next();
        pagesCount = resultSet.getInt(1) / FETCH_COUNT;
        statement.close();
    }

    public void drawTablePanel(){
        mainMenu.getJPanel().setVisible(false);
        mainMenu.add(p);
        p.setVisible(true);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        currentSelectQuery = tableInitSelectQuery;
        try {
            setMetaInfo();
            drawTable(tableInitSelectQuery);
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        p.add(buttonsP);
        addInsertButton(buttonsP);
        addFirstButton(buttonsP);
        addPreviousButton(buttonsP);
        addNextButton(buttonsP);
        addLastButton(buttonsP);
        addSearchButton(buttonsP);
        addExitButton(buttonsP);
    }
    protected void addExitButton(JPanel panel){
        JButton exitB = new JButton("Назад");
        exitB.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitB.setFont(new Font("impact",Font.PLAIN,30));
        ActionListener exitListener = e -> {
            mainMenu.remove(p);
            oldP.setVisible(true);
            mainMenu.revalidate();
        };
        exitB.addActionListener(exitListener);
        panel.add(exitB);
    }
    protected void addInsertButton(JPanel panel){
        JButton exitB = new JButton("Добавить запись");
        exitB.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitB.setFont(new Font("impact",Font.PLAIN,30));
        ActionListener exitListener = e -> {
            insertRow();
        };
        exitB.addActionListener(exitListener);
        panel.add(exitB);
    }
    protected void addNextButton(JPanel panel){
        JButton exitB = new JButton(">");
        exitB.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitB.setFont(new Font("impact",Font.BOLD,30));
        ActionListener exitListener = e -> {
            if(currentPage < pagesCount){
                currentPage++;
                try {
                    drawTable(tableInitSelectQuery);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        };
        exitB.addActionListener(exitListener);
        panel.add(exitB);
    }
    protected void addPreviousButton(JPanel panel){
        JButton exitB = new JButton("<");
        exitB.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitB.setFont(new Font("impact",Font.BOLD,30));
        ActionListener exitListener = e -> {
            if(currentPage > 0){
                currentPage--;
                try {
                    drawTable(tableInitSelectQuery);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        };
        exitB.addActionListener(exitListener);
        panel.add(exitB);
    }
    protected void addFirstButton(JPanel panel){
        JButton exitB = new JButton("<<");
        exitB.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitB.setFont(new Font("impact",Font.BOLD,30));
        ActionListener exitListener = e -> {
            if(currentPage != 0){
                currentPage = 0;
                try {
                    drawTable(tableInitSelectQuery);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        };
        exitB.addActionListener(exitListener);
        panel.add(exitB);
    }
    protected void addLastButton(JPanel panel){
        JButton exitB = new JButton(">>");
        exitB.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitB.setFont(new Font("impact",Font.BOLD,30));
        ActionListener exitListener = e -> {
            if(currentPage != pagesCount){
                currentPage = pagesCount;
                try {
                    drawTable(tableInitSelectQuery);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        };
        exitB.addActionListener(exitListener);
        panel.add(exitB);
    }
    protected void addSearchButton(JPanel panel){
        JButton exitB = new JButton("поиск");
        exitB.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitB.setFont(new Font("impact",Font.PLAIN,30));
        ActionListener exitListener = e -> {
            findRows();
        };
        exitB.addActionListener(exitListener);
        panel.add(exitB);
    }
}
