package controller;

import gui.ToDoDialog;
import model.*;

import javax.swing.JOptionPane;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.awt.event.ActionListener;

/**
 * Gestisce la logica della finestra di dialogo per la creazione e modifica dei ToDo.
 * <p>
 * Questa classe controlla la {@link ToDoDialog}, gestendo l'apertura della finestra,
 * il popolamento dei campi (in caso di modifica), la validazione dell'input
 * e il salvataggio dei dati (delegando poi l'aggiornamento al {@link ToDoController}).
 * </p>
 *
 * @author marrenza
 * @version 1.0
 */
public class ToDoDialogController {
    /** Riferimento al controller principale per delegare le operazioni di persistenza. */
    private ToDoController mainController;

    /** La vista (dialog) gestita da questo controller. */
    private ToDoDialog toDoDialog;

    /** * Il ToDo attualmente in fase di modifica.
     * Se è {@code null}, significa che si sta creando un nuovo ToDo.
     */
    private ToDo toDoToEdit;

    /**
     * Costruttore del controller della dialog.
     *
     * @param mainController Il controller principale dell'applicazione.
     */
    public ToDoDialogController(ToDoController mainController) {
        this.mainController = mainController;
    }

    /**
     * Apre la finestra di dialogo in modalità "Creazione".
     * Inizializza un form vuoto, resetta la variabile {@code toDoToEdit} a null
     * e collega il listener per il salvataggio.
     */
    public void openAddToDoDialog() {
        this.toDoToEdit = null;
        toDoDialog = new ToDoDialog(null, "Aggiungi ToDo", true);
        toDoDialog.clearForm();

        for (ActionListener al : toDoDialog.getBtnSalva().getActionListeners()) {
            toDoDialog.getBtnSalva().removeActionListener(al);
        }

        toDoDialog.getBtnSalva().addActionListener(e -> handleSave());

        toDoDialog.setVisible(true);
    }

    /**
     * Apre la finestra di dialogo in modalità "Modifica".
     * Popola i campi del form con i dati del ToDo passato come parametro.
     * Ricostruisce anche la visualizzazione della Checklist e imposta la bacheca corretta.
     *
     * @param toDoToEdit Il ToDo esistente da modificare.
     */
    public void openEditToDoDialog(ToDo toDoToEdit) { //
        this.toDoToEdit = toDoToEdit;
        toDoDialog = new ToDoDialog(null, "Modifica ToDo", true);

        toDoDialog.setTxtTitolo(toDoToEdit.getTitolo());
        toDoDialog.setTxtDescrizione(toDoToEdit.getDescrizione());
        if (toDoToEdit.getScadenza() != null) {
            toDoDialog.getTxtScadenza().setText(toDoToEdit.getScadenza().toString());
        } else {
            toDoDialog.getTxtScadenza().setText("");
        }
        toDoDialog.setImagePath(toDoToEdit.getImaginePath());
        toDoDialog.getTxtURL().setText(toDoToEdit.getURL());
        toDoDialog.setColoreSfondo(toDoToEdit.getColoreSfondo());
        if (toDoToEdit.getBacheca() != null) {
            toDoDialog.setCmbBacheca(toDoToEdit.getBacheca().getTitolo());
        }

        if (toDoToEdit.getChecklist() != null) {
            for (Attivita attivita : toDoToEdit.getChecklist().getAttivita()) {
                toDoDialog.addChecklistActivityField(attivita.getNome(), attivita.getStato() == StatoAttivita.COMPLETATO);
            }
        }

        for (ActionListener al : toDoDialog.getBtnSalva().getActionListeners()) {
            toDoDialog.getBtnSalva().removeActionListener(al);
        }

        toDoDialog.getBtnSalva().addActionListener(e -> handleSave());

        toDoDialog.setVisible(true);
    }

    /**
     * Gestisce l'evento di salvataggio (click sul pulsante "Salva").
     * <p>
     * Esegue le seguenti operazioni:
     * <ol>
     * <li>Valida i campi obbligatori (Titolo e Scadenza).</li>
     * <li>Effettua il parsing della data.</li>
     * <li>Raccoglie i dati dal form (inclusi i campi dinamici della Checklist).</li>
     * <li>Se {@code toDoToEdit} è null, crea un nuovo ToDo e chiama {@code salvaNuovoToDo}.</li>
     * <li>Se {@code toDoToEdit} esiste, aggiorna l'oggetto e chiama {@code aggiornaToDoEsistente}.</li>
     * </ol>
     * </p>
     */
    private void handleSave() {
        try {
            String titolo = toDoDialog.getTxtTitolo().getText();
            String scadenzaText = toDoDialog.getTxtScadenza().getText();

            if (titolo == null || titolo.trim().isEmpty()) {
                JOptionPane.showMessageDialog(toDoDialog, "Il campo 'Titolo (*)' è obbligatorio.", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (scadenzaText == null || scadenzaText.trim().isEmpty()) {
                JOptionPane.showMessageDialog(toDoDialog, "Il campo 'Scadenza (*)' è obbligatorio.", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }
            LocalDate scadenza;
            try {
                scadenza = LocalDate.parse(scadenzaText);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(toDoDialog, "Formato data non valido. Usa YYYY-MM-DD.", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String descrizione = toDoDialog.getTxtDescrizione().getText();
            String imagePath = toDoDialog.getImagePath();
            String url = toDoDialog.getTxtURL().getText();
            String coloreSfondo = toDoDialog.getColoreSfondo();
            TitoloBacheca selectedBacheca = (TitoloBacheca) toDoDialog.getCmbBacheca().getSelectedItem();

            Checklist checklist = new Checklist();
            List<String> activityNames = toDoDialog.getChecklistActivityNames();
            List<Boolean> activityStates = toDoDialog.getChecklistActivityCompletionStates();

            for (int i = 0; i < activityNames.size(); i++) {
                String nomeAttivita = activityNames.get(i);
                if (nomeAttivita != null && !nomeAttivita.trim().isEmpty()) {
                    Attivita nuovaAttivita = new Attivita(nomeAttivita);
                    nuovaAttivita.setStato(activityStates.get(i) ? StatoAttivita.COMPLETATO : StatoAttivita.NON_COMPLETATO);
                    checklist.aggiungiAttivita(nuovaAttivita);
                }
            }

            if (toDoToEdit == null) {
                ToDo newToDo = new ToDo(0, titolo, descrizione, scadenza, imagePath, url, coloreSfondo, 0, mainController.getUtenteCorrente());
                newToDo.setChecklist(checklist);
                newToDo.aggiornaStatoDaChecklist();

                mainController.salvaNuovoToDo(newToDo, selectedBacheca);

            } else {
                TitoloBacheca oldBacheca = toDoToEdit.getBacheca().getTitolo();

                toDoToEdit.setTitolo(titolo); //
                toDoToEdit.setDescrizione(descrizione);
                toDoToEdit.setScadenza(scadenza);
                toDoToEdit.setImaginePath(imagePath);
                toDoToEdit.setURL(url);
                toDoToEdit.setColoreSfondo(coloreSfondo);
                toDoToEdit.setChecklist(checklist);
                toDoToEdit.aggiornaStatoDaChecklist();

                mainController.aggiornaToDoEsistente(toDoToEdit, oldBacheca, selectedBacheca);
            }

            toDoDialog.dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(toDoDialog, "Errore nel salvataggio del ToDo: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
