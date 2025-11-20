package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta un utente registrato nel sistema.
 * Ogni utente possiede credenziali di accesso univoche (login e password)
 * e gestisce un set personale di bacheche. Secondo le specifiche del progetto,
 * ogni utente può avere fino a un massimo di 3 bacheche (Università, Lavoro, Tempo Libero).
 *
 * @author marrenza
 * @version 1.0
 */
public class Utente {
    /** L'identificativo univoco dell'utente nel database. */
    private int id;

    /** Il nome completo o visualizzato dell'utente. */
    private String nome;

    /** Il nome utente usato per il login (deve essere univoco). */
    private String login;

    /** La password per l'accesso al sistema. */
    private String password;

    /** Lista delle condivisioni attive per l'utente (ToDo condivisi con altri). */
    private List<Condivisione> condivisioni;

    /**
     * Lista delle bacheche possedute dall'utente.
     * Inizializzata come lista vuota per evitare NullPointerException.
     */
    private List<Bacheca> bacheche = new ArrayList<>();

    /**
     * Costruttore completo per creare un oggetto Utente (solitamente dal database).
     *
     * @param id       L'ID univoco dell'utente.
     * @param nome     Il nome completo dell'utente.
     * @param login    Il login (username).
     * @param password La password.
     */
    public Utente(int id, String nome, String login, String password) {
        this.id = id;
        this.nome = nome;
        this.login = login;
        this.password = password;
    }

    /**
     * Costruttore per la registrazione di un nuovo utente.
     * L'ID viene inizializzato a 0 in attesa che il database assegni un valore definitivo.
     *
     * @param nome     Il nome completo dell'utente.
     * @param login    Il login (username).
     * @param password La password scelta.
     */
    public Utente(String nome, String login, String password) {
        this.id = 0;
        this.nome = nome;
        this.login = login;
        this.password = password;
    }

    /**
     * Restituisce l'ID dell'utente.
     * @return L'identificativo intero.
     */
    public int getId() {
        return id;
    }

    /**
     * Imposta l'ID dell'utente.
     * @param id Il nuovo ID.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Restituisce il nome dell'utente.
     * @return Il nome completo.
     */
    public String getNome() {
        return nome;
    }

    /**
     * Imposta il nome dell'utente.
     * @param nome Il nuovo nome.
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Restituisce il login (username) dell'utente.
     * @return La stringa di login.
     */
    public String getLogin() {
        return login;
    }

    /**
     * Imposta il login dell'utente.
     * @param login Il nuovo login.
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * Restituisce la password dell'utente.
     * @return La password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Imposta la password dell'utente.
     * @param password La nuova password.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Restituisce la lista delle bacheche associate all'utente.
     * @return Una lista di oggetti Bacheca.
     */
    public List<Bacheca> getBacheche() {
        return bacheche;
    }

    /**
     * Aggiunge una bacheca alla lista dell'utente, se non si è superato il limite massimo.
     * Secondo i requisiti, un utente può avere al massimo 3 bacheche.
     * Se il limite è raggiunto, viene stampato un errore su System.err e la bacheca non viene aggiunta.
     *
     * @param bacheca La bacheca da aggiungere.
     */
    public void aggiungiBacheca(Bacheca bacheca) {
        if(bacheche.size() < 3) {
            bacheche.add(bacheca);
            bacheca.setProprietario(this);
        } else {
            System.err.println("Un utente può avere al massimo 3 bacheche");
        }
    }

    /**
     * Restituisce la lista delle condivisioni dell'utente.
     * @return Una lista di oggetti Condivisione.
     */
    public List<Condivisione> getCondivisioni() {
        return condivisioni;
    }

    /**
     * Imposta la lista delle condivisioni.
     * @param condivisioni La nuova lista di condivisioni.
     */
    public void setCondivisioni(List<Condivisione> condivisioni) {
        this.condivisioni = condivisioni;
    }

    /**
     * Aggiunge una nuova condivisione alla lista dell'utente.
     * @param condivisione L'oggetto condivisione da aggiungere.
     */
    public void aggiungiCondivisione(Condivisione condivisione) {
        if (this.condivisioni == null) {
            this.condivisioni = new ArrayList<>();
        }
        this.condivisioni.add(condivisione);
    }

    /**
     * Rimuove una condivisione dalla lista dell'utente.
     * @param condivisione L'oggetto condivisione da rimuovere.
     */
    public void rimuoviCondivisione(Condivisione condivisione) {
        if (this.condivisioni != null) {
            this.condivisioni.remove(condivisione);
        }
    }
}

