package org.example.view.tablepanels;

import org.example.view.MainMenu;

import javax.swing.*;
import java.sql.Connection;

public class EntertainmentsTablePanel extends TablePanel {
    public EntertainmentsTablePanel(MainMenu mainMenu, JPanel oldP, String tableName, Connection connection){
        super(mainMenu,oldP, tableName,connection);
        tableInitSelectQuery = "SELECT Р_ID, Р_НАЗВАНИЕ, Р_СТОИМОСТЬ FROM РАЗВЛЕЧЕНИЯ";
        drawTablePanel();
    }
}
