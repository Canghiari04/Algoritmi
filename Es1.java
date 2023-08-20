/* 
* Nome: Matteo
* Cognome: Canghiari
* Email: matteo.canghiari@studio.unibo.it
* Matricola: 1032059
* 
* Considerazioni:
* L'implementazione prevede l'uso di una tabella hash, mediante l'utilizzo di liste di trabocco. 
* La scelta ricade soprattutto per la natura dell'oggetto Random(); infatti, in relazione al range 
* dato come da specifica progettuale, permette di generare valori casuali distribuiti uniformemente.
* Per cui in relazione ad una corretta funzione hash di base, e' possibile garantire una buona distribuzione
* delle coppie chiave-valore.
* 
* Passo fondamentale e' relativo al calcolo della dimensione della struttura esterna, ossia del vettore contenente 
* il puntatore di ogni lista di trabocco, implementate mediante ArrayList. Innanzitutto occorre ricavare 
* il valore del fattore di carico, rispetto al parametro T (ossia il numero medio di accessi) dato come parametro 
* del costruttore della classe HashTable, attraverso la formula generale T = a + 1 => a = T - 1. 
* Ottenuto il fattore di carico (a), tramite a = n / m => m = n / a, si ottiene la dimensione della struttura dati rappresentante
* la tabella hash. Infine in ogni cella verra' inizializzato un ArrayList contenente le differenti coppie, reindirizzate in posizioni
* congrue rispetto all'intervalSize posto tra range di valori casuali e grandezza del vettore
*/

import java.util.Locale;
import java.util.Random;
import java.util.ArrayList;
import java.io.IOException;
import java.io.PrintWriter;

public class Es1 {
    /*
     * Parametri utilizzati per generare casualmente le coppie [chiave, valore],
     * rispetto ai seed impostati agli oggetti Random().
     */
    static final int maxKey = 1000000000;
    static final int maxNumberPage = 700;
    static final int min = 1;
    static final Random rnd_1 = new Random(1032059);
    static final Random rnd_2 = new Random(7105914);

    static int randomKey;
    static int randomNumberPage;
    static double sum;
    static HashTable table;

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        if (args.length == 0 || Integer.parseInt(args[0]) == 1) {
            System.out.println("Attenzione inserimento errato o mancante del parametro T!");
            System.exit(0);
        } else {
            table = new HashTable(Integer.parseInt(args[0]));
        }

        try {
            table.searchDuplicate("Es1.txt");
        } catch (IOException e) {
            System.out.println("Attenzione lettura errata del file di output!");
            e.getMessage();
        }
    }
}

class HashTable {
    final int min = 1;
    final int maxKey = 1000000000;
    final int maxNumberPage = 700;
    final Random rnd_1 = new Random(1032059);
    final Random rnd_2 = new Random(7105914);

    int randomKey;
    int randomNumberPage;

    /*
     * Variabile usate come di seguito:
     * n --> numero di coppie chiave-valore da inserire.
     * m --> capacita' massima della struttura dati esterna, calcolata all'interno
     * del costruttore.
     */
    final int n = 10000;
    private int m;
    private int size;
    private int intervalSize;

    /*
     * Variabile utilizzate come di seguito:
     * count --> contiene il numero di accessi necessario per risalire alla chiave
     * di verifica generata casualmente.
     * sum --> somma di tutti i contatori durante la fase di ricerca delle 300
     * chiavi generate, fondamentale per il calcolo successivo del numero medio di
     * accessi.
     */
    private int count;
    private int sum;

    private ArrayList<ArrayList<Node>> arrayNode;

    public HashTable(int T) {
        /*
         * Valorizzazione della formula inversa per il calcolo della dimensione del
         * vettore, ossia
         * a = T - 1
         * m = n / a
         * combinando le due => m = (n / (T - 1)).
         */
        this.m = (n / (T - 1));
        this.intervalSize = (maxKey / m);

        /*
         * E' bene creare una struttura come ArrayList<ArrayList<>> poiche' in relazione
         * alla costruzione mediante un array statico potrebbe generare differenti
         * warning.
         */
        arrayNode = new ArrayList<ArrayList<Node>>(m);

        for (int i = 0; i < m; i++) {
            arrayNode.add(i, new ArrayList<Node>());
        }

        populate();
    }

