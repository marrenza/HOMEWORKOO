package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta una lista di controllo (Checklist) associata a un {@link ToDo}.
 * Una checklist è composta da una serie di {@link Attivita} (sotto-task).
 * Questa classe fornisce i metodi per gestire l'aggiunta, la rimozione e
 * la verifica dello stato di completamento delle attività.
 *
 * @author marrenza
 * @version 1.0
 */
public class Checklist {
    /** La lista delle attività che compongono la checklist. */
    private List<Attivita> attivita;

    /**
     * Costruttore predefinito.
     * Inizializza una checklist vuota pronta per l'aggiunta di attività.
     */
    public Checklist() {
        this.attivita = new ArrayList<>();
    }

    /**
     * Restituisce la lista delle attività presenti nella checklist.
     * @return Una lista di oggetti {@link Attivita}.
     */
    public List<Attivita> getAttivita() {
        return attivita;
    }

    /**
     * Imposta una nuova lista di attività per la checklist.
     * @param attivita La lista di attività da impostare.
     */
    public void setAttivita(List<Attivita> attivita) {
        this.attivita = attivita;
    }

    /**
     * Aggiunge una singola attività alla checklist.
     * @param attivita L'oggetto attività da aggiungere.
     */
    public void aggiungiAttivita(Attivita attivita) {
        this.attivita.add(attivita);
    }

    /**
     * Rimuove un'attività specifica dalla checklist.
     * @param attivita L'oggetto attività da rimuovere.
     */
    public void rimuoviAttivita(Attivita attivita) {
        this.attivita.remove(attivita);
    }

    /**
     * Verifica se la checklist è interamente completata.
     * Una checklist è considerata completata se:
     * 1. Non è vuota.
     * 2. Tutte le attività contenute hanno stato {@link StatoAttivita#COMPLETATO}.
     * Questo metodo è essenziale per determinare se il ToDo genitore deve
     * essere segnato automaticamente come completato.
     *
     * @return {@code true} se tutte le attività sono completate, altrimenti {@code false}.
     */
    public boolean isCompletata() {
        if (attivita.isEmpty()) {
            return false;
        }

        for(Attivita a : attivita) {
            if(a.getStato() != StatoAttivita.COMPLETATO) {
                return false;
            }
        }
        return true;
    }
}
