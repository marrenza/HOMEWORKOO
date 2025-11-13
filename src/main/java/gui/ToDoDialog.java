package gui;

import model.TitoloBacheca;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class ToDoDialog extends JDialog {
    private JTextField txtTitolo;
    private JTextArea txtDescrizione;
    private JTextField txtScadenza; // Formato YYYY-MM-DD
    private JTextField txtURL;
    private JComboBox<TitoloBacheca> cmbBacheca;
    private JPanel checklistPanel;
    private JButton btnAddActivity;
    private JButton btnSalva;
    private JButton btnAnnulla;
    private JLabel lblImagePath;
    private JButton btnSfogliaImmagine;
    private JFileChooser imageChooser;
    private JLabel lblColoreScelto;
    private JButton btnScegliColore;
    private String hexColoreSelezionato = "";

    private List<JTextField> activityNameFields;
    private List<JCheckBox> activityCompletionCheckboxes;

    private JScrollPane checklistScrollPane;

    public ToDoDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        setSize(500, 700);
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

        txtURL = new JTextField(20);

        lblImagePath = new JLabel("Nessun file selezionato.");
        lblImagePath.setBorder(BorderFactory.createEtchedBorder());
        btnSfogliaImmagine = new JButton("Sfoglia");
        imageChooser = new JFileChooser();
        imageChooser.setFileFilter(new FileNameExtensionFilter("Immagini (jpg, png, gif)", "jpg", "png", "gif"));

        btnSfogliaImmagine.addActionListener(e -> {
            int result = imageChooser.showOpenDialog(this);
            if(result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = imageChooser.getSelectedFile();
                lblImagePath.setText(selectedFile.getAbsolutePath());
            }
        });

        lblColoreScelto = new JLabel(" (Nessun colore) ");
        lblColoreScelto.setOpaque(true);
        lblColoreScelto.setBackground(Color.WHITE);
        lblColoreScelto.setBorder(BorderFactory.createEtchedBorder());
        btnScegliColore = new JButton("Scegli colore: ");

        btnScegliColore.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(this, "Scegli un colore di sfondo", Color.WHITE);
            if(newColor != null) {
                lblColoreScelto.setBackground(newColor);
                hexColoreSelezionato = String.format("#%06x", newColor.getRGB() & 0xFFFFFF).toUpperCase();
                lblColoreScelto.setText(hexColoreSelezionato);
            }
        });

        cmbBacheca = new JComboBox<>(TitoloBacheca.values()); // Popola la combobox con i titoli delle bacheche

        checklistPanel = new JPanel();
        checklistPanel.setLayout(new BoxLayout(checklistPanel, BoxLayout.Y_AXIS));
        checklistPanel.setBorder(BorderFactory.createTitledBorder("Checklist Attività"));
        checklistScrollPane = new JScrollPane(checklistPanel);
        checklistScrollPane.setPreferredSize(new Dimension(650,400)); // Dimensione fissa per lo scroll pane della checklist

        btnAddActivity = new JButton("Aggiungi Attività");
        btnSalva = new JButton("Salva");
        btnAnnulla = new JButton("Annulla");

        btnAnnulla.addActionListener(e -> dispose());

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
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Titolo (*):"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 1.0; formPanel.add(txtTitolo, gbc);

        // Descrizione
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Descrizione:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 1.0; formPanel.add(new JScrollPane(txtDescrizione), gbc);

        // Scadenza
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Scadenza (YYYY-MM-DD) (*):"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 1.0; formPanel.add(txtScadenza, gbc);

        // Immagine Path
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Percorso immagine:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 1.0;
        JPanel imagePanel = new JPanel(new BorderLayout(5,0));
        imagePanel.add(lblImagePath, BorderLayout.CENTER);
        imagePanel.add(btnSfogliaImmagine, BorderLayout.EAST);
        formPanel.add(imagePanel, gbc);

        // URL
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("URL:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 1.0; formPanel.add(txtURL, gbc);

        // Colore Sfondo
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Colore Sfondo:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 1.0;
        JPanel colorPanel = new JPanel(new BorderLayout(5,0));
        colorPanel.add(lblColoreScelto, BorderLayout.CENTER);
        colorPanel.add(btnScegliColore, BorderLayout.EAST);
        formPanel.add(colorPanel, gbc);

        // Bacheca
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Bacheca:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 1.0; formPanel.add(cmbBacheca, gbc);

        // Checklist Panel
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(checklistScrollPane, gbc);
        row++;

        // Add Activity Button
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2; gbc.weighty = 0.0;
        formPanel.add(btnAddActivity, gbc);


        add(formPanel, BorderLayout.CENTER);

        // Pulsanti Salva e Annulla
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(btnSalva);
        buttonPanel.add(btnAnnulla);
        add(buttonPanel, BorderLayout.SOUTH);
    }


    public void addChecklistActivityField(String activityName, boolean isCompleted) {
        JPanel activityRow = new JPanel(new BorderLayout());
        JTextField activityField = new JTextField(activityName);

        Dimension fieldSize = new Dimension(Integer.MAX_VALUE, activityField.getPreferredSize().height);
        activityField.setMaximumSize(fieldSize);
        activityRow.setMaximumSize(fieldSize);

        JCheckBox activityCheckbox = new JCheckBox();
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
    public JTextField getTxtURL() { return txtURL; }
    public JComboBox<TitoloBacheca> getCmbBacheca() { return cmbBacheca; }
    public JButton getBtnSalva() { return btnSalva; }
    public JButton getBtnAnnulla() { return btnAnnulla; }
    public JButton getBtnAddActivity() { return btnAddActivity; }
    public String getImagePath() {
        String path = lblImagePath.getText();
        return path.equals("Nessun file selezionato.") ? "" : path;
    }
    public String getColoreSfondo() {
        return hexColoreSelezionato;
    }


    public List<String> getChecklistActivityNames() {
        List<String> names = new ArrayList<>();
        for (JTextField field : activityNameFields) {
            names.add(field.getText());
        }
        return names;
    }

    public List<Boolean> getChecklistActivityCompletionStates() {
        List<Boolean> states = new ArrayList<>();
        for (JCheckBox checkbox : activityCompletionCheckboxes) {
            states.add(checkbox.isSelected());
        }
        return states;
    }

    public void clearForm() {
        txtTitolo.setText("");
        txtDescrizione.setText("");
        txtScadenza.setText("");
        txtURL.setText("");
        cmbBacheca.setSelectedIndex(0);
        lblImagePath.setText("Nessun file selezionato.");
        hexColoreSelezionato = "";
        lblColoreScelto.setText(" (Nessun colore) ");
        lblColoreScelto.setBackground(Color.WHITE);
        checklistPanel.removeAll();
        activityNameFields.clear();
        activityCompletionCheckboxes.clear();
        checklistPanel.revalidate();
        checklistPanel.repaint();
    }

    public void setTxtTitolo(String s) { txtTitolo.setText(s); }
    public void setTxtDescrizione(String s) { txtDescrizione.setText(s); }
    public void setTxtScadenza(String s) { txtScadenza.setText(s); }
    public void setTxtURL(String s) { txtURL.setText(s); }
    public void setCmbBacheca(TitoloBacheca t) { cmbBacheca.setSelectedItem(t); }

    public void setImagePath(String s) {
        if (s == null || s.isEmpty()) {
            lblImagePath.setText("Nessun file selezionato.");
        } else {
            lblImagePath.setText(s);
        }
    }

    public void setColoreSfondo(String hex) {
        if (hex == null || hex.isEmpty()) {
            hexColoreSelezionato = "";
            lblColoreScelto.setText(" (Nessun colore) ");
            lblColoreScelto.setBackground(Color.WHITE);
        } else {
            try {
                Color c = Color.decode(hex);
                hexColoreSelezionato = hex;
                lblColoreScelto.setBackground(c);
                lblColoreScelto.setText(hex);
            } catch (NumberFormatException e) {
                // Se il colore salvato non è valido, resetta
                setColoreSfondo(null);
            }
        }
    }
}
