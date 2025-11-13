package gui;
import javax.swing.*;
import java.awt.*;

public class RegistrazioneDialog extends JDialog {
    private JTextField txtNome;
    private JTextField txtLogin;
    private JPasswordField txtPassword;
    private JPasswordField txtConfermaPassword;
    private JButton btnConferma;
    private JButton btnAnnulla;

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

    public String getNome() {
        return txtNome.getText();
    }

    public String getLogin() {
        return txtLogin.getText();
    }
    public String getPassword() {
        return new String(txtPassword.getPassword());
    }
    public String getConfermaPassword() {
        return new String(txtConfermaPassword.getPassword());
    }
    public JButton getBtnConferma() {
        return btnConferma; }
}
