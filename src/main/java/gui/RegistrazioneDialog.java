package gui;
import javax.swing.*;
import java.awt.*;

/**
 * Finestra di dialogo modale per la registrazione di un nuovo utente.
 * Questa classe presenta un modulo per l'inserimento dei dati necessari alla creazione
 * di un nuovo account: Nome, Login (username) e Password (con campo di conferma).
 *
 * @author marrenza
 * @version 1.0
 */
public class RegistrazioneDialog extends JDialog {
    /** Campo di testo per l'inserimento del nome completo. */
    private JTextField txtNome;

    /** Campo di testo per l'inserimento del login (username). */
    private JTextField txtLogin;

    /** Campo per l'inserimento della password. */
    private JPasswordField txtPassword;

    /** Campo per la conferma della password (controllo errori di digitazione). */
    private JPasswordField txtConfermaPassword;

    /** Pulsante per confermare la registrazione. */
    private JButton btnConferma;

    /** Pulsante per annullare l'operazione e chiudere la finestra. */
    private JButton btnAnnulla;

    /**
     * Costruisce e inizializza la finestra di registrazione.
     * Configura il layout del form (GridBagLayout) e aggiunge i componenti
     * per l'input dei dati utente. Imposta anche il comportamento del pulsante "Annulla".
     *
     * @param owner Il frame proprietario della dialog (solitamente {@link LoginFrame}).
     */
    public RegistrazioneDialog(Frame owner) {
        super(owner, "Registra Nuovo Utente", true);

        setSize(400, 300);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));
        setResizable(false);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtNome = new JTextField(20);
        txtLogin = new JTextField(20);
        txtPassword = new JPasswordField(20);
        txtConfermaPassword = new JPasswordField(20);

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; formPanel.add(txtNome, gbc);
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Login (username):"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; formPanel.add(txtLogin, gbc);
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; formPanel.add(txtPassword, gbc);
        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(new JLabel("Conferma Password:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; formPanel.add(txtConfermaPassword, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnConferma = new JButton("Conferma");
        btnAnnulla = new JButton("Annulla");
        buttonPanel.add(btnAnnulla);
        buttonPanel.add(btnConferma);

        btnAnnulla.addActionListener(e -> dispose());

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Restituisce il nome inserito nel campo di testo.
     * @return La stringa del nome.
     */
    public String getNome() {
        return txtNome.getText();
    }

    /**
     * Restituisce il login inserito nel campo di testo.
     * @return La stringa del login.
     */
    public String getLogin() {
        return txtLogin.getText();
    }

    /**
     * Restituisce la password inserita.
     * @return La password come stringa.
     */
    public String getPassword() {
        return new String(txtPassword.getPassword());
    }

    /**
     * Restituisce la conferma della password inserita.
     * @return La password di conferma come stringa.
     */
    public String getConfermaPassword() {
        return new String(txtConfermaPassword.getPassword());
    }

    /**
     * Restituisce il pulsante di conferma.
     * Utilizzato dal Controller per aggiungere l'ActionListener che gestisce il salvataggio.
     * @return Il JButton "Conferma".
     */
    public JButton getBtnConferma() {
        return btnConferma; }
}
