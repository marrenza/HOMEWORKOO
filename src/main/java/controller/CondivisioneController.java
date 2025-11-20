package controller;

import gui.ShareDialog;
import model.Condivisione;
import model.ToDo;
import model.Utente;
import model.TitoloBacheca;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import java.util.logging.Logger;

/**
 * Gestisce la logica di business relativa alla condivisione dei ToDo tra utenti.
 * Questa classe si occupa di aprire le finestre di dialogo per la selezione degli utenti,
 * salvare le nuove condivisioni nel database tramite il DAO e rimuovere le condivisioni esistenti.
 * Collabora strettamente con il {@link ToDoController} per accedere ai dati globali e ai DAO.
 *
 * @author marrenza
 * @version 1.0
 */
public class CondivisioneController {
    /** Riferimento al controller principale per accedere a utenti e DAO. */
    private ToDoController mainController;

    /** Finestra di dialogo per la selezione multipla degli utenti. */
    private ShareDialog shareDialog;

    private static final Logger logger = Logger.getLogger(CondivisioneController.class.getName());

    /**
     * Costruttore del controller per le condivisioni.
     *
     * @param mainController Il controller principale dell'applicazione.
     */
    public CondivisioneController(ToDoController mainController) {
        this.mainController = mainController;
    }

    /**
     * Apre la finestra di dialogo per condividere un ToDo specifico.
     * Recupera la lista di tutti gli utenti registrati (escluso l'utente corrente)
     * e inizializza la {@link ShareDialog}. Gestisce l'evento del pulsante "Condividi"
     * per salvare le condivisioni selezionate.
     *
     * @param toDoToShare Il ToDo che si intende condividere.
     */
    public void openShareToDoDialog(ToDo toDoToShare) {
        List<Utente> otherUsers = mainController.getUtentiRegistrati().stream()
                .filter(u -> u.getId() != mainController.getUtenteCorrente().getId())
                .collect(Collectors.toList());

        shareDialog = new ShareDialog(null, "Condividi ToDo: " + toDoToShare.getTitolo(), true, otherUsers);
        shareDialog.getBtnCondividi().addActionListener(e -> {
            List<Utente> selectedUsers = shareDialog.getSelectedUsers();
            if (selectedUsers.isEmpty()) {
                JOptionPane.showMessageDialog(shareDialog, "Seleziona almeno un utente con cui condividere.", "Nessuna Selezione", JOptionPane.WARNING_MESSAGE);
                return;
            }
            for (Utente u : selectedUsers) {
                shareToDoWithUser(toDoToShare, u);
            }
            shareDialog.dispose();
            JOptionPane.showMessageDialog(shareDialog, "ToDo condiviso con successo!");
        });
        shareDialog.setVisible(true);
    }

    /**
     * Metodo interno per effettuare la condivisione effettiva con un singolo utente.
     * Controlla se il ToDo è già condiviso per evitare duplicati, aggiorna il database
     * e il modello locale.
     *
     * @param toDoToShare Il ToDo da condividere.
     * @param targetUser  L'utente destinatario della condivisione.
     */
    private void shareToDoWithUser(ToDo toDoToShare, Utente targetUser) {
        if(toDoToShare.getCondivisioni() != null) {
            boolean alreadyShared = toDoToShare.getCondivisioni().stream()
                    .anyMatch(c -> c.getUtente() != null && c.getUtente().getId() == targetUser.getId());
            if (alreadyShared) {
                return;
            }
        }
        Condivisione condivisione = new Condivisione(targetUser.getId(), toDoToShare.getId());
        mainController.getCondivisioneDAO().addCondivisione(condivisione);
        condivisione.setUtente(targetUser);
        condivisione.setToDo(toDoToShare);
        toDoToShare.aggiungiCondivisione(condivisione);

    }

    /**
     * Mostra una finestra informativa con l'elenco degli utenti con cui il ToDo è condiviso.
     *
     * @param toDo Il ToDo di cui visualizzare le condivisioni.
     */
    public void showSharedUsers(ToDo toDo) {
        if (toDo.getCondivisioni() == null || toDo.getCondivisioni().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Questo ToDo non è condiviso con nessun altro utente.", "Condivisioni", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder("Questo ToDo è condiviso con:\n");
        for (Condivisione c : toDo.getCondivisioni()) {
            sb.append("- ").append(c.getUtente().getNome()).append(" (Login: ").append(c.getUtente().getLogin()).append(")\n");
        }
        JOptionPane.showMessageDialog(null, sb.toString(), "Condivisioni ToDo", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Rimuove la condivisione di un ToDo con uno specifico utente.
     * Elimina il record dal database e aggiorna il modello locale.
     *
     * @param toDo          Il ToDo da cui rimuovere la condivisione.
     * @param userToRemove  L'utente a cui revocare l'accesso.
     */
    public void removeSharing(ToDo toDo, Utente userToRemove) {
        Condivisione condivisioneDaRimuovere = null;
        if (toDo.getCondivisioni() != null) {
            for (Condivisione c : toDo.getCondivisioni()) {
                if (c.getUtente() != null && c.getUtente().getId() == userToRemove.getId()) {
                    condivisioneDaRimuovere = c;
                    break;
                }
            }
        }
        if (condivisioneDaRimuovere == null) {
            logger.warning("Tentativo di rimuovere una condivisione non trovata.");
            return;
        }

        mainController.getCondivisioneDAO().deleteCondivisione(condivisioneDaRimuovere);
        toDo.rimuoviCondivisione(condivisioneDaRimuovere);

        if (mainController.getUtenteCorrente().getId() == userToRemove.getId()) {
            mainController.caricaDatiUtente(mainController.getUtenteCorrente());
            mainController.refreshMainFrameToDos();
        }
    }

    /**
     * Apre una finestra di dialogo per gestire (rimuovere) le condivisioni esistenti.
     * Permette all'autore di selezionare un utente dalla lista delle condivisioni attive
     * e di revocarne l'accesso.
     *
     * @param todo Il ToDo di cui gestire le condivisioni.
     */
    public void openManageSharingDialog(ToDo todo) {
        if (todo.getCondivisioni() == null || todo.getCondivisioni().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Questo ToDo non è condiviso con nessuno.", "Gestione Condivisioni", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        List<Utente> sharedUsers = todo.getCondivisioni().stream()
                .map(Condivisione::getUtente)
                .collect(Collectors.toList());

        String[] userNames = sharedUsers.stream().map(Utente::getNome).toArray(String[]::new);

        String userToManage = (String) JOptionPane.showInputDialog(
                null,
                "Seleziona un utente da rimuovere:",
                "Gestisci Condivisioni",
                JOptionPane.PLAIN_MESSAGE,
                null,
                userNames,
                userNames[0]
        );

        if (userToManage == null) return;

        Utente utenteSelezionato = sharedUsers.stream()
                .filter(u -> u.getNome().equals(userToManage))
                .findFirst().orElse(null);

        if (utenteSelezionato != null) {
            int confirm = JOptionPane.showConfirmDialog(
                    null,
                    "Sei sicuro di voler rimuovere la condivisione con " + utenteSelezionato.getNome() + "?",
                    "Conferma Rimozione",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                removeSharing(todo, utenteSelezionato);
                JOptionPane.showMessageDialog(null, "Condivisione con " + utenteSelezionato.getNome() + " rimossa.");
            }
        }
    }
}
