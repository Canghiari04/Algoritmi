/* 
* Nome: Matteo
* Cognome: Canghiari
* Email: matteo.canghiari@studio.unibo.it
* Matricola: 1032059
* 
* Considerazioni:
* L'implementazione adotta una tabella hash, limitando il numero di collisioni tramite l'uso delle liste di trabocco il cui riferimento è
* memorizzato nell'array posto esternamente. Fornito il numero di accessi (T), mediante formule inverse, è stato calcolato nel 
* costruttore della classe HashTable il fattore di carico (a); passo fondamentale per stabilire la dimensione dell'array esterno. 
* La funzione hash utilizzata è basata sul metodo della divisione, poiché l'indicizzazione delle coppie key-value avviene tramite la 
* valorizzazione di sotto-intervalli, che riescano a coprire l'intero range di generazione casuale delle chiavi.
* Si nota, come la generazione di chiavi mediante valori casuali, dovrebbe gia' garantire una buona equi-distanza tra le coppie, 
* proprio per natura dell'oggetto Random(), attribuendo un'accettabile distribuzione nelle celle del vettore.
*/

import java.util.Locale;
import java.util.Random;
import java.util.ArrayList;
import java.io.IOException;
import java.io.PrintWriter;

public class Es1 {
    static HashTable table;

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        if (args.length == 0 || Integer.parseInt(args[0]) <= 1) {
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
    /*
     * Variabili utilizzate per la generazione di valori interi casuali.
     */
    final int min = 1;
    final int maxKey = 1000000000;
    final int maxNumberPage = 700;
    final Random rnd_1 = new Random(1032059);
    final Random rnd_2 = new Random(7105419);

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
         * Valorizzazione della formula inversa per il calcolo della dimensione dell'
         * ArrayList esterno, ossia
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
     * casualmente e calcolo del numero medio di accessi sperimentale in relazione
     * alla tabella hash popolata mediante funzione populate().
     */
    public void searchDuplicate(String str) throws IOException {
        PrintWriter file = new PrintWriter(str);
        count = 0;

        for (int i = 0; i < 300; i++) {
            randomKey = rnd_2.nextInt((maxKey) + 1);

            int j = verify(randomKey);

            /*
             * Condizione che verifica la presenza della chiave generata.
             * Qualora non sia presente (j == -1) allora il numero di accessi e' pari al
             * caso di insuccesso, ossia T = 1 + a. Oppure se presente, e' necessario
             * incrementare il numero di accessi affinche' non si trovi la corrispondenza
             * tra le due chiavi, ottenendo il dato sperimentale.
             */
            if (j == -1) {

                /*
                 * Variabile count incrementata di 1 poiche' avviene pur sempre un accesso alla
                 * cella dell'ArrayList esterno tramite la funzione hash, indipendentemente
                 * dalla lista di trabocco di riferimento.
                 */
                count = arrayNode.get(hash(randomKey)).size() + 1;
                file.println(randomKey + ", " + count);
                sum += count;
            } else {
                ArrayList<Node> array = arrayNode.get(hash(randomKey));
                count = 1;

                for (Node n : array) {
                    if (n.getKey() == randomKey) {
                        count++;
                        System.out.println(n);
                        break;
                    }

                    count++;
                }

                sum += count;
            }
        }

        System.out.println("Il numero medio di accessi risulta (NMA): " + (sum / 300));
        file.close();
    }

    private void populate() {
        for (int i = 0; i < n; i++) {
            randomKey = rnd_1.nextInt((maxKey - min) + 1) + min;
            randomNumberPage = rnd_1.nextInt((maxNumberPage - min) + 1) + min;

            insert(randomKey, randomNumberPage);
        }
    }

    private void insert(int key, int value) {
        int i = hash(key);
        int j = verify(key);

        /*
         * Verifica la presenza della chiave nelle liste di trabocco rispetto al
         * reindirizzamento della funzione hash precedente.
         * 
         * Se l'indice e' negativo indica che la chiave da inserire ancora non e' stata
         * riscontrata nelle liste di trabocco.
         * Mentre in caso contrario, avviene la sovrascrittura del value della coppia
         * che abbia la stessa chiave.
         */
        if (j == -1) {
            arrayNode.get(i).add(new Node(key, value));
            size++;
        } else {
            arrayNode.get(i).get(j).setValue(value);
        }
    }

    private int verify(int key) {
        /*
         * Controllo della presenza della chiave passata come parametro. Cio' avviene
         * tramite il primo accesso alla cella di riferimento attraverso la funzione
         * hash, risalendo al puntatore della lista di trabocco, per poi ciclare sulla
         * stessa per garantire veridicita' o meno.
         */
        int i = hash(key);
        ArrayList<Node> array = arrayNode.get(i);
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
     */
    private int hash(int key) {
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
