package org.example.view.tablepanels.functables;

import org.example.view.MainMenu;
import org.example.view.QueryMenu;
import org.example.view.tablepanels.TablePanel;
import org.example.view.utility.TableButtonDelete;
import org.example.view.utility.TableButtonEdit;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class QueryPanel extends TablePanel {
    public QueryPanel(MainMenu mainMenu, JPanel oldP, String tableName, Connection connection,String selectQuery){
        super(mainMenu,oldP, tableName,connection);
        this.tableInitSelectQuery = selectQuery;
        drawTablePanel();
    }
    @Override
    protected void drawTable(String query) throws SQLException {
        Statement statement = connection.createStatement();
        statement.setFetchSize(FETCH_COUNT);
        ResultSet resultSet = statement.executeQuery(query);
        Object[][] data = new Object[FETCH_COUNT][visibleColumnsCount];
        for (int i = 0; i < currentPage*FETCH_COUNT; i++){
            resultSet.next();
        }
        for (int i = 0; i < FETCH_COUNT && resultSet.next(); i++) {
            for(int j = 0; j < visibleColumnsCount; j++){
                data[i][j] = resultSet.getString(j + 1);
            }
        }
        tableModel = new DefaultTableModel(data, visibleColumnsNames.toArray());
        table.setModel(tableModel);
        table.getTableHeader().setFont(new Font("",Font.BOLD,14));
        table.setFont(new Font("",Font.PLAIN,14));
        resultSet.close();
        statement.close();
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
        statement.setFetchSize(FETCH_COUNT);
        ResultSetMetaData metaDataVisible = statement.executeQuery(tableInitSelectQuery).getMetaData();
        visibleColumnsCount = metaDataVisible.getColumnCount();
        for(int i = 0; i < visibleColumnsCount; i++){
            String columnName = metaDataVisible.getColumnName(i+1);
            String processedColumnName = columnName.substring(columnName.indexOf('_') + 1).replace('_', ' ');
            visibleColumnsNames.add(processedColumnName);
        }
        ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM ( " + tableInitSelectQuery + " )");
        resultSet.next();
        pagesCount = resultSet.getInt(1) / FETCH_COUNT;
        statement.close();
    }
}
