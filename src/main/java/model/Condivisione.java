package model;

public class Condivisione {
    private Utente utente;
    private ToDo toDo;
    private int idUtente;
    private int idToDo;

    //costruttore
    public Condivisione(Utente utente, ToDo toDo) {
        this.utente = utente;
        this.toDo = toDo;
    }

    public Condivisione(int idUtente, int idToDo) {
        this.idUtente = idUtente;
        this.idToDo = idToDo;
    }

    public Utente getUtente() {
        return utente;
    }

    public void setUtente(Utente utente) {
        this.utente = utente;
    }

    public ToDo getToDo() {
        return toDo;
    }

    public void setToDo(ToDo toDo) {
        this.toDo = toDo;
    }

    public int getIdUtente() {
        return idUtente;
    }

    public int getIdToDo() {
        return idToDo;
    }
}
