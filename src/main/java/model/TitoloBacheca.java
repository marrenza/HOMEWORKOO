package model;

public enum TitoloBacheca {
    UNIVERSITA("Università"),
    LAVORO("Lavoro"),
    TEMPO_LIBERO("Tempo Libero");

    private final String nomeVisibile;
    TitoloBacheca(String nomeVisibile) {
        this.nomeVisibile = nomeVisibile;
    }

    @Override
    public String toString() {
        return nomeVisibile;
    }

    public String getNomeVisibile() {
        return nomeVisibile;
    }

    public static TitoloBacheca fromString(String text) {
        if (text != null) {
            for (TitoloBacheca b : TitoloBacheca.values()) {
                if (text.equalsIgnoreCase(b.name())) {
                    return b;
                }
            }
        }
        return null;
    }
}
