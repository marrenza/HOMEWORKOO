package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;


public class BachecaPanel extends JPanel {
    private JPanel todoListPanel;
    private List<ToDoPanel> currentToDoPanels;

    public BachecaPanel(String titolo) {
        setLayout(new BorderLayout());
        setBackground(new Color(11, 0, 128));

        JLabel titoloLabel = new JLabel(titolo, SwingConstants.CENTER);
        titoloLabel.setForeground(Color.WHITE);
        titoloLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        add(titoloLabel, BorderLayout.NORTH);

        todoListPanel = new JPanel();
        todoListPanel.setLayout(new BoxLayout(todoListPanel, BoxLayout.Y_AXIS));
        todoListPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(todoListPanel);
        scrollPane.setBorder(null);

        add(scrollPane, BorderLayout.CENTER);
        currentToDoPanels = new ArrayList<>();
    }

    // Metodo per aggiungere ToDoPanel dinamicamente
    public void aggiungiToDo(ToDoPanel todoPanel) {
        todoListPanel.add(todoPanel);
        currentToDoPanels.add(todoPanel);
        todoListPanel.revalidate();
        todoListPanel.repaint();
    }

    public void clearToDos() {
        todoListPanel.removeAll();
        currentToDoPanels.clear();
        todoListPanel.revalidate();
        todoListPanel.repaint();
    }
    public List<ToDoPanel> getCurrentToDoPanels() {
        return currentToDoPanels;
    }
}
