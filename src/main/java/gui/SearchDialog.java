package gui;

import javax.swing.*;
import java.awt.*;

/**
 * Finestra di dialogo per la ricerca dei ToDo.
 * Questa classe fornisce l'interfaccia grafica che permette all'utente di filtrare
 * le proprie attività. Offre tre modalità di interazione:
 * <ul>
 * <li>Ricerca testuale (per Titolo o Descrizione).</li>
 * <li>Ricerca per data di scadenza specifica.</li>
 * <li>Ricerca rapida per i ToDo in scadenza nella giornata odierna.</li>
 * </ul>
 *
 * @author marrenza
 * @version 1.0
 */
public class SearchDialog extends JDialog {
    /** Campo di testo per inserire la parola chiave da cercare nel titolo o descrizione. */
    private JTextField txtSearchTerm;

    /** Campo di testo per inserire una data di scadenza specifica (formato YYYY-MM-DD). */
    private JTextField txtScadenzaSearch;

    /** Pulsante per avviare la ricerca basata sui campi di testo compilati. */
    private JButton btnCerca;

    /** Pulsante per avviare una ricerca immediata delle attività che scadono oggi. */
    private JButton btnScadenzaOdierna;

    /** Pulsante per chiudere la finestra di ricerca. */
    private JButton btnAnnulla;

    /**
     * Costruttore della finestra di ricerca.
     * Configura le proprietà della finestra (modale, non ridimensionabile) e inizializza i componenti.
     *
     * @param owner Il frame proprietario (la finestra principale).
     * @param title Il titolo della finestra di dialogo.
     * @param modal Se {@code true}, blocca l'input sulle altre finestre finché questa è aperta.
     */
    public SearchDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        setSize(400, 250);
        setLocationRelativeTo(owner);
        setLayout(new GridBagLayout());
        setResizable(false);

        initComponents();
        layoutComponents();
    }

    /**
     * Inizializza i componenti grafici (bottoni, campi di testo).
     * Imposta i tooltip e gestisce l'azione di chiusura del pulsante "Annulla".
     */
    private void initComponents() {
        txtSearchTerm = new JTextField(20);
        txtScadenzaSearch = new JTextField(10);
        txtScadenzaSearch.setToolTipText("Formato: YYYY-MM-DD");

        btnCerca = new JButton("Cerca");
        btnScadenzaOdierna = new JButton("ToDo in Scadenza Oggi");
        btnAnnulla = new JButton("Annulla");

        btnAnnulla.addActionListener(e -> dispose());
    }

    /**
     * Dispone i componenti all'interno della finestra utilizzando {@link GridBagLayout}.
     * Organizza etichette e campi di input su righe successive.
     */
    private void layoutComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        gbc.gridx = 0; gbc.gridy = row; add(new JLabel("Cerca per Titolo/Descrizione:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 1.0; add(txtSearchTerm, gbc);

        gbc.gridx = 0; gbc.gridy = row; add(new JLabel("Cerca per Scadenza (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 1.0; add(txtScadenzaSearch, gbc);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; add(btnCerca, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; add(btnScadenzaOdierna, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; add(btnAnnulla, gbc);
    }

    /**
     * Restituisce il campo di testo per la ricerca per parola chiave.
     * @return Il componente JTextField.
     */
    public JTextField getTxtSearchTerm() { return txtSearchTerm; }

    /**
     * Restituisce il campo di testo per la ricerca per data.
     * @return Il componente JTextField.
     */
    public JTextField getTxtScadenzaSearch() { return txtScadenzaSearch; }

    /**
     * Restituisce il pulsante di ricerca standard.
     * Utilizzato dal controller per associare la logica di filtro personalizzato.
     * @return Il componente JButton.
     */
    public JButton getBtnCerca() { return btnCerca; }

    /**
     * Restituisce il pulsante per la ricerca rapida "Oggi".
     * Utilizzato dal controller per associare la logica di filtro per data odierna.
     * @return Il componente JButton.
     */
    public JButton getBtnScadenzaOdierna() { return btnScadenzaOdierna; }
}
