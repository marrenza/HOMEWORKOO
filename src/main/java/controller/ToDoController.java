package controller;

import gui.*;
import model.*;
import database.DatabaseConnection;
import dao.*;
import dao.postgresimpl.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.sql.Connection;
import java.sql.SQLException;

import gui.RegistrazioneDialog;
import model.TitoloBacheca;

public class ToDoController {
    private LoginFrame loginFrame;
    private MainFrame mainFrame;
    private Utente utenteCorrente;

    private ToDoDialogController toDoDialogController;
    private RicercaController ricercaController;
    private CondivisioneController condivisioneController;

    private Connection connection;
    private UtenteDAO utenteDAO;
    private BachecaDAO bachecaDAO;
    private ToDoDAO toDoDAO;
    private AttivitaDAO attivitaDAO;
    private CondivisioneDAO condivisioneDAO;

    public ToDoController(){
        try {
            this.connection = DatabaseConnection.getConnection();
            if (this.connection == null) {
                throw new SQLException("Connessione fallita!");
            }

            this.utenteDAO = new PostgresUtenteDAO(this.connection);
            this.bachecaDAO = new PostgresBachecaDAO(this.connection);
            this.attivitaDAO = new PostgresAttivitaDAO(this.connection);
            this.condivisioneDAO = new PostgresCondivisioneDAO(this.connection);
            this.toDoDAO = new PostgresToDoDAO(this.connection, this.utenteDAO, this.attivitaDAO, this.condivisioneDAO);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Errore fatale di connessione al Database.\n" + e.getMessage(), "Errore DB", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        toDoDialogController = new ToDoDialogController(this);
        ricercaController = new RicercaController(this);
        condivisioneController = new CondivisioneController(this);
        initLogin();
    }

    private void initLogin(){
        loginFrame = new LoginFrame();

        loginFrame.getLoginButton().addActionListener(e -> {
            String username = loginFrame.getUserField().getText();
            String password = new String(loginFrame.getPassField().getPassword());
            Utente utente = utenteDAO.getUtenteByLogin(username);
            if(utente != null && utente.getPassword().equals(password)) {
                utenteCorrente = utente;
                caricaDatiUtente(utenteCorrente);
                apriMainFrame(utente);
                loginFrame.dispose();
            } else {
                JOptionPane.showMessageDialog(loginFrame, "Credenziali errate", "Errore login", JOptionPane.ERROR_MESSAGE);
            }
        });
        loginFrame.getRegisterButton().addActionListener(e -> {
            openRegistrazioneDialog();
        });

        loginFrame.setVisible(true);
    }

    private void openRegistrazioneDialog() {
        RegistrazioneDialog dialog = new RegistrazioneDialog(loginFrame);

        dialog.getBtnConferma().addActionListener(e -> {
            String nome = dialog.getNome();
            String login = dialog.getLogin();
            String pass = dialog.getPassword();
            String confermaPass = dialog.getConfermaPassword();

            if (nome.trim().isEmpty() || login.trim().isEmpty() || pass.trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Tutti i campi sono obbligatori.", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!pass.equals(confermaPass)) {
                JOptionPane.showMessageDialog(dialog, "Le password non coincidono.", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (utenteDAO.getUtenteByLogin(login) != null) {
                JOptionPane.showMessageDialog(dialog, "Questo login è già in uso. Scegline un altro.", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Utente nuovoUtente = new Utente(nome, login, pass);
                utenteDAO.addUtente(nuovoUtente);
                Bacheca uni = new Bacheca(0, TitoloBacheca.UNIVERSITA, "Attività universitarie", nuovoUtente.getId());
                Bacheca lavoro = new Bacheca(0, TitoloBacheca.LAVORO, "Attività lavorative", nuovoUtente.getId());
                Bacheca tempo = new Bacheca(0, TitoloBacheca.TEMPO_LIBERO, "Hobby e tempo libero", nuovoUtente.getId());
                bachecaDAO.addBacheca(uni);
                bachecaDAO.addBacheca(lavoro);
                bachecaDAO.addBacheca(tempo);
                JOptionPane.showMessageDialog(dialog, "Registrazione completata! Ora puoi effettuare il login.", "Successo", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Errore durante il salvataggio nel database.", "Errore DB", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
        dialog.setVisible(true);
    }

    public void caricaDatiUtente(Utente utente) {
        List<Bacheca> bacheche = bachecaDAO.getBachecaByUserId(utente.getId());
        for (Bacheca b : bacheche) {
            b.setProprietario(utente);
            List<ToDo> todos = toDoDAO.getToDosForBachecaAndUtente(b.getId(), utente.getId());
            b.setToDoList(todos);
            for (ToDo t : todos) {
                t.setBacheca(b);
            }
        }
        utente.getBacheche().clear();
        utente.getBacheche().addAll(bacheche);
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
        if(bacheca == null) return;

        bacheca.getToDoList().sort((t1, t2) -> Integer.compare(t1.getPosizione(), t2.getPosizione()));
        List<ToDo> todoList = bacheca.getToDoList();
        int currentIndex = -1;
        for (int i = 0; i < todoList.size(); i++) {
            if (todoList.get(i).getId() == todo.getId()) {
                currentIndex = i;
                break;
            }
        }
        if (currentIndex == -1) {
            System.err.println("Errore: ToDo non trovato nella lista ordinata.");
            return;
        }
        int newIndex = currentIndex + direzione;
        if (newIndex < 0 || newIndex >= todoList.size()) {
            System.err.println("Errore: Spostamento fuori dai limiti.");
            return;
        }

        ToDo otherTodo = todoList.get(newIndex);

        int oldPos = todo.getPosizione();
        int otherPos = otherTodo.getPosizione();

        if (oldPos == otherPos) {
            oldPos = currentIndex;
            otherPos = newIndex;
        }

        todo.setPosizione(otherPos);
        otherTodo.setPosizione(oldPos);

        toDoDAO.updateToDo(todo);
        toDoDAO.updateToDo(otherTodo);

        refreshMainFrameToDos();
    }

    private void handleToDoCompletionChange(ToDo todo, boolean isCompleted) {
        if (isCompleted) {
            todo.setStato(StatoToDo.COMPLETATO);
        } else {
            todo.setStato(StatoToDo.NON_COMPLETATO);
        }
        toDoDAO.updateToDo(todo);
        refreshMainFrameToDos();
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
            todo.setBacheca(targetBacheca);
            toDoDAO.addToDo(todo);
            refreshMainFrameToDos();
            if (todo.getChecklist() != null) {
                for (Attivita a : todo.getChecklist().getAttivita()) {
                    a.setIdTodo(todo.getId());
                    attivitaDAO.addAttivita(a);
                }
            }
            JOptionPane.showMessageDialog(mainFrame, "ToDo aggiunto con successo!");
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Bacheca non trovata per il titolo: " + bachecaTitle, "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateToDoInBacheca(ToDo updatedToDo) {
        toDoDAO.updateToDo(updatedToDo);
        refreshMainFrameToDos();
        attivitaDAO.deleteAttivitaByToDoId(updatedToDo.getId());
        if (updatedToDo.getChecklist() != null) {
            for (Attivita a : updatedToDo.getChecklist().getAttivita()) {
                a.setIdTodo(updatedToDo.getId());
                attivitaDAO.addAttivita(a);
            }
        }
        JOptionPane.showMessageDialog(mainFrame, "ToDo modificato con successo!");
    }

    public void deleteToDo(ToDo todo) {
        int confirm = JOptionPane.showConfirmDialog(mainFrame, "Sei sicuro di voler eliminare questo ToDo?", "Conferma Eliminazione", JOptionPane.YES_NO_OPTION);
        if(confirm == JOptionPane.YES_OPTION) {
            toDoDAO.deleteToDo(todo.getId());
            boolean removed = false;
            for(Bacheca b : utenteCorrente.getBacheche()) {
                if(b.getToDoList().remove(todo)) {
                    removed = true;
                    break;
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
        if (selectedTitle != null) {
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
            targetBacheca.aggiungiToDo(todo);
            todo.setBacheca(targetBacheca);
            toDoDAO.updateToDo(todo);
            refreshMainFrameToDos();
            JOptionPane.showMessageDialog(mainFrame, "ToDo spostato con successo.");
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Errore: Bacheca di destinazione non trovata.", "Errore", JOptionPane.ERROR_MESSAGE);
            if (sourceBacheca != null) {
                sourceBacheca.aggiungiToDo(todo);
            }
        }
    }

    public Utente getUtenteCorrente() {
        return utenteCorrente;
    }

    public List<Utente> getUtentiRegistrati() {
        return utenteDAO.getAllUtenti();
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
            bachecaDAO.addBacheca(nuovaBacheca);
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
                    "Sei sicuro di voler eliminare la bacheca '" + titoloDaEliminare.name() + "'?\n Tutti i ToDo al suo interno saranno persi.", // Ho corretto un refuso
                    "Conferma Eliminazione", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if(confirm == JOptionPane.YES_OPTION) {
                Bacheca bachecaDaEliminare = utenteCorrente.getBacheche().stream()
                        .filter(b -> b.getTitolo() == titoloDaEliminare)
                        .findFirst()
                        .orElse(null);
                if (bachecaDaEliminare != null) {
                    try {
                        bachecaDAO.deleteBacheca(bachecaDaEliminare.getId());
                        utenteCorrente.getBacheche().remove(bachecaDaEliminare);
                        refreshMainFrameToDos();
                        JOptionPane.showMessageDialog(mainFrame, "Bacheca eliminata con successo.");
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(mainFrame, "Errore durante l'eliminazione dal database.", "Errore DB", JOptionPane.ERROR_MESSAGE);
                        e.printStackTrace();
                    }
                }
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
            bachecaDAO.updateBacheca(bacheca);
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
        attivitaDAO.updateAttivita(subTask);
        parentToDo.aggiornaStatoDaChecklist();

        if (parentToDo.getStato() == StatoToDo.COMPLETATO && isCompleted) {
            toDoDAO.updateToDo(parentToDo);
        }
        refreshMainFrameToDos();
    }

    public void aggiornaToDoEsistente(ToDo todo, TitoloBacheca oldBachecaTitolo, TitoloBacheca newBachecaTitolo) {
        try {
            if (oldBachecaTitolo != newBachecaTitolo) {
                utenteCorrente.getBacheche().stream()
                        .filter(b -> b.getTitolo() == oldBachecaTitolo)
                        .findFirst().ifPresent(b -> b.getToDoList().remove(todo));

                Bacheca targetBacheca = utenteCorrente.getBacheche().stream()
                        .filter(b -> b.getTitolo() == newBachecaTitolo)
                        .findFirst().orElse(null);

                if (targetBacheca != null) {
                    targetBacheca.aggiungiToDo(todo);
                    todo.setBacheca(targetBacheca);
                }
            }
            toDoDAO.updateToDo(todo);
            attivitaDAO.deleteAttivitaByToDoId(todo.getId());
            if (todo.getChecklist() != null) {
                for (Attivita a : todo.getChecklist().getAttivita()) {
                    a.setIdTodo(todo.getId());
                    attivitaDAO.addAttivita(a);
                }
            }
            refreshMainFrameToDos();
            JOptionPane.showMessageDialog(mainFrame, "ToDo modificato con successo!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainFrame, "Errore aggiornamento ToDo.", "Errore DB", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public void salvaNuovoToDo(ToDo todo, TitoloBacheca bachecaTitle) {
        Bacheca targetBacheca = utenteCorrente.getBacheche().stream()
                .filter(b -> b.getTitolo() == bachecaTitle)
                .findFirst().orElse(null);
        if (targetBacheca != null) {
            try {
                todo.setBacheca(targetBacheca);
                int maxPos = targetBacheca.getToDoList().stream()
                        .mapToInt(ToDo::getPosizione)
                        .max().orElse(-1);
                todo.setPosizione(maxPos + 1);
                toDoDAO.addToDo(todo);
                if (todo.getChecklist() != null) {
                    for (Attivita a : todo.getChecklist().getAttivita()) {
                        a.setIdTodo(todo.getId());
                        attivitaDAO.addAttivita(a);
                    }
                }
                targetBacheca.aggiungiToDo(todo);
                refreshMainFrameToDos();
                JOptionPane.showMessageDialog(mainFrame, "ToDo aggiunto con successo!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(mainFrame, "Errore salvataggio nuovo ToDo.", "Errore DB", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Bacheca di destinazione non trovata.", "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    public ToDoDAO getToDoDAO() {
        return toDoDAO;
    }

    public UtenteDAO getUtenteDAO() {
        return utenteDAO;
    }

    public CondivisioneDAO getCondivisioneDAO() {
        return condivisioneDAO;
    }

    public AttivitaDAO getAttivitaDAO() {
        return attivitaDAO;
    }

    public BachecaDAO getBachecaDAO() {
        return bachecaDAO;
    }
}



