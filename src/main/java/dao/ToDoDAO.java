package dao;

import model.ToDo;
import java.util.List;

public interface ToDoDAO {
    void addToDo(ToDo todo);
    ToDo getToDoById(int id);
    List<ToDo> getAllToDo();
    void updateToDo(ToDo todo);
    void deleteToDo(int id);
    List<ToDo> getToDoByBachecaId(int bachecaId);
    void markAllToDoAsCompletedByBachecaId(int bachecaId);
}
