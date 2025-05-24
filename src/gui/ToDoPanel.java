package gui;

import model.ToDo;
import model.StatoToDo;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;


public class ToDoPanel extends JPanel {
    private JLabel titleLabel;
    private JCheckBox completatoCheckbox;
    private ToDo toDo;

    public ToDoPanel(ToDo toDo) {
        this.toDo = toDo;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        setBackground(Color.WHITE);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        titleLabel = new JLabel(toDo.getTitolo());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        completatoCheckbox = new JCheckBox("Completato");
        completatoCheckbox.setSelected(toDo.getStato() == StatoToDo.COMPLETATO);
        completatoCheckbox.setBackground(Color.WHITE);
        completatoCheckbox.setFocusPainted(false);

        add(titleLabel, BorderLayout.CENTER);
        add(completatoCheckbox, BorderLayout.EAST);

        if(toDo.getColoreSfondo() != null && !toDo.getColoreSfondo().isEmpty()) {
            try {
                Color bgColor = Color.decode(toDo.getColoreSfondo());
                setBackground(bgColor);
                completatoCheckbox.setBackground(bgColor);
            } catch (NumberFormatException ex) {
                System.err.println("Formato colore non valido per ToDo " + toDo.getId() + ": " + toDo.getColoreSfondo());
                setBackground(Color.WHITE);
                completatoCheckbox.setBackground(Color.WHITE);
            }
        }
        checkAndMarkExpired();
    }

    public void checkAndMarkExpired() {
        if(toDo.getScadenza() != null && toDo.getScadenza().isBefore(LocalDate.now())) {
            titleLabel.setForeground(Color.RED);
        } else {
            titleLabel.setForeground(Color.BLACK);
        }
    }

    public void setBackgroundColor(Color color) {
        setBackground(color);
        completatoCheckbox.setBackground(color);
    }

    public JCheckBox getCompletatoCheckbox() {
        return completatoCheckbox;
    }

    public ToDo getToDo() {
        return toDo;
    }
}
