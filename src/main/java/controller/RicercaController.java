package controller;

import gui.SearchDialog;
import model.Bacheca;
import model.ToDo;
import model.TitoloBacheca;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import javax.swing.*;
import java.awt.Dimension;

public class RicercaController {
    private ToDoController mainController;
    private SearchDialog searchDialog;

    public RicercaController(ToDoController mainController) {
        this.mainController = mainController;
    }


    public void openSearchDialog() {
        searchDialog = new SearchDialog(null, "Cerca ToDo", true);

        searchDialog.getBtnCerca().addActionListener(e -> performSearch());
        searchDialog.getBtnScadenzaOdierna().addActionListener(e -> showTodayExpiringToDos());

        searchDialog.setVisible(true);
    }

    private void performSearch() {
        String searchTerm = searchDialog.getTxtSearchTerm().getText();
        String scadenzaText = searchDialog.getTxtScadenzaSearch().getText();

        int currentUserId = mainController.getUtenteCorrente().getId();
        List<ToDo> results = new ArrayList<>();

        try {
            if (!scadenzaText.isEmpty()) {
                LocalDate scadenzaCerca = LocalDate.parse(scadenzaText);
                results = mainController.getToDoDAO().findToDosByScadenza(scadenzaCerca, currentUserId);
            } else if (!searchTerm.isEmpty()) {
                results = mainController.getToDoDAO().findToDosByTerm(searchTerm, currentUserId);
            } else {
                JOptionPane.showMessageDialog(searchDialog, "Inserisci un termine o una data.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            showSearchResults(results);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(searchDialog, "Formato data non valido. Usa YYYY-MM-DD.", "Errore", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(searchDialog, "Errore durante la ricerca.", "Errore DB", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void showTodayExpiringToDos() {
        int currentUserId = mainController.getUtenteCorrente().getId();
        List<ToDo> expiringToday = mainController.getToDoDAO().findToDosScadenzaOggi(currentUserId);
        showSearchResults(expiringToday);
    }

    private void showSearchResults(List<ToDo> results) {
        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(searchDialog, "Nessun ToDo trovato.", "Risultati Ricerca", JOptionPane.INFORMATION_MESSAGE);
        } else {
            StringBuilder sb = new StringBuilder("ToDo Trovati:\n");
            for (ToDo todo : results) {
                sb.append("- ").append(todo.getTitolo());
                if (todo.getScadenza() != null) {
                    sb.append(" (Scadenza: ").append(todo.getScadenza()).append(")");
                }
                Bacheca bachecaTrovata = mainController.getUtenteCorrente().getBacheche().stream()
                        .filter(b -> b.getId() == todo.getIdBacheca())
                        .findFirst().orElse(null);

                if (bachecaTrovata != null) {
                    sb.append(" [Bacheca: ").append(bachecaTrovata.getTitolo()).append("]");
                }
                sb.append("\n");
            }
            JTextArea textArea = new JTextArea(sb.toString());
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(400, 250));
            JOptionPane.showMessageDialog(searchDialog, scrollPane, "Risultati Ricerca", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
