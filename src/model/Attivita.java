package model;

public class Attivita {
    private String nome;
    private StatoAttivita stato;

    //costruttore
    public Attivita(String nome, StatoAttivita stato) {
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
}
