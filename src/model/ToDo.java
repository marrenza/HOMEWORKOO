package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ToDo {
    private int id;
    private String titolo;
    private String descrizione;
    private LocalDate scadenza;
    private String imaginePath;
    private String URL;
    private String coloreSfondo;
    private StatoToDo stato;
    private int posizione;

    private Checklist checklist;

    private Bacheca bacheca;

    private List<Condivisione> condivisioni;

    //costruttore
    public ToDo(int id, String titolo, String descrizione, LocalDate scadenza,
                String imaginePath, String URL, String coloreSfondo, int posizione) {
        this.id = id;
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.scadenza = scadenza;
        this.imaginePath = imaginePath;
        this.URL = URL;
        this.coloreSfondo = coloreSfondo;
        this.posizione = posizione;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public LocalDate getScadenza() {
        return scadenza;
    }

    public void setScadenza(LocalDate scadenza) {
        this.scadenza = scadenza;
    }

    public String getImaginePath() {
        return imaginePath;
    }

    public void setImaginePath(String imaginePath) {
        this.imaginePath = imaginePath;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getColoreSfondo() {
        return coloreSfondo;
    }

    public void setColoreSfondo(String coloreSfondo) {
        this.coloreSfondo = coloreSfondo;
    }

    public int getPosizione() {
        return posizione;
    }

    public void setPosizione(int posizione) {
        this.posizione = posizione;
    }

    public Checklist getChecklist() {
        return checklist;
    }

    public void setChecklist(Checklist checklist) {
        this.checklist = checklist;
    }

    public Bacheca getBacheca() {
        return bacheca;
    }

    public void setBacheca(Bacheca bacheca) {
        this.bacheca = bacheca;
    }

    public List<Condivisione> getCondivisioni() {
        return condivisioni;
    }

    public void setCondivisioni(List<Condivisione> condivisioni) {
        this.condivisioni = condivisioni;
    }
}
