package client.view;

import client.view.tablepanels.TablePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.util.HashMap;

public class TableMenu {
    private final MainMenu mainMenu;
    private final JPanel p;
    private static final HashMap<String,String> ruEngTableNames = new HashMap<>();
    private void setButtonSize(JButton jButton,int width, int height){
        jButton.setPreferredSize(new Dimension(width,height));
        jButton.setMaximumSize(new Dimension(width,height));
        jButton.setMinimumSize(new Dimension(width,height));
    }
    private JButton addTableButton(String caption) {
        JButton button = new JButton(caption.replace('_', ' '));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(new Font("impact",Font.PLAIN,45));
        ActionListener buttonListener = e -> {
            try {
                Class<? extends TablePanel> tablePanelClass = (Class<? extends TablePanel>) Class.forName(this.getClass().getPackage().getName() + ".tablepanels." + ruEngTableNames.get(caption) + "TablePanel");
                TablePanel tablePanel = tablePanelClass.getConstructor(MainMenu.class,JPanel.class,String.class, Connection.class).newInstance(mainMenu, this.p, caption, mainMenu.getConnection());
            }
            catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                   InvocationTargetException ex){
                ex.printStackTrace();
            }
        };
        button.addActionListener(buttonListener);
        setButtonSize(button,500,70);
        p.add(button);
        return button;
    }
    public TableMenu(MainMenu mainMenu){
        this.mainMenu = mainMenu;
        mainMenu.getJPanel().setVisible(false);
        p = new JPanel();
        mainMenu.add(p);
        drawTableMenu();
        mainMenu.revalidate();
    }
    private void drawTableMenu(){
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        initRuEngMap();
        JButton buildingsB = addTableButton("Корпуса");
        JButton roomsB = addTableButton("Номера");
        JButton clientsB = addTableButton("Клиенты");
        JButton livingsB = addTableButton("Проживания");
        JButton bookB = addTableButton("Брони");
        JButton entertainmentsB = addTableButton("Развлечения");
        JButton servicesB = addTableButton("Службы_быта");
        JButton organizationsB = addTableButton("Организации");
        JButton contractsB = addTableButton("Договора");
        JButton complainsB = addTableButton("Жалобы");
        JButton backB = new JButton("Назад");
        backB.setAlignmentX(Component.CENTER_ALIGNMENT);
        backB.setFont(new Font("impact",Font.PLAIN,45));
        ActionListener backListener = e -> {
            mainMenu.remove(p);
            mainMenu.getJPanel().setVisible(true);
            mainMenu.revalidate();
        };
        backB.addActionListener(backListener);
        setButtonSize(backB,500,70);
        p.add(buildingsB);
        p.add(roomsB);
        p.add(clientsB);
        p.add(livingsB);
        p.add(bookB);
        p.add(entertainmentsB);
        p.add(servicesB);
        p.add(organizationsB);
        p.add(contractsB);
        p.add(complainsB);
        p.add(backB);
    }
    private void initRuEngMap(){
        ruEngTableNames.put("Корпуса","Buildings");
        ruEngTableNames.put("Номера","Rooms");
        ruEngTableNames.put("Клиенты","Clients");
        ruEngTableNames.put("Проживания","Livings");
        ruEngTableNames.put("Брони","Book");
        ruEngTableNames.put("Развлечения","Entertainments");
        ruEngTableNames.put("Службы_быта","Services");
        ruEngTableNames.put("Организации","Organizations");
        ruEngTableNames.put("Договора","Contracts");
        ruEngTableNames.put("Жалобы","Complains");
    }
}
