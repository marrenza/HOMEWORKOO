package gui;

import model.TitoloBacheca; // Importa l'enum TitoloBacheca
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class ToDoDialog extends JDialog {
    private JTextField txtTitolo;
    private JTextArea txtDescrizione;
    private JTextField txtScadenza; // Formato YYYY-MM-DD
    private JTextField txtImagePath;
    private JTextField txtURL;
    private JTextField txtColoreSfondo; // Formato #RRGGBB
    private JComboBox<TitoloBacheca> cmbBacheca;
    private JPanel checklistPanel; // Pannello per la checklist
    private JButton btnAddActivity;
    private JButton btnSalva;
    private JButton btnAnnulla;

    // Lista per tenere traccia dei campi di testo delle attività della checklist
    private List<JTextField> activityNameFields;
    private List<JCheckBox> activityCompletionCheckboxes;

    public ToDoDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        setSize(500, 600);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        setResizable(false); // Non ridimensionabile

        activityNameFields = new ArrayList<>();
        activityCompletionCheckboxes = new ArrayList<>();

        initComponents();
        layoutComponents();
    }

    private void initComponents() {
        txtTitolo = new JTextField(20);
        txtDescrizione = new JTextArea(5, 20);
        txtDescrizione.setLineWrap(true);
        txtDescrizione.setWrapStyleWord(true);
        JScrollPane descScrollPane = new JScrollPane(txtDescrizione);

        txtScadenza = new JTextField(10);
        txtScadenza.setToolTipText("Formato: YYYY-MM-DD");

        txtImagePath = new JTextField(20);
        txtURL = new JTextField(20);
        txtColoreSfondo = new JTextField(10);
        txtColoreSfondo.setToolTipText("Formato: #RRGGBB (es. #FF0000 per rosso)");

        cmbBacheca = new JComboBox<>(TitoloBacheca.values()); // Popola la combobox con i titoli delle bacheche

        checklistPanel = new JPanel();
        checklistPanel.setLayout(new BoxLayout(checklistPanel, BoxLayout.Y_AXIS));
        checklistPanel.setBorder(BorderFactory.createTitledBorder("Checklist Attività"));
        JScrollPane checklistScrollPane = new JScrollPane(checklistPanel);
        checklistScrollPane.setPreferredSize(new Dimension(400, 150)); // Dimensione fissa per lo scroll pane della checklist

        btnAddActivity = new JButton("Aggiungi Attività");
        btnSalva = new JButton("Salva");
        btnAnnulla = new JButton("Annulla");

        // Listener per il pulsante "Annulla"
        btnAnnulla.addActionListener(e -> dispose());

        // Listener per il pulsante "Aggiungi Attività"
        btnAddActivity.addActionListener(e -> addChecklistActivityField(null, false));
    }

    private void layoutComponents() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding esterno
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Margini tra i componenti
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Titolo
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Titolo:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 1.0; formPanel.add(txtTitolo, gbc);

        // Descrizione
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Descrizione:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 1.0; formPanel.add(new JScrollPane(txtDescrizione), gbc);

        // Scadenza
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Scadenza (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 1.0; formPanel.add(txtScadenza, gbc);

        // Immagine Path
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Percorso Immagine:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 1.0; formPanel.add(txtImagePath, gbc);

        // URL
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("URL:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 1.0; formPanel.add(txtURL, gbc);

        // Colore Sfondo
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Colore Sfondo (#RRGGBB):"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 1.0; formPanel.add(txtColoreSfondo, gbc);

        // Bacheca
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Bacheca:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 1.0; formPanel.add(cmbBacheca, gbc);

        // Checklist Panel
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; gbc.weighty = 1.0; // Espande verticalmente
        formPanel.add(new JScrollPane(checklistPanel), gbc);
        row++;

        // Add Activity Button
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2; gbc.weighty = 0.0; // Reset weighty
        formPanel.add(btnAddActivity, gbc);


        add(formPanel, BorderLayout.CENTER);

        // Pulsanti Salva e Annulla
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(btnSalva);
        buttonPanel.add(btnAnnulla);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Aggiunge un campo per un'attività della checklist al pannello.
     * @param activityName Il nome dell'attività (può essere null per una nuova attività).
     * @param isCompleted Lo stato di completamento dell'attività.
     */
    public void addChecklistActivityField(String activityName, boolean isCompleted) {
        JPanel activityRow = new JPanel(new BorderLayout());
        JTextField activityField = new JTextField(activityName);
        JCheckBox activityCheckbox = new JCheckBox("Completato");
        activityCheckbox.setSelected(isCompleted);

        activityNameFields.add(activityField);
        activityCompletionCheckboxes.add(activityCheckbox);

        JButton removeButton = new JButton("X"); // Pulsante per rimuovere l'attività
        removeButton.setForeground(Color.RED);
        removeButton.setFocusPainted(false);
        removeButton.setBorderPainted(false);
        removeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        removeButton.addActionListener(e -> {
            checklistPanel.remove(activityRow);
            activityNameFields.remove(activityField);
            activityCompletionCheckboxes.remove(activityCheckbox);
            checklistPanel.revalidate();
            checklistPanel.repaint();
        });

        activityRow.add(activityField, BorderLayout.CENTER);
        activityRow.add(activityCheckbox, BorderLayout.EAST);
        activityRow.add(removeButton, BorderLayout.WEST); // Pulsante di rimozione a sinistra
        checklistPanel.add(activityRow);
        checklistPanel.revalidate();
        checklistPanel.repaint();
    }

    // Getters per i campi del form
    public JTextField getTxtTitolo() { return txtTitolo; }
    public JTextArea getTxtDescrizione() { return txtDescrizione; }
    public JTextField getTxtScadenza() { return txtScadenza; }
    public JTextField getTxtImagePath() { return txtImagePath; }
    public JTextField getTxtURL() { return txtURL; }
    public JTextField getTxtColoreSfondo() { return txtColoreSfondo; }
    public JComboBox<TitoloBacheca> getCmbBacheca() { return cmbBacheca; }
    public JButton getBtnSalva() { return btnSalva; }
    public JButton getBtnAnnulla() { return btnAnnulla; }
    public JButton getBtnAddActivity() { return btnAddActivity; }

    /**
     * Restituisce la lista dei nomi delle attività della checklist.
     * @return Una lista di String contenente i nomi delle attività.
     */
    public List<String> getChecklistActivityNames() {
        List<String> names = new ArrayList<>();
        for (JTextField field : activityNameFields) {
            names.add(field.getText());
        }
        return names;
    }

    /**
     * Restituisce la lista degli stati di completamento delle attività della checklist.
     * @return Una lista di boolean che indica lo stato di completamento.
     */
    public List<Boolean> getChecklistActivityCompletionStates() {
        List<Boolean> states = new ArrayList<>();
        for (JCheckBox checkbox : activityCompletionCheckboxes) {
            states.add(checkbox.isSelected());
        }
        return states;
    }

    /**
     * Pulisce tutti i campi del form.
     */
    public void clearForm() {
        txtTitolo.setText("");
        txtDescrizione.setText("");
        txtScadenza.setText("");
        txtImagePath.setText("");
        txtURL.setText("");
        txtColoreSfondo.setText("");
        cmbBacheca.setSelectedIndex(0);
        checklistPanel.removeAll();
        activityNameFields.clear();
        activityCompletionCheckboxes.clear();
        checklistPanel.revalidate();
        checklistPanel.repaint();
    }
}
