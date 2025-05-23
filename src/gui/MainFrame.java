package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class MainFrame extends JFrame {
    public MainFrame(String username){
        setTitle("ToDo Manager - Bacheche");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        //Header
        JLabel welcomeLabel = new JLabel("Benvenuto," + username);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        welcomeLabel.setForeground(Color.WHITE);

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setBackground(new Color(6, 0, 64)); //#060040
        header.add(welcomeLabel);

        //Contenuto bacheche
        JPanel boardsPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        boardsPanel.setBackground(Color.WHITE);
        boardsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        boardsPanel.add(new BachecaPanel("Università"));
        boardsPanel.add(new BachecaPanel("Lavoro"));
        boardsPanel.add(new BachecaPanel("Tempo Libero"));

        add(header, BorderLayout.NORTH);
        add(boardsPanel, BorderLayout.CENTER);
    }
}