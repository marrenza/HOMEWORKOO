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

/**
 * Finestra di dialogo per la creazione e la modifica di un ToDo.
 * Questa classe gestisce un form complesso che permette all'utente di inserire
 * o aggiornare tutte le proprietà di un ToDo: titolo, descrizione, scadenza,
 * immagine, URL, colore di sfondo, bacheca di appartenenza e checklist.
 * Supporta l'aggiunta dinamica di elementi alla checklist.
 *
 * @author marrenza
 * @version 1.0
 */
public class ToDoDialog extends JDialog {
    /** Campo di testo per il titolo del ToDo.*/
    private JTextField txtTitolo;

    /** Area di testo per la descrizione dettagliata. */
    private JTextArea txtDescrizione;

    /** Campo di testo per la data di scadenza.*/
    private JTextField txtScadenza;

    /** Campo di testo per l'URL collegato.*/
    private JTextField txtURL;

    /** Menu a tendina per selezionare la bacheca di appartenenza. */
    private JComboBox<TitoloBacheca> cmbBacheca;

    /** Pannello contenitore per le righe della checklist.*/
    private JPanel checklistPanel;

    /** Pulsante per aggiungere una nuova riga alla checklist.*/
    private JButton btnAddActivity;

    /** Pulsante per salvare le modifiche. */
    private JButton btnSalva;

    /** Pulsante per annullare le modifiche. */
    private JButton btnAnnulla;

    /** Etichetta che mostra il percorso dell'immagine selezionata. */
    private JLabel lblImagePath;

    /** Pulsante per aprire il selettore di file (FileChooser). */
    private JButton btnSfogliaImmagine;

    /** Componente per la selezione del file immagine. */
    private JFileChooser imageChooser;

    /** Etichetta che mostra l'anteprima del colore selezionato. */
    private JLabel lblColoreScelto;

    /** Pulsante per aprire il selettore di colore (ColorChooser). */
    private JButton btnScegliColore;

    /** Stringa che memorizza il codice esadecimale del colore selezionato. */
    private String hexColoreSelezionato = "";


    /** Lista di campi di testo per i nomi delle attività della checklist. */
    private List<JTextField> activityNameFields;

    /** Lista di checkbox per lo stato di completamento delle attività della checklist. */
    private List<JCheckBox> activityCompletionCheckboxes;


    /** Pannello scrollabile che contiene la checklist. */
    private JScrollPane checklistScrollPane;

    /**
     * Costruisce la finestra di dialogo.
     *
     * @param owner Il frame proprietario.
     * @param title Il titolo della finestra (es. "Aggiungi ToDo" o "Modifica ToDo").
     * @param modal Se {@code true}, la finestra è modale.
     */
    public ToDoDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        setSize(500, 700);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        setResizable(false);

        activityNameFields = new ArrayList<>();
        activityCompletionCheckboxes = new ArrayList<>();

