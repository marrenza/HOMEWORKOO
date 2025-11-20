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

/**
 * Pannello grafico che rappresenta un singolo ToDo all'interno di una bacheca.
 * Questo componente è responsabile della visualizzazione dello stato del ToDo:
 * <ul>
 * <li>Titolo (che diventa rosso se scaduto).</li>
 * <li>Checkbox di completamento (disabilitata se presente una checklist).</li>
 * <li>Menu contestuale (pulsante "...").</li>
 * <li>Colore di sfondo personalizzato.</li>
 * <li>Lista delle sotto-attività (Checklist) se presenti.</li>
 * </ul>
 *
 * @author marrenza
 * @version 1.0
 */
public class ToDoPanel extends JPanel {
    /** Etichetta per il titolo del ToDo. */
    private JLabel titleLabel;

    /** Checkbox principale per segnare il ToDo come completato. */
    private JCheckBox completatoCheckbox;

    /** Il modello dati del ToDo associato a questo pannello. */
    private ToDo toDo;

    /** Pannello destro contenente il menu e la checkbox. */
    private JPanel eastPanel;

    /** Pulsante per aprire il menu contestuale (modifica, elimina, sposta). */
    private JButton menuButton;

    /** Lista delle checkbox relative alle sotto-attività della checklist. */
    private List<JCheckBox> subTaskCheckboxes;

    /**
     * Costruisce il pannello grafico per un singolo ToDo.
     * Configura il layout verticale per ospitare eventualmente la checklist sotto il titolo.
     * Gestisce la logica di abilitazione della checkbox principale: se esiste una checklist
     * non vuota, la checkbox principale viene disabilitata per forzare l'utente a
     * completare le sotto-attività.
     *
     * @param toDo L'oggetto ToDo da visualizzare.
     */
    public ToDoPanel(ToDo toDo) {
        this.toDo = toDo;
        this.subTaskCheckboxes = new ArrayList<>();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        setBackground(Color.WHITE);

        JPanel mainInfoPanel = new JPanel(new BorderLayout(10, 0));
        mainInfoPanel.setOpaque(false);
        mainInfoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        titleLabel = new JLabel(toDo.getTitolo());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        mainInfoPanel.add(titleLabel, BorderLayout.CENTER);

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

        mainInfoPanel.add(eastPanel, BorderLayout.EAST);

        add(mainInfoPanel);

        boolean haChecklist = (toDo.getChecklist() != null && !toDo.getChecklist().getAttivita().isEmpty());
        if (haChecklist) {
            completatoCheckbox.setEnabled(false);
            completatoCheckbox.setToolTipText("Completa le sotto-attività per finire questo ToDo");
        } else {
            completatoCheckbox.setEnabled(true);
            completatoCheckbox.setToolTipText("Clicca per completare");
        }

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

                subCb.putClientProperty("ATTIVITA_OBJ", a);

                subTaskCheckboxes.add(subCb);
                subTaskPanel.add(subCb);
            }
            add(subTaskPanel);
        }
        updateColoreSfondo();
        checkAndMarkExpired();
    }

    /**
     * Applica uno stile minimalista al pulsante del menu.
     *
     * @param button Il pulsante da stilizzare.
     */
    private void styleMiniButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 10));
        button.setMargin(new Insets(2, 5, 2, 5));
        button.setFocusPainted(false);
    }

    /**
     * Verifica se il ToDo è scaduto e non completato.
     * Se la data di scadenza è precedente alla data odierna e lo stato è NON_COMPLETATO,
     * il colore del titolo viene cambiato in rosso per evidenziare l'urgenza.
     */
    public void checkAndMarkExpired() {
        if (toDo.getStato() == StatoToDo.NON_COMPLETATO &&
                toDo.getScadenza() != null &&
                toDo.getScadenza().isBefore(LocalDate.now())) {
            titleLabel.setForeground(Color.RED);
        } else {
            titleLabel.setForeground(Color.BLACK);
        }
    }

    /**
     * Applica il colore di sfondo personalizzato al pannello e ai suoi componenti figli.
     * Decodifica la stringa esadecimale salvata nel modello.
     */
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
        for (Component c : getComponents()) {
            if (c instanceof JPanel) {
                c.setBackground(bgColor);
            }
        }
    }

    /**
     * Restituisce la checkbox principale di completamento.
     * Utilizzata dal Controller per gestire l'evento di click.
     * @return Il JCheckBox principale.
     */
    public JCheckBox getCompletatoCheckbox() {
        return completatoCheckbox;
    }

    /**
     * Restituisce l'oggetto ToDo associato a questo pannello.
     * @return Il modello ToDo.
     */
    public ToDo getToDo() {
        return toDo;
    }

    /**
     * Restituisce il pulsante per aprire il menu contestuale.
     * Utilizzato dal Controller per collegare il popup menu.
     * @return Il JButton del menu ("...").
     */
    public JButton getMenuButton() {
        return menuButton;
    }

    /**
     * Restituisce la lista delle checkbox relative alle sotto-attività.
     * Utilizzata dal Controller per aggiungere i listener di completamento per ogni attività.
     * @return Una lista di JCheckBox.
     */
    public List<JCheckBox> getSubTaskCheckboxes() {
        return subTaskCheckboxes;
    }
}