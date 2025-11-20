package gui;

import model.Bacheca;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta il pannello grafico di una singola bacheca (colonna).
 * Visualmente, questo componente corrisponde a una delle tre colonne principali (Università, Lavoro, Tempo Libero).
 * È composto da:
 * <ul>
 * <li>Un'intestazione (Header) con titolo, descrizione e pulsante di modifica.</li>
 * <li>Un'area centrale scrollabile che contiene la lista verticale dei {@link ToDoPanel}.</li>
 * </ul>
 *
 * @author marrenza
 * @version 1.0
 */
public class BachecaPanel extends JPanel {
    /** Pannello interno che contiene la lista verticale dei ToDo. */
    private JPanel todoListPanel;

    /** Lista di riferimenti ai pannelli ToDo attualmente visualizzati (utile per la gestione). */
    private List<ToDoPanel> currentToDoPanels;

    /** Il modello dati della bacheca associata a questo pannello. */
    private Bacheca bacheca;


    /** Etichetta per il titolo della bacheca. */
    private JLabel titoloLabel;

    /** Etichetta per la descrizione della bacheca. */
    private JLabel descrizioneLabel;

    /** Pulsante per modificare la descrizione della bacheca. */
    private JButton modifyDescButton;

    /**
     * Costruisce il pannello grafico per una specifica bacheca.
     * Imposta il layout, lo stile grafico (sfondo blu scuro) e inizializza
     * l'header e l'area di scorrimento per i ToDo.
     *
     * @param bacheca Il modello dati della bacheca da visualizzare.
     */
    public BachecaPanel(Bacheca bacheca) {
        this.bacheca = bacheca;
        setLayout(new BorderLayout());
        setBackground(new Color(11, 0, 128));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        titoloLabel = new JLabel(bacheca.getTitolo().toString(), SwingConstants.CENTER);
        titoloLabel.setForeground(Color.WHITE);
        titoloLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        headerPanel.add(titoloLabel, BorderLayout.NORTH);

        descrizioneLabel = new JLabel(bacheca.getDescrizione(), SwingConstants.CENTER);
        descrizioneLabel.setForeground(Color.LIGHT_GRAY);
        descrizioneLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        headerPanel.add(descrizioneLabel, BorderLayout.CENTER);

        modifyDescButton = new JButton("Modifica descrizione");
        modifyDescButton.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        modifyDescButton.setMargin(new Insets(2, 5, 2, 5));
        modifyDescButton.setFocusPainted(false);
        headerPanel.add(modifyDescButton, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        todoListPanel = new JPanel();
        todoListPanel.setLayout(new BoxLayout(todoListPanel, BoxLayout.Y_AXIS));
        todoListPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(todoListPanel);
        scrollPane.setBorder(null);

        add(scrollPane, BorderLayout.CENTER);
        currentToDoPanels = new ArrayList<>();
    }

    /**
     * Aggiunge dinamicamente un pannello ToDo alla lista visuale.
     * Aggiunge il pannello al contenitore {@code todoListPanel} e forza
     * il ricalcolo del layout per mostrare immediatamente il nuovo elemento.
     *
     * @param todoPanel Il pannello grafico del ToDo da aggiungere.
     */
    public void aggiungiToDo(ToDoPanel todoPanel) {
        todoListPanel.add(todoPanel);
        currentToDoPanels.add(todoPanel);
        todoListPanel.revalidate();
        todoListPanel.repaint();
    }

    /**
     * Rimuove tutti i ToDo visualizzati nel pannello.
     * Utilizzato prima di ricaricare i dati dal database (refresh).
     */
    public void clearToDos() {
        todoListPanel.removeAll();
        currentToDoPanels.clear();
        todoListPanel.revalidate();
        todoListPanel.repaint();
    }

    /**
     * Restituisce la lista dei pannelli ToDo attualmente presenti nella bacheca.
     * @return Una lista di {@link ToDoPanel}.
     */
    public List<ToDoPanel> getCurrentToDoPanels() {

        return currentToDoPanels;
    }

    /**
     * Restituisce il pulsante per la modifica della descrizione.
     * Utilizzato dal Controller per aggiungere l'ActionListener.
     * @return Il JButton di modifica.
     */
    public JButton getModifyDescButton() {
        return modifyDescButton;
    }

    /**
     * Aggiorna il testo della descrizione visualizzata nell'header.
     * Da chiamare dopo che la descrizione è stata modificata nel modello.
     */
    public void updateDescrizioneLabel() {
        this.descrizioneLabel.setText(this.bacheca.getDescrizione());
    }
}
