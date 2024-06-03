package org.example.view.tablepanels;

import org.example.view.MainMenu;

import javax.swing.*;
import java.sql.Connection;

public class ClientsTablePanel extends TablePanel {
    public ClientsTablePanel(MainMenu mainMenu, JPanel oldP, String tableName, Connection connection){
        super(mainMenu,oldP, tableName,connection);
        tableInitSelectQuery = "SELECT КЛ_ID, КЛ_ФИО, КЛ_ДАТА_РОЖДЕНИЯ, КЛ_ПОЛ, КЛ_СЕРИЯ_И_НОМЕР_ПАСПОРТА FROM КЛИЕНТЫ";
        drawTablePanel();
    }
}
