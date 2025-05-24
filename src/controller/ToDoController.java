package controller;

import gui.*;
import model.*;

import javax.swing.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ToDoController {
    private LoginFrame loginFrame;
    private MainFrame mainFrame;
    private Utente utenteCorrente;

    //controller specializzati
    private ToDoDialogController toDoDialogController;
    private RicercaController ricercaController;
    private CondivisioneController condivisioneController;

    //demo lista utenti con bacheche e to do
    private List<Utente> utentiRegistrati;

    public ToDoController(){
        utentiRegistrati = creaUtentiDemo();
        toDoDialogController = new ToDoDialogController(this);
        ricercaController = new RicercaController(this);
        condivisioneController = new CondivisioneController(this);
        initLogin();
    }

    private void initLogin(){
        loginFrame = new LoginFrame();
        loginFrame.getLoginButton().addActionListener(e->{
            String username = loginFrame.getUserField().getText();
            String password = new String(loginFrame.getPassField().getPassword());

            Utente utente = autenticaUtente(username, password);
            if(utente != null) {
                utenteCorrente = utente;
                apriMainFrame(utente);
                loginFrame.dispose();
            } else {
                JOptionPane.showMessageDialog(loginFrame, "Credenziali errate", "Errore login", JOptionPane.ERROR_MESSAGE);

            }
        });

        loginFrame.setVisible(true);
    }

    private Utente autenticaUtente(String username, String password) {
        for(Utente u : utentiRegistrati){
            if(u.getLogin().equals(username) && u.getPassword().equals(password)) {
                return u;
            }
        }
        return null;
    }

    private void apriMainFrame(Utente utente) {
        mainFrame = new MainFrame(utente.getNome());
        mainFrame.getAddToDoButton().addActionListener(e -> toDoDialogController.openAddToDoDialog());
        mainFrame.getSearchButton().addActionListener(e -> ricercaController.openSearchDialog());

        refreshMainFrameToDos();
        mainFrame.setVisible(true);
    }

    public void refreshMainFrameToDos() {
        mainFrame.getUniversitaPanel().clearToDos();
        mainFrame.getTempoLiberoPanel().clearToDos();
        mainFrame.getLavoroPanel().clearToDos();

        for(Bacheca b : utenteCorrente.getBacheche()) {
            b.getToDoList().sort((t1, t2) -> Integer.compare(t1.getPosizione(), t2.getPosizione()));
            for(ToDo todo : b.getToDoList()) {
                ToDoPanel todoPanel = new ToDoPanel(todo);
                todoPanel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if(e.isPopupTrigger()) {
                            showToDoContextMenu(e, todo);
                        }
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        if(e.isPopupTrigger()) {
                            showToDoContextMenu(e, todo);
                        }
                    }
                });
                todoPanel.getCompletatoCheckbox().addActionListener(e -> handleToDoCompletionChange(todo, todoPanel.getCompletatoCheckbox().isSelected()));
                if(b.getTitolo() == TitoloBacheca.UNIVERSITA) {
                    mainFrame.getUniversitaPanel().aggiungiToDo(todoPanel);
                } else if(b.getTitolo() == TitoloBacheca.TEMPO_LIBERO) {
                    mainFrame.getTempoLiberoPanel().aggiungiToDo((todoPanel));
                } else if(b.getTitolo() == TitoloBacheca.LAVORO) {
                    mainFrame.getLavoroPanel().aggiungiToDo(todoPanel);
                }
            }
        }
    }

    private void showToDoContextMenu(MouseEvent e, ToDo todo) {
        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem editItem = new JMenuItem("Modifica");
        JMenuItem deleteItem = new JMenuItem("Elimina");
        JMenuItem moveItem = new JMenuItem("Sposta in...");
        JMenuItem shareItem = new JMenuItem("Condividi");
        JMenuItem viewSharedUsersitem = new JMenuItem("Visualizza condivisioni");

        editItem.addActionListener(actionEvent -> toDoDialogController.openEditToDoDialog(todo));
        deleteItem.addActionListener(actionEvent -> deleteToDo(todo));
        moveItem.addActionListener(actionEvent -> openMoveToDoDialog(todo));
        shareItem.addActionListener(actionEvent -> condivisioneController.openShareToDoDialog(todo));
        viewSharedUsersitem.addActionListener(actionEvent -> condivisioneController.showSharedUsers(todo));

        popupMenu.add(editItem);
        popupMenu.add(deleteItem);
        popupMenu.add(moveItem);
        popupMenu.add(shareItem);
        popupMenu.add(viewSharedUsersitem);
        popupMenu.show(e.getComponent(), e.getX(), e.getY());
    }

    private void handleToDoCompletionChange(ToDo todo, boolean isCompleted) {
        if (isCompleted) {
            todo.setStato(StatoToDo.COMPLETATO);
        } else {
                todo.setStato(StatoToDo.NON_COMPLETATO);
            }

        if (todo.getChecklist() != null && todo.getChecklist().isCompletata()) {
            todo.setStato(StatoToDo.COMPLETATO); // Auto-completa il ToDo se la checklist è completa
        } else if (todo.getChecklist() != null && !todo.getChecklist().isCompletata() && todo.getStato() == StatoToDo.COMPLETATO) {

            }

            refreshMainFrameToDos(); // Aggiorna la UI per riflettere il cambio di stato
        }

        public void addToDoToBacheca(ToDo todo, TitoloBacheca bachecaTitle) {
            Bacheca targetBacheca = null;
            for (Bacheca b : utenteCorrente.getBacheche()) {
                if (b.getTitolo() == bachecaTitle) {
                    targetBacheca = b;
                    break;
                }
            }
            if (targetBacheca != null) {
                targetBacheca.aggiungiToDo(todo);
                refreshMainFrameToDos();
                JOptionPane.showMessageDialog(mainFrame, "ToDo aggiunto con successo!");
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Bacheca non trovata per il titolo: " + bachecaTitle, "Errore", JOptionPane.ERROR_MESSAGE);
            }
        }

        public void updateToDoInBacheca(ToDo updatedToDo) {
            refreshMainFrameToDos();
            JOptionPane.showMessageDialog(mainFrame, "ToDo modificato con successo!");
        }

        public void deleteToDo(ToDo todo) {
            int confirm = JOptionPane.showConfirmDialog(mainFrame, "Sei sicuro di voler eliminare questo ToDo?", "Conferma Eliminazione", JOptionPane.YES_NO_OPTION);
            if(confirm == JOptionPane.YES_OPTION) {
                boolean removed = false;
                for(Bacheca b : utenteCorrente.getBacheche()) {
                    if(b.getToDoList().remove(todo)) {
                        removed = true;
                        break;
                    }
                }

                for(Utente u : utentiRegistrati) {
                    if(u.getCondivisioni() != null) {
                        u.getCondivisioni().removeIf(c -> c.getToDo().getId() == todo.getId());
                    }
                    for(Bacheca b : u.getBacheche()) {
                        b.getToDoList().removeIf(t -> t.getId() == todo.getId());
                    }
                }

                if(removed) {
                    refreshMainFrameToDos();
                    JOptionPane.showMessageDialog(mainFrame, "ToDo eliminato con successo.");
                } else {
                    JOptionPane.showMessageDialog(mainFrame, "Errore nella cancellazione del ToDo.", "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        private void openMoveToDoDialog(ToDo todo) {
            String[] bachecaTitles = {TitoloBacheca.UNIVERSITA.name(), TitoloBacheca.LAVORO.name(), TitoloBacheca.TEMPO_LIBERO.name()};
            String selectedTitle = (String) JOptionPane.showInputDialog(
                    mainFrame, "Seleziona la bacheca di destinazione:",
                    "Sposta ToDo", JOptionPane.QUESTION_MESSAGE, null, bachecaTitles,
                    todo.getBacheca() != null ? todo.getBacheca().getTitolo().name() : bachecaTitles[0]
            );

            if(selectedTitle != null) {
                TitoloBacheca targetTitoloBacheca = TitoloBacheca.valueOf(selectedTitle);
                moveToDo(todo, targetTitoloBacheca);
            }
        }

        public void moveToDo(ToDo todo, TitoloBacheca targetTitoloBacheca) {
            Bacheca sourceBacheca = todo.getBacheca();
            if(sourceBacheca != null) {
                sourceBacheca.getToDoList().remove(todo);
            }

            Bacheca targetBacheca = null;
            for(Bacheca b : utenteCorrente.getBacheche()) {
                if(b.getTitolo() == targetTitoloBacheca) {
                    targetBacheca = b;
                    break;
                }
            }

            if (targetBacheca != null) {
                targetBacheca.aggiungiToDo(todo); // Aggiungi il ToDo alla nuova bacheca
                todo.setBacheca(targetBacheca); // Aggiorna il riferimento alla bacheca nel ToDo
                refreshMainFrameToDos(); // Aggiorna la UI
                JOptionPane.showMessageDialog(mainFrame, "ToDo spostato con successo.");
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Errore: Bacheca di destinazione non trovata.", "Errore", JOptionPane.ERROR_MESSAGE);
                // Se lo spostamento fallisce, ripristina il ToDo nella bacheca originale
                if (sourceBacheca != null) {
                    sourceBacheca.aggiungiToDo(todo);
                }
            }
        }

        public Utente getUtenteCorrente() {
            return utenteCorrente;
        }

        public List<Utente> getUtentiRegistrati() {
            return utentiRegistrati;
        }

    private List<Utente> creaUtentiDemo(){
        List<Utente> lista = new ArrayList<>();

        Utente u1 = new Utente(123, "Marianna", "marianna", "stress");

        Bacheca uni = new Bacheca(1, TitoloBacheca.UNIVERSITA, "Organizzazione per l'università");
        ToDo todo1 = new ToDo(14, "Studiare Java", "Studiare capitolo 7 + esercizi", LocalDate.parse("2025-06-08"), "path/to/image.png", "https://url.com", "#FFFFFF", 1);
        todo1.setStato(StatoToDo.NON_COMPLETATO);
        Checklist checklist1 = new Checklist();
        checklist1.aggiungiAttivita(new Attivita("Leggere Cap 7", StatoAttivita.NON_COMPLETATO));
        checklist1.aggiungiAttivita(new Attivita("Fare esercizi", StatoAttivita.NON_COMPLETATO));
        todo1.setChecklist(checklist1);
        uni.aggiungiToDo(todo1);

        ToDo todo2 = new ToDo(25, "Esame informatica", "Sede Monte Sant'Angelo",LocalDate.parse("2025-06-15") , "path/to/image.png", "https://url.com", "#FFFFFF", 2);
        todo2.setStato(StatoToDo.NON_COMPLETATO);
        uni.aggiungiToDo(todo1);

        Bacheca tempo = new Bacheca(2, TitoloBacheca.TEMPO_LIBERO, "Organizzazione degli hobby");
        ToDo todo3 = new ToDo(32, "Leggere libro", "Guida galattica per autostoppisti, pagina 42", null, "path/to/image.png", "https://url.com", "#FFFFFF", 3);
        todo3.setStato(StatoToDo.NON_COMPLETATO);
        tempo.aggiungiToDo(todo3);

        Bacheca lavoro = new Bacheca(3, TitoloBacheca.LAVORO, "Organizazzione giornata lavorativa");
        ToDo todo4 = new ToDo(45, "Inviare report", "Report finale", LocalDate.parse("2025-05-31"), "path/to/image.png", "https.//url.com", "#FFFFFF", 4);
        todo4.setStato(StatoToDo.NON_COMPLETATO);
        lavoro.aggiungiToDo(todo4);

        u1.aggiungiBacheca(uni);
        u1.aggiungiBacheca(lavoro);
        u1.aggiungiBacheca(tempo);

        lista.add(u1);
        Utente u2 = new Utente(124, "Antonietta", "anto", "pass");
        Bacheca uniAnto = new Bacheca(4, TitoloBacheca.UNIVERSITA, "Organizzazione universitaria di Marco");
        Bacheca tempoAnto = new Bacheca(5, TitoloBacheca.TEMPO_LIBERO, "Hobby di Marco");
        u2.aggiungiBacheca(uniAnto);
        u2.aggiungiBacheca(tempoAnto);

        Condivisione c1 = new Condivisione(u2, todo1);
        todo1.aggiungiCondivisione(c1);
        u2.aggiungiCondivisione(c1);

        boolean todo1InUniAnto = uniAnto.getToDoList().stream().anyMatch(t -> t.getId() == todo1.getId());
        if (!todo1InUniAnto) {
            uniAnto.aggiungiToDo(todo1);
        }
        lista.add(u2);
        return lista;
    }
}



