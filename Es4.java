/*
 * Nome: Matteo
 * Cognome: Canghiari
 * Email: matteo.canghiari@studio.unibo.it
 * Matricola: 1032059
 * 
 * Considerazioni:
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
 */

import java.io.*;
import java.util.*;

public class Es4 {
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        if (args.length != 1) {
            System.out.println("Specificare il nome del file!");
        }

        AbileneGraph example = new AbileneGraph(args[0]);

        /*
         * Metodo utilizzato per ottenere la totalita' dei nodi appartenenti al grafo,
         * da cui avviene la stampa di tutti i cammini minimi rispetto ai singoli nodi.
         */
        int idNumberNode = example.getNumberNode();

        for (int i = 0; i < idNumberNode; i++) {
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
    int[] p;
    double[] d;
    Edge[] sp;

    /*
     * Varibiale intera maxCapacity attuata affinche' possa essere stabilito il peso
     * di ogni edge.
     */
    private double maxCapacity = 0;
    private Map<String, Integer> nodes = new HashMap<String, Integer>();
    private LinkedList<Edge> edges = new LinkedList<Edge>();

    public AbileneGraph(String nameFile) {
        File file = new File(nameFile);
        scanFile(file);
    }

    public int getNumberNode() {
        return this.n;
    }

    /*
     * Lettura del grafo da parte dell'input file.
     */
    public void scanFile(File file) {
        try {
            Scanner scanFile = new Scanner(file);
            while (scanFile.hasNextLine()) {
                String line = scanFile.nextLine();

                /*
                 * I nodi sono ammessi all'interno di una mappa, affinche' possano essere poi
                 * utilizzati per la costruzione degli edge, rispetto ai valori interi
                 * associati.
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
        } catch (FileNotFoundException e) {
            e.getMessage();
        }
    }

    /*
     * Costruzione del cammino minimo di ogni nodo appartenete al grafo, rispetto al
     * paradigma di Bellman-Ford.
     * Nel caso dovessero essere individuati pesi negativi restituisce false, dove
     * rispetto alla condizione posta all'interno del costrutto, non permette la
     * stampa di nessun cammino minimo.
     */
    public boolean shortestPaths(int s) {
        source = s;
        d = new double[n];
        p = new int[n];
        sp = new Edge[n];

        /*
         * Inizializzazione delle strutture dati usate.
         */
        Arrays.fill(d, Double.POSITIVE_INFINITY);
        Arrays.fill(p, -1);

        d[s] = 0.0;
        for (int i = 0; i < n - 1; i++) {
            for (Edge e : edges) {
                final int src = e.src;
                final int dst = e.dst;
                final double w = e.getWeigth();

                /*
                 * Formula generale per calcolo del cammino minimo secondo Bellman-Ford, dati
                 * peso, partenza e destinazione.
                 */
                if (d[src] + w < d[dst]) {
                    d[dst] = d[src] + w;
                    p[dst] = src;
                    sp[dst] = e;
                }
            }
        }

        /*
         * Ciclo utilizzato per accertarsi la presenza di pesi negativi associati agli
         * archi.
         */
        for (Edge e : edges) {
            final int src = e.src;
            final int dst = e.dst;
            final double w = e.getWeigth();

            if (d[src] + w < d[dst]) {
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
            System.out.printf("%4d %4d %12.4f ", source, dst, d[dst]);
            print_path(dst);
            System.out.println();
        }
    }

    /*
     * Stampa del cammino minimo che collega un certo nodo source e un nodo
     * destination.
     */
    protected void print_path(int dst) {
        if (dst == source)
            System.out.print(dst);
        else if (p[dst] < 0)
            System.out.print("Irraggiungibile");
        else {
            print_path(p[dst]);
            System.out.print("->" + dst);
        }
    }
}

class Edge {
    final int src;
    final int dst;
    private double capacity;
    private double w;

    public Edge(int src, int dst, double capacity) {
        this.src = src;
        this.dst = dst;
        this.capacity = capacity;
    }

    public double getWeigth() {
        return this.w;
    }

    public void setWeight(double maxCapacity) {
        this.w = (maxCapacity / this.capacity);
    }

    @Override
    public String toString() {
        return this.src + " " + this.dst + " " + this.w;
    }
}