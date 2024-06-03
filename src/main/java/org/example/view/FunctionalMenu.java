package org.example.view;

import org.example.view.tablepanels.functables.ChooseLivingTable;
import org.example.view.tablepanels.functables.QueryPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

public class FunctionalMenu {
    private final MainMenu mainMenu;
    private final Connection connection;
    private final JPanel p;
    private void setButtonSize(JButton jButton,int width, int height){
        jButton.setPreferredSize(new Dimension(width,height));
        jButton.setMaximumSize(new Dimension(width,height));
        jButton.setMinimumSize(new Dimension(width,height));
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
    private void bookRoomsOrg(){
        String [] columnNames = {"Название организации","Класс отеля", "Этаж", "Количество номеров","Количество людей","Дата заселения(гггг-мм-дд)", "Дата освобождения(гггг-мм-дд)"};
        JTextField [] dTextFields = new JTextField[columnNames.length];
        JDialog jDialog = createDialog(dTextFields,columnNames);
        JButton editB = new JButton("✔");
        jDialog.add(editB);
        jDialog.setVisible(true);
        editB.addActionListener(e -> {
            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT О_ID FROM ОРГАНИЗАЦИИ WHERE О_НАЗВАНИЕ = '" + dTextFields[0].getText() + "' ");
                resultSet.next();
                int oid = resultSet.getInt(1);
                resultSet = statement.executeQuery("SELECT MAX(Б_ID) FROM БРОНИ");
                resultSet.next();
                int bid = resultSet.getInt(1) + 1;
                statement.close();
                System.out.println("INSERT INTO БРОНИ VALUES(" +
                        bid + ", " + dTextFields[1].getText() + "," + dTextFields[2].getText() + ","  + dTextFields[3].getText() + "," + dTextFields[4].getText() + ",'"
                        + dTextFields[5].getText() + "','"  + dTextFields[6].getText() + "'," + oid + ", null)");
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO БРОНИ VALUES(" +
                        bid + ", " + dTextFields[1].getText() + "," + dTextFields[2].getText() + ","  + dTextFields[3].getText() + "," + dTextFields[4].getText() + ",'"
                        + dTextFields[5].getText() + "','"  + dTextFields[6].getText() + "'," + oid + ", null)");
                preparedStatement.executeUpdate();
                connection.commit();
                preparedStatement.close();
                jDialog.dispose();
            } catch (SQLException ex) {
                System.out.println("Incorrect arguments");
            }
        });
    }
    private void bookRoomsClient(){
        String [] columnNames = {"Серия и номер паспорта клиента","Класс отеля", "Этаж", "Количество номеров","Количество людей","Дата заселения(гггг-мм-дд)", "Дата освобождения(гггг-мм-дд)"};
        JTextField [] dTextFields = new JTextField[columnNames.length];
        JDialog jDialog = createDialog(dTextFields,columnNames);
        JButton editB = new JButton("✔");
        jDialog.add(editB);
        jDialog.setVisible(true);
        editB.addActionListener(e -> {
            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT КЛ_ID FROM КЛИЕНТЫ WHERE КЛ_СЕРИЯ_И_НОМЕР_ПАСПОРТА = '" + dTextFields[0].getText() + "' ");
                resultSet.next();
                int cid = resultSet.getInt(1);
                resultSet = statement.executeQuery("SELECT MAX(Б_ID) FROM БРОНИ");
                resultSet.next();
                int bid = resultSet.getInt(1) + 1;
                statement.close();
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO БРОНИ VALUES(" +
                        bid + ", " + dTextFields[1].getText() + "," + dTextFields[2].getText() + "," + dTextFields[3].getText() + "," + dTextFields[4].getText() + ",'"
                        + dTextFields[5].getText() + "','" + dTextFields[6].getText() + "'," + "null, " + cid +")");
                preparedStatement.executeUpdate();
                connection.commit();
                preparedStatement.close();
                jDialog.dispose();
            } catch (SQLException ex) {
                System.out.println("Incorrect arguments");
            }
        });
    }
    private void livingOrg(){
        String [] columnNames = {"Название организации",""};
        JTextField [] dTextFields = new JTextField[columnNames.length];
        JDialog jDialog = createDialog(dTextFields,columnNames);
        JButton editB = new JButton("✔");
        jDialog.add(editB);
        jDialog.setVisible(true);
        editB.addActionListener(e -> {
            String query ="SELECT Б_ID, Б_КЛАСС_ОТЕЛЯ, Б_ЭТАЖ, Б_КОЛИЧЕТСТВО_НОМЕРОВ, Б_КОЛИЧЕТСТВО_ЛЮДЕЙ, Б_ДАТА_ЗАСЕЛЕНИЯ, " +
                    "Б_ДАТА_ОСВОБОЖДЕНИЯ FROM БРОНИ join ОРГАНИЗАЦИИ using(О_ID) WHERE Б_ID NOT IN (SELECT Б_ID FROM ПРОЖИВАНИЯ) AND О_НАЗВАНИЕ = '"+ dTextFields[0].getText()+"'";
            System.out.println(query);
            new ChooseLivingTable(mainMenu,p,"",connection,query);
            jDialog.dispose();
        });
    }
    private void livingClient(){
        String [] columnNames = {"Серия и номер паспорта",""};
        JTextField [] dTextFields = new JTextField[columnNames.length];
        JDialog jDialog = createDialog(dTextFields,columnNames);
        JButton editB = new JButton("✔");
        jDialog.add(editB);
        jDialog.setVisible(true);
        editB.addActionListener(e -> {
            String query ="SELECT Б_ID, Б_КЛАСС_ОТЕЛЯ, Б_ЭТАЖ, Б_КОЛИЧЕТСТВО_НОМЕРОВ, Б_КОЛИЧЕТСТВО_ЛЮДЕЙ, Б_ДАТА_ЗАСЕЛЕНИЯ, " +
                    "Б_ДАТА_ОСВОБОЖДЕНИЯ FROM БРОНИ join КЛИЕНТЫ using(КЛ_ID) WHERE Б_ID NOT IN (SELECT Б_ID FROM ПРОЖИВАНИЯ) AND КЛ_СЕРИЯ_И_НОМЕР_ПАСПОРТА = '"+ dTextFields[0].getText()+"'";
            System.out.println(query);
            new ChooseLivingTable(mainMenu,p,"",connection,query);
            jDialog.dispose();
        });
    }
    private void bookRooms(){
        JDialog jDialog = new JDialog();
        jDialog.setSize(1000,150);
        jDialog.setLayout(new GridLayout(1,  3));
        JButton exitB = new JButton("Назад");
        exitB.addActionListener(e -> {jDialog.dispose();});
        jDialog.add(exitB);
        JButton orgB = new JButton("От организации");
        orgB.addActionListener(e -> {bookRoomsOrg();jDialog.dispose();});
        jDialog.add(orgB);
        JButton clientB = new JButton("От частного лица");
        clientB.addActionListener(e -> {bookRoomsClient();jDialog.dispose();});
        jDialog.add(clientB);
        jDialog.setVisible(true);
    }
    private void living(){
        JDialog jDialog = new JDialog();
        jDialog.setSize(1000,150);
        jDialog.setLayout(new GridLayout(1,  3));
        JButton exitB = new JButton("Назад");
        exitB.addActionListener(e -> {jDialog.dispose();});
        jDialog.add(exitB);
        JButton orgB = new JButton("От организации");
        orgB.addActionListener(e -> {livingOrg();jDialog.dispose();});
        jDialog.add(orgB);
        JButton clientB = new JButton("От частного лица");
        clientB.addActionListener(e -> {livingClient();jDialog.dispose();});
        jDialog.add(clientB);
        jDialog.setVisible(true);
    }
    private void buyService(){
        String [] columnNames = {"Серия и номер паспорта",""};
        JTextField [] dTextFields = new JTextField[columnNames.length];
        JDialog jDialog = createDialog(dTextFields,columnNames);
        JButton editB = new JButton("✔");
        jDialog.add(editB);
        jDialog.setVisible(true);
        editB.addActionListener(e -> {
            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT СБ_НАЗВАНИЕ FROM СЛУЖБЫ_БЫТА ");
                ArrayList<String> serviceNames = new ArrayList<>();
                while (resultSet.next()){
                    serviceNames.add(resultSet.getString(1));
                }
                JComboBox<String> comboBox = new JComboBox<String>( serviceNames.toArray(new String[0]));
                jDialog.remove(editB);
                jDialog.add(comboBox);
                comboBox.setVisible(true);
                jDialog.revalidate();
                comboBox.addActionListener(e1 ->{
                    try {
                        String serviceName = (String) comboBox.getSelectedItem();
                        Statement st = connection.createStatement();
                        ResultSet rs = statement.executeQuery("SELECT СБ_ID FROM СЛУЖБЫ_БЫТА WHERE СБ_НАЗВАНИЕ = '" + serviceName+ "' ");
                        rs.next();
                        int sid = rs.getInt(1);
                        rs = statement.executeQuery("SELECT П_ID FROM ПРОЖИВАНИЯ join КЛИЕНТЫ using(КЛ_ID) WHERE КЛ_СЕРИЯ_И_НОМЕР_ПАСПОРТА = " + dTextFields[0].getText());
                        rs.next();
                        int lid = rs.getInt(1);
                        String query = "INSERT INTO ПРОЖИВАНИЯ_СЛУЖБЫ_БЫТА VALUES("+lid + ", " + sid + ", NOW())";
                        PreparedStatement preparedStatement = connection.prepareStatement(query);
                        preparedStatement.executeUpdate();
                        connection.commit();
                        statement.close();
                        preparedStatement.close();
                        jDialog.dispose();
                    } catch (SQLException ex) {
                        System.out.println("Incorrect arguments");
                    }
                });
            } catch (SQLException ex) {
                System.out.println("Incorrect arguments");
            }
        });
    }
    private void buyEntertainment(){
        String [] columnNames = {"Серия и номер паспорта",""};
        JTextField [] dTextFields = new JTextField[columnNames.length];
        JDialog jDialog = createDialog(dTextFields,columnNames);
        JButton editB = new JButton("✔");
        jDialog.add(editB);
        jDialog.setVisible(true);
        editB.addActionListener(e -> {
            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT Р_НАЗВАНИЕ FROM РАЗВЛЕЧЕНИЯ ");
                ArrayList<String> serviceNames = new ArrayList<>();
                while (resultSet.next()){
                    serviceNames.add(resultSet.getString(1));
                }
                JComboBox<String> comboBox = new JComboBox<String>( serviceNames.toArray(new String[0]));
                jDialog.remove(editB);
                jDialog.add(comboBox);
                comboBox.setVisible(true);
                jDialog.revalidate();
                comboBox.addActionListener(e1 ->{
                    try {
                        String serviceName = (String) comboBox.getSelectedItem();
                        Statement st = connection.createStatement();
                        ResultSet rs = statement.executeQuery("SELECT Р_ID FROM РАЗВЛЕЧЕНИЯ WHERE Р_НАЗВАНИЕ = '" + serviceName+ "' ");
                        rs.next();
                        int sid = rs.getInt(1);
                        rs = statement.executeQuery("SELECT П_ID FROM ПРОЖИВАНИЯ join КЛИЕНТЫ using(КЛ_ID) WHERE КЛ_СЕРИЯ_И_НОМЕР_ПАСПОРТА = " + dTextFields[0].getText());
                        rs.next();
                        int lid = rs.getInt(1);
                        String query = "INSERT INTO ПРОЖИВАНИЯ_РАЗВЛЕЧЕНИЯ VALUES("+lid + ", " + sid + ", NOW())";
                        System.out.println(query);
                        PreparedStatement preparedStatement = connection.prepareStatement(query);
                        preparedStatement.executeUpdate();
                        connection.commit();
                        statement.close();
                        preparedStatement.close();
                        jDialog.dispose();
                    } catch (SQLException ex) {
                        System.out.println("Incorrect arguments");
                    }
                });
            } catch (SQLException ex) {
                System.out.println("Incorrect arguments");
            }
        });
    }
    private void leave(){
        String [] columnNames = {"Серия и номер паспорта","дата отбытия (гггг-мм-дд)"};
        JTextField [] dTextFields = new JTextField[columnNames.length];
        JDialog jDialog = createDialog(dTextFields,columnNames);
        JButton editB = new JButton("✔");
        jDialog.add(editB);
        jDialog.setVisible(true);
        editB.addActionListener(e -> {
            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT КЛ_ID FROM КЛИЕНТЫ WHERE КЛ_СЕРИЯ_И_НОМЕР_ПАСПОРТА = '" + dTextFields[0].getText() + "' ");
                resultSet.next();
                int cid = resultSet.getInt(1);
                String query ="UPDATE ПРОЖИВАНИЯ SET П_ДАТА_ОСВОБОЖДЕНИЯ = ";
                if(!dTextFields[1].getText().equalsIgnoreCase("")){
                    query += " '" + dTextFields[1].getText() + "' ";
                }
                else{
                    query += "NOW() ";
                }
                query += "WHERE П_ДАТА_ОСВОБОЖДЕНИЯ IS NULL AND КЛ_ID = " + cid;
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.executeUpdate();
                connection.commit();
                statement.close();
                preparedStatement.close();
                jDialog.dispose();
            } catch (SQLException ex) {
                System.out.println("Incorrect arguments");
            }
        });
    }
    private void payCheck(){
        String [] columnNames = {"Серия и номер паспорта","дата оплаты (гггг-мм-дд)"};
        JTextField [] dTextFields = new JTextField[columnNames.length];
        JDialog jDialog = createDialog(dTextFields,columnNames);
        JButton editB = new JButton("✔");
        jDialog.add(editB);
        jDialog.setVisible(true);
        editB.addActionListener(e -> {
            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT КЛ_ID FROM КЛИЕНТЫ WHERE КЛ_СЕРИЯ_И_НОМЕР_ПАСПОРТА = '" + dTextFields[0].getText() + "' ");
                resultSet.next();
                int cid = resultSet.getInt(1);
                String query ="UPDATE ПРОЖИВАНИЯ SET П_ДАТА_ОПЛАТЫ_СЧЁТА = ";
                if(!dTextFields[1].getText().equalsIgnoreCase("")){
                    query += " '" + dTextFields[1].getText() + "' ";
                }
                else{
                    query += "NOW() ";
                }
                query += "WHERE П_ДАТА_ОПЛАТЫ_СЧЁТА IS NULL AND КЛ_ID = " + cid;
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.executeUpdate();
                connection.commit();
                statement.close();
                preparedStatement.close();
                jDialog.dispose();
            } catch (SQLException ex) {
                System.out.println("Incorrect arguments");
            }
        });
    }
    private void roomExpenses(){
        String [] columnNames = {"Название корпуса","Номер комнаты", "Расходы", "Дата (гггг-мм-дд)"};
        JTextField [] dTextFields = new JTextField[columnNames.length];
        JDialog jDialog = createDialog(dTextFields,columnNames);
        JButton editB = new JButton("✔");
        jDialog.add(editB);
        jDialog.setVisible(true);
        editB.addActionListener(e -> {
            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery( "SELECT Н_ID FROM НОМЕРА JOIN КОРПУСА using(К_ID) WHERE К_ИМЯ = '" + dTextFields[0].getText() + "' AND Н_НОМЕР_КОМНАТЫ = " + dTextFields[1].getText());
                resultSet.next();
                int id = resultSet.getInt(1);
                String query = "INSERT INTO РАСХОДЫ_НА_СООДЕРЖАНИЕ_НОМЕРА VALUES(" + id + ", ";
                if(!dTextFields[3].getText().equalsIgnoreCase("")){
                    query += " '" + dTextFields[3].getText() + "', ";
                }
                else{
                    query += "NOW(), ";
                }
                query += dTextFields[2].getText() + ")";
                System.out.println(query);
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.executeUpdate();
                connection.commit();
                statement.close();
                preparedStatement.close();
                jDialog.dispose();
            } catch (SQLException ex) {
                System.out.println("Incorrect arguments");
            }
        });
    }
    private void  addComplain(){
        String [] columnNames = {"Серия и номер паспорта", "Название корпуса","Номер комнаты", "Жалоба", "Дата (гггг-мм-дд)"};
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
                resultSet = statement.executeQuery("SELECT КЛ_ID FROM КЛИЕНТЫ WHERE КЛ_СЕРИЯ_И_НОМЕР_ПАСПОРТА = '" + dTextFields[0].getText() + "' ");
                resultSet.next();
                int cid = resultSet.getInt(1);
                resultSet = statement.executeQuery("SELECT MAX(Ж_ID) FROM ЖАЛОБЫ");
                resultSet.next();
                int comid = resultSet.getInt(1) + 1;
                String query = "INSERT INTO ЖАЛОБЫ VALUES(" + comid + ", " + cid + ", " + nid + ", '" + dTextFields[3].getText() + "', ";
                if(!dTextFields[4].getText().equalsIgnoreCase("")){
                    query += " '" + dTextFields[4].getText() + "') ";
                }
                else{
                    query += "NOW())";
                }
                System.out.println(query);
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.executeUpdate();
                connection.commit();
                statement.close();
                preparedStatement.close();
                jDialog.dispose();
            } catch (SQLException ex) {
                System.out.println("Incorrect arguments");
            }
        });
    }
    private void addContract(){
        String [] columnNames = {"Название организации", "Дата заключения (гггг-мм-дд)","Дата завершения (гггг-мм-дд)","Скидка"};
        JTextField [] dTextFields = new JTextField[columnNames.length];
        JDialog jDialog = createDialog(dTextFields,columnNames);
        JButton editB = new JButton("✔");
        jDialog.add(editB);
        jDialog.setVisible(true);
        editB.addActionListener(e -> {
            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery( "SELECT О_ID FROM ОРГАНИЗАЦИИ WHERE О_НАЗВАНИЕ = '" + dTextFields[0].getText() + "' ");
                resultSet.next();
                int oid = resultSet.getInt(1);
                resultSet = statement.executeQuery("SELECT MAX(Д_ID) FROM ДОГОВОРА");
                resultSet.next();
                int cid = resultSet.getInt(1) + 1;
                String query = "INSERT INTO ДОГОВОРА VALUES(" + cid + ", " + oid + ", '" + dTextFields[1].getText() + "', '" + dTextFields[2].getText() + "', " + dTextFields[3].getText() + ")";
                System.out.println(query);
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.executeUpdate();
                connection.commit();
                statement.close();
                preparedStatement.close();
                jDialog.dispose();
            } catch (SQLException ex) {
                System.out.println("Incorrect arguments");
            }
        });
    }
    private JButton addButton(String label,ActionListener actionListener){
        JButton button = new JButton(label);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(new Font("impact",Font.PLAIN,30));
        button.addActionListener(actionListener);
        setButtonSize(button,1600,50);
        return button;
    }
    public FunctionalMenu(MainMenu mainMenu){
        this.mainMenu = mainMenu;
        this.connection = mainMenu.getConnection();
        mainMenu.getJPanel().setVisible(false);
        p = new JPanel();
        mainMenu.add(p);
        drawFunctionalMenu();
        mainMenu.revalidate();
    }
    private void drawFunctionalMenu(){
        JButton backB = new JButton("Назад");
        backB.setAlignmentX(Component.CENTER_ALIGNMENT);
        backB.setFont(new Font("impact",Font.PLAIN,45));
        ActionListener backListener = e -> {
            mainMenu.remove(p);
            mainMenu.getJPanel().setVisible(true);
            mainMenu.revalidate();
        };
        backB.addActionListener(backListener);
        addBookButton(p);
        addLivingButton(p);
        addBuyServiceButton(p);
        addBuyEntertainmentButton(p);
        addLeavingButton(p);
        addPayCheckButton(p);
        addRoomExpensesButton(p);
        addComplaintButton(p);
        addContractButton(p);
        p.add(backB);
    }
    private void addBookButton(JPanel panel){
        ActionListener actionListener = e -> {
            bookRooms();
        };
        JButton exitB = addButton("Бронировать номер",actionListener);
        panel.add(exitB);
    }
    private void addLivingButton(JPanel panel){
        ActionListener actionListener = e -> {
            living();
        };
        JButton exitB = addButton("Заселение",actionListener);
        panel.add(exitB);
    }
    private void addBuyServiceButton(JPanel panel){
        ActionListener actionListener = e -> {
            buyService();
        };
        JButton exitB = addButton("Покупка службы быта",actionListener);
        panel.add(exitB);
    }
    private void addBuyEntertainmentButton(JPanel panel){
        ActionListener actionListener = e -> {
            buyEntertainment();
        };
        JButton exitB = addButton("Покупка развлечения",actionListener);
        panel.add(exitB);
    }
    private void addLeavingButton(JPanel panel){
        ActionListener actionListener = e -> {
            leave();
        };
        JButton exitB = addButton("Отбытие",actionListener);
        panel.add(exitB);
    }
    private void addPayCheckButton(JPanel panel){
        ActionListener actionListener = e -> {
            payCheck();
        };
        JButton exitB = addButton("Оплата чека",actionListener);
        panel.add(exitB);
    }
    private void addRoomExpensesButton(JPanel panel){
        ActionListener actionListener = e -> {
            roomExpenses();
        };
        JButton exitB = addButton("Добавить накладные расходы на содержание номера",actionListener);
        panel.add(exitB);
    }
    private void addComplaintButton(JPanel panel){
        ActionListener actionListener = e -> {
            addComplain();
        };
        JButton exitB = addButton("Добавить жалобу",actionListener);
        panel.add(exitB);
    }
    private void addContractButton(JPanel panel){
        ActionListener actionListener = e -> {
            addContract();
        };
        JButton exitB = addButton("Добавить договор с организацией",actionListener);
        panel.add(exitB);
    }
}