    /*
     * Metodo per la ricerca di chiavi duplicate, rispetto alle 300 generate
     * casualemente e calcolo del numero medio di accessi in relazione alla tabella
     * hash popolata mediante funzione populate().
     */
    public void searchDuplicate(String str) throws IOException {
        PrintWriter file = new PrintWriter(str);
        count = 0;

        for (int i = 0; i < 300; i++) {
            randomKey = rnd_2.nextInt((maxKey - min) + 1) + min;

            int j = verify(randomKey);
            if (j == -1) {
                count = arrayNode.get(hash(randomKey)).size() + 1;
                file.println(randomKey + ", \t" + count);
            } else {
                ArrayList<Node> array = arrayNode.get(j);

                for (Node n : array) {
                    if (n.getKey() == randomKey) {
                        count++;
                        System.out.println(n);
                        break;
                    }

                    count++;
                }

                count += 1;
            }

            /*
             * Variabile count incrementata di 1 poiche' avviene pur sempre un accesso alla
             * cella dell'ArrayList esterno tramite la funzione hash, indipendentemente
             * dalla lista di trabocco di riferimento.
             */
            sum += count + 1;
        }

        System.out.println("Il numero medio di accessi risulta (NMA): " + (sum / 300));
        file.close();
    }

    private void populate() {
        for (int i = 0; i < (n - 1); i++) {
            randomKey = rnd_1.nextInt((maxKey - min) + 1) + min;
            randomNumberPage = rnd_1.nextInt((maxNumberPage - min) + 1) + min;

            insert(randomKey, randomNumberPage);
        }
    }

    public void insert(int key, int value) {
        int i = hash(key);
        int j = verify(key);

        /*
         * Verifica della presenza della chiave nelle liste di trabocco rispetto al
         * reindirizzamente della funzione hash precedente.
         * 
         * Se l'indice e' negativo indica che la chiave da inserire ancora non e' stata
         * riscontrata nelle liste di trabocco.
         * Mentre in caso contrario, avviene la sovrascrittura del value della coppia.
         */
        if (j == -1) {
            arrayNode.get(i).add(new Node(key, value));
            size++;
        } else {
            arrayNode.get(i).get(j).setValue(value);
        }
    }

    public int verify(int key) {
        int i = hash(key);
        ArrayList<Node> array = arrayNode.get(i);

        /*
         * Controllo della presenza della possbile chiave duplicata, causando possibile
         * collisione. Cio' avviene tramite il precedente reindirizzamento della
         * funzione hash, pur sempre calcolato in funzione della chiave.
         */
        for (int j = 0; j < array.size(); j++) {
            if (array.get(j).getKey() == key) {
                System.out.println(array.get(j));
                return j;
            }
        }

        return -1;
    }

    /*
     * Funzione hash che individua la cella dell'ArrayList posto esternamente,
     * affinche' sia possibile risalire alle strutture dati interne.
     * 
     * Adottata una funzione hash relativa al metodo della divisione, con un
     * approccio differente. Questo e' dovuto per ovviare alla possibilita' di una
     * mancata egemone distribuzione delle chiavi, qualora m sia potenza di 2, ossia
     * m = 2^p; anche se l'oggetto Random() puo' gia' garantire una buona
     * distribuzione.
     */
    public int hash(int key) {
        return (key - 1) / intervalSize;
    }

    public int getSize() {
        return this.size;
    }
}

class Node {
    private int key;
    private int value;

    public Node(int key, int value) {
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return this.key;
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int x) {
        this.value = x;
    }

    public String toString() {
        return "K: " + this.key + " V:" + this.value;
    }
}