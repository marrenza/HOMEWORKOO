package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class BachecaPanel extends JPanel {
    public BachecaPanel(String titolo) {
        setLayout(new BorderLayout());
        setBackground(new Color(11, 0, 128));

        JLabel titoloLabel = new JLabel(titolo, SwingConstants.CENTER);
        titoloLabel.setForeground(Color.WHITE);
        titoloLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        add(titoloLabel, BorderLayout.NORTH);

        JPanel todoList = new JPanel();
        todoList.setLayout(new BoxLayout(todoList, BoxLayout.Y_AXIS));
        todoList.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(todoList);
        scrollPane.setBorder(null);

        add(scrollPane, BorderLayout.CENTER);
    }
}
