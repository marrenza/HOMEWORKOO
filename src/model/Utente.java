package model;

public class Utente {
    private int id;
    private String nome;
    private String login;
    private String password;

    public Utente(int id, String nome, String login, String password) {
        this.id = id;
        this.nome = nome;
        this.login = login;
        this.password = password;
    }
}


//public e private sono modificatori di visibilità
//il cazzolino che si chiama public Utente è un costruttire
//this.cazzo significa che cazzo prende il valore del parametro cazzo
//Il costruttore serve letteralmente a COSTRUIRE un oggetto