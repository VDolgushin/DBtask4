package client.view.tablepanels;

import client.view.MainMenu;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;

public class ComplainsTablePanel extends TablePanel {
    public ComplainsTablePanel(MainMenu mainMenu, JPanel oldP, String tableName, Connection connection){
        super(mainMenu,oldP, tableName,connection);
        tableInitSelectQuery = "SELECT Ж_ID, Ж_ЖАЛОБА, Ж_ДАТА, КЛ_ФИО, Н_НОМЕР_КОМНАТЫ FROM ЖАЛОБЫ LEFT JOIN КЛИЕНТЫ using(КЛ_ID) " +
                "LEFT JOIN НОМЕРА using(Н_ID)";
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
}
