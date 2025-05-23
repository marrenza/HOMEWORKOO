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

    //demo lista utenti con bacheche e to do
    private List<Utente> utentiRegistrati;

    public ToDoController(){
        utentiRegistrati = creaUtentiDemo();
        initLogin();
    }

    private void initLogin(){
        loginFrame = new LoginFrame();
        loginFrame.getLoginButton().addActionListener(e->{
            String username = loginFrame.getUserField().getText();
            String password = new String(loginFrame.getPassField().getPassword());

            Utente utente =
                    autenticaUtente(username, password);
            if(utente != null) {
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

        for(Bacheca b : utente.getBacheche()) {
            for(ToDo todo : b.getToDoList()) {
                ToDoPanel todoPanel = new ToDoPanel(
                        todo.getTitolo(),
                        todo.getStato() == StatoToDo.COMPLETATO,
                        todo.getStato() == StatoToDo.NON_COMPLETATO
                );

                if(b.getTitolo() == TitoloBacheca.UNIVERSITA) {
                    mainFrame.getUniversitaPanel().aggiungiToDo(todoPanel);
                } else if(b.getTitolo() == TitoloBacheca.TEMPO_LIBERO) {
                    mainFrame.getTempoLiberoPanel().aggiungiToDo(todoPanel);
                } else if(b.getTitolo() == TitoloBacheca.LAVORO) {
                    mainFrame.getLavoroPanel().aggiungiToDo(todoPanel);
                }
            }
        }
        mainFrame.setVisible(true);
    }
    private List<Utente> creaUtentiDemo(){
        List<Utente> lista = new ArrayList<>();

        Utente u1 = new Utente("marianna", "1234", "Marianna");

        Bacheca uni = new Bacheca("Università");
        uni.aggiungiToDo(new ToDo(1, "Studiare Java", "Studiare capitolo 7 + esercizi", LocalDate.now(), "path/to/image.png", "https://url.com", "#FFFFFF", 1));
        uni.aggiungiToDo(new ToDo(2, "Esame informatica", "Sede monte sant'angelo", "16 Giugno 2025", "path/to/image.png", "https://url.com", "#FFFFFF", 2));

        Bacheca tempo = new Bacheca("Tempo Libero");
        tempo.aggiungiToDo(new ToDo(3, "Leggere libro", "Guida galattica per autostoppisti, pagina 42", null, "path/to/image.png", "https://url.com", "#FFFFFF", 3));

        Bacheca lavoro = new Bacheca("Lavoro");
        lavoro.aggiungiToDo(new ToDo(4, "Inviare report", "Report finale", "25 Maggio 2025", "path/to/image.png", "https.//url.com", "#FFFFFF", 4));

        u1.aggiungiBacheca(uni);
        u1.aggiungiBacheca(lavoro);
        u1.aggiungiBacheca(tempo);

        lista.add(u1);
        return lista;
    }
}
