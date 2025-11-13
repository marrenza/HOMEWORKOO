package dao;

import model.Attivita;
import java.util.List;

public interface AttivitaDAO {
    void addAttivita(Attivita attivita);
    List<Attivita> getAttivitaByToDoId(int todoId);
    void updateAttivita(Attivita attivita);
    void deleteAttivita(int attivitaId);
    void deleteAttivitaByToDoId(int todoId);
}
