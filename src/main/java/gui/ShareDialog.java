package gui;

import model.Utente; // Importa la classe Utente
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Finestra di dialogo per la condivisione di un ToDo con altri utenti.
 * Presenta una lista di utenti disponibili (escluso l'autore stesso) e permette
 * la selezione multipla. Utilizza un {@link ListCellRenderer} personalizzato
 * per visualizzare le informazioni dell'utente (Nome e Login) in modo leggibile
 * all'interno della lista grafica.
 *
 * @author marrenza
 * @version 1.0
 */
public class ShareDialog extends JDialog {
    /** Componente grafico che visualizza la lista degli utenti. */
    private JList<Utente> userList;

    /** Modello dei dati sottostante alla JList. */
    private DefaultListModel<Utente> userListModel;

    /** Pulsante per confermare la condivisione con gli utenti selezionati. */
    private JButton btnCondividi;

    /** Pulsante per annullare l'operazione e chiudere la finestra. */
    private JButton btnAnnulla;

    /**
     * Costruisce e inizializza la finestra di condivisione.
     *
     * @param owner          Il frame proprietario della dialog.
     * @param title          Il titolo della finestra.
     * @param modal          Se {@code true}, la finestra blocca l'interazione con le altre finestre.
     * @param availableUsers La lista degli utenti con cui è possibile condividere il ToDo (già filtrata dal controller).
     */
    public ShareDialog(Frame owner, String title, boolean modal, List<Utente> availableUsers) {
        super(owner, title, modal);
        setSize(350, 300);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        setResizable(false);

        initComponents(availableUsers);
        layoutComponents();
    }

    /**
     * Inizializza i componenti grafici e il modello della lista.
     * Configura il {@link ListCellRenderer} per formattare le stringhe visualizzate
     * come "Nome (Login)". Abilita la selezione multipla.
     *
     * @param availableUsers La lista di utenti da inserire nel modello.
     */
    private void initComponents(List<Utente> availableUsers) {
        userListModel = new DefaultListModel<>();
        for (Utente user : availableUsers) {
            userListModel.addElement(user);
        }
        userList = new JList<>(userListModel);
        userList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION); // Permette selezioni multiple
        userList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Utente) {
                    setText(((Utente) value).getNome() + " (" + ((Utente) value).getLogin() + ")");
                }
                return this;
            }
        });


        btnCondividi = new JButton("Condividi");
        btnAnnulla = new JButton("Annulla");

        btnAnnulla.addActionListener(e -> dispose());
    }

    /**
     * Dispone i componenti nel layout della finestra.
     * Posiziona la lista scrollabile al centro e i pulsanti in basso a destra.
     */
    private void layoutComponents() {
        add(new JScrollPane(userList), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(btnCondividi);
        buttonPanel.add(btnAnnulla);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Restituisce il pulsante di conferma condivisione.
     * Utilizzato dal Controller per aggiungere l'ActionListener.
     * @return Il JButton "Condividi".
     */
    public JButton getBtnCondividi() {
        return btnCondividi;
    }

    /**
     * Restituisce la lista degli utenti che sono stati selezionati dall'interfaccia.
     * Converte la selezione grafica in una lista di oggetti del modello.
     *
     * @return Una lista di oggetti {@link Utente} selezionati.
     */
    public List<Utente> getSelectedUsers() {
        List<Utente> selected = new ArrayList<>();
        for (Utente user : userList.getSelectedValuesList()) {
            selected.add(user);
        }
        return selected;
    }
}
