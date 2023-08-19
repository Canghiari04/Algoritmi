/* 
 * Nome: Matteo
 * Cognome: Canghiari 
 * Email: matteo.canghiari@studio.unibo.it
 * Matricola: 1032059 
 * 
 * Considerazioni:
 * L'implementazione prevede l'uso di un array il quale memorizza la coppia <Integer, String>.
 * Pertanto viene salvato il valore intero della coppia come chiave, mentre contrariamente l'insieme dei caratteri ne rappresenta il valore.
 * Grazie all'utilizzo di un array e' possibile garantire la memorizzazione di coppie duplicate, sia per medesima chiave e valore, e sia 
 * nel caso di soli valori interi uguali; proprio per questo e' stata scelta tale struttura dati.
 * 
 * Come gia' accennato, l'uso di un array, su cui pone l'utilizzo di un oggetto Couple caratterizzato, come da richiesta progettuale, da 
 * un valore intero e da una stringa, permette di garantire tale ordine di dispendio.
 * 
 * L'inserimento ha un costo computazionale pari a O(1); questo e' dovuto poiche' in relazione alla dichiarazione della struttura
 * dati e' stata espressa la dimensione dell'array, passo che evita la possibilita' di ridimensionamenti automatici in caso non fosse
 * specificata la capienza massima e sia sorpassata la capacita' relegata in maniera totalmente autonoma durante la fase di inizializzazione, 
 * qualora fosse stata implementata un arraylist. Contrariamente l'operazione di ricerca richiede che l'intera struttura dati sia osservata, 
 * pertanto in relazione al numero di coppie presenti, si ottiene un costo computazionale pari a O(n); si noti come tale complessita' 
 * e' relativa anche al metodo getFileSize() da cui ne deriva l'intera scannerizzazione del sfile di testo per ottenere il numero totale di coppie presenti.
 */

import java.io.*;
import java.util.*;

public class Es2 {

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        /*
         * Richiesto, qualora lanciato il file .java, il nome del .txt per permettere la
         * lettura delle variabili e delle strutture dati in questione.
         */
        if (args.length != 1) {
            System.err.println("Specificare il nome del file di input!");
            System.exit(1);
        }

        Scanner scan = new Scanner(System.in);

        /*
         * Variabili che mantengono i valori delle soglie a cui si riferiscono le due
         * stampe richieste.
         */
        int a, b, y, s;

        /*
         * Rappresenta la variabile che permette la gestione delle stampe e della
         * conclusione dell'esecuzione.
         */
        int v;

        /*
         * Oggetto SocietyIcarus a cui è passato come parametro il nome del file da cui
         * avviene il riempimento della struttura dati in questione.
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
                 * Ricerca e stampa di tutte le coppie di valori (key:x, value:str) che
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
                 * Ricerca e stampa di tutte le coppie di valori (key:x, value:str) che
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
                System.out.println(key);
                value = tokens[1];

                Couple c = new Couple(key, value);

                /*
                 * Condizione necessaria per gestire e monitorare la possibilita' di duplicati,
                 * ossia coppie che abbiano stessa chiave.
                 */
                coupleArray[i] = c;
            }
            scanFile.close();
        } catch (FileNotFoundException e) {
            e.getMessage();
        }
    }

    /*
     * Metodo utilizzato per individuare il numero totale di coppie dettate dal file
     * txt. Inoltre e' un metodo utilizzato per favorire il minor costo
     * computazionale possibile in relazione all'operazione di inserimento, evitando
     * un consecutivo ridimesionamento qualora sorpassata la soglia prefissata al
     * momento della dichiarazione dell'arrayList.
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
            if (c.key > y) {
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