package controller;

import gui.*;
import model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

        mainFrame.getAddBachecaButton().addActionListener(e -> openCreaBachecaDialog());
        mainFrame.getDeleteBachecaButton().addActionListener(e -> openEliminaBachecaDialog());
        refreshMainFrameToDos();
        mainFrame.setVisible(true);
    }

    public void refreshMainFrameToDos() {
        JPanel bachechePanel = mainFrame.getBachechePanel();
        bachechePanel.removeAll();

        int numBacheche = utenteCorrente.getBacheche().size();
        if(numBacheche > 0) {
            bachechePanel.setLayout(new GridLayout(1, numBacheche, 10, 10));
        }
        for(Bacheca b : utenteCorrente.getBacheche()) {
            BachecaPanel bachecaPanel = new BachecaPanel(b);
            bachecaPanel.getModifyDescButton().addActionListener(e ->
                    openModificaDescrizioneDialog(b, bachecaPanel));
            b.getToDoList().sort((t1, t2) -> Integer.compare(t1.getPosizione(), t2.getPosizione()));
            for(ToDo toDo : b.getToDoList()) {
                ToDoPanel toDoPanel = new ToDoPanel(toDo);
                toDoPanel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if(e.isPopupTrigger()) {
                            showToDoContextMenu(e, toDo);
                        }
                    }
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        if(e.isPopupTrigger()) {
                            showToDoContextMenu(e, toDo);
                        }
                    }
                });
                toDoPanel.getCompletatoCheckbox().addActionListener(e ->
                        handleToDoCompletionChange(toDo, toDoPanel.getCompletatoCheckbox().isSelected()));

                toDoPanel.getMenuButton().addActionListener(actionEvent -> {
                    JPopupMenu popupMenu = createToDoContextMenu(toDo);
                    JButton menuButton = (JButton) actionEvent.getSource();
                    popupMenu.show(menuButton, 0, menuButton.getHeight());
                });

                for (JCheckBox subTaskCb : toDoPanel.getSubTaskCheckboxes()) {
                    Attivita attivita = (Attivita) subTaskCb.getClientProperty("ATTIVITA_OBJ");

                    subTaskCb.addActionListener(e ->
                            handleSubTaskCompletionChange(toDo, attivita, subTaskCb.isSelected())
                    );
                }

                bachecaPanel.aggiungiToDo(toDoPanel);
            }
            bachechePanel.add(bachecaPanel);
        }
        bachechePanel.revalidate();
        bachechePanel.repaint();
    }

    private JPopupMenu createToDoContextMenu(ToDo todo) {
        JPopupMenu popupMenu = new JPopupMenu();

        // --- Azione "Info" ---
        JMenuItem infoItem = new JMenuItem("Info");
        infoItem.addActionListener(actionEvent -> showToDoInfo(todo));
        popupMenu.add(infoItem);
        popupMenu.addSeparator();

        // --- Creazione Voci Menu ---
        JMenuItem editItem = new JMenuItem("Modifica");
        JMenuItem deleteItem = new JMenuItem("Elimina");
        JMenuItem moveItem = new JMenuItem("Sposta in...");
        JMenuItem shareItem = new JMenuItem("Condividi (Aggiungi)");
        JMenuItem manageShareItem = new JMenuItem("Gestisci/Rimuovi Condivisi");
        JMenuItem viewSharedUsersitem = new JMenuItem("Visualizza condivisioni");
        JMenuItem moveUpItem = new JMenuItem("Sposta Su");
        JMenuItem moveDownItem = new JMenuItem("Sposta Giù");

        // --- Collegamento Azioni ---
        editItem.addActionListener(actionEvent -> toDoDialogController.openEditToDoDialog(todo));
        deleteItem.addActionListener(actionEvent -> deleteToDo(todo));
        moveItem.addActionListener(actionEvent -> openMoveToDoDialog(todo));
        shareItem.addActionListener(actionEvent -> condivisioneController.openShareToDoDialog(todo));
        viewSharedUsersitem.addActionListener(actionEvent -> condivisioneController.showSharedUsers(todo));
        manageShareItem.addActionListener(actionEvent -> condivisioneController.openManageSharingDialog(todo));
        moveUpItem.addActionListener(actionEvent -> spostaToDo(todo, -1));
        moveDownItem.addActionListener(actionEvent -> spostaToDo(todo, 1));

        // --- Logica Abilitazione Pulsanti (Req. [14]) ---
        boolean isAutore = false;
        if (todo.getAutore() != null) {
            isAutore = (todo.getAutore().getId() == utenteCorrente.getId());
        }

        editItem.setEnabled(isAutore);
        deleteItem.setEnabled(isAutore);
        shareItem.setEnabled(isAutore);
        manageShareItem.setEnabled(isAutore);
        viewSharedUsersitem.setEnabled(true);
        moveItem.setEnabled(true);
        moveUpItem.setEnabled(true);
        moveDownItem.setEnabled(true);

        if (todo.getPosizione() == 0) {
            moveUpItem.setEnabled(false);
        }
        if (todo.getBacheca() != null && todo.getPosizione() == todo.getBacheca().getToDoList().size() - 1) {
            moveDownItem.setEnabled(false);
        }

        // --- Aggiunta Voci al Menu ---
        popupMenu.add(editItem);
        popupMenu.add(deleteItem);
        popupMenu.add(moveItem);
        popupMenu.addSeparator();
        popupMenu.add(shareItem);
        popupMenu.add(manageShareItem);
        popupMenu.add(viewSharedUsersitem);
        popupMenu.addSeparator();
        popupMenu.add(moveUpItem);
        popupMenu.add(moveDownItem);

        return popupMenu;
    }

    private void showToDoContextMenu(MouseEvent e, ToDo todo) {
        JPopupMenu popupMenu = createToDoContextMenu(todo);
        popupMenu.show(e.getComponent(), e.getX(), e.getY());
    }

    private void spostaToDo(ToDo todo, int direzione) {
        Bacheca bacheca = todo.getBacheca();
        if (bacheca == null) return;

        List<ToDo> todoList = bacheca.getToDoList();

        todoList.sort((t1, t2) -> Integer.compare(t1.getPosizione(), t2.getPosizione()));

        // Usiamo la posizione salvata nel ToDo come indice
        int currentIndex = -1;
        for (int i = 0; i < todoList.size(); i++) {
            if (todoList.get(i).getId() == todo.getId()) {
                currentIndex = i;
                break;
            }
        }
        if(currentIndex == -1) return;

        int newIndex =  currentIndex + direzione;

        if(newIndex < 0 || newIndex >= todoList.size()) return;

        ToDo otherTodo = todoList.get(newIndex);

        int oldPos = todo.getPosizione();
        int otherPos = otherTodo.getPosizione();

        todo.setPosizione(otherPos);
        otherTodo.setPosizione(oldPos);

        refreshMainFrameToDos();
    }

    private void handleToDoCompletionChange(ToDo todo, boolean isCompleted) {
        if (isCompleted) {
            todo.setStato(StatoToDo.COMPLETATO);
        } else {
                todo.setStato(StatoToDo.NON_COMPLETATO);
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
            TitoloBacheca[] bachecaTitoli = TitoloBacheca.values();
            TitoloBacheca selectedTitle = (TitoloBacheca) JOptionPane.showInputDialog(
                    mainFrame, "Seleziona la bacheca di destinazione: ",
                    "Sposta ToDo", JOptionPane.QUESTION_MESSAGE, null,
                    bachecaTitoli,
                    todo.getBacheca() != null ? todo.getBacheca().getTitolo() : bachecaTitoli[0]
            );
            if(selectedTitle != null) {
                moveToDo(todo, selectedTitle);
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

        Bacheca uni = new Bacheca(1, TitoloBacheca.UNIVERSITA, "Organizzazione per l'università", 123);
        ToDo todo1 = new ToDo(14, "Studiare Java", "Studiare capitolo 7 + esercizi", LocalDate.parse("2025-06-08"), "path/to/image.png", "https://url.com", "#FFFFFF", 1, u1);
        todo1.setStato(StatoToDo.NON_COMPLETATO);
        Checklist checklist1 = new Checklist();
        checklist1.aggiungiAttivita(new Attivita("Leggere Cap 7"));
        checklist1.aggiungiAttivita(new Attivita("Fare esercizi"));
        todo1.setChecklist(checklist1);
        uni.aggiungiToDo(todo1);

        ToDo todo2 = new ToDo(25, "Esame informatica", "Sede Monte Sant'Angelo",LocalDate.parse("2025-06-15") , "path/to/image.png", "https://url.com", "#FFFFFF", 2, u1);
        todo2.setStato(StatoToDo.NON_COMPLETATO);
        uni.aggiungiToDo(todo1);

        Bacheca tempo = new Bacheca(2, TitoloBacheca.TEMPO_LIBERO, "Organizzazione degli hobby", 123);
        ToDo todo3 = new ToDo(32, "Leggere libro", "Guida galattica per autostoppisti, pagina 42", null, "path/to/image.png", "https://url.com", "#FFFFFF", 3, u1);
        todo3.setStato(StatoToDo.NON_COMPLETATO);
        tempo.aggiungiToDo(todo3);

        Bacheca lavoro = new Bacheca(3, TitoloBacheca.LAVORO, "Organizazzione giornata lavorativa", 123);
        ToDo todo4 = new ToDo(45, "Inviare report", "Report finale", LocalDate.parse("2025-05-31"), "path/to/image.png", "https.//url.com", "#FFFFFF", 4, u1);
        todo4.setStato(StatoToDo.NON_COMPLETATO);
        lavoro.aggiungiToDo(todo4);

        u1.aggiungiBacheca(uni);
        u1.aggiungiBacheca(lavoro);
        u1.aggiungiBacheca(tempo);

        lista.add(u1);
        Utente u2 = new Utente(124, "Antonietta", "anto", "pass");
        Bacheca uniAnto = new Bacheca(4, TitoloBacheca.UNIVERSITA, "Organizzazione universitaria di Marco", 124);
        Bacheca tempoAnto = new Bacheca(5, TitoloBacheca.TEMPO_LIBERO, "Hobby di Marco", 124);
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

    private void openCreaBachecaDialog() {
        List<TitoloBacheca> titoliPosseduti = utenteCorrente.getBacheche().stream().map(Bacheca::getTitolo).collect(Collectors.toList());
        List<TitoloBacheca> titoliDisponibili = new ArrayList<>();
        for(TitoloBacheca t : TitoloBacheca.values()) {
            if(!titoliPosseduti.contains(t)) {
                titoliDisponibili.add(t);
            }
        }
        if(titoliDisponibili.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "Hai già creato tutte le bacheche disponibili.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        TitoloBacheca titoloScelto = (TitoloBacheca) JOptionPane.showInputDialog(
                mainFrame, "Scegli quale bacheca creare:",
                "Crea Bacheca", JOptionPane.QUESTION_MESSAGE, null,
                titoliDisponibili.toArray(),
                titoliDisponibili.get(0)
        );
        if (titoloScelto != null) {
            int newId = utenteCorrente.getBacheche().size()+100;
            String desc = JOptionPane.showInputDialog(mainFrame, "Inserisci una descrizione per la bacheca " + titoloScelto.name() + ":");

            Bacheca nuovaBacheca = new Bacheca(newId, titoloScelto, (desc != null ? desc : ""), utenteCorrente.getId());
            utenteCorrente.aggiungiBacheca(nuovaBacheca);

            refreshMainFrameToDos();
        }
    }

    private void openEliminaBachecaDialog() {
        if(utenteCorrente.getBacheche().size() <= 1) {
            JOptionPane.showMessageDialog(mainFrame, "Non puoi eliminare l'ultima bacheca. Deve rimanerne almeno una.", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<TitoloBacheca> titoliPosseduti = utenteCorrente.getBacheche().stream()
                .map(Bacheca::getTitolo)
                .collect(Collectors.toList());

        TitoloBacheca titoloDaEliminare = (TitoloBacheca) JOptionPane.showInputDialog(
                mainFrame, "Scegli quale bacheca eliminare:",
                "Elimina Bacheca", JOptionPane.QUESTION_MESSAGE, null,
                titoliPosseduti.toArray(),
                titoliPosseduti.get(0)
        );

        if(titoloDaEliminare != null) {
            int confirm = JOptionPane.showConfirmDialog(mainFrame,
                    "Sei sicuro di voler eliminare la bacheca '" + titoloDaEliminare.name() + "'?\n Tutti i ToDo al suo interno saranno persii.",
                    "Conferma Eliminazione", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if(confirm == JOptionPane.YES_OPTION) {
                utenteCorrente.getBacheche().removeIf(b -> b.getTitolo() == titoloDaEliminare);
                refreshMainFrameToDos();
            }
        }
    }

    private void openModificaDescrizioneDialog(Bacheca bacheca, BachecaPanel panelView) {
        String oldDesc =  bacheca.getDescrizione();
        String newDesc = (String) JOptionPane.showInputDialog(
                mainFrame,
                "Inserisci la nuova descrizione per " + bacheca.getTitolo().name() + " :",
                "Modifica descrizione bacheca",
                JOptionPane.PLAIN_MESSAGE,
                null, null, oldDesc
        );
        if(newDesc != null) {
            bacheca.setDescrizione(newDesc);
            panelView.updateDescrizioneLabel();
            JOptionPane.showMessageDialog(mainFrame, "Descrizione aggiornata con successo.");
        }
    }

    private void showToDoInfo(ToDo todo) {
        JPanel infoPanel = new JPanel(new BorderLayout(10, 10));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        if(todo.getImaginePath() != null && !todo.getImaginePath().isEmpty()) {
            try {
                ImageIcon icon = new ImageIcon(todo.getImaginePath());
                Image image = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                JLabel imageLabel = new JLabel(new ImageIcon(image));
                imageLabel.setBorder(BorderFactory.createEtchedBorder());
                infoPanel.add(imageLabel, BorderLayout.WEST);
            } catch (Exception e) {
                infoPanel.add(new JLabel("Immagine non trovata."), BorderLayout.WEST);
            }
        }

        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setOpaque(false);
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);
        infoArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        StringBuilder sb = new StringBuilder();
        sb.append("Titolo: ").append(todo.getTitolo()).append("\n\n");
        sb.append("Descrizione:\n").append(todo.getDescrizione() != null && !todo.getDescrizione().isEmpty() ? todo.getDescrizione() : "Nessuna.").append("\n\n");
        sb.append("Bacheca: ").append(todo.getBacheca().getTitolo().toString()).append("\n");
        sb.append("Scadenza: ").append(todo.getScadenza() != null ? todo.getScadenza().toString() : "Nessuna.").append("\n");
        sb.append("URL: ").append(todo.getURL() != null && !todo.getURL().isEmpty() ? todo.getURL() : "Nessuno.").append("\n");
        infoArea.setText(sb.toString());

        infoPanel.add(infoArea, BorderLayout.CENTER);

        if (todo.getChecklist() != null && !todo.getChecklist().getAttivita().isEmpty()) {
            JPanel checklistDisplayPanel = new JPanel();
            checklistDisplayPanel.setLayout(new BoxLayout(checklistDisplayPanel, BoxLayout.Y_AXIS));
            checklistDisplayPanel.setBorder(BorderFactory.createTitledBorder("Checklist"));

            for (Attivita a : todo.getChecklist().getAttivita()) {
                JCheckBox cb = new JCheckBox(a.getNome());
                cb.setSelected(a.getStato() == StatoAttivita.COMPLETATO);
                cb.setEnabled(false); // Sola lettura
                checklistDisplayPanel.add(cb);
            }
            infoPanel.add(new JScrollPane(checklistDisplayPanel), BorderLayout.SOUTH);
        }
        JOptionPane.showMessageDialog(
                mainFrame,
                infoPanel,
                "Info ToDo: " + todo.getTitolo(),
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void handleSubTaskCompletionChange(ToDo parentToDo, Attivita subTask, boolean isCompleted) {
        subTask.setStato(isCompleted ? StatoAttivita.COMPLETATO : StatoAttivita.NON_COMPLETATO);

        parentToDo.aggiornaStatoDaChecklist();

        refreshMainFrameToDos();
    }
}



