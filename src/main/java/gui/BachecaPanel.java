package gui;

import model.Bacheca;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class BachecaPanel extends JPanel {
    private JPanel todoListPanel;
    private List<ToDoPanel> currentToDoPanels;
    private Bacheca bacheca;

    private JLabel titoloLabel;
    private JLabel descrizioneLabel;
    private JButton modifyDescButton;

    public BachecaPanel(Bacheca bacheca) {
        this.bacheca = bacheca;
        setLayout(new BorderLayout());
        setBackground(new Color(11, 0, 128));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        titoloLabel = new JLabel(bacheca.getTitolo().toString(), SwingConstants.CENTER);
        titoloLabel.setForeground(Color.WHITE);
        titoloLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        headerPanel.add(titoloLabel, BorderLayout.NORTH);

        descrizioneLabel = new JLabel(bacheca.getDescrizione(), SwingConstants.CENTER);
        descrizioneLabel.setForeground(Color.LIGHT_GRAY);
        descrizioneLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        headerPanel.add(descrizioneLabel, BorderLayout.CENTER);

        modifyDescButton = new JButton("Modifica descrizione");
        modifyDescButton.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        modifyDescButton.setMargin(new Insets(2, 5, 2, 5));
        modifyDescButton.setFocusPainted(false);
        headerPanel.add(modifyDescButton, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

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

    public JButton getModifyDescButton() {
        return modifyDescButton;
    }

    public void updateDescrizioneLabel() {
        this.descrizioneLabel.setText(this.bacheca.getDescrizione());
    }
}
