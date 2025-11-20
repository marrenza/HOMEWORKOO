package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta una bacheca virtuale all'interno del sistema.
 * Una bacheca funge da contenitore per una lista di {@link ToDo}.
 * Ogni bacheca è caratterizzata da un titolo specifico (definito dall'enum {@link TitoloBacheca}),
 * una descrizione e appartiene a un determinato {@link Utente}.
 * @author marrenza
 * @version 1.0
 */
public class Bacheca {
    /** L'identificativo univoco della bacheca nel database. */
    private int id;

    /** Il titolo o la categoria della bacheca (es. Università, Lavoro). */
    private TitoloBacheca titolo;

    /** Una breve descrizione dello scopo della bacheca. */
    private String descrizione;

    /** La lista dei ToDo contenuti in questa bacheca. */
    private List<ToDo> toDoList;

    /** L'oggetto Utente che possiede questa bacheca. */
    private Utente proprietario;

    /** L'ID dell'utente proprietario (utile per la persistenza nel database). */
    private int idUtente;

    /**
     * Costruisce una nuova bacheca con le informazioni specificate.
     * Inizializza automaticamente la lista dei ToDo come una lista vuota.
     *
     * @param id          L'ID univoco della bacheca.
     * @param titolo      Il titolo della bacheca (da {@link TitoloBacheca}).
     * @param descrizione La descrizione della bacheca.
     * @param idUtente    L'ID dell'utente proprietario.
     */
    public Bacheca(int id, TitoloBacheca titolo, String descrizione, int idUtente) {
        this.id = id;
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.toDoList = new ArrayList<>();
        this.idUtente = idUtente;
    }

    /**
     * Restituisce l'identificativo della bacheca.
     * @return L'ID intero.
     */
    public int getId() {
        return id;
    }

    /**
     * Imposta l'identificativo della bacheca.
     * @param id Il nuovo ID.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Restituisce il titolo della bacheca.
     * @return Il valore dell'enum {@link TitoloBacheca}.
     */
    public TitoloBacheca getTitolo() {
        return titolo;
    }

    /**
     * Imposta il titolo della bacheca.
     * @param titolo Il nuovo titolo.
     */
    public void setTitolo(TitoloBacheca titolo) {
        this.titolo = titolo;
    }

    /**
     * Restituisce la descrizione della bacheca.
     * @return La descrizione testuale.
     */
    public String getDescrizione() {
        return descrizione;
    }

    /**
     * Imposta la descrizione della bacheca.
     * @param descrizione La nuova descrizione.
     */
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    /**
     * Restituisce la lista completa dei ToDo presenti in questa bacheca.
     * @return Una lista di oggetti {@link ToDo}.
     */
    public List<ToDo> getToDoList() {
        return toDoList;
    }

    /**
     * Imposta la lista dei ToDo della bacheca.
     * @param toDoList La nuova lista di ToDo.
     */
    public void setToDoList(List<ToDo> toDoList) {
        this.toDoList = toDoList;
    }

    /**
     * Aggiunge un nuovo ToDo alla lista della bacheca.
     * Questo metodo stabilisce anche la relazione inversa, impostando questa bacheca
     * come riferimento all'interno dell'oggetto ToDo passato come parametro.
     *
     * @param todo L'oggetto ToDo da aggiungere.
     */
    public void aggiungiToDo(ToDo todo) {
        toDoList.add(todo);
        todo.setBacheca(this);
    }

    /**
     * Restituisce l'utente proprietario della bacheca.
     * @return L'oggetto Utente proprietario.
     */
    public Utente getProprietario() {
        return proprietario;
    }

    /**
     * Imposta l'utente proprietario della bacheca.
     * Aggiorna automaticamente anche il campo {@code idUtente} corrispondente.
     *
     * @param proprietario Il nuovo proprietario.
     */
    public void setProprietario(Utente proprietario) {
        this.proprietario = proprietario;
        if (proprietario != null) {
            this.idUtente = proprietario.getId();
        }
    }

    /**
     * Restituisce l'ID dell'utente proprietario.
     * @return L'ID intero dell'utente.
     */
    public int getIdUtente() {
        return idUtente;
    }

    /**
     * Imposta manualmente l'ID dell'utente proprietario.
     * @param idUtente Il nuovo ID utente.
     */
    public void setIdUtente(int idUtente) {
        this.idUtente = idUtente;
    }
}
