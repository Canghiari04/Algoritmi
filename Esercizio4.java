/*
 * Nome: Matteo
 * Cognome: Canghiari
 * Email: matteo.canghiari@studio.unibo.it
 * Matricola: 1032059
 * 
 * Riflessione:
 * 
 * 
 * 1 - lettura del file 
 * 2 - 
 * L'implementazione prevede l'uso di differenti strutture dati combinate fra loro, pur di ottenere i cammini minimi rispetto 
 * all'algoritmo di Bellman-Ford.
 * 
 * E' inizializzata una mappa, contenente come coppia chiave-valore, rispettivamente, il nominativo del nodo e un suo corrispettivo
 * indice intero. Questo e' dovuto essenzialmente affinche' sia possibile costruire i diversi edge in maniera molto 
 * piu' rapida e semplice, attraverso una partenza (src) e una destinazione (dst) descritte attraverso un valore intero.
 * L'inserimento all'interno della Map ha un costo computazionale pari a O(1), mentre, ugualmente, la ricerca dei nodi per la realizzazione degli archi
 * possiede un valore costante a O(1), poiche' ogni qual volta e' specificata la chiave di riferimento del nodo.
 * 
 * L'algoritmo richiesto per individuare i cammini minimi, ossia Bellman-Ford, prevede un costo computazionale dovuto a tale moltiplicazione:
 * (numero archi * numero nodi), piu' brevemente (n * edges.size()), per cui un costo relativo all'ordine di O(2n).
 * 
 * Stabilito l'ordine dell'algoritmo implementato, le funzioni che permettono la stampa tabellare dei differenti cammini minimi dovrebbero essere
 * influenti rispetto al costo computazionale descritto precedentemente.
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
        }

        AbileneGraph example = new AbileneGraph(args[0]);

        for (int i = 0; i < example.n; i++) {
            if (!example.shortestPaths(i))
                example.print_paths();
            else
                System.out.println("Attenzione presenza di archi con peso negativo!");
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
        System.out.println("   s    d         dist                path");
        System.out.println("---- ---- ------------ -------------------");
        for (int dst = 0; dst < n; dst++) {
            System.out.printf("%4d %4d %12.4f ", source, dst, D[dst]);
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