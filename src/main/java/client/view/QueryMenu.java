package client.view;

import client.view.tablepanels.functables.QueryPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class QueryMenu {
    private final MainMenu mainMenu;
    private final JPanel buttonsP;
    private final Connection connection;
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
    private void upsetClientsQuery() throws SQLException {
        String query = "SELECT КЛ_ФИО, КЛ_ПОЛ, Ж_ЖАЛОБА FROM ЖАЛОБЫ join КЛИЕНТЫ using(КЛ_ID)";
        QueryPanel queryPanel = new QueryPanel(mainMenu,buttonsP,"",connection,query);
    }

    private void reqularClientsQuery(){
        String [] columnNames = {"Название корпуса",""};
        JTextField [] dTextFields = new JTextField[columnNames.length];
        JDialog jDialog = createDialog(dTextFields,columnNames);
        JButton editB = new JButton("✔");
        jDialog.add(editB);
        jDialog.setVisible(true);
        editB.addActionListener(e -> {
            String query = "SELECT КЛ_ID, COUNT(КЛ_ID) as S_КОЛИЧЕСТВО_ПОСЕЩЕНИЙ FROM ПРОЖИВАНИЯ JOIN КЛИЕНТЫ using(КЛ_ID) JOIN НОМЕРА using(Н_ID) JOIN КОРПУСА using(К_ID) ";
            if(!dTextFields[0].getText().equals("")){
                query += " WHERE (К_ИМЯ = '"+ dTextFields[0].getText() + "')";
            }
            query +=  " GROUP BY КЛ_ID ";
            query = "SELECT КЛ_ФИО, S_КОЛИЧЕСТВО_ПОСЕЩЕНИЙ FROM (" + query + ") JOIN КЛИЕНТЫ using (КЛ_ID) ORDER BY S_КОЛИЧЕСТВО_ПОСЕЩЕНИЙ desc";
            //System.out.println(query);
            new QueryPanel(mainMenu,buttonsP,"",connection, query);
            jDialog.dispose();
        });
    }

    private void newClientsQuery(){
        String [] columnNames = {"Начало периода (гггг-мм-дд)","Конец периода (гггг-мм-дд)"};
        JTextField [] dTextFields = new JTextField[columnNames.length];
        JDialog jDialog = createDialog(dTextFields,columnNames);
        JButton editB = new JButton("✔");
        jDialog.add(editB);
        jDialog.setVisible(true);
        editB.addActionListener(e -> {
            String query = "SELECT DISTINCT(КЛ_ФИО), КЛ_ПОЛ, КЛ_ДАТА_РОЖДЕНИЯ, КЛ_СЕРИЯ_И_НОМЕР_ПАСПОРТА FROM КЛИЕНТЫ JOIN ПРОЖИВАНИЯ using(КЛ_ID) ";
            if(!dTextFields[0].getText().equals("") && !dTextFields[1].getText().equals("")){
                query += " WHERE П_ДАТА_ЗАСЕЛЕНИЯ > '"+ dTextFields[0].getText() + "' AND ";
                query += " П_ДАТА_ЗАСЕЛЕНИЯ < '"+ dTextFields[1].getText() + "' ";
            }
            //System.out.println(query);
            new QueryPanel(mainMenu,buttonsP,"",connection, query);
            jDialog.dispose();
        });
    }
    private void specificClientQuery(){
        String [] columnNames = {"ФИО клиента", ""};
        JTextField [] dTextFields = new JTextField[columnNames.length];
        JDialog jDialog = createDialog(dTextFields,columnNames);
        JButton editB = new JButton("✔");
        jDialog.add(editB);
        jDialog.setVisible(true);
        editB.addActionListener(e -> {
            String query = "SELECT КЛ_ФИО, П_ДАТА_ЗАСЕЛЕНИЯ, П_ДАТА_ОСВОБОЖДЕНИЯ, П_СЧЁТ_ЗА_ПРОЖИВАНИЯ, П_СЧЁТ_ЗА_ДОПОЛНИТЕЛЬЫЕ_УСЛУГИ, " +
                    "COUNT(КЛ_ID) OVER (PARTITION BY П_ID) AS S_КОЛИЧЕСТВО_ПОСЕЩЕНИЙ FROM КЛИЕНТЫ LEFT JOIN ПРОЖИВАНИЯ using(КЛ_ID) " +
                    "LEFT JOIN НОМЕРА using(Н_ID) ";
            if(!dTextFields[0].getText().equals("")){
                query += " WHERE КЛ_ФИО = '"+ dTextFields[0].getText() + "' ";
            }
            //System.out.println(query);
            new QueryPanel(mainMenu,buttonsP,"",connection, query);
            jDialog.dispose();
        });
    }
    private void firmBookListQuery(){
        String [] columnNames = {"минимальное количество мест", "Начало периода (гггг-мм-дд)","Конец периода (гггг-мм-дд)"};
        JTextField [] dTextFields = new JTextField[columnNames.length];
        JDialog jDialog = createDialog(dTextFields,columnNames);
        JButton editB = new JButton("✔");
        jDialog.add(editB);
        jDialog.setVisible(true);
        editB.addActionListener(e -> {
            String query = "SELECT О_НАЗВАНИЕ as S_НАЗВАНИЕ_ФИРМЫ, S_КОЛИЧЕСТВО_ЗАБРОНИРОВАННЫХ_МЕСТ, COUNT(*) OVER () as S_ОБЩЕЕ_ЧИСЛО_ФИРМ\n" +
                    "FROM\n" +
                    "(SELECT О_ID, О_НАЗВАНИЕ, sum(Б_КОЛИЧЕТСТВО_ЛЮДЕЙ) as S_КОЛИЧЕСТВО_ЗАБРОНИРОВАННЫХ_МЕСТ\n" +
                    "FROM ОРГАНИЗАЦИИ\n" +
                    "join БРОНИ using (О_ID)\n";
            if(!dTextFields[1].getText().equals("") && !dTextFields[2].getText().equals("")){
                query +=  "WHERE  Б_ДАТА_ЗАСЕЛЕНИЯ >= '" + dTextFields[1].getText() + "' AND Б_ДАТА_ЗАСЕЛЕНИЯ <= '" + dTextFields[2].getText() + "' ";
            }
            query += "GROUP BY О_ID ORDER BY S_КОЛИЧЕСТВО_ЗАБРОНИРОВАННЫХ_МЕСТ desc) as _ WHERE S_КОЛИЧЕСТВО_ЗАБРОНИРОВАННЫХ_МЕСТ >= " + dTextFields[0].getText();
            //System.out.println(query);
            new QueryPanel(mainMenu,buttonsP,"",connection, query);
            jDialog.dispose();
        });
    }

    private void specificFirmBookDataQuery(){
        String [] columnNames = {"Название фирмы", "Начало периода (гггг-мм-дд)","Конец периода (гггг-мм-дд)"};
        JTextField [] dTextFields = new JTextField[columnNames.length];
        JDialog jDialog = createDialog(dTextFields,columnNames);
        JButton editB = new JButton("✔");
        jDialog.add(editB);
        jDialog.setVisible(true);
        editB.addActionListener(e -> {
            String query = "SELECT Н_НОМЕР_КОМНАТЫ, COUNT(Н_ID) as S_БРОНИРОВАЛИ_РАЗ, COUNT(*) OVER() as S_ВСЕГО_ЗАБРОНИРОВАНО_НОМЕРОВ\n" +
                    "FROM БРОНИ\n" +
                    "join ОРГАНИЗАЦИИ using(О_ID)\n" +
                    "join ПРОЖИВАНИЯ using(Б_ID)\n" +
                    "join НОМЕРА using (Н_ID)\n" +
                    "WHERE О_НАЗВАНИЕ = '" + dTextFields[0].getText() +"' ";
            if(!dTextFields[1].getText().equals("") && !dTextFields[2].getText().equals("")){
                query += " AND Б_ДАТА_ЗАСЕЛЕНИЯ => '" + dTextFields[1].getText() + "' AND Б_ДАТА_ЗАСЕЛЕНИЯ <= '" + dTextFields[2].getText() +"' ";
            }
            query += "GROUP BY Н_НОМЕР_КОМНАТЫ ORDER BY S_БРОНИРОВАЛИ_РАЗ desc";
            //System.out.println(query);
            new QueryPanel(mainMenu,buttonsP,"",connection, query);
            jDialog.dispose();
        });
    }
    private void specificContractsFirmDataQuery(){
        String [] columnNames = {"Начало периода (гггг-мм-дд)","Конец периода (гггг-мм-дд)"};
        JTextField [] dTextFields = new JTextField[columnNames.length];
        JDialog jDialog = createDialog(dTextFields,columnNames);
        JButton editB = new JButton("✔");
        jDialog.add(editB);
        jDialog.setVisible(true);
        editB.addActionListener(e -> {
            String query = "SELECT О_НАЗВАНИЕ\n" +
                    "FROM ОРГАНИЗАЦИИ\n" +
                    "JOIN ДОГОВОРА using(О_ID)";
            if(!dTextFields[0].getText().equals("") && !dTextFields[1].getText().equals("")){
                query += " WHERE Д_ДАТА_ЗАКЛЮЧЕНИЯ <= '" + dTextFields[0].getText() + "' AND Д_ДАТА_ЗАВЕРШЕНИЯ >= '" + dTextFields[1].getText() +"' ";
            }
            System.out.println(query);
            new QueryPanel(mainMenu,buttonsP,"",connection, query);
            jDialog.dispose();
        });
    }
    private void freeRoomsQuery(){
        String [] columnNames = {"Класс корпуса","Местность номера","Этаж"};
        JTextField [] dTextFields = new JTextField[columnNames.length];
        JDialog jDialog = createDialog(dTextFields,columnNames);
        JButton editB = new JButton("✔");
        jDialog.add(editB);
        jDialog.setVisible(true);
        editB.addActionListener(e -> {
            String query = "SELECT COUNT(DISTINCT Н_ID)\n" +
                    "FROM НОМЕРА\n" +
                    "LEFT join ПРОЖИВАНИЯ using(Н_ID)\n" +
                    "join КОРПУСА using(К_ID)\n" +
                    "WHERE (Н_ID NOT IN\n" +
                    "(SELECT Н_ID\n" +
                    "FROM НОМЕРА\n" +
                    "join ПРОЖИВАНИЯ using(Н_ID)\n" +
                    "WHERE (П_ДАТА_ЗАСЕЛЕНИЯ < NOW() AND (П_ДАТА_ОСВОБОЖДЕНИЯ > NOW() OR П_ДАТА_ОСВОБОЖДЕНИЯ IS NULL))\n" +
                    ")";
            if(!dTextFields[0].getText().equals("")){
                query += " AND К_КЛАСС = " + dTextFields[0].getText();
            }
            if(!dTextFields[2].getText().equals("")){
                query += " AND Н_МЕСТНОСТЬ = " + dTextFields[1].getText();
            }
            if(!dTextFields[1].getText().equals("")){
                query += " AND Н_ЭТАЖ = " + dTextFields[2].getText();
            }
            query += ")";
            System.out.println(query);
            new QueryPanel(mainMenu,buttonsP,"",connection, query);
            jDialog.dispose();
        });
    }
    private void specificFreeRoomQuery() {
        String [] columnNames = {"Название корпуса","Номер комнаты"};
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
                String query = "SELECT DISTINCT *\n" +
                        "FROM\n" +
                        "(SELECT Н_ID, Н_НОМЕР_КОМНАТЫ, Н_ЭТАЖ, Н_МЕСТНОСТЬ, Н_СТОИМОСТЬ_НОМЕРА, Н_ЭТАЖ, К_КЛАСС as S_КЛАСС_КОРПУСА\n" +
                        "FROM НОМЕРА\n" +
                        "LEFT join ПРОЖИВАНИЯ using(Н_ID)\n" +
                        "join КОРПУСА using(К_ID)\n" +
                        "WHERE Н_ID NOT IN\n" +
                        "(SELECT Н_ID\n" +
                        "FROM НОМЕРА\n" +
                        "join ПРОЖИВАНИЯ using(Н_ID)\n" +
                        "WHERE (П_ДАТА_ЗАСЕЛЕНИЯ < NOW() AND (П_ДАТА_ОСВОБОЖДЕНИЯ > NOW() OR П_ДАТА_ОСВОБОЖДЕНИЯ IS NULL))\n" +
                        ") AND Н_ID =" + id + ") as T1\n" +
                        "LEFT JOIN \n" +
                        "(SELECT Н_ID, MIN(П_ДАТА_ЗАСЕЛЕНИЯ) as S_СВОБОДЕН_ДО \n" +
                        "FROM НОМЕРА\n" +
                        "LEFT join ПРОЖИВАНИЯ using(Н_ID)\n" +
                        "WHERE (Н_ID = " + id + " AND П_ДАТА_ЗАСЕЛЕНИЯ > NOW())\n" +
                        "GROUP BY Н_ID) as T2 using(Н_ID)";
                //System.out.println(query);
                new QueryPanel(mainMenu,buttonsP,"",connection, query);
                jDialog.dispose();
            } catch (SQLException ex) {
                System.out.println("Incorrect arguments");
            }

        });
    }
    private void occupiedRoomsQuery(){
        String [] columnNames = {"Дата (гггг-мм-дд)",""};
        JTextField [] dTextFields = new JTextField[columnNames.length];
        JDialog jDialog = createDialog(dTextFields,columnNames);
        JButton editB = new JButton("✔");
        jDialog.add(editB);
        jDialog.setVisible(true);
        editB.addActionListener(e -> {
            String query = "SELECT DISTINCT Н_НОМЕР_КОМНАТЫ\n" +
                    "FROM\n" +
                    "(SELECT Н_НОМЕР_КОМНАТЫ\n" +
                    "FROM НОМЕРА\n" +
                    "LEFT join ПРОЖИВАНИЯ using(Н_ID)\n" +
                    "WHERE (П_ДАТА_ЗАСЕЛЕНИЯ < NOW() AND (П_ДАТА_ОСВОБОЖДЕНИЯ > NOW() OR П_ДАТА_ОСВОБОЖДЕНИЯ IS NULL))) as T1\n" +
                    "JOIN\n" +
                    "(SELECT Н_НОМЕР_КОМНАТЫ\n" +
                    "FROM НОМЕРА\n" +
                    "WHERE Н_ID NOT IN\n" +
                    "(SELECT Н_ID\n" +
                    "FROM НОМЕРА\n" +
                    "LEFT join ПРОЖИВАНИЯ using(Н_ID)\n" +
                    "WHERE (П_ДАТА_ЗАСЕЛЕНИЯ < '"+ dTextFields[0].getText() +"' AND (П_ДАТА_ОСВОБОЖДЕНИЯ > '"+ dTextFields[0].getText() +"' OR П_ДАТА_ОСВОБОЖДЕНИЯ IS NULL))\n" +
                    ")) as T2 using(Н_НОМЕР_КОМНАТЫ)";
            //System.out.println(query);
            new QueryPanel(mainMenu,buttonsP,"",connection, query);
            jDialog.dispose();
        });
    }
    private void roomsProfitQuery(){
        String [] columnNames = {"Начало периода (гггг-мм-дд)","Конец периода (гггг-мм-дд)", "Название корпуса", "Класс корпуса",
                "Местность номера", "Этаж номера ", "Номер комнаты", "Нижняя граница стоимости", "Верхняя граница стоимости"};
        JTextField [] dTextFields = new JTextField[columnNames.length];
        JDialog jDialog = createDialog(dTextFields,columnNames);
        JButton editB = new JButton("✔");
        jDialog.add(editB);
        jDialog.setVisible(true);
        editB.addActionListener(e -> {
            String query = "SELECT Н_ID, ДОХОДЫ/РАСХОДЫ as РЕНТАБЕЛЬНОСТЬ" +
                    " FROM (SELECT Н_ID, SUM(РСН_РАСХОДЫ) as РАСХОДЫ FROM НОМЕРА " +
                    " join РАСХОДЫ_НА_СООДЕРЖАНИЕ_НОМЕРА using(Н_ID) join КОРПУСА using(К_ID) ";
            query += " WHERE РСН_ДАТА <= '" + dTextFields[1].getText() + "' AND РСН_ДАТА >= '" + dTextFields[0].getText() + "' ";
            if(!dTextFields[2].getText().equals("")){
                query += " AND К_ИМЯ =  '" + dTextFields[2].getText() + "' ";
            }
            if(!dTextFields[3].getText().equals("")){
                query += " AND К_КЛАСС =  '" + dTextFields[3].getText() + "' ";
            }
            if(!dTextFields[4].getText().equals("")){
                query += " AND Н_МЕСТНОСТЬ =  '" + dTextFields[4].getText() + "' ";
            }
            if(!dTextFields[5].getText().equals("")){
                query += " AND Н_ЭТАЖ =  '" + dTextFields[5].getText() + "' ";
            }
            if(!dTextFields[6].getText().equals("")){
                query += " AND Н_НОМЕР_КОМНАТЫ =  '" + dTextFields[6].getText() + "' ";
            }
            if(!dTextFields[7].getText().equals("")){
                query += " AND Н_СТОИМОСТЬ_НОМЕРА <= '" + dTextFields[7].getText() + "' ";
            }
            if(!dTextFields[8].getText().equals("")){
                query += " AND Н_СТОИМОСТЬ_НОМЕРА >= '" + dTextFields[8].getText() + "' ";
            }
            query += " GROUP BY Н_ID) as T1 join (SELECT Н_ID, SUM(П_СЧЁТ_ЗА_ПРОЖИВАНИЯ) as ДОХОДЫ FROM НОМЕРА " +
                    " join ПРОЖИВАНИЯ using (Н_ID) WHERE (П_ДАТА_ОПЛАТЫ_СЧЁТА <= '" +dTextFields[1].getText() + "' AND П_ДАТА_ОПЛАТЫ_СЧЁТА >= '" +
                    dTextFields[0].getText() + "') GROUP BY Н_ID) as T2 using (Н_ID) ";
            //System.out.println(query);
            new QueryPanel(mainMenu,buttonsP,"",connection, query);
            jDialog.dispose();
        });
    }
    private void specificRoomInfoQuery(){
        String [] columnNames = {"Начало периода (гггг-мм-дд)","Конец периода (гггг-мм-дд)", "Название корпуса", "Номер комнаты"};
        JTextField [] dTextFields = new JTextField[columnNames.length];
        JDialog jDialog = createDialog(dTextFields,columnNames);
        JButton editB = new JButton("✔");
        jDialog.add(editB);
        jDialog.setVisible(true);
        editB.addActionListener(e -> {
            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery( "SELECT Н_ID FROM НОМЕРА JOIN КОРПУСА using(К_ID) WHERE К_ИМЯ = '" + dTextFields[2].getText() + "' AND Н_НОМЕР_КОМНАТЫ = " + dTextFields[3].getText());
                resultSet.next();
                int id = resultSet.getInt(1);
                String query = "SELECT Н_НОМЕР_КОМНАТЫ, Н_ЭТАЖ, Н_МЕСТНОСТЬ, Н_СТОИМОСТЬ_НОМЕРА, КЛ_ФИО, П_ДАТА_ЗАСЕЛЕНИЯ, П_ДАТА_ОСВОБОЖДЕНИЯ " +
                        "FROM НОМЕРА JOIN ПРОЖИВАНИЯ using(Н_ID) JOIN КЛИЕНТЫ using(КЛ_ID) " +
                        "WHERE Н_ID = " + id;
                if(!dTextFields[0].getText().equals("") && !dTextFields[1].getText().equals("")){
                    query += " AND П_ДАТА_ЗАСЕЛЕНИЯ >= '"+ dTextFields[0].getText()+"'  AND П_ДАТА_ЗАСЕЛЕНИЯ <= '" +  dTextFields[1].getText() + "' ";
                }
                //System.out.println(query);
                new QueryPanel(mainMenu,buttonsP,"",connection, query);
                jDialog.dispose();
            } catch (SQLException ex) {
                System.out.println("Incorrect arguments");
            }
        });
    }
    private void clientsInSpecificRoomsQuery(){
        String [] columnNames = {"Начало периода (гггг-мм-дд)","Конец периода (гггг-мм-дд)", "Название корпуса", "Класс корпуса",
                "Местность номера", "Этаж номера ", "Номер комнаты", "Нижняя граница стоимости", "Верхняя граница стоимости"};
        JTextField [] dTextFields = new JTextField[columnNames.length];
        JDialog jDialog = createDialog(dTextFields,columnNames);
        JButton editB = new JButton("✔");
        jDialog.add(editB);
        jDialog.setVisible(true);
        editB.addActionListener(e -> {
            String query = "SELECT DISTINCT КЛ_ФИО, COUNT(*) OVER () as S_ОБЩЕЕ_ЧИСЛО_ПОСТОЯЛЬЦЕВ FROM КЛИЕНТЫ " +
                    " join ПРОЖИВАНИЯ using(КЛ_ID) join НОМЕРА using(Н_ID) join КОРПУСА using(К_ID) ";
            query += " WHERE П_ДАТА_ЗАСЕЛЕНИЯ <= '" + dTextFields[1].getText() + "' AND П_ДАТА_ЗАСЕЛЕНИЯ >= '" + dTextFields[0].getText() + "' ";
            if(!dTextFields[2].getText().equals("")){
                query += " AND К_ИМЯ =  '" + dTextFields[2].getText() + "' ";
            }
            if(!dTextFields[3].getText().equals("")){
                query += " AND К_КЛАСС =  '" + dTextFields[3].getText() + "' ";
            }
            if(!dTextFields[4].getText().equals("")){
                query += " AND Н_МЕСТНОСТЬ =  '" + dTextFields[4].getText() + "' ";
            }
            if(!dTextFields[5].getText().equals("")){
                query += " AND Н_ЭТАЖ =  '" + dTextFields[5].getText() + "' ";
            }
            if(!dTextFields[6].getText().equals("")){
                query += " AND Н_НОМЕР_КОМНАТЫ =  '" + dTextFields[6].getText() + "' ";
            }
            if(!dTextFields[7].getText().equals("")){
                query += " AND Н_СТОИМОСТЬ_НОМЕРА <= '" + dTextFields[7].getText() + "' ";
            }
            if(!dTextFields[8].getText().equals("")){
                query += " AND Н_СТОИМОСТЬ_НОМЕРА >= '" + dTextFields[8].getText() + "' ";
            }
            //System.out.println(query);
            new QueryPanel(mainMenu,buttonsP,"",connection, query);
            jDialog.dispose();
        });
    }
    private void clientFromSpecificRoom(){
        String [] columnNames = {"Название корпуса", "Номер комнаты"};
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
                String query = "SELECT КЛ_ФИО, КЛ_ПОЛ, П_СЧЁТ_ЗА_ПРОЖИВАНИЯ, П_СЧЁТ_ЗА_ДОПОЛНИТЕЛЬЫЕ_УСЛУГИ, Р_НАЗВАНИЕ, СБ_НАЗВАНИЕ, Ж_ЖАЛОБА\n" +
                        "FROM НОМЕРА join ПРОЖИВАНИЯ using(Н_ID) join КЛИЕНТЫ using(КЛ_ID) left join ЖАЛОБЫ using(КЛ_ID, Н_ID)\n" +
                        "left join ПРОЖИВАНИЯ_СЛУЖБЫ_БЫТА using(П_ID) left join СЛУЖБЫ_БЫТА using(СБ_ID)\n" +
                        "left join ПРОЖИВАНИЯ_РАЗВЛЕЧЕНИЯ using(П_ID) left join РАЗВЛЕЧЕНИЯ using(Р_ID)\n" +
                        "WHERE (Н_ID = " + id + " AND П_ДАТА_ЗАСЕЛЕНИЯ < NOW() AND (П_ДАТА_ОСВОБОЖДЕНИЯ > NOW() OR П_ДАТА_ОСВОБОЖДЕНИЯ IS NULL))";
                System.out.println(query);
                new QueryPanel(mainMenu,buttonsP,"",connection, query);
                jDialog.dispose();
            } catch (SQLException ex) {
                System.out.println("Incorrect arguments");
            }
        });
    }
    private void allRoomsToOrgRoomsRatioQuery() throws SQLException {
        String query = "SELECT CAST(ОРГ as float) / CAST(ВСЕ as float) * 100.0 as S_ПРОЦЕНТ_НОМЕРОВ" +
                "FROM (SELECT COUNT(DISTINCT Н_ID) as ВСЕ FROM НОМЕРА JOIN ПРОЖИВАНИЯ using(Н_ID) JOIN БРОНИ using(Б_ID)) as T1, " +
                "(SELECT COUNT(DISTINCT Н_ID) as ОРГ FROM НОМЕРА JOIN ПРОЖИВАНИЯ using(Н_ID) JOIN БРОНИ using(Б_ID)\n" +
                "JOIN ОРГАНИЗАЦИИ using(О_ID)) as T2\n";
        new QueryPanel(mainMenu,buttonsP,"",connection,query);
    }
    private void setButtonSize(JButton jButton,int width, int height){
        jButton.setPreferredSize(new Dimension(width,height));
        jButton.setMaximumSize(new Dimension(width,height));
        jButton.setMinimumSize(new Dimension(width,height));
    }
    private JButton addQueryButton(String label,ActionListener actionListener){
        JButton button = new JButton(label);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(new Font("impact",Font.PLAIN,30));
        button.addActionListener(actionListener);
        setButtonSize(button,1600,50);
        return button;
    }
    public QueryMenu(MainMenu mainMenu){
        this.mainMenu = mainMenu;
        this.connection = mainMenu.getConnection();
        mainMenu.getJPanel().setVisible(false);
        buttonsP = new JPanel();
        mainMenu.add(buttonsP);
        drawQueryMenu();
        mainMenu.revalidate();
    }
    private void drawQueryMenu(){
        buttonsP.setLayout(new BoxLayout(buttonsP, BoxLayout.Y_AXIS));
        JButton backB = new JButton("Назад");
        backB.setAlignmentX(Component.CENTER_ALIGNMENT);
        backB.setFont(new Font("impact",Font.PLAIN,45));

        addGetUpsetClientsButton(buttonsP);
        addGetRegularClientsButton(buttonsP);
        addNewClientsButton(buttonsP);
        addSpecificClientButton(buttonsP);
        addFirmBookListButton(buttonsP);
        addSpecificFirmBookDataButton(buttonsP);
        addSpecificContractsFirmDataButton(buttonsP);
        addFreeRoomsButton(buttonsP);
        addSpecificFreeRoomInfoButton(buttonsP);
        addOccupiedRoomsButton(buttonsP);
        addRoomsProfitButton(buttonsP);
        addSpecificRoomInfoButton(buttonsP);
        addClientsInSpecificRoomsButton(buttonsP);
        addClientFromSpecificRoomButton(buttonsP);
        addAllRoomsToOrgRoomsRatioButton(buttonsP);

        ActionListener backListener = e -> {
            mainMenu.remove(buttonsP);
            mainMenu.getJPanel().setVisible(true);
            mainMenu.revalidate();
        };
        backB.addActionListener(backListener);
        buttonsP.add(backB);
    }
    private void addGetUpsetClientsButton(JPanel panel){
        ActionListener actionListener = e -> {
            try {
                upsetClientsQuery();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        };
        JButton exitB = addQueryButton("Получить список недоволньых клиентов и их жалобы",actionListener);
        panel.add(exitB);
    }
    private void addGetRegularClientsButton(JPanel panel){
        ActionListener actionListener = e -> {
            reqularClientsQuery();
        };
        JButton exitB = addQueryButton("Получить список наиболее часто посещающих постояльцев",actionListener);
        panel.add(exitB);
    }
    private void addNewClientsButton(JPanel panel){
        ActionListener actionListener = e -> {
            newClientsQuery();
        };
        JButton exitB = addQueryButton("Получить список новых клиентов",actionListener);
        panel.add(exitB);
    }
    private void addSpecificClientButton(JPanel panel){
        ActionListener actionListener = e -> {
            specificClientQuery();
        };
        JButton exitB = addQueryButton("Получить сведения о конкретном клиенте",actionListener);
        panel.add(exitB);
    }
    private void addFirmBookListButton(JPanel panel){
        ActionListener actionListener = e -> {
            firmBookListQuery();
        };
        JButton exitB = addQueryButton("Получить список фирм забронировавших места в обхъеме больше указанного",actionListener);
        panel.add(exitB);
    }
    private void addSpecificFirmBookDataButton(JPanel panel){
        ActionListener actionListener = e -> {
            specificFirmBookDataQuery();
        };
        JButton exitB = addQueryButton("Получить данные об объёме бронирования конкретной фирмы",actionListener);
        panel.add(exitB);
    }
    private void addSpecificContractsFirmDataButton(JPanel panel){
        ActionListener actionListener = e -> {
            specificContractsFirmDataQuery();
        };
        JButton exitB = addQueryButton("Получить сведения о фирмах с которыми заключён договор за указанный период",actionListener);
        panel.add(exitB);
    }
    private void addFreeRoomsButton(JPanel panel){
        ActionListener actionListener = e -> {
            freeRoomsQuery();
        };
        JButton exitB = addQueryButton("Получить количество свободных номеров",actionListener);
        panel.add(exitB);
    }
    private void addSpecificFreeRoomInfoButton(JPanel panel){
        ActionListener actionListener = e -> {
            specificFreeRoomQuery();
        };
        JButton exitB = addQueryButton("Получить сведения о конкретном свободном номере",actionListener);
        panel.add(exitB);
    }
    private void addOccupiedRoomsButton(JPanel panel){
        ActionListener actionListener = e -> {
            occupiedRoomsQuery();
        };
        JButton exitB = addQueryButton("Получить список занятых номеров, освобождающихся к сроку",actionListener);
        panel.add(exitB);
    }
    private void addRoomsProfitButton(JPanel panel){
        ActionListener actionListener = e -> {
            roomsProfitQuery();
        };
        JButton exitB = addQueryButton("Получить данные о рентабельности номеров",actionListener);
        panel.add(exitB);
    }
    private void addSpecificRoomInfoButton(JPanel panel){
        ActionListener actionListener = e -> {
            specificRoomInfoQuery();
        };
        JButton exitB = addQueryButton("Получить сведения о конкретном номере: кем он был занят в определенный период",actionListener);
        panel.add(exitB);
    }
    private void addClientsInSpecificRoomsButton(JPanel panel){
        ActionListener actionListener = e -> {
            clientsInSpecificRoomsQuery();
        };
        JButton exitB = addQueryButton("Получить число постояльцев заселившихся в номера с указанными характеристиками",actionListener);
        panel.add(exitB);
    }
    private void addClientFromSpecificRoomButton(JPanel panel){
        ActionListener actionListener = e -> {
            clientFromSpecificRoom();
        };
        JButton exitB = addQueryButton("Получить сведения о постояльце из заданного номера",actionListener);
        panel.add(exitB);
    }
    private void addAllRoomsToOrgRoomsRatioButton(JPanel panel){
        ActionListener actionListener = e -> {
            try {
                allRoomsToOrgRoomsRatioQuery();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        };
        JButton exitB = addQueryButton("Получить процентное отношение всех номеров к номерам, бронируемым партнерами",actionListener);
        panel.add(exitB);
    }
}
