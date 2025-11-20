package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta un'attività personale (ToDo) all'interno del sistema.
 * Ogni ToDo è caratterizzato da un titolo, una descrizione, una data di scadenza,
 * un'immagine e un collegamento URL. I ToDo possono essere organizzati in bacheche
 * e possono contenere una checklist di sotto-attività.
 * @author marrenza
 * @version 1.0
 */
public class ToDo {
    /** Identificativo univoco del ToDo nel database. */
    private int id;

    /** Titolo dell'attività. */
    private String titolo;

    /** Descrizione dettagliata dell'attività. */
    private String descrizione;

    /** Data di scadenza prevista per il completamento. */
    private LocalDate scadenza;

    /** Percorso locale del file immagine associato al ToDo. */
    private String imaginePath;

    /** Link (URL) a una risorsa esterna correlata. */
    private String URL;

    /** Codice esadecimale del colore di sfondo per la visualizzazione grafica. */
    private String coloreSfondo;

    /** Stato attuale del ToDo (Completato o Non Completato). */
    private StatoToDo stato;

    /** Posizione ordinale del ToDo all'interno della lista della bacheca. */
    private int posizione;

    /** Lista di sotto-attività (Checklist) associata al ToDo. */
    private Checklist checklist;

    /** La bacheca a cui appartiene questo ToDo. */
    private Bacheca bacheca;

    /** L'utente che ha creato il ToDo. */
    private Utente autore;

    /** ID dell'autore (utile per il database). */
    private int idAutore;

    /** ID della bacheca di appartenenza (utile per il database). */
    private int idBacheca;

    /** Lista degli utenti con cui è condiviso il ToDo. */
    private List<Condivisione> condivisioni;

    /**
     * Costruisce un nuovo oggetto ToDo con i dettagli specificati.
     * Lo stato iniziale viene impostato di default a {@code NON_COMPLETATO}.
     *
     * @param id           L'ID univoco del ToDo.
     * @param titolo       Il titolo dell'attività.
     * @param descrizione  La descrizione dell'attività.
     * @param scadenza     La data di scadenza.
     * @param imaginePath  Il percorso dell'immagine associata.
     * @param URL          Un URL correlato.
     * @param coloreSfondo Il colore di sfondo preferito.
     * @param posizione    La posizione nella lista.
     * @param autore       L'utente autore del ToDo.
     */
    public ToDo(int id, String titolo, String descrizione, LocalDate scadenza,
                String imaginePath, String URL, String coloreSfondo, int posizione, Utente autore) {
        this.id = id;
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.scadenza = scadenza;
        this.imaginePath = imaginePath;
        this.URL = URL;
        this.coloreSfondo = coloreSfondo;
        this.posizione = posizione;
        this.stato = StatoToDo.NON_COMPLETATO;
        this.autore = autore;
        if (autore != null) {
            this.idAutore = autore.getId();
        }
    }

    /**
     * Restituisce l'ID del ToDo.
     * @return L'identificativo intero.
     */
    public int getId() {
        return id;
    }

    /**
     * Imposta l'ID del ToDo.
     * @param id Il nuovo identificativo.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Restituisce il titolo del ToDo.
     * @return Una stringa rappresentante il titolo.
     */
    public String getTitolo() {
        return titolo;
    }

    /**
     * Imposta il titolo del ToDo.
     * @param titolo Il nuovo titolo.
     */
    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    /**
     * Restituisce la descrizione del ToDo.
     * @return La descrizione testuale.
     */
    public String getDescrizione() {
        return descrizione;
    }

    /**
     * Imposta la descrizione del ToDo.
     * @param descrizione La nuova descrizione.
     */
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    /**
     * Restituisce la data di scadenza del ToDo.
     * @return Un oggetto {@code LocalDate} o null se non impostata.
     */
    public LocalDate getScadenza() {
        return scadenza;
    }

    /**
     * Imposta la data di scadenza.
     * @param scadenza La nuova data di scadenza.
     */
    public void setScadenza(LocalDate scadenza) {
        this.scadenza = scadenza;
    }

    /**
     * Restituisce il percorso dell'immagine associata.
     * @return Il path del file immagine.
     */
    public String getImaginePath() {
        return imaginePath;
    }

    /**
     * Imposta il percorso dell'immagine.
     * @param imaginePath Il percorso del file.
     */
    public void setImaginePath(String imaginePath) {
        this.imaginePath = imaginePath;
    }

    /**
     * Restituisce l'URL associato al ToDo.
     * @return L'indirizzo URL come stringa.
     */
    public String getURL() {
        return URL;
    }

    /**
     * Imposta l'URL associato.
     * @param URL Il nuovo URL.
     */
    public void setURL(String URL) {
        this.URL = URL;
    }

    /**
     * Restituisce il colore di sfondo del ToDo.
     * @return Il codice esadecimale del colore (es. "#FFFFFF").
     */
    public String getColoreSfondo() {
        return coloreSfondo;
    }

