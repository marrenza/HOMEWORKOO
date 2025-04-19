package main;

import model.*;

import java.time.LocalDate;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        //Creazione utenti
        Utente utente1 = new Utente(1, "Marianna", "marianna", "password123");
        Utente utente2 = new Utente(2, "Antonietta", "antonietta", "sicura456");

        //Creazione bacheca e assegnazione all'utente1
        Bacheca bachecaUniversita = new Bacheca(1, TitoloBacheca.UNIVERSITA, "Cose da fare per l'università");
        bachecaUniversita.setProprietario(utente1);
        utente1.aggiungiBacheca(bachecaUniversita);

        //Creazione ToDo
        ToDo esameOO = new ToDo(
                1,
                "Studiare per l'esame di Object Orientation",
                "Capitoli 5-10 + Esercizi UML",
                LocalDate.of(2025, 4, 24),
                "path/to/img.png",
                "https://informatica.dieti.unina.it/index.php/it/laurea-triennale/insegnamenti/41-laurea-triennale/insegnamenti-lt/123-object-orientation",
                "#FFFFCC",
                1
        );
        esameOO.setStato(StatoToDo.NON_COMPLETATO);

        //Aggiunta checklist con attività
        Checklist checklist = new Checklist();
        checklist.aggiungiAttivita(new Attivita("Leggere capitolo 5", StatoAttivita.NON_COMPLETATO));
        checklist.aggiungiAttivita(new Attivita("Fare esercizi UML", StatoAttivita.NON_COMPLETATO));
        esameOO.setChecklist(checklist);

        //Aggiungta del ToDo alla bacheca
        bachecaUniversita.aggiungiToDo(esameOO);

        //Condivisione con utente2
        Condivisione condivisione = new Condivisione(utente2, esameOO);
        ArrayList<Condivisione> condivisioni = new ArrayList<>();
        condivisioni.add(condivisione);
        esameOO.setCondivisioni(condivisioni);
        utente2.setCondivisioni(condivisioni);

        //Output di test
        System.out.println("Utente: " + utente1.getNome());
        System.out.println("Bacheca: " + bachecaUniversita.getTitolo());
        System.out.println("ToDo: " + esameOO.getTitolo());
        System.out.println("Checklist: ");
        for (Attivita att : esameOO.getChecklist().getAttivita()) {
            System.out.println("- " + att.getNome() + " [" + att.getStato() + "]");
        }
        System.out.println("Condiviso con: " + esameOO.getCondivisioni().get(0).getUtente().getNome());
    }
}
