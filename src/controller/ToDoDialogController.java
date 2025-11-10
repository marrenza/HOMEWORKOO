package controller;

import gui.ToDoDialog;
import model.*;

import javax.swing.JOptionPane;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ToDoDialogController {
    private ToDoController mainController;
    private ToDoDialog toDoDialog;

    public ToDoDialogController(ToDoController mainController) {
        this.mainController = mainController;
    }


    public void openAddToDoDialog() {
        toDoDialog = new ToDoDialog(null, "Aggiungi ToDo", true);
        toDoDialog.clearForm(); // Pulisce il form per un nuovo inserimento

        // Listener per il pulsante Salva
        toDoDialog.getBtnSalva().addActionListener(e -> {
            try {
                // Recupera i dati dal dialogo
                String titolo = toDoDialog.getTxtTitolo().getText();
                String descrizione = toDoDialog.getTxtDescrizione().getText();
                LocalDate scadenza = null;
                if (!toDoDialog.getTxtScadenza().getText().isEmpty()) {
                    scadenza = LocalDate.parse(toDoDialog.getTxtScadenza().getText());
                }
                String imagePath = toDoDialog.getImagePath();
                String url = toDoDialog.getTxtURL().getText();
                String coloreSfondo = toDoDialog.getColoreSfondo();
                TitoloBacheca selectedBacheca = (TitoloBacheca) toDoDialog.getCmbBacheca().getSelectedItem();

                // Genera un ID univoco per il nuovo ToDo (semplice auto-increment per demo)
                int newId = mainController.getUtenteCorrente().getBacheche().stream()
                        .flatMap(b -> b.getToDoList().stream())
                        .mapToInt(ToDo::getId)
                        .max().orElse(0) + 1;

                // Crea il nuovo oggetto ToDo
                ToDo newToDo = new ToDo(newId, titolo, descrizione, scadenza, imagePath, url, coloreSfondo, 0, mainController.getUtenteCorrente()); // Posizione 0 di default
                newToDo.setStato(StatoToDo.NON_COMPLETATO); // Di default non completato

                // Gestisce la checklist
                Checklist checklist = new Checklist();
                List<String> activityNames = toDoDialog.getChecklistActivityNames();
                List<Boolean> activityStates = toDoDialog.getChecklistActivityCompletionStates();



                for (int i = 0; i < activityNames.size(); i++) {
                    StatoAttivita statoAttivita = activityStates.get(i) ? StatoAttivita.COMPLETATO : StatoAttivita.NON_COMPLETATO;
                    Attivita nuovaAttivita = new Attivita(activityNames.get(i));
                    nuovaAttivita.setStato(statoAttivita);
                    checklist.aggiungiAttivita(nuovaAttivita);
                }
                newToDo.setChecklist(checklist);
                newToDo.aggiornaStatoDaChecklist();

                // Aggiunge il ToDo alla bacheca tramite il controller principale
                mainController.addToDoToBacheca(newToDo, selectedBacheca);
                toDoDialog.dispose(); // Chiude il dialogo
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(toDoDialog, "Formato data scadenza non valido. Usa YYYY-MM-DD.", "Errore Formato Data", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(toDoDialog, "Errore nel salvataggio del ToDo: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
        toDoDialog.setVisible(true);
    }


    public void openEditToDoDialog(ToDo toDoToEdit) {
        toDoDialog = new ToDoDialog(null, "Modifica ToDo", true);
        // Popola i campi del dialogo con i dati del ToDo esistente
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

        // Popola la checklist nel dialogo
        if (toDoToEdit.getChecklist() != null) {
            for (Attivita attivita : toDoToEdit.getChecklist().getAttivita()) {
                toDoDialog.addChecklistActivityField(attivita.getNome(), attivita.getStato() == StatoAttivita.COMPLETATO);
            }
        }

        // Listener per il pulsante Salva
        toDoDialog.getBtnSalva().addActionListener(e -> {
            try {
                // Aggiorna l'oggetto ToDoToEdit con i nuovi dati dal dialogo
                toDoToEdit.setTitolo(toDoDialog.getTxtTitolo().getText());
                toDoToEdit.setDescrizione(toDoDialog.getTxtDescrizione().getText());
                if (!toDoDialog.getTxtScadenza().getText().isEmpty()) {
                    toDoToEdit.setScadenza(LocalDate.parse(toDoDialog.getTxtScadenza().getText()));
                } else {
                    toDoToEdit.setScadenza(null);
                }
                toDoToEdit.setImaginePath(toDoDialog.getImagePath());
                toDoToEdit.setURL(toDoDialog.getTxtURL().getText());
                toDoToEdit.setColoreSfondo(toDoDialog.getColoreSfondo());

                // Aggiorna la checklist
                Checklist updatedChecklist = new Checklist();
                List<String> activityNames = toDoDialog.getChecklistActivityNames();
                List<Boolean> activityStates = toDoDialog.getChecklistActivityCompletionStates();
                for (int i = 0; i < activityNames.size(); i++) {
                    StatoAttivita statoAttivita = activityStates.get(i) ? StatoAttivita.COMPLETATO : StatoAttivita.NON_COMPLETATO;

                    Attivita nuovaAttivita = new Attivita(activityNames.get(i));
                    nuovaAttivita.setStato(statoAttivita);
                    updatedChecklist.aggiungiAttivita(nuovaAttivita);
                }
                toDoToEdit.setChecklist(updatedChecklist);
                toDoToEdit.aggiornaStatoDaChecklist();

                // Controlla se la bacheca è cambiata e sposta il ToDo se necessario
                TitoloBacheca newSelectedBacheca = (TitoloBacheca) toDoDialog.getCmbBacheca().getSelectedItem();
                if (toDoToEdit.getBacheca() == null || toDoToEdit.getBacheca().getTitolo() != newSelectedBacheca) {
                    mainController.moveToDo(toDoToEdit, newSelectedBacheca);
                } else {
                    mainController.updateToDoInBacheca(toDoToEdit); // Altrimenti, solo aggiorna
                }
                toDoDialog.dispose(); // Chiude il dialogo
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(toDoDialog, "Formato data scadenza non valido. Usa YYYY-MM-DD.", "Errore Formato Data", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(toDoDialog, "Errore nella modifica del ToDo: " + ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
        toDoDialog.setVisible(true);
    }
}