    /**
     * Imposta il colore di sfondo.
     * @param coloreSfondo Il codice esadecimale del colore.
     */
    public void setColoreSfondo(String coloreSfondo) {
        this.coloreSfondo = coloreSfondo;
    }

    /**
     * Restituisce la posizione del ToDo nella lista.
     * @return Un intero che indica l'ordine di visualizzazione.
     */
    public int getPosizione() {
        return posizione;
    }

    /**
     * Imposta la posizione del ToDo.
     * @param posizione La nuova posizione.
     */
    public void setPosizione(int posizione) {
        this.posizione = posizione;
    }

    /**
     * Restituisce la Checklist associata al ToDo.
     * @return L'oggetto Checklist contenente le sotto-attività.
     */
    public Checklist getChecklist() {
        return checklist;
    }

    /**
     * Assegna una Checklist al ToDo.
     * @param checklist La checklist da associare.
     */
    public void setChecklist(Checklist checklist) {
        this.checklist = checklist;
    }

    /**
     * Restituisce la bacheca a cui appartiene il ToDo.
     * @return L'oggetto Bacheca.
     */
    public Bacheca getBacheca() {
        return bacheca;
    }

    /**
     * Imposta la bacheca di appartenenza e aggiorna l'ID bacheca corrispondente.
     * @param bacheca La bacheca in cui inserire il ToDo.
     */
    public void setBacheca(Bacheca bacheca) {
        this.bacheca = bacheca;
        if (bacheca != null) {
            this.idBacheca = bacheca.getId();
        }
    }

    /**
     * Restituisce la lista delle condivisioni attive per questo ToDo.
     * @return Una lista di oggetti {@code Condivisione}.
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
     * Restituisce lo stato corrente del ToDo.
     * @return {@code StatoToDo.COMPLETATO} o {@code StatoToDo.NON_COMPLETATO}.
     */
    public StatoToDo getStato() {
        return stato;
    }

    /**
     * Imposta lo stato del ToDo.
     * @param stato Il nuovo stato.
     */
    public void setStato(StatoToDo stato) {
        this.stato = stato;
    }

    /**
     * Restituisce l'autore del ToDo.
     * @return L'oggetto Utente che ha creato il ToDo.
     */
    public Utente getAutore() {
        return autore;
    }

    /**
     * Imposta l'autore del ToDo e aggiorna l'ID autore corrispondente.
     * @param autore L'utente autore.
     */
    public void setAutore(Utente autore) {
        this.autore = autore;
        if (autore != null) {
            this.idAutore = autore.getId();
        }
    }

    /**
     * Restituisce l'ID dell'autore del ToDo.
     * @return L'ID dell'autore.
     */
    public int getIdAutore() {
        return idAutore;
    }

    /**
     * Imposta manualmente l'ID dell'autore del ToDo.
     * @param idAutore Il nuovo ID dell'autore.
     */
    public void setIdAutore(int idAutore) {
        this.idAutore = idAutore;
    }

    /**
     * Restituisce l'ID della bacheca a cui appartiene il ToDo.
     * @return L'ID della bacheca.
     */
    public int getIdBacheca() {
        return idBacheca;
    }

    /**
     * Imposta manualmente l'ID della bacheca di appartenenza.
     * @param idBacheca Il nuovo ID della bacheca.
     */
    public void setIdBacheca(int idBacheca) {
        this.idBacheca = idBacheca;
    }

    /**
     * Aggiunge una nuova condivisione alla lista.
     * Se la lista non è inizializzata, ne crea una nuova.
     * @param condivisione L'oggetto condivisione da aggiungere.
     */
    public void aggiungiCondivisione(Condivisione condivisione) {
        if(this.condivisioni == null) {
            this.condivisioni = new ArrayList<>();
        }
        this.condivisioni.add(condivisione);
    }

    /**
     * Rimuove una condivisione dalla lista esistente.
     * @param condivisione L'oggetto condivisione da rimuovere.
     */
    public void rimuoviCondivisione(Condivisione condivisione) {
        if(this.condivisioni != null) {
            this.condivisioni.remove(condivisione);
        }
    }

    /**
     * Aggiorna automaticamente lo stato del ToDo basandosi sul completamento della Checklist.
     * Secondo i requisiti, se tutte le sotto-attività della checklist sono completate,
     * il ToDo viene automaticamente impostato come {@code COMPLETATO}.
     * Altrimenti, viene impostato come {@code NON_COMPLETATO}.
     */
    public void aggiornaStatoDaChecklist() {
        if (this.checklist != null && !this.checklist.getAttivita().isEmpty()) {

            if (this.checklist.isCompletata()) {
                this.setStato(StatoToDo.COMPLETATO);
            } else {
                this.setStato(StatoToDo.NON_COMPLETATO);
            }
        }
    }

}
