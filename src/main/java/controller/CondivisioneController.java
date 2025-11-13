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

public class CondivisioneController {
    private ToDoController mainController;
    private ShareDialog shareDialog;

    private static final Logger logger = Logger.getLogger(CondivisioneController.class.getName());

    public CondivisioneController(ToDoController mainController) {
        this.mainController = mainController;
    }

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
