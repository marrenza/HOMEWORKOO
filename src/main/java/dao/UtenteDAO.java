package dao;

import model.Utente;
import java.util.List;

public interface UtenteDAO {
    void addUtente(Utente utente);
    Utente getUtenteById(int id);
    Utente getUtenteByLogin(String login);
    List<Utente> getAllUtenti();
    void updateUtente(Utente utente);
    void deleteUtenteById(int id);
}
