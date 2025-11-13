package dao;

import model.ToDo;

import java.time.LocalDate;
import java.util.List;

public interface ToDoDAO {
    void addToDo(ToDo todo);
    ToDo getToDoById(int id);
    List<ToDo> getAllToDo();
    void updateToDo(ToDo todo);
    void deleteToDo(int id);
    List<ToDo> findToDosByTerm(String searchTerm, int userId);
    List<ToDo> findToDosByScadenza(LocalDate date, int userId);
    List<ToDo> findToDosScadenzaOggi(int userId);
    List<ToDo> getToDosForBachecaAndUtente(int bachecaId,  int utenteId);
    void markAllToDoAsCompletedByBachecaId(int bachecaId);
}
