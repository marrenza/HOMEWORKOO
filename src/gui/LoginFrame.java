package gui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {
    private JTextField userField;
    private JPasswordField passField;
    private JButton loginButton;

    public LoginFrame() {
        setTitle("Login - ToDo Manager");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.WHITE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        JLabel userLabel = new JLabel("Username:");
        JLabel passLabel = new JLabel("Password:");
        userField = new JTextField(15);
        passField = new JPasswordField(15);
        loginButton = new JButton("Login");


        userLabel.setForeground(Color.BLACK);
        passLabel.setForeground(Color.BLACK);
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        loginButton.setBackground(new Color(83, 69, 230));
        loginButton.setForeground(Color.BLACK);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setFocusPainted(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0; gbc.gridy = 0; add(userLabel, gbc);
        gbc.gridx = 1; add(userField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; add(passLabel, gbc);
        gbc.gridx = 1; add(passField, gbc);
        gbc.gridx = 1; gbc.gridy = 2; add(loginButton, gbc);
    }

    // Getter per controller
    public JButton getLoginButton() {
        return loginButton;
    }

    public JTextField getUserField() {
        return userField;
    }

    public JPasswordField getPassField() {
        return passField;
    }

}
