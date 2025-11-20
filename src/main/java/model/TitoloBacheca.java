package model;

/**
 * Enumera i titoli fissi disponibili per le bacheche del sistema.
 * Secondo le specifiche del progetto, le bacheche sono limitate a tre categorie precise:
 * Università, Lavoro e Tempo Libero. Questa enum gestisce anche la rappresentazione
 * testuale di questi titoli per l'interfaccia grafica.
 *
 * @author marrenza
 * @version 1.0
 */
public enum TitoloBacheca {
    /** Bacheca dedicata alle attività universitarie (esami, studio, progetti). */
    UNIVERSITA("Università"),

    /** Bacheca dedicata agli impegni lavorativi. */
    LAVORO("Lavoro"),

    /** Bacheca dedicata agli hobby e al tempo libero. */
    TEMPO_LIBERO("Tempo Libero");

    /** Il nome formattato da visualizzare nella GUI. */
    private final String nomeVisibile;

    /**
     * Costruttore privato dell'enum.
     * @param nomeVisibile La stringa leggibile da mostrare all'utente.
     */
    TitoloBacheca(String nomeVisibile) {
        this.nomeVisibile = nomeVisibile;
    }

    /**
     * Restituisce il nome visibile del titolo (es. "Tempo Libero").
     * Questo metodo viene usato implicitamente quando l'enum viene stampato o inserito in componenti GUI.
     *
     * @return La stringa formattata del titolo.
     */
    @Override
    public String toString() {
        return nomeVisibile;
    }

    /**
     * Restituisce il nome visibile (getter esplicito).
     * @return La stringa formattata.
     */
    public String getNomeVisibile() {
        return nomeVisibile;
    }

    /**
     * Converte una stringa nel corrispondente valore enum {@code TitoloBacheca}.
     * La ricerca viene effettuata confrontando la stringa in ingresso con il nome interno dell'enum (case-insensitive).
     *
     * @param text Il testo da convertire.
     * @return L'enum corrispondente, oppure {@code null} se il testo non corrisponde a nessun titolo valido.
     */
    public static TitoloBacheca fromString(String text) {
        if (text != null) {
            for (TitoloBacheca b : TitoloBacheca.values()) {
                if (text.equalsIgnoreCase(b.nomeVisibile) || text.equalsIgnoreCase(b.name())) {
                    return b;
                }
            }
        }
        return null;
    }
}
