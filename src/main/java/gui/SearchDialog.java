package gui;

import javax.swing.*;
import java.awt.*;

public class SearchDialog extends JDialog {
    private JTextField txtSearchTerm;
    private JTextField txtScadenzaSearch; // Per la ricerca per scadenza
    private JButton btnCerca;
    private JButton btnScadenzaOdierna;
    private JButton btnAnnulla;

    public SearchDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        setSize(400, 250);
        setLocationRelativeTo(owner);
        setLayout(new GridBagLayout());
        setResizable(false);

        initComponents();
        layoutComponents();
    }

    private void initComponents() {
        txtSearchTerm = new JTextField(20);
        txtScadenzaSearch = new JTextField(10);
        txtScadenzaSearch.setToolTipText("Formato: YYYY-MM-DD");

        btnCerca = new JButton("Cerca");
        btnScadenzaOdierna = new JButton("ToDo in Scadenza Oggi");
        btnAnnulla = new JButton("Annulla");

        btnAnnulla.addActionListener(e -> dispose());
    }

    private void layoutComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Campo di ricerca per nome/titolo
        gbc.gridx = 0; gbc.gridy = row; add(new JLabel("Cerca per Titolo/Descrizione:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 1.0; add(txtSearchTerm, gbc);

        // Campo di ricerca per scadenza
        gbc.gridx = 0; gbc.gridy = row; add(new JLabel("Cerca per Scadenza (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 1.0; add(txtScadenzaSearch, gbc);

        // Pulsante "Cerca"
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; add(btnCerca, gbc);
        row++;

        // Pulsante "ToDo in Scadenza Oggi"
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; add(btnScadenzaOdierna, gbc);
        row++;

        // Pulsante "Annulla"
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; add(btnAnnulla, gbc);
    }

    // Getters per i campi del form
    public JTextField getTxtSearchTerm() { return txtSearchTerm; }
    public JTextField getTxtScadenzaSearch() { return txtScadenzaSearch; }
    public JButton getBtnCerca() { return btnCerca; }
    public JButton getBtnScadenzaOdierna() { return btnScadenzaOdierna; }
}
