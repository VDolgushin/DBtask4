package client.view.tablepanels;

import client.view.MainMenu;

import javax.swing.*;
import java.sql.Connection;

public class ServicesTablePanel extends TablePanel {
    public ServicesTablePanel(MainMenu mainMenu, JPanel oldP, String tableName, Connection connection){
        super(mainMenu,oldP, tableName,connection);
        tableInitSelectQuery = "SELECT СБ_ID, СБ_НАЗВАНИЕ, СБ_СТОИМОСТЬ FROM СЛУЖБЫ_БЫТА";
        drawTablePanel();
    }
}
