/*
 * Nome: Matteo
 * Cognome: Canghiari
 * Email: matteo.canghiari@studio.unibo.it
 * Matricola: 1032059
 * 
 * Riflessione:
 * L'implementazione prevede la costruzione di cammini minimi rispetto ad una sorgente multipla, ossia per una qualunque coppia di 
 * nodi (u, v) appartenente all'insieme di nodi V. L'algoritmo adottato si basa sulla condizione di ottimalita' di Bellman, oltre
 * alla tecnica del rilassamento, ottenendo una sequenza di nodi le cui funzioni di costo associate agli archi attraversati, abbiano
 * il peso minimo.
 * 
 * Come da codice, la prima operazione effettuata prevede la lettura del grafo G = (V, E) non orientato, mediante l'ausilio di una mappa 
 * capace di memorizzare coppie (chiave, valore), dove la key e' un valore intero mentre il value di tipo stringa, rappresentandone l'id del nodo.
 * Per la costruzione della soluzione ottima e' implementato l'algoritmo Bellman-Ford, il quale dopo (n - 1) passi di rilassamento sulla totalita'
 * degli archi, garantisce la costruzione del cammino minimo. Il costo computazionale risulta O(n * m), data la presenza di una concatenazione 
 * di cicli, dove rispettivamente il primo prevede la scansione sul numero di nodi mentre il secondo permette la visita di tutti gli archi del grafo.
 * Tuttavia la richiesta prevede soluzioni rispetto a sorgenti multiple per cui l'algoritmo e' ripetuto (n) volte, per una complessita' pari a O(n^3), ossia:
 * - O(n^3) --> n * O(n * m).
 * 
 * Nota: 
 * Il file dato a riga di comando ha la medesima impostazione fornita dalla traccia dell'esame.
 */

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Arrays;
import java.io.File;

public class Esercizio4 {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Esercizio4 Esercizio4.txt");
        } else {
            AbileneGraph example = new AbileneGraph(args[0]);

            for (int i = 0; i < example.n; i++) {
                if (!example.shortestPaths(i))
                    example.print_paths();
                else
                    System.out.println("Attenzione presenza di archi con peso negativo!");
            }
        }
    }
}

class AbileneGraph {
    /*
     * Variabile intera n per stabilire quali siano la totalita' dei nodi del grafo.
     */
    int n;

    /*
     * Variabili utilizzate per il calcolo del cammino minimo rispetto ad un nodo
     * sorgente.
     */
    int source;
    int[] T;
    double[] D;

    /*
     * Varibiale intera maxCapacity attuata affinche' possa essere stabilito il peso
     * di ogni edge.
     */
    private double maxCapacity = 0;
    private HashMap<String, Integer> nodes = new HashMap<String, Integer>();
    private LinkedList<Edge> edges = new LinkedList<Edge>();

    public AbileneGraph(String nameFile) {
        File file = new File(nameFile);
        scanFile(file);
    }

    public void scanFile(File file) {
        try {
            Scanner scanFile = new Scanner(file);
            while (scanFile.hasNextLine()) {
                String line = scanFile.nextLine();

                /*
                 * I nodi sono ammessi all'interno di una mappa, affinche' possano essere poi
                 * utilizzati i valori interi associati per la costruzione degli edge.
                 */
                if (line.equals("NODES (")) {
                    line = scanFile.nextLine();

                    while (!line.equals(")")) {

                        String[] tokens = line.split(" ");
                        nodes.put(tokens[0], n);
                        n++;

                        line = scanFile.nextLine();
                    }
                } else if (line.equals("LINKS (")) {
                    line = scanFile.nextLine();

                    while (!line.equals(")")) {
                        String[] tokens = line.split(" ");

                        int idStart = nodes.get(tokens[2]);
                        int idEnd = nodes.get(tokens[3]);
                        double capacity = Double.parseDouble(tokens[5]);

                        /*
                         * Passaggio necessario per mantenere traccia della massima capacita' da cui
                         * verra' poi calcolato, in base al rapporto (maxCapacity / singleCapacity), il
                         * peso di ogni singolo edge.
                         */
                        if (capacity > maxCapacity) {
                            maxCapacity = capacity;
                        }

                        /*
                         * Costruzione degli archi, affinche' sia possibile ottenere un grafo
                         * bidirezionale, rispetto ai valori interi che identificano i differenti nodi
                         * che compongono la rete.
                         */
                        Edge edge = new Edge(idStart, idEnd, capacity);
                        Edge edge2 = new Edge(idEnd, idStart, capacity);

                        edges.add(edge);
                        edges.add(edge2);

                        line = scanFile.nextLine();
                    }

                    scanFile.close();
                    break;
                }
            }

            for (Edge e : edges) {
                e.setWeight(maxCapacity);
            }
        } catch (Exception e) {
            System.out.println("Attenzione lettura errata del file di input!");
        }
    }

