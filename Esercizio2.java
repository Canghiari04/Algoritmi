/* 
* Cognome: Canghiari 
* Nome: Matteo
* Email: matteo.canghiari@studio.unibo.it
* Matricola: 1032059 
* 
* Riflessione:
* All'inizio, come da codice, è posto un menu basato su un ciclo iterativo, dove rispetto al numero intero letto verranno stampate a terminale 
* le coppie che soddisfino i requisiti introdotti dall'utente.
* L'implementazione prevede l'uso di un vettore Couple, il quale memorizza oggetti coppia posti come <key --> int, value --> String>
* La scelta di utilizzare un Array è dovuta per garantire la memorizzazione di coppie duplicate, sia per chiave che per valore, dove l'uso di 
* strutture dati basate su dizionari o insiemi non avrebbero soddisfatto la richiesta. 
*
* A livello di costo computazionale è possibile stabilire un limite asintotico superiore pari a O(n), il quale è verificato, in maniera consecutiva
* nei metodi proposti:
* - getFileSize(), implementato per la lettura del numero totale di coppie contenute nel file, affinché sia stanziata la dimensione del vettore; 
* - scanFile(), scansione del file per l'aggiunta degli oggetti Couple all'interno del vettore;
* - firstStamp() e secondStamp(), dato che avviene una totale scansione sulla dimensione dell'Array.
* Quindi, rispetto alla soluzione proposta, il costo computazionale è lineare ossia O(n).
* 
* Nota: 
* Il file dato a riga di comando ha la medesima impostazione fornita dalla traccia dell'esame.
*/

import java.util.Locale;
import java.util.Scanner;
import java.io.File;

public class Esercizio2 {

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        Scanner scan = new Scanner(System.in);
        if (args.length != 1) {
            System.err.println("Usage -> java Esercizio2 Esercizio2.txt");
            System.exit(0);
        }

        /*
         * Variabili che mantengono i valori delle soglie a cui si riferiscono le due
         * stampe.
         */
        int a, b, y, s;

        /*
         * Rappresenta la variabile che permette la gestione delle stampe e della
         * conclusione dell'esecuzione.
         */
        int v;

        /*
         * Oggetto SocietyIcarus a cui è passato come parametro il nome del file da cui
         * avviene il riempimento del vettore.
         */
        SocietyIcarus example = new SocietyIcarus(args[0]);

        System.out.println(
                "Inserisci il valore del parametro v, in base all'azione voluta:\n - (v == 0) --> (termina l'esecuzione);"
                        + "\n - (v == 1) --> (ricerca e stampa di tutte le coppie con valore intero a ≤ x ≤ b e lunghezza della stringa ≤ s);"
                        + "\n - (v == 2) --> (ricerca e stampa di tutte le coppie aventi valori interi x ≥ y).");
        System.out.print("Risposta: ");
        v = scan.nextInt();

