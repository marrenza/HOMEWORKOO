package model;

import java.util.ArrayList;
import java.util.List;

public class Utente {
    private int id;
    private String nome;
    private String login;
    private String password;

    private List<Condivisione> condivisioni;

    public Utente(int id, String nome, String login, String password) {
        this.id = id;
        this.nome = nome;
        this.login = login;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private List<Bacheca> bacheche = new ArrayList<>();

    public List<Bacheca> getBacheche() {
        return bacheche;
    }

    public void aggiungiBacheca(Bacheca bacheca) {
        if(bacheche.size() < 3) {
            bacheche.add(bacheca);
        } else {
            throw new IllegalStateException("Un utente può avere massimo 3 bacheche.");
        }
    }

    public List<Condivisione> getCondivisioni() {
        return condivisioni;
    }

    public void setCondivisioni(List<Condivisione> condivisioni) {
        this.condivisioni = condivisioni;
    }
}

