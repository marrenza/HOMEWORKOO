package model;

public class Attivita {
    private String nome;
    private StatoAttivita stato;
    private int id;
    private int idToDo;

    //costruttore
    public Attivita(String nome) {
        this.nome = nome;
        this.stato = StatoAttivita.NON_COMPLETATO;
    }

    public Attivita(int id, int idToDo, String nome, StatoAttivita stato) {
        this.id = id;
        this.idToDo = idToDo;
        this.nome = nome;
        this.stato = stato;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public StatoAttivita getStato() {
        return stato;
    }

    public void setStato(StatoAttivita stato) {
        this.stato = stato;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdTodo() {
        return idToDo;
    }

    public void setIdTodo(int idToDo) {
        this.idToDo = idToDo;
    }
}
