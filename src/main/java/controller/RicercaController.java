package controller;

import gui.SearchDialog;
import model.ToDo;
import model.TitoloBacheca;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;

public class RicercaController {
    private ToDoController mainController;
    private SearchDialog searchDialog; // Campo per l'istanza del dialogo di ricerca

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
        LocalDate scadenzaCerca = null;
        if (!searchDialog.getTxtScadenzaSearch().getText().isEmpty()) {
            try {
                scadenzaCerca = LocalDate.parse(searchDialog.getTxtScadenzaSearch().getText());
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(searchDialog, "Formato data scadenza non valido. Usa YYYY-MM-DD.", "Errore Formato Data", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        List<ToDo> results = new ArrayList<>();
        for (model.Bacheca b : mainController.getUtenteCorrente().getBacheche()) {
            for (ToDo todo : b.getToDoList()) {
                boolean matches = true;

                if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                    if (!((todo.getTitolo() != null && todo.getTitolo().toLowerCase().contains(searchTerm.toLowerCase())) ||
                            (todo.getDescrizione() != null && todo.getDescrizione().toLowerCase().contains(searchTerm.toLowerCase())))) {
                        matches = false;
                    }
                }


                if (matches && scadenzaCerca != null) {
                    if (todo.getScadenza() == null || todo.getScadenza().isAfter(scadenzaCerca)) {
                        matches = false; //
                    }
                }

                if (matches) {
                    results.add(todo);
                }
            }
        }
        showSearchResults(results);
    }

    private void showTodayExpiringToDos() {
        LocalDate today = LocalDate.now();
        List<ToDo> expiringToday = mainController.getUtenteCorrente().getBacheche().stream()
                .flatMap(b -> b.getToDoList().stream())
                .filter(todo -> todo.getScadenza() != null && todo.getScadenza().isEqual(today))
                .collect(Collectors.toList());

        showSearchResults(expiringToday);
    }

    private void showSearchResults(List<ToDo> results) {
        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(searchDialog, "Nessun ToDo trovato con i criteri specificati.", "Risultati Ricerca", JOptionPane.INFORMATION_MESSAGE);
        } else {
            StringBuilder sb = new StringBuilder("Risultati Ricerca:\n");
            for (ToDo todo : results) {
                sb.append("- ").append(todo.getTitolo());
                if (todo.getScadenza() != null) {
                    sb.append(" (Scadenza: ").append(todo.getScadenza()).append(")");
                }
                sb.append(" [Bacheca: ").append(todo.getBacheca() != null ? todo.getBacheca().getTitolo() : "N/D").append("]");
                sb.append("\n");
            }
            JOptionPane.showMessageDialog(searchDialog, sb.toString(), "Risultati Ricerca", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
