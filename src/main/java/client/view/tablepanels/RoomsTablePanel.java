package client.view.tablepanels;

import client.view.MainMenu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.*;

public class RoomsTablePanel extends TablePanel {
    public RoomsTablePanel(MainMenu mainMenu, JPanel oldP, String tableName, Connection connection){
        super(mainMenu,oldP, tableName,connection);
        tableInitSelectQuery = "SELECT Н_ID, Н_НОМЕР_КОМНАТЫ, Н_ЭТАЖ, Н_СТОИМОСТЬ_НОМЕРА, К_ИМЯ as К_ИМЯ_КОРПУСА  FROM НОМЕРА " +
                "LEFT JOIN КОРПУСА using(К_ID)";
        drawTablePanel();
        //addExpensesButton(buttonsP);
    }
    protected void addExpensesButton(JPanel panel){
        JButton exitB = new JButton("Редактировать расходы на содержание номеров");
        exitB.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitB.setFont(new Font("impact",Font.PLAIN,20));
        ActionListener exitListener = e -> {
        };
        exitB.addActionListener(exitListener);
        panel.add(exitB);
    }

    protected JDialog createDialog(JTextField [] dTextFields, String [] columnsNames){
        JDialog jDialog = new JDialog();
        jDialog.setSize(1000,150);
        jDialog.setLayout(new GridLayout(3,  columnsNames.length));
        for (int i = 0; i <  columnsNames.length; i++){
            JLabel jLabel = new JLabel(columnsNames[i]);
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
    @Override
    protected void insertRow(){
        String [] columnNames = {"Название корпуса", "Номер комнаты","Этаж","Местность","Стоимость"};
        JTextField [] dTextFields = new JTextField[columnNames.length];
        JDialog jDialog = createDialog(dTextFields,columnNames);
        JButton editB = new JButton("✔");
        jDialog.add(editB);
        jDialog.setVisible(true);
        editB.addActionListener(e -> {
            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT MAX(Н_ID) FROM НОМЕРА");
                resultSet.next();
                int nid = resultSet.getInt(1) + 1;
                resultSet = statement.executeQuery("SELECT К_ID FROM КОРПУСА WHERE К_ИМЯ = '" + dTextFields[0].getText() + "' ");
                resultSet.next();
                int bid = resultSet.getInt(1);

                String query = "INSERT INTO " + tableName + " VALUES (" + nid + ", " + bid + ", " + dTextFields[1].getText() + ", " + dTextFields[2].getText()
                        + ", " + dTextFields[3].getText() + ", " + dTextFields[4].getText() + ")";
                System.out.println(query);
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.executeUpdate();
                connection.commit();
                statement.close();
                preparedStatement.close();
                jDialog.dispose();
                drawTable(tableInitSelectQuery);
            } catch (SQLException ex) {
                System.out.println("Incorrect arguments");
            }
        });
    }
}
