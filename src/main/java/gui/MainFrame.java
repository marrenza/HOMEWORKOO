package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * La finestra principale (Dashboard) dell'applicazione ToDo Manager.
 * Questa classe rappresenta l'interfaccia utente principale che compare dopo il login.
 * È divisa in due aree logiche:
 * <ul>
 * <li><b>Header (Nord):</b> Contiene il messaggio di benvenuto e la toolbar con i pulsanti
 * per le operazioni globali (Aggiungi ToDo, Cerca, Gestione Bacheche).</li>
 * <li><b>Area Centrale:</b> Visualizza le bacheche dell'utente (Università, Lavoro, Tempo Libero)
 * organizzate in colonne affiancate.</li>
 * </ul>
 *
 * @author marrenza
 * @version 1.0
 */
public class MainFrame extends JFrame {
    /** Pannello centrale che ospita le colonne delle bacheche. */
    private JPanel bachechePanel;

    /** Pulsante per aprire la finestra di creazione di un nuovo ToDo. */
    private JButton addToDoButton;

    /** Pulsante per aprire la finestra di ricerca. */
    private JButton searchButton;

    /** Pulsante per creare una nuova bacheca (se il limite non è stato raggiunto). */
    private JButton  addBachecaButton;

    /** Pulsante per eliminare una bacheca esistente. */
    private JButton deleteBachecaButton;

    /** Etichetta di benvenuto che mostra il nome dell'utente loggato. */
    private JLabel welcomeLabel;

    /**
     * Costruisce e inizializza la finestra principale.
     * Imposta il layout (BorderLayout), massimizza la finestra all'avvio,
     * configura l'header con lo stile grafico dell'app (blu scuro) e prepara
     * il pannello centrale per accogliere le bacheche.
     *
     * @param username Il nome dell'utente loggato da visualizzare nel messaggio di benvenuto.
     */
    public MainFrame(String username){
        setTitle("ToDo Manager - Bacheche");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        //Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(6, 0, 64));

        welcomeLabel = new JLabel("Benvenuto, " + username);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.add(welcomeLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);


        addToDoButton = new JButton("Aggiungi ToDo");
        styleButton(addToDoButton);
        buttonPanel.add(addToDoButton);

        searchButton = new JButton("Cerca ToDo");
        styleButton(searchButton);
        buttonPanel.add(searchButton);

        addBachecaButton = new JButton("Crea Bacheca");
        styleButton(addBachecaButton);
        buttonPanel.add(addBachecaButton);

        deleteBachecaButton = new JButton("Elimina Bacheca");
        styleButton(deleteBachecaButton);
        buttonPanel.add(deleteBachecaButton);

        headerPanel.add(buttonPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        bachechePanel = new JPanel(new GridLayout(1, 3, 10, 10));
        bachechePanel.setBackground(Color.WHITE);
        bachechePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


        add(bachechePanel, BorderLayout.CENTER);
    }

    /**
     * Metodo di utilità per applicare uno stile grafico coerente ai pulsanti della toolbar.
     * Imposta colori, font e bordi.
     *
     * @param button Il pulsante a cui applicare lo stile.
     */
    private void styleButton(JButton button) {
        button.setBackground(new Color(83, 69, 230));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 50, 200), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /**
     * Restituisce il pannello che contiene le bacheche.
     * Utilizzato dal controller per aggiungere dinamicamente i {@link BachecaPanel}.
     *
     * @return Il JPanel centrale.
     */
    public JPanel getBachechePanel() {
        return bachechePanel;
    }

    /**
     * Restituisce il pulsante "Aggiungi ToDo".
     * Utilizzato dal controller per collegare l'azione di apertura del dialog.
     * @return Il JButton corrispondente.
     */
    public JButton getAddToDoButton() {
        return addToDoButton;
    }

    /**
     * Restituisce il pulsante "Cerca ToDo".
     * Utilizzato dal controller per collegare l'azione di apertura della ricerca.
     * @return Il JButton corrispondente.
     */
    public JButton getSearchButton() {
        return searchButton;
    }

    /**
     * Restituisce il pulsante "Crea Bacheca".
     * Utilizzato dal controller per gestire la creazione di nuove bacheche.
     * @return Il JButton corrispondente.
     */
    public JButton getAddBachecaButton() {
        return addBachecaButton;
    }

    /**
     * Restituisce il pulsante "Elimina Bacheca".
     * Utilizzato dal controller per gestire l'eliminazione delle bacheche.
     * @return Il JButton corrispondente.
     */
    public JButton getDeleteBachecaButton() {
        return deleteBachecaButton;
    }
}
