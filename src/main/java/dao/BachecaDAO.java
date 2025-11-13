package dao;

import model.Bacheca;
import java.util.List;

public interface BachecaDAO {
    void addBacheca(Bacheca bacheca);
    Bacheca getBachecaById(int id);
    List<Bacheca> getBachecaByUserId(int userId);
    void updateBacheca(Bacheca bacheca);
    void deleteBacheca(int id);

    Bacheca getBachecaByTitoloAndUtente(String titolo, int idUtente);
}
