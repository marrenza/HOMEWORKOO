package model;

import java.util.ArrayList;
import java.util.List;

public class Checklist {
    private List<Attivita> attivita;

    public Checklist() {
        this.attivita = new ArrayList<>();
    }

    public List<Attivita> getAttivita() {
        return attivita;
    }

    public void setAttivita(List<Attivita> attivita) {
        this.attivita = attivita;
    }

    public void aggiungiAttivita(Attivita attivita) {
        this.attivita.add(attivita);
    }

    public void rimuoviAttivita(Attivita attivita) {
        this.attivita.remove(attivita);
    }

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
