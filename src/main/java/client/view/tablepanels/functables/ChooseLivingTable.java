package client.view.tablepanels.functables;

import client.view.MainMenu;
import client.view.tablepanels.TablePanel;
import client.view.utility.TableButtonDelete;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ChooseLivingTable extends TablePanel {
    protected JDialog createDialog(JTextField [] dTextFields, String [] columnsNames){
        JDialog jDialog = new JDialog();
        jDialog.setSize(1000,150);
        jDialog.setLayout(new GridLayout(3,  columnsNames.length));
        for (String columnsName : columnsNames) {
            JLabel jLabel = new JLabel(columnsName);
            jDialog.add(jLabel);
        }
        for (int i = 0; i <  columnsNames.length; i++){
            dTextFields[i] = new JTextField();
            jDialog.add(dTextFields[i]);
        }
        JButton exitB = new JButton("Назад");
        exitB.addActionListener(e -> {jDialog.dispose();});
        jDialog.add(exitB);
        for (int i = 0; i < columnsNames.length-2;i++) {
            jDialog.add(new JLabel());
        }
        return jDialog;
    }
    private void insertLiving(int row){
        String [] columnNames = {"Серия и номер паспорта клиента","Название корпуса", "Номер комнаты", "Дата заселения(гггг-мм-дд)"};
        JTextField [] dTextFields = new JTextField[columnNames.length];
        JDialog jDialog = createDialog(dTextFields,columnNames);
        JButton editB = new JButton("✔");
        jDialog.add(editB);
        jDialog.setVisible(true);
        editB.addActionListener(e -> {
            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery( "SELECT Н_ID FROM НОМЕРА JOIN КОРПУСА using(К_ID) WHERE К_ИМЯ = '" + dTextFields[1].getText() + "' AND Н_НОМЕР_КОМНАТЫ = " + dTextFields[2].getText());
                resultSet.next();
                int nid = resultSet.getInt(1);
                statement = connection.createStatement();
                resultSet = statement.executeQuery("SELECT КЛ_ID FROM КЛИЕНТЫ WHERE КЛ_СЕРИЯ_И_НОМЕР_ПАСПОРТА = '" + dTextFields[0].getText() + "' ");
                resultSet.next();
                int cid = resultSet.getInt(1);
                resultSet = statement.executeQuery("SELECT MAX(П_ID) FROM ПРОЖИВАНИЯ");
                resultSet.next();
                int lid = resultSet.getInt(1) + 1;
                statement.close();
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO ПРОЖИВАНИЯ VALUES(" +
                        lid + ", " + cid + "," + nid + "," + rowId.get(row) + ",'" + dTextFields[3].getText() + "',"
                        + "null, " + "null, " + "null, " + "null)");
                preparedStatement.executeUpdate();
                connection.commit();
                preparedStatement.close();
                jDialog.dispose();
                mainMenu.remove(p);
                oldP.setVisible(true);
                mainMenu.revalidate();
            } catch (SQLException ex) {
                System.out.println("Incorrect arguments");
            }
        });
    }
    @Override
    protected void drawTable(String query) throws SQLException {
        Statement statement = connection.createStatement();
        statement.setFetchSize(FETCH_COUNT);
        ResultSet resultSet = statement.executeQuery(query);
        Object[][] data = new Object[FETCH_COUNT][visibleColumnsCount+1];
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
        TableButtonDelete tableButtonDelete = new TableButtonDelete("✔");
        tableButtonDelete.addTableButtonListener((row, col) -> {
            insertLiving(row);
        });
        table.getColumn("Выбрать").setCellRenderer(tableButtonDelete);
        table.getColumn("Выбрать").setCellEditor(tableButtonDelete);
        table.getColumn("Выбрать").setMaxWidth(80);
        statement.close();
    }
    public ChooseLivingTable(MainMenu mainMenu, JPanel oldP, String tableName, Connection connection, String selectQuery){
        super(mainMenu,oldP, tableName,connection);
        this.tableInitSelectQuery = selectQuery;
        drawTablePanel();
    }
    @Override
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
        addFirstButton(buttonsP);
        addPreviousButton(buttonsP);
        addNextButton(buttonsP);
        addLastButton(buttonsP);
        addSearchButton(buttonsP);
        addExitButton(buttonsP);
    }

    @Override
    protected void setMetaInfo() throws SQLException {
        Statement statement = connection.createStatement();
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
        visibleColumnsNames.add("Выбрать");
        statement.close();
    }
}
