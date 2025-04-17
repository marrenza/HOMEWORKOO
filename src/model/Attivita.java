package model;

public class Attivita {
    private String nome;
    private StatoAttivita stato;

    //costruttore
    public Attivita(String nome, StatoAttivita stato) {
        this.nome = nome;
        this.stato = stato;
    }
}
