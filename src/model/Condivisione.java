package model;

public class Condivisione {
    private Utente utente;
    private ToDo toDo;

    //costruttore
    public Condivisione(Utente utente, ToDo toDo) {
        this.utente = utente;
        this.toDo = toDo;
    }
}
