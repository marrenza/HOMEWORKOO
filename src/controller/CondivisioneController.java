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

public class CondivisioneController {
    private ToDoController mainController;
    private ShareDialog shareDialog;

    public CondivisioneController(ToDoController mainController) {
        this.mainController = mainController;
    }

    /**
     * Apre il dialogo di condivisione per un ToDo specifico.
     * Permette di selezionare altri utenti con cui condividere il ToDo.
     * @param toDoToShare Il ToDo che si desidera condividere.
     */
    public void openShareToDoDialog(ToDo toDoToShare) {
        // Ottieni tutti gli utenti registrati tranne l'utente corrente
        List<Utente> otherUsers = mainController.getUtentiRegistrati().stream()
                .filter(u -> u.getId() != mainController.getUtenteCorrente().getId())
                .collect(Collectors.toList());

        // Crea e mostra il dialogo di condivisione
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
            mainController.refreshMainFrameToDos(); // Aggiorna la UI del proprietario
        });
        shareDialog.setVisible(true);
    }

    /**
     * Condivide un ToDo con un utente specifico.
     * Il ToDo condiviso apparirà nella bacheca corrispondente dell'utente target.
     * @param toDoToShare Il ToDo da condividere.
     * @param targetUser L'utente con cui condividere il ToDo.
     */
    private void shareToDoWithUser(ToDo toDoToShare, Utente targetUser) {
        // Evita condivisioni duplicate
        if (toDoToShare.getCondivisioni() != null) {
            boolean alreadyShared = toDoToShare.getCondivisioni().stream()
                    .anyMatch(c -> c.getUtente().getId() == targetUser.getId());
            if (alreadyShared) {
                JOptionPane.showMessageDialog(shareDialog, "Il ToDo è già condiviso con " + targetUser.getNome() + ".", "Già Condiviso", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        } else {
            toDoToShare.setCondivisioni(new ArrayList<>()); // Inizializza se null
        }

        // Crea l'oggetto Condivisione
        Condivisione condivisione = new Condivisione(targetUser, toDoToShare);
        toDoToShare.aggiungiCondivisione(condivisione); // Aggiungi al ToDo originale
        targetUser.aggiungiCondivisione(condivisione); // Aggiungi alla lista delle condivisioni dell'utente target

        // Aggiungi il ToDo condiviso alla bacheca corrispondente dell'utente target
        TitoloBacheca originalBachecaTitle = toDoToShare.getBacheca().getTitolo();
        boolean addedToTargetBacheca = false;
        for (model.Bacheca b : targetUser.getBacheche()) {
            if (b.getTitolo() == originalBachecaTitle) {
                // Controlla se il ToDo è già nella bacheca target per evitare duplicati visivi
                if (!b.getToDoList().contains(toDoToShare)) {
                    b.aggiungiToDo(toDoToShare); // Aggiungi il ToDo alla bacheca dell'utente target
                }
                addedToTargetBacheca = true;
                break;
            }
        }

        if (!addedToTargetBacheca) {
            // Questo caso si verifica se l'utente target non ha una bacheca con lo stesso titolo.
            // In un sistema reale si potrebbe creare la bacheca o notificare l'utente.
            JOptionPane.showMessageDialog(shareDialog, "L'utente " + targetUser.getNome() + " non ha una bacheca di tipo " + originalBachecaTitle + ". Il ToDo non è stato aggiunto alla sua bacheca.", "Attenzione", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Mostra un dialogo con la lista degli utenti con cui un ToDo è condiviso.
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
     * Rimuove la condivisione di un ToDo con un utente specifico.
     * @param toDo Il ToDo da cui rimuovere la condivisione.
     * @param userToRemove L'utente da cui rimuovere la condivisione.
     */
    public void removeSharing(ToDo toDo, Utente userToRemove) {
        // Rimuovi la condivisione dal ToDo originale
        if (toDo.getCondivisioni() != null) {
            toDo.getCondivisioni().removeIf(c -> c.getUtente().getId() == userToRemove.getId());
        }
        // Rimuovi la condivisione dalla lista delle condivisioni dell'utente target
        if (userToRemove.getCondivisioni() != null) {
            userToRemove.getCondivisioni().removeIf(c -> c.getToDo().getId() == toDo.getId());
        }
        // Rimuovi il ToDo dalla bacheca dell'utente target
        TitoloBacheca originalBachecaTitle = toDo.getBacheca().getTitolo();
        for (model.Bacheca b : userToRemove.getBacheche()) {
            if (b.getTitolo() == originalBachecaTitle) {
                b.getToDoList().remove(toDo);
                break;
            }
        }
        // Se l'utente corrente è quello da cui è stata rimossa la condivisione, aggiorna la sua UI
        if (mainController.getUtenteCorrente().getId() == userToRemove.getId()) {
            mainController.refreshMainFrameToDos();
        }
        JOptionPane.showMessageDialog(null, "Condivisione rimossa.");
    }
}
