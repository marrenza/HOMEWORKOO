package model;

import java.util.ArrayList;
import java.util.List;

public class Bacheca {
    private int id;
    private TitoloBacheca titolo;
    private String descrizione;
    private List<ToDo> toDoList;

    private Utente proprietario;

    private int idUtente;


    public Bacheca(int id, TitoloBacheca titolo, String descrizione, int idUtente) {
        this.id = id;
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.toDoList = new ArrayList<>();
        this.idUtente = idUtente;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TitoloBacheca getTitolo() {
        return titolo;
    }

    public void setTitolo(TitoloBacheca titolo) {
        this.titolo = titolo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public List<ToDo> getToDoList() {
        return toDoList;
    }

    public void setToDoList(List<ToDo> toDoList) {
        this.toDoList = toDoList;
    }

    public void aggiungiToDo(ToDo todo) {
        toDoList.add(todo);
        todo.setBacheca(this);
    }

    public Utente getProprietario() {
        return proprietario;
    }

    public void setProprietario(Utente proprietario) {
        this.proprietario = proprietario;
        if (proprietario != null) {
            this.idUtente = proprietario.getId();
        }
    }
    public int getIdUtente() {
        return idUtente;
    }

    public void setIdUtente(int idUtente) {
        this.idUtente = idUtente;
    }
}
