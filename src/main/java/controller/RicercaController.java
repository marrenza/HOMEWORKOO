package controller;

import gui.SearchDialog;
import model.Bacheca;
import model.ToDo;
import model.TitoloBacheca;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import javax.swing.*;
import java.awt.Dimension;

/**
 * Gestisce le funzionalità di ricerca dei ToDo all'interno dell'applicazione.
 * <p>
 * Questa classe funge da intermediario tra la vista {@link gui.SearchDialog}
 * e il livello dati (DAO), permettendo all'utente di cercare attività
 * [cite_start]per titolo, descrizione o data di scadenza[cite: 20].
 * </p>
 *
 * @author marrenza
 * @version 1.0
 */
public class RicercaController {
    /** Riferimento al controller principale per l'accesso ai dati globali (utente, DAO). */
    private ToDoController mainController;

    /** La finestra di dialogo utilizzata per l'inserimento dei parametri di ricerca. */
    private SearchDialog searchDialog;

    /**
     * Costruttore del controller di ricerca.
     *
     * @param mainController Il controller principale dell'applicazione.
     */
    public RicercaController(ToDoController mainController) {
        this.mainController = mainController;
    }

    /**
     * Inizializza e visualizza la finestra di dialogo per la ricerca.
     * <p>
     * Configura gli ActionListener per i pulsanti:
     * <ul>
     * <li><b>Cerca:</b> Esegue la ricerca personalizzata in base ai campi compilati.</li>
     * <li><b>ToDo in Scadenza Oggi:</b> Esegue una ricerca rapida per la data odierna.</li>
     * </ul>
     */
    public void openSearchDialog() {
        searchDialog = new SearchDialog(null, "Cerca ToDo", true);

        searchDialog.getBtnCerca().addActionListener(e -> performSearch());
        searchDialog.getBtnScadenzaOdierna().addActionListener(e -> showTodayExpiringToDos());

        searchDialog.setVisible(true);
    }

    /**
     * Esegue la ricerca recuperando i criteri inseriti dall'utente nella dialog.
     * <p>
     * La logica segue questa priorità:
     * [cite_start]1. Se il campo data è compilato, esegue una ricerca per scadenza (formato YYYY-MM-DD)[cite: 19].
     * [cite_start]2. Se il campo data è vuoto ma c'è del testo, esegue una ricerca per titolo o descrizione[cite: 20].
     * </p>
     * Gestisce l'eccezione {@link DateTimeParseException} se il formato della data non è valido.
     */
    private void performSearch() {
        String searchTerm = searchDialog.getTxtSearchTerm().getText();
        String scadenzaText = searchDialog.getTxtScadenzaSearch().getText();

        int currentUserId = mainController.getUtenteCorrente().getId();
        List<ToDo> results = new ArrayList<>();

        try {
            if (!scadenzaText.isEmpty()) {
                LocalDate scadenzaCerca = LocalDate.parse(scadenzaText);
                results = mainController.getToDoDAO().findToDosByScadenza(scadenzaCerca, currentUserId);
            } else if (!searchTerm.isEmpty()) {
                results = mainController.getToDoDAO().findToDosByTerm(searchTerm, currentUserId);
            } else {
                JOptionPane.showMessageDialog(searchDialog, "Inserisci un termine o una data.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            showSearchResults(results);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(searchDialog, "Formato data non valido. Usa YYYY-MM-DD.", "Errore", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(searchDialog, "Errore durante la ricerca.", "Errore DB", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * [cite_start]Esegue una ricerca rapida di tutti i ToDo che scadono nella data odierna[cite: 19].
     * Utilizza l'utente corrente per filtrare i risultati.
     */
    private void showTodayExpiringToDos() {
        int currentUserId = mainController.getUtenteCorrente().getId();
        List<ToDo> expiringToday = mainController.getToDoDAO().findToDosScadenzaOggi(currentUserId);
        showSearchResults(expiringToday);
    }

    /**
     * Formatta e visualizza i risultati della ricerca in una finestra di messaggio (JOptionPane).
     * Per ogni ToDo trovato, mostra il titolo, la scadenza (se presente) e la bacheca di appartenenza.
     * Se la lista dei risultati è vuota, avvisa l'utente.
     *
     * @param results La lista dei {@link ToDo} ottenuti dalla ricerca.
     */
    private void showSearchResults(List<ToDo> results) {
        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(searchDialog, "Nessun ToDo trovato.", "Risultati Ricerca", JOptionPane.INFORMATION_MESSAGE);
        } else {
            StringBuilder sb = new StringBuilder("ToDo Trovati:\n");
            for (ToDo todo : results) {
                sb.append("- ").append(todo.getTitolo());
                if (todo.getScadenza() != null) {
                    sb.append(" (Scadenza: ").append(todo.getScadenza()).append(")");
                }
                Bacheca bachecaTrovata = mainController.getUtenteCorrente().getBacheche().stream()
                        .filter(b -> b.getId() == todo.getIdBacheca())
                        .findFirst().orElse(null);

                if (bachecaTrovata != null) {
                    sb.append(" [Bacheca: ").append(bachecaTrovata.getTitolo()).append("]");
                }
                sb.append("\n");
            }
            JTextArea textArea = new JTextArea(sb.toString());
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(400, 250));
            JOptionPane.showMessageDialog(searchDialog, scrollPane, "Risultati Ricerca", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
