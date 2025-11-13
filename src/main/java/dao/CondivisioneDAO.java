package dao;
import model.Condivisione;
import model.ToDo;
import model.Utente;
import java.util.List;

public interface CondivisioneDAO {
    void addCondivisione(Condivisione condivisione);
    void deleteCondivisione(Condivisione condivisione);
    List<Condivisione> getCondivisioniByToDoId(int todoId);
    List<Condivisione> getCondivisioniByUtenteId(int utenteId);
}