        while (true) {
            if (v == 0) {
                break;
            } else if (v == 1) {
                System.out.println("\nInserisci il valore dei parametri a, b, s (nello stesso ordine proposto)...");
                a = scan.nextInt();
                b = scan.nextInt();
                s = scan.nextInt();

                System.out.println("\nPrima stampa con " + a + " ≤ value.key ≤ " + b + " e str ≤ " + s);
                System.out.println("-------------------------------------------------");

                /*
                 * Ricerca e stampa di tutte le coppie di valori (key --> x, value --> str) che
                 * rispettino: a ≤ x ≤ b e str ≤ s.
                 */
                example.firstStamp(a, b, s);
                System.out.println("-------------------------------------------------");

                System.out.println();

                /*
                 * Visualizzazione del menu per la scelta dell'operazione successiva da
                 * effetture.
                 */
                System.out.println(
                        "Inserisci il valore del parametro v, in base all'azione voluta:\n - (v == 0) --> (termina l'esecuzione);"
                                + "\n - (v == 1) --> (ricerca e stampa di tutte le coppie con valore intero a ≤ x ≤ b e lunghezza della stringa ≤ s);"
                                + "\n - (v == 2) --> (ricerca e stampa di tutte le coppie aventi valori interi x ≥ y).");
                System.out.print("Risposta: ");
                v = scan.nextInt();
            } else if (v == 2) {
                System.out.println("\nInserisci il valore del parametro y");
                y = scan.nextInt();

                System.out.println("\nSeconda stampa con value.key ≥ " + y);
                System.out.println("-------------------------------------------------");

                /*
                 * Ricerca e stampa di tutte le coppie di valori (key --> x, value --> str) che
                 * rispettino: x ≥ c
                 */
                example.secondStamp(y);
                System.out.println("-------------------------------------------------");
                System.out.println();

                /*
                 * Visualizzazione del menu per la scelta dell'operazione successiva da
                 * effetture.
                 */
                System.out.println(
                        "Inserisci il valore del parametro v, in base all'azione voluta:\n - (v == 0) --> (termina l'esecuzione);"
                                + "\n - (v == 1) --> (ricerca e stampa di tutte le coppie con valore intero a ≤ x ≤ b e lunghezza della stringa ≤ s);"
                                + "\n - (v == 2) --> (ricerca e stampa di tutte le coppie aventi valori interi x ≥ y).");
                System.out.print("Risposta: ");
                v = scan.nextInt();
            } else {
                System.out.println("Attenzione comando non riconosciuto");
                System.out.println();

                /*
                 * Visualizzazione del menu per la scelta dell'operazione successiva da
                 * effetture.
                 */
                System.out.println(
                        "Inserisci il valore del parametro v, in base all'azione voluta:\n - (v == 0) --> (termina l'esecuzione);"
                                + "\n - (v == 1) --> (ricerca e stampa di tutte le coppie con valore intero a ≤ x ≤ b e lunghezza della stringa ≤ s);"
                                + "\n - (v == 2) --> (ricerca e stampa di tutte le coppie aventi valori interi x ≥ y).");
                System.out.print("Risposta: ");
                v = scan.nextInt();

            }
        }
        scan.close();
    }
}

class SocietyIcarus {
    private int key;
    private String value;
    private int sizeFile;

    private Couple[] coupleArray;

    public SocietyIcarus(String nameFile) {
        File file = new File(nameFile);
        coupleArray = new Couple[getFileSize(nameFile)];
        scanFile(file);
    }

    public void scanFile(File file) {
        int i = 0;
        try {
            Scanner scanFile = new Scanner(file);
            while (scanFile.hasNextLine()) {
                String line = scanFile.nextLine();
                String[] tokens = line.split(" ");

                key = Integer.parseInt(tokens[0]);
                value = tokens[1];

                Couple c = new Couple(key, value);

                /*
                 * Condizione necessaria per gestire e monitorare la possibilita' di duplicati,
                 * ossia coppie che abbiano stessa chiave e valore.
                 */
                coupleArray[i] = c;
                i++;
            }
            scanFile.close();
        } catch (Exception e) {
            System.out.println("Compilazione errata del file!");
            System.exit(0);
        }
    }

    /*
     * Metodo utilizzato per individuare il numero totale di coppie contenute dal
     * file txt. Inoltre e' un metodo utilizzato per favorire il minor costo
     * computazionale possibile in relazione all'operazione di inserimento.
     */
    public int getFileSize(String nameFile) {
        File file = new File(nameFile);

        try {
            Scanner scanFile = new Scanner(file);

            while (scanFile.hasNextLine()) {
                sizeFile++;
                scanFile.nextLine();
            }

            scanFile.close();
        } catch (Exception e) {
            System.out.println("Attenzione errore nelle lettura del file!");
            System.exit(0);
        }

        return sizeFile;
    }

    /*
     * Stampa che racchiude tutte le coppie (key: x, string: str) tali che a ≤ x ≤ b
     * e str ≤ s, rispetto ai parametri introdotti.
     */
    public void firstStamp(int a, int b, int s) {
        for (Couple c : coupleArray) {
            if (c.key >= a && c.key <= b) {
                int size = c.value.length();
                if (size < s) {
                    System.out.println("(" + c.key + ", " + c.value + ")");
                }
            }
        }
    }

    /*
     * Stampa che racchiude tutte le coppie (int: x, string: str) tali che x ≥ c,
     * parametro introdotto.
     */
    public void secondStamp(int y) {
        for (Couple c : coupleArray) {
            if (c.key >= y) {
                System.out.println("(" + c.key + ", " + c.value + ")");
            }
        }
    }
}

class Couple {
    int key;
    String value;

    public Couple(int key, String value) {
        this.key = key;
        this.value = value;
    }

    public String toString() {
        return this.key + " " + this.value;
    }
}
