package gui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Finestra iniziale per l'autenticazione dell'utente nel sistema ToDo Manager.
 * Questa classe estende {@link JFrame} e fornisce l'interfaccia grafica per il login.
 * Contiene i campi per l'inserimento di username e password e i pulsanti per
 * confermare l'accesso o navigare verso la registrazione di un nuovo account.
 *
 * @author marrenza
 * @version 1.0
 */
public class LoginFrame extends JFrame {
    /** Campo di testo per l'inserimento dello username (login). */
    private JTextField userField;

    /** Campo di testo mascherato per l'inserimento sicuro della password. */
    private JPasswordField passField;

    /** Pulsante per avviare la procedura di login. */
    private JButton loginButton;

    /** Pulsante per aprire la finestra di registrazione di un nuovo utente. */
    private JButton registerButton;

    /**
     * Costruttore che inizializza e configura la finestra di login.
     * Imposta le dimensioni, il layout (GridBagLayout), i colori e lo stile dei componenti.
     * Posiziona le etichette, i campi di input e i pulsanti all'interno della finestra.
     */
    public LoginFrame() {
        setTitle("Login - ToDo Manager");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.WHITE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        JLabel welcomeLabel = new JLabel("Benvenuto!");
        welcomeLabel.setForeground(new Color(6, 0, 64));
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));

        JLabel userLabel = new JLabel("Username:");
        JLabel passLabel = new JLabel("Password:");
        userField = new JTextField(15);
        passField = new JPasswordField(15);
        loginButton = new JButton("Login");
        registerButton = new JButton("Registrati");

        userLabel.setForeground(Color.BLACK);
        passLabel.setForeground(Color.BLACK);
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);
        Color buttonBg = new Color(83, 69, 230);
        Color buttonFg = Color.WHITE;

        loginButton.setBackground(buttonBg);
        loginButton.setForeground(buttonFg);
        loginButton.setFont(buttonFont);
        loginButton.setFocusPainted(false);

        registerButton.setBackground(buttonBg);
        registerButton.setForeground(buttonFg);
        registerButton.setFont(buttonFont);
        registerButton.setFocusPainted(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(welcomeLabel, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_END;

        gbc.gridx = 0; gbc.gridy = 1; add(userLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.anchor = GridBagConstraints.LINE_START; add(userField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.LINE_END; add(passLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.anchor = GridBagConstraints.LINE_START; add(passField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(registerButton);
        buttonPanel.add(loginButton);

        gbc.gridx = 1; gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.LINE_END;
        add(buttonPanel, gbc);
    }

    /**
     * Restituisce il pulsante di login.
     * Utilizzato dal controller per aggiungere l'ActionListener che gestisce l'autenticazione.
     * @return Il JButton "Login".
     */
    public JButton getLoginButton() {
        return loginButton;
    }

    /**
     * Restituisce il pulsante di registrazione.
     * Utilizzato dal controller per aggiungere l'ActionListener che apre il dialog di registrazione.
     * @return Il JButton "Registrati".
     */
    public JButton getRegisterButton() {
        return registerButton;
    }

    /**
     * Restituisce il campo di testo dove l'utente inserisce lo username.
     * @return Il JTextField per lo username.
     */
    public JTextField getUserField() {
        return userField;
    }

    /**
     * Restituisce il campo password.
     * @return Il JPasswordField contenente la password mascherata.
     */
    public JPasswordField getPassField() {
        return passField;
    }

}
