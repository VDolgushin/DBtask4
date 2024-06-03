package org.example.view.tablepanels;

import org.example.view.MainMenu;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;

public class BookTablePanel extends TablePanel {
    public BookTablePanel(MainMenu mainMenu, JPanel oldP, String tableName, Connection connection){
        super(mainMenu,oldP, tableName,connection);
        tableInitSelectQuery = "SELECT Б_ID, Б_КЛАСС_ОТЕЛЯ, Б_ЭТАЖ, Б_КОЛИЧЕТСТВО_НОМЕРОВ, Б_КОЛИЧЕТСТВО_ЛЮДЕЙ, Б_ДАТА_ЗАСЕЛЕНИЯ, Б_ДАТА_ОСВОБОЖДЕНИЯ," +
                " О_НАЗВАНИЕ as О_НАЗВАНИЕ_ОРГАНИЗАЦИИ, КЛ_ФИО as КЛ_ФИО_КЛИЕНТА FROM БРОНИ LEFT JOIN ОРГАНИЗАЦИИ using(О_ID) " +
                "LEFT JOIN КЛИЕНТЫ using(КЛ_ID)";
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
