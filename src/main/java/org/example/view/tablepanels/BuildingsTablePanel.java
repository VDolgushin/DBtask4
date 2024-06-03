package org.example.view.tablepanels;

import org.example.view.MainMenu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

public class BuildingsTablePanel extends TablePanel {
    private void addEntertainments(){
        JDialog jDialog = new JDialog();
        jDialog.setSize(500,150);
        jDialog.setLayout(new GridLayout(2,  2));
        JButton editB = new JButton("✔");
        try {
            JButton exitB = new JButton("Назад");
            exitB.addActionListener(e -> {jDialog.dispose();});

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT К_ИМЯ FROM КОРПУСА");
            ArrayList<String> serviceNames = new ArrayList<>();
            while (resultSet.next()) {
                serviceNames.add(resultSet.getString(1));
            }
            JComboBox<String> comboBoxB = new JComboBox<String>(serviceNames.toArray(new String[0]));
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT Р_НАЗВАНИЕ FROM РАЗВЛЕЧЕНИЯ");
            serviceNames = new ArrayList<>();
            while (resultSet.next()) {
                serviceNames.add(resultSet.getString(1));
            }
            statement.close();
            JComboBox<String> comboBoxE = new JComboBox<String>(serviceNames.toArray(new String[0]));
            jDialog.add(comboBoxB);
            jDialog.add(comboBoxE);
            jDialog.add(exitB);
            jDialog.add(editB);
            editB.addActionListener(e->{
                try {
                    Statement st = connection.createStatement();
                    ResultSet rs = st.executeQuery("SELECT К_ID FROM КОРПУСА WHERE К_ИМЯ = '" + comboBoxB.getSelectedItem() + "' ");
                    rs.next();
                    int bid = rs.getInt(1);
                    st = connection.createStatement();
                    rs = st.executeQuery("SELECT Р_ID FROM РАЗВЛЕЧЕНИЯ WHERE Р_НАЗВАНИЕ = '" + comboBoxE.getSelectedItem() + "' ");
                    rs.next();
                    int eid = rs.getInt(1);
                    String query = "INSERT INTO КОРПУСА_РАЗВЛЕЧЕНИЯ VALUES(" + bid + ", " + eid + ")";
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.executeUpdate();
                    System.out.println(query);
                    connection.commit();
                    preparedStatement.close();
                    st.close();
                    jDialog.dispose();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            });
            comboBoxB.setVisible(true);
            comboBoxE.setVisible(true);
            jDialog.setVisible(true);
            jDialog.revalidate();
        }
        catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
    private void addService(){
        JDialog jDialog = new JDialog();
        jDialog.setSize(500,150);
        jDialog.setLayout(new GridLayout(2,  2));
        JButton editB = new JButton("✔");
        try {
            JButton exitB = new JButton("Назад");
            exitB.addActionListener(e -> {jDialog.dispose();});
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT К_ИМЯ FROM КОРПУСА");
            ArrayList<String> serviceNames = new ArrayList<>();
            while (resultSet.next()) {
                serviceNames.add(resultSet.getString(1));
            }
            JComboBox<String> comboBoxB = new JComboBox<String>(serviceNames.toArray(new String[0]));
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT СБ_НАЗВАНИЕ FROM СЛУЖБЫ_БЫТА");
            serviceNames = new ArrayList<>();
            while (resultSet.next()) {
                serviceNames.add(resultSet.getString(1));
            }
            statement.close();
            JComboBox<String> comboBoxE = new JComboBox<String>(serviceNames.toArray(new String[0]));
            jDialog.add(comboBoxB);
            jDialog.add(comboBoxE);
            jDialog.add(exitB);
            jDialog.add(editB);
            editB.addActionListener(e->{
                try {
                    Statement st = connection.createStatement();
                    ResultSet rs = st.executeQuery("SELECT К_ID FROM КОРПУСА WHERE К_ИМЯ = '" + comboBoxB.getSelectedItem() + "' ");
                    rs.next();
                    int bid = rs.getInt(1);
                    st = connection.createStatement();
                    rs = st.executeQuery("SELECT СБ_ID FROM СЛУЖБЫ_БЫТА WHERE СБ_НАЗВАНИЕ = '" + comboBoxE.getSelectedItem() + "' ");
                    rs.next();
                    int eid = rs.getInt(1);
                    String query = "INSERT INTO КОРПУСА_СЛУЖБЫ_БЫТА VALUES(" + bid + ", " + eid + ")";
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.executeUpdate();
                    connection.commit();
                    preparedStatement.close();
                    st.close();
                    jDialog.dispose();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            });
            comboBoxB.setVisible(true);
            comboBoxE.setVisible(true);
            jDialog.setVisible(true);
            jDialog.revalidate();
        }
        catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
    public BuildingsTablePanel(MainMenu mainMenu,JPanel oldP, String tableName, Connection connection){
        super(mainMenu,oldP, tableName,connection);
        tableInitSelectQuery = "SELECT К_ID, К_ИМЯ, К_КЛАСС, К_КОЛИЧЕСТВО_ЭТАЖЕЙ, К_КОЛИЧЕСТВО_НОМЕРОВ_НА_ЭТАЖЕ FROM КОРПУСА";
        drawTablePanel();
        addEntertainmentsButton(buttonsP);
        addServicesButton(buttonsP);
    }
    protected void addEntertainmentsButton(JPanel panel){
        JButton exitB = new JButton("Редактировать развлечения в корпусе");
        exitB.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitB.setFont(new Font("impact",Font.PLAIN,30));
        ActionListener exitListener = e -> {
            addEntertainments();
        };
        exitB.addActionListener(exitListener);
        panel.add(exitB);
    }
    protected void addServicesButton(JPanel panel){
        JButton exitB = new JButton("Редактировать службы быта в корпусе");
        exitB.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitB.setFont(new Font("impact",Font.PLAIN,30));
        ActionListener exitListener = e -> {
            addService();
        };
        exitB.addActionListener(exitListener);
        panel.add(exitB);
    }
}