    /*
     * Costruzione del cammino minimo di ogni nodo appartenente al grafo, rispetto
     * al
     * paradigma di Bellman-Ford.
     * Nel caso dovessero essere individuati cicli di peso negativo restituisce
     * false, non permettendo la stampa di nessun cammino minimo.
     */
    public boolean shortestPaths(int s) {
        source = s;

        /*
         * Variabili usate come di seguito:
         * D --> vettore delle distanze;
         * T --> vettore dei padri.
         */
        D = new double[n];
        T = new int[n];

        /*
         * Sovrastima della distanza per ogni nodo u appartenente all'insieme dei nodi
         * del grafo, affinchè durante la composizione della soluzione, tale limite
         * possa descrescere fino a quando non raggiunga il valore minimo associato alla
         * sequenza di archi e nodi attraversati.
         */
        Arrays.fill(D, Double.POSITIVE_INFINITY);
        Arrays.fill(T, -1);

        /*
         * Primo passo di rilassamento, il quale consiste nella valorizzazione del
         * vettore delle distanze rispetto alla sorgente (s) pari a 0.
         */
        D[s] = 0.0;

        /*
         * Eseguiti (n - 1) passi di rilassamento sul totale degli archi contenuti dal
         * grafo. Solamente al termine di tali passaggi si è sicuri della soluzione
         * costruita, e da cui deriva il controllo per cicli negativi.
         */
        for (int i = 0; i < (n - 1); i++) {
            for (Edge e : edges) {
                final int src = e.src;
                final int dst = e.dst;
                final double w = e.w;

                /*
                 * Adottata la condizione di ottimalita' di Bellman, in cui la soluzione
                 * ammissibile e' ottima se e solo se:
                 * - (u, v) appartiene a T (soluzione ammissibile) allora d[v] = d[u] + w(u, v);
                 * - (u, v) appartiene a E allora d[v] ≤ d[u] = w(u, v).
                 * Qualora una delle due condizioni non sia rispettata allora il limite
                 * superiore e' rilassato, ossia modificato rispetto alla nuova soluzione
                 * ottima.
                 */
                if (D[src] + w < D[dst]) {
                    D[dst] = D[src] + w;
                    T[dst] = src;
                }
            }
        }

        /*
         * Ciclo utilizzato per accertarsi della presenza di cicli negativi.
         */
        for (Edge e : edges) {
            final int src = e.src;
            final int dst = e.dst;
            final double w = e.w;

            if (D[src] + w < D[dst]) {
                return true;
            }
        }

        return false;
    }

    /*
     * Stampa di tutti i cammini minimi rispetto alla costruzione del metodo
     * precedente shortestPaths() in cui e' dato un nodo source.
     */
    public void print_paths() {
        System.out.println();
        System.out.println("s       d         dist                path");
        System.out.println("---- ---- ------------ -------------------");
        for (int dst = 0; dst < n; dst++) {
            System.out.print(source + "\t" + dst + "\t  " + D[dst] + "\t");
            print_path(dst);
            System.out.println();
        }
    }

    /*
     * Stampa del cammino minimo da un certo nodo di destinazione (dst)
     * raggiungibile dal nodo sorgente (s). Tale algoritmo si basa sulla stampa
     * dell'albero di copertura adottata durante l'operazione di visita in ampiezza
     * (BFS), tramite l'impiego del vettore dei padri (T[]).
     */
    protected void print_path(int dst) {
        if (dst == source)
            System.out.print(dst);
        else if (T[dst] < 0)
            System.out.print("Attenzione impossibile raggiungere il nodo di destinazione!");
        else {
            print_path(T[dst]);
            System.out.print("->" + dst);
        }
    }
}

class Edge {
    final int src;
    final int dst;
    double capacity;
    double w;

    public Edge(int src, int dst, double capacity) {
        this.src = src;
        this.dst = dst;
        this.capacity = capacity;
    }

    public void setWeight(double maxCapacity) {
        this.w = (maxCapacity / this.capacity);
    }

    @Override
    public String toString() {
        return this.src + " " + this.dst + " " + this.w;
    }
}