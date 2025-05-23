package gui;

import javax.swing.*;
import java.awt.*;


public class ToDoPanel extends JPanel {
    public ToDoPanel(String titolo, boolean completato, boolean scaduto) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        Color bgColor;
        if(completato) {
            bgColor = new Color(169, 162, 243);
        } else if  (scaduto) {
            bgColor = new Color(255, 200, 200);
        } else {
            bgColor = Color.WHITE;
        }

        setBackground(bgColor);

        JLabel title = new JLabel(titolo);
        title.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        title.setForeground(Color.BLACK);

        add(title, BorderLayout.CENTER);
    }
}
