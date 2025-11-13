package gui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {
    private JTextField userField;
    private JPasswordField passField;
    private JButton loginButton;
    private JButton registerButton;

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

    // Getter per controller
    public JButton getLoginButton() {
        return loginButton;
    }

    public JButton getRegisterButton() {
        return registerButton;
    }

    public JTextField getUserField() {
        return userField;
    }

    public JPasswordField getPassField() {
        return passField;
    }

}
