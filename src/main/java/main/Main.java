package main;

import controller.ToDoController;
import javax.swing.SwingUtilities;

/**
 * Classe principale (Entry Point) dell'applicazione ToDo Manager.
 * Contiene il metodo {@code main} che avvia l'intero sistema.
 *
 * @author marrenza
 * @version 1.0
 */
public class Main {
    /**
     * Metodo principale che viene eseguito all'avvio del programma.
     * Utilizza {@link SwingUtilities#invokeLater(Runnable)} per garantire che
     * l'inizializzazione dell'interfaccia grafica (gestita dal {@link ToDoController})
     * avvenga all'interno dell'Event Dispatch Thread (EDT), rispettando le
     * best practices di concorrenza di Java Swing.
     *
     * @param args Argomenti da riga di comando (non utilizzati in questa applicazione).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ToDoController();
            }
        });
    }
}