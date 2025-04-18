package model;

public class Condivisione {
    private Utente utente;
    private ToDo toDo;

    //costruttore
    public Condivisione(Utente utente, ToDo toDo) {
        this.utente = utente;
        this.toDo = toDo;
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
}
