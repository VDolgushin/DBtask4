package org.example.view.tablepanels;

import org.example.view.MainMenu;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;

public class ContractsTablePanel extends TablePanel {
    public ContractsTablePanel(MainMenu mainMenu, JPanel oldP, String tableName, Connection connection){
        super(mainMenu,oldP, tableName,connection);
        tableInitSelectQuery = "SELECT Д_ID, Д_ДАТА_ЗАКЛЮЧЕНИЯ, Д_ДАТА_ЗАВЕРШЕНИЯ, Д_СКИДКА, О_НАЗВАНИЕ as О_НАЗВАНИЕ_ОРГАНИЗАЦИИ FROM ДОГОВОРА " +
                "LEFT JOIN ОРГАНИЗАЦИИ using(О_ID)";
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
