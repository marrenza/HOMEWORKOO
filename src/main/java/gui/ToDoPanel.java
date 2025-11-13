package gui;

import model.StatoAttivita;
import model.ToDo;
import model.StatoToDo;
import model.Attivita; // <-- IMPORTA Attivita
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList; // <-- IMPORTA ArrayList
import java.util.List; // <-- IMPORTA List

public class ToDoPanel extends JPanel {
    private JLabel titleLabel;
    private JCheckBox completatoCheckbox;
    private ToDo toDo;
    private JPanel eastPanel;
    private JButton menuButton;

    // ▼▼ 1. AGGIUNTA VARIABILE per le sotto-attività ▼▼
    private List<JCheckBox> subTaskCheckboxes;

    public ToDoPanel(ToDo toDo) {
        this.toDo = toDo;
        this.subTaskCheckboxes = new ArrayList<>();

        // ▼▼ 2. MODIFICA LAYOUT PRINCIPALE ▼▼
        // Da BorderLayout a BoxLayout verticale, per impilare
        // il ToDo e la sua checklist
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        setBackground(Color.WHITE);
        // ▼▼ 3. RIMOZIONE ALTEZZA FISSA ▼▼
        // Rimuoviamo setMaximumSize, l'altezza ora è dinamica
        // setMaximumSize(new Dimension(Integer.MAX_VALUE, 70)); // <-- RIMOSSO

        // --- 4. CREAZIONE PANNELLO INFO (La riga del ToDo) ---
        // Creiamo un pannello interno per contenere Titolo, Menu e Checkbox
        JPanel mainInfoPanel = new JPanel(new BorderLayout(10, 0));
        mainInfoPanel.setOpaque(false); // Sfondo gestito dal pannello genitore
        // Diamo un'altezza fissa a *questa* riga
        mainInfoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        titleLabel = new JLabel(toDo.getTitolo());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        mainInfoPanel.add(titleLabel, BorderLayout.CENTER); // Aggiunto a mainInfoPanel

        eastPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        eastPanel.setOpaque(false);

        menuButton = new JButton("...");
        styleMiniButton(menuButton);
        eastPanel.add(menuButton);

        completatoCheckbox = new JCheckBox();
        completatoCheckbox.setSelected(toDo.getStato() == StatoToDo.COMPLETATO);
        completatoCheckbox.setBackground(Color.WHITE);
        completatoCheckbox.setFocusPainted(false);
        eastPanel.add(completatoCheckbox);

        mainInfoPanel.add(eastPanel, BorderLayout.EAST); // Aggiunto a mainInfoPanel

        // Aggiungiamo il pannello info al pannello principale (verticale)
        add(mainInfoPanel);
        // ---------------------------------------------------------

        // --- 5. LOGICA ABILITAZIONE CHECKBOX PRINCIPALE ---
        boolean haChecklist = (toDo.getChecklist() != null && !toDo.getChecklist().getAttivita().isEmpty());
        if (haChecklist) {
            completatoCheckbox.setEnabled(false); // Disabilita se ci sono sotto-attività
            completatoCheckbox.setToolTipText("Completa le sotto-attività per finire questo ToDo");
        } else {
            completatoCheckbox.setEnabled(true);
            completatoCheckbox.setToolTipText("Clicca per completare");
        }
        // ----------------------------------------------------

        // --- 6. CREAZIONE PANNELLO SOTTO-ATTIVITÀ (Checklist) ---
        if (haChecklist) {
            JPanel subTaskPanel = new JPanel();
            subTaskPanel.setLayout(new BoxLayout(subTaskPanel, BoxLayout.Y_AXIS));
            subTaskPanel.setOpaque(false);
            subTaskPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 5)); // Indenta le attività

            for (Attivita a : toDo.getChecklist().getAttivita()) {
                JCheckBox subCb = new JCheckBox(a.getNome());
                subCb.setSelected(a.getStato() == StatoAttivita.COMPLETATO);
                subCb.setOpaque(false);
                subCb.setFont(new Font("Segoe UI", Font.PLAIN, 12));

                // Mettiamo l'oggetto Attivita nel JCheckBox per recuperarlo nel controller
                subCb.putClientProperty("ATTIVITA_OBJ", a);

                subTaskCheckboxes.add(subCb); // Aggiungi alla lista
                subTaskPanel.add(subCb);
            }
            add(subTaskPanel); // Aggiunge il pannello delle sotto-attività
        }
        // -----------------------------------------------------------

        updateColoreSfondo();
        checkAndMarkExpired();
    }

    private void styleMiniButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 10));
        // Modificato Margin per il tuo stile
        button.setMargin(new Insets(2, 5, 2, 5));
        button.setFocusPainted(false);
    }

    // Logica Colore Rosso (già corretta nel tuo file)
    public void checkAndMarkExpired() {
        if (toDo.getStato() == StatoToDo.NON_COMPLETATO &&
                toDo.getScadenza() != null &&
                toDo.getScadenza().isBefore(LocalDate.now())) {
            titleLabel.setForeground(Color.RED);
        } else {
            titleLabel.setForeground(Color.BLACK);
        }
    }

    // ▼▼ 7. MODIFICA LOGICA SFONDO ▼▼
    // Deve colorare anche i nuovi pannelli interni
    private void updateColoreSfondo() {
        Color bgColor = Color.WHITE;
        if(toDo.getColoreSfondo() != null && !toDo.getColoreSfondo().isEmpty()) {
            try {
                bgColor = Color.decode(toDo.getColoreSfondo());
            } catch (NumberFormatException ex) {
                System.err.println("Formato colore non valido per ToDo " + toDo.getId() + ": " + toDo.getColoreSfondo());
            }
        }
        setBackground(bgColor);
        completatoCheckbox.setBackground(bgColor);
        if (eastPanel != null) {
            eastPanel.setBackground(bgColor);
        }
        // Applica lo sfondo anche ai figli (mainInfoPanel e subTaskPanel)
        for (Component c : getComponents()) {
            if (c instanceof JPanel) {
                // Imposta ricorsivamente lo sfondo (se vuoi che anche le sub-checkbox l'abbiano)
                c.setBackground(bgColor);
            }
        }
    }
    // ------------------------------------

    // --- Getters ---
    public JCheckBox getCompletatoCheckbox() {
        return completatoCheckbox;
    }
    public ToDo getToDo() {
        return toDo;
    }
    public JButton getMenuButton() {
        return menuButton;
    }

    // ▼▼ 8. AGGIUNTA GETTER PER SOTTO-ATTIVITÀ ▼▼
    // Serve al Controller per aggiungere i listener
    public List<JCheckBox> getSubTaskCheckboxes() {
        return subTaskCheckboxes;
    }
    // ------------------------------------------

    /* (I vecchi getter commentati sono giustamente rimossi) */
}