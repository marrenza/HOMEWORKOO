package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class MainFrame extends JFrame {
    private JPanel bachechePanel;
    private JButton addToDoButton;
    private JButton searchButton;
    private JButton  addBachecaButton;
    private JButton deleteBachecaButton;
    private JLabel welcomeLabel;

    public MainFrame(String username){
        setTitle("ToDo Manager - Bacheche");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        //Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(6, 0, 64));

        welcomeLabel = new JLabel("Benvenuto, " + username);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.add(welcomeLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);


        addToDoButton = new JButton("Aggiungi ToDo");
        styleButton(addToDoButton);
        buttonPanel.add(addToDoButton);

        searchButton = new JButton("Cerca ToDo");
        styleButton(searchButton);
        buttonPanel.add(searchButton);

        addBachecaButton = new JButton("Crea Bacheca");
        styleButton(addBachecaButton);
        buttonPanel.add(addBachecaButton);

        deleteBachecaButton = new JButton("Elimina Bacheca");
        styleButton(deleteBachecaButton);
        buttonPanel.add(deleteBachecaButton);

        headerPanel.add(buttonPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        bachechePanel = new JPanel(new GridLayout(1, 3, 10, 10));
        bachechePanel.setBackground(Color.WHITE);
        bachechePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


        add(bachechePanel, BorderLayout.CENTER);
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(83, 69, 230));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 50, 200), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    // Getter per i pannelli per poter aggiungere ToDo dal controller
    public JPanel getBachechePanel() {
        return bachechePanel;
    }

    public JButton getAddToDoButton() {
        return addToDoButton;
    }

    public JButton getSearchButton() {
        return searchButton;
    }
    public JButton getAddBachecaButton() {
        return addBachecaButton;
    }
    public JButton getDeleteBachecaButton() {
        return deleteBachecaButton;
    }
}
