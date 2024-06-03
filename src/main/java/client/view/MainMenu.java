package client.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MainMenu extends JFrame {
    private final JPanel p;
    private static final String URL = "jdbc:postgresql://172.27.191.197:5432/task4";
    private static final String USER = "postgres";
    private static final String PASSWORD = "2442";
    private Connection connection;
    private void setButtonSize(JButton jButton,int width, int height){
        jButton.setPreferredSize(new Dimension(width,height));
        jButton.setMaximumSize(new Dimension(width,height));
        jButton.setMinimumSize(new Dimension(width,height));
    }

    public MainMenu(){
        connectToDB();
        this.setSize(1600, 900);
        p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Гостинничный комплекс \" Уберж\"");
        title.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        title.setFont(new Font("impact", Font.PLAIN,60));

        JButton tableMenuB = new JButton("Таблицы");
        tableMenuB.setAlignmentX(Component.CENTER_ALIGNMENT);
        tableMenuB.setFont(new Font("impact",Font.PLAIN,45));
        ActionListener tableMenuListener = e -> {
            TableMenu tableMenu = new TableMenu(this);
        };
        tableMenuB.addActionListener(tableMenuListener);
        setButtonSize(tableMenuB,500,70);

        JButton queryMenuB = new JButton("Запросы");
        queryMenuB.setAlignmentX(Component.CENTER_ALIGNMENT);
        queryMenuB.setFont(new Font("impact",Font.PLAIN,45));
        ActionListener queryMenuListener = e -> {
            QueryMenu queryMenu = new QueryMenu(this);
        };
        queryMenuB.addActionListener(queryMenuListener);
        setButtonSize(queryMenuB,500,70);

        JButton functionalMenuB = new JButton("Функционал");
        functionalMenuB.setAlignmentX(Component.CENTER_ALIGNMENT);
        functionalMenuB.setFont(new Font("impact",Font.PLAIN,45));
        ActionListener functionalMenuListener = e -> {
            FunctionalMenu functionalMenu = new FunctionalMenu(this);
        };
        functionalMenuB.addActionListener(functionalMenuListener);
        setButtonSize(functionalMenuB,500,70);


        JButton exitB = new JButton("Выход");
        exitB.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitB.setFont(new Font("impact",Font.PLAIN,45));
        ActionListener exitListener = e -> {
            MainMenu.this.setVisible(false);
            MainMenu.this.dispose();
        };
        exitB.addActionListener(exitListener);
        setButtonSize(exitB,500,70);
        p.add(title);
        p.add(tableMenuB);
        p.add(queryMenuB);
        p.add(functionalMenuB);
        p.add(exitB);
        add(p);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
    public JPanel getJPanel(){
        return p;
    }
    public Connection getConnection(){
        return connection;
    }

    private void connectToDB(){
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            connection.setAutoCommit(false);
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }
}
