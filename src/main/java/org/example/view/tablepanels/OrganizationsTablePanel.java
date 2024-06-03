package org.example.view.tablepanels;

import org.example.view.MainMenu;

import javax.swing.*;
import java.sql.Connection;

public class OrganizationsTablePanel extends TablePanel {
    public OrganizationsTablePanel(MainMenu mainMenu,JPanel oldP, String tableName, Connection connection){
        super(mainMenu,oldP, tableName,connection);
        tableInitSelectQuery = "SELECT О_ID, О_НАЗВАНИЕ FROM ОРГАНИЗАЦИИ";
        drawTablePanel();
    }
}
