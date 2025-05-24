package gui;

import model.Utente; // Importa la classe Utente
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ShareDialog extends JDialog {
    private JList<Utente> userList;
    private DefaultListModel<Utente> userListModel;
    private JButton btnCondividi;
    private JButton btnAnnulla;

    public ShareDialog(Frame owner, String title, boolean modal, List<Utente> availableUsers) {
        super(owner, title, modal);
        setSize(350, 300);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        setResizable(false);

        initComponents(availableUsers);
        layoutComponents();
    }

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

    private void layoutComponents() {
        add(new JScrollPane(userList), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(btnCondividi);
        buttonPanel.add(btnAnnulla);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // Getter per il pulsante di condivisione
    public JButton getBtnCondividi() {
        return btnCondividi;
    }

    /**
     * Restituisce la lista degli utenti selezionati nella JList.
     * @return Una lista di oggetti Utente selezionati.
     */
    public List<Utente> getSelectedUsers() {
        List<Utente> selected = new ArrayList<>();
        for (Utente user : userList.getSelectedValuesList()) {
            selected.add(user);
        }
        return selected;
    }
}