        initComponents();
        layoutComponents();
    }

    /**
     * Inizializza tutti i componenti grafici della finestra.
     * Configura i listener per i pulsanti di sfoglia immagine, scelta colore
     * e gestione della checklist.
     */
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

        cmbBacheca = new JComboBox<>(TitoloBacheca.values());

        checklistPanel = new JPanel();
        checklistPanel.setLayout(new BoxLayout(checklistPanel, BoxLayout.Y_AXIS));
        checklistPanel.setBorder(BorderFactory.createTitledBorder("Checklist Attività"));
        checklistScrollPane = new JScrollPane(checklistPanel);
        checklistScrollPane.setPreferredSize(new Dimension(650,400));

        btnAddActivity = new JButton("Aggiungi Attività");
        btnSalva = new JButton("Salva");
        btnAnnulla = new JButton("Annulla");

        btnAnnulla.addActionListener(e -> dispose());

        btnAddActivity.addActionListener(e -> addChecklistActivityField(null, false));
    }

    /**
     * Dispone i componenti nel pannello utilizzando un GridBagLayout per il form
     * e un BorderLayout per i pulsanti di azione.
     */
    private void layoutComponents() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Titolo (*):"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 1.0; formPanel.add(txtTitolo, gbc);

        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Descrizione:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 1.0; formPanel.add(new JScrollPane(txtDescrizione), gbc);

        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Scadenza (YYYY-MM-DD) (*):"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 1.0; formPanel.add(txtScadenza, gbc);

        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Percorso immagine:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 1.0;
        JPanel imagePanel = new JPanel(new BorderLayout(5,0));
        imagePanel.add(lblImagePath, BorderLayout.CENTER);
        imagePanel.add(btnSfogliaImmagine, BorderLayout.EAST);
        formPanel.add(imagePanel, gbc);

        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("URL:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 1.0; formPanel.add(txtURL, gbc);

        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Colore Sfondo:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 1.0;
        JPanel colorPanel = new JPanel(new BorderLayout(5,0));
        colorPanel.add(lblColoreScelto, BorderLayout.CENTER);
        colorPanel.add(btnScegliColore, BorderLayout.EAST);
        formPanel.add(colorPanel, gbc);

        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Bacheca:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 1.0; formPanel.add(cmbBacheca, gbc);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(checklistScrollPane, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2; gbc.weighty = 0.0;
        formPanel.add(btnAddActivity, gbc);


        add(formPanel, BorderLayout.CENTER);


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(btnSalva);
        buttonPanel.add(btnAnnulla);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Aggiunge dinamicamente una riga per un'attività della checklist.
     * Crea un pannello contenente un campo di testo per il nome, una checkbox per lo stato
     * e un pulsante "X" per rimuovere la riga. Aggiunge i riferimenti alle liste interne
     * per poter recuperare i dati in seguito.
     *
     * @param activityName Il nome dell'attività (se null, campo vuoto).
     * @param isCompleted  Lo stato di completamento iniziale.
     */
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

        JButton removeButton = new JButton("X");
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
        activityRow.add(removeButton, BorderLayout.WEST);
        checklistPanel.add(activityRow);
        checklistPanel.revalidate();
        checklistPanel.repaint();
    }

    /**
     * Restituisce il campo di testo per l'inserimento del titolo.
     * @return Il componente JTextField del titolo.
     */
    public JTextField getTxtTitolo() { return txtTitolo; }

    /**
     * Restituisce l'area di testo per l'inserimento della descrizione.
     * @return Il componente JTextArea della descrizione.
     */
    public JTextArea getTxtDescrizione() { return txtDescrizione; }

    /**
     * Restituisce il campo di testo per l'inserimento della data di scadenza.
     * @return Il componente JTextField della scadenza.
     */
    public JTextField getTxtScadenza() { return txtScadenza; }

    /**
     * Restituisce il campo di testo per l'inserimento dell'URL.
     * @return Il componente JTextField dell'URL.
     */
    public JTextField getTxtURL() { return txtURL; }

    /**
     * Restituisce il menu a tendina per la selezione della bacheca.
     * @return Il componente JComboBox contenente i titoli delle bacheche.
     */
    public JComboBox<TitoloBacheca> getCmbBacheca() { return cmbBacheca; }

    /**
     * Restituisce il pulsante di salvataggio.
     * Utilizzato dal controller per associare la logica di validazione e persistenza.
     * @return Il componente JButton "Salva".
     */
    public JButton getBtnSalva() { return btnSalva; }

    /**
     * Restituisce il pulsante di annullamento.
     * @return Il componente JButton "Annulla".
     */
    public JButton getBtnAnnulla() { return btnAnnulla; }

    /**
     * Restituisce il pulsante per aggiungere una nuova attività alla checklist.
     * Utilizzato dal controller (o dalla vista stessa) per generare dinamicamente nuovi campi.
     * @return Il componente JButton "Aggiungi Attività".
     */
    public JButton getBtnAddActivity() { return btnAddActivity; }

    /**
     * Restituisce il percorso dell'immagine selezionata.
     * @return Il path assoluto del file, o una stringa vuota se non selezionato.
     */
    public String getImagePath() {
        String path = lblImagePath.getText();
        return path.equals("Nessun file selezionato.") ? "" : path;
    }

    /**
     * Restituisce il codice esadecimale del colore di sfondo selezionato.
     * @return La stringa del colore (es. "#RRGGBB").
     */
    public String getColoreSfondo() {
        return hexColoreSelezionato;
    }

    /**
     * Recupera i nomi di tutte le attività inserite nella checklist.
     * @return Una lista di stringhe.
     */
    public List<String> getChecklistActivityNames() {
        List<String> names = new ArrayList<>();
        for (JTextField field : activityNameFields) {
            names.add(field.getText());
        }
        return names;
    }

    /**
     * Recupera gli stati di completamento delle attività della checklist.
     * @return Una lista di booleani corrispondente all'ordine dei nomi.
     */
    public List<Boolean> getChecklistActivityCompletionStates() {
        List<Boolean> states = new ArrayList<>();
        for (JCheckBox checkbox : activityCompletionCheckboxes) {
            states.add(checkbox.isSelected());
        }
        return states;
    }

    /**
     * Pulisce tutti i campi del form, resettandoli allo stato iniziale.
     * Utilizzato quando si apre il dialog per creare un *nuovo* ToDo.
     */
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

    /**
     * Imposta il testo del campo titolo.
     * Utilizzato per visualizzare il titolo corrente quando si modifica un ToDo esistente.
     *
     * @param s La stringa da inserire nel campo titolo.
     */
    public void setTxtTitolo(String s) { txtTitolo.setText(s); }

    /**
     * Imposta il testo dell'area di descrizione.
     *
     * @param s La descrizione da visualizzare.
     */
    public void setTxtDescrizione(String s) { txtDescrizione.setText(s); }

    /**
     * Imposta il testo del campo scadenza.
     *
     * @param s La data di scadenza sotto forma di stringa (solitamente YYYY-MM-DD).
     */
    public void setTxtScadenza(String s) { txtScadenza.setText(s); }

    /**
     * Imposta il testo del campo URL.
     *
     * @param s L'URL da visualizzare.
     */
    public void setTxtURL(String s) { txtURL.setText(s); }

    /**
     * Seleziona la bacheca specificata nel menu a tendina (ComboBox).
     *
     * @param t Il titolo della bacheca da selezionare come attiva.
     */
    public void setCmbBacheca(TitoloBacheca t) { cmbBacheca.setSelectedItem(t); }

    /**
     * Imposta il percorso dell'immagine visualizzato nella label.
     * @param s Il path del file.
     */
    public void setImagePath(String s) {
        if (s == null || s.isEmpty()) {
            lblImagePath.setText("Nessun file selezionato.");
        } else {
            lblImagePath.setText(s);
        }
    }

    /**
     * Imposta il colore di sfondo visualizzato nell'anteprima.
     * @param hex Il codice esadecimale del colore.
     */

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
                setColoreSfondo(null);
            }
        }
    }
}
