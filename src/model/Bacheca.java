package model;

import java.util.ArrayList;
import java.util.List;

public class Bacheca {
    private int id;
    private TitoloBacheca titolo;
    private String descrizione;
    private List<ToDo> toDoList;

    //creiamo il costruttore

    public Bacheca(int id, TitoloBacheca titolo, String descrizione) {
        this.id = id;
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.toDoList = new ArrayList<>();


    }


}
