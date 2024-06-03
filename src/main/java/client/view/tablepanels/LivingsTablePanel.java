package client.view.tablepanels;

import client.view.MainMenu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;

public class LivingsTablePanel extends TablePanel {
    public LivingsTablePanel(MainMenu mainMenu, JPanel oldP, String tableName, Connection connection){
        super(mainMenu,oldP, tableName,connection);
        tableInitSelectQuery = "SELECT П_ID, П_ДАТА_ЗАСЕЛЕНИЯ, П_ДАТА_ОСВОБОЖДЕНИЯ, П_СЧЁТ_ЗА_ПРОЖИВАНИЯ, П_СЧЁТ_ЗА_ДОПОЛНИТЕЛЬЫЕ_УСЛУГИ," +
                " П_ДАТА_ОПЛАТЫ_СЧЁТА, КЛ_ФИО, Н_НОМЕР_КОМНАТЫ, К_ИМЯ, Б_ID as Б_ID_БРОНИ FROM ПРОЖИВАНИЯ " +
                "LEFT JOIN КЛИЕНТЫ USING(КЛ_ID) LEFT JOIN НОМЕРА USING(Н_ID) LEFT JOIN КОРПУСА using(К_ID) LEFT JOIN БРОНИ USING(Б_ID)";
        drawTablePanel();
        //addEntertainmentsButton(buttonsP);
        //addServicesButton(buttonsP);
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
    protected void addEntertainmentsButton(JPanel panel){
        JButton exitB = new JButton("Редактировать развлечения приобретённые за время проживания");
        exitB.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitB.setFont(new Font("impact",Font.PLAIN,30));
        ActionListener exitListener = e -> {
        };
        exitB.addActionListener(exitListener);
        panel.add(exitB);
    }
    protected void addServicesButton(JPanel panel){
        JButton exitB = new JButton("Редактировать службы быта приобретённые за время проживания");
        exitB.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitB.setFont(new Font("impact",Font.PLAIN,30));
        ActionListener exitListener = e -> {
        };
        exitB.addActionListener(exitListener);
        panel.add(exitB);
    }
}
