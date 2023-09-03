/*
 * Nome: Matteo
 * Cognome: Canghiari
 * Email: matteo.canghiari@studio.unibo.it
 * Matricola: 1032059
 * 
 * Considerazioni:
 * L'implementazione richiede la valorizzazione di cammini minimi dato un nodo sorgente, tramite il paradigma della programmazione dinamica.
 * Per cui rispetto alla richiesta occorre implementare l'algoritmo di Floyd-Warshall.
 * 
 * Nota: 
 * Il file dato a riga di comando ha la medesima impostazione fornita da traccia di esame.
 * 
 * OCCORRE PRIMA STUDIARE LA SEZIONE SUI GRAFI!
 */

import java.io.*;
import java.util.*;

public class Esercizio5 {
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        if (args.length != 1) {
            System.out.println("Usage: java Esercizio5 Esercizio4.txt");
        }

        AbileneGraphDynamicProgram example = new AbileneGraphDynamicProgram(args[0]);

        example.shortestPaths();
        example.print_paths();
    }
}

class AbileneGraphDynamicProgram {
    /*
     * Variabile intera n per stabilire quali siano la totalita' dei nodi del grafo.
     */
    int n;

    /*
     * Variabile intera m per numero di archi.
     */
    int m;
    int[][] next;
    double[][] d;

    /*
     * Varibiale intera maxCapacity attuata affinche' possa essere stabilito il peso
     * di ogni edge.
     */
    private double maxCapacity = 0;
    private Map<String, Integer> nodes = new HashMap<String, Integer>();
    private LinkedList<Edge> edges = new LinkedList<Edge>();

    public AbileneGraphDynamicProgram(String nameFile) {
        File file = new File(nameFile);
        scanFile(file);
    }

    public void scanFile(File file) {
        try {
            Scanner scanFile = new Scanner(file);
            while (scanFile.hasNextLine()) {
                String line = scanFile.nextLine();

                /*
                 * I nodi sono inseriti all'interno di una mappa, affinche' possano essere poi
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
                         * bidirezionale.
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
     * Costruzione del cammino minimo di ogni nodo appartenete al grafo, mediante
     * Floy-Warshall.
     */
    public void shortestPaths() {
        int u, v, k;
        d = new double[n][n];
        next = new int[n][n];

        /*
         * Inizializzata la matrica di distanza.
         */
        for (u = 0; u < n; u++) {
            for (v = 0; v < n; v++) {
                d[u][v] = (u == v ? 0 : Double.POSITIVE_INFINITY);
                next[u][v] = (u == v ? u : -1);
            }
        }

        for (final Edge edge : edges) {
            final double w = edge.getWeigth();
            u = edge.src;
            v = edge.dst;

            /*
             * Riempimento della matrice contenente la distanza posta tra nodo u e nodo v,
             * senza passare per nodi intermedi.
             */
            d[u][v] = w;
            next[u][v] = v;
        }

        /*
         * Ciclo essenziale su cui basa la costruzione del cammino minimo rispetto al
         * paradigma richiesto.
         */
        for (k = 0; k < n; k++) {
            for (u = 0; u < n; u++) {
                for (v = 0; v < n; v++) {
                    if (d[u][k] + d[k][v] < d[u][v]) {
                        d[u][v] = d[u][k] + d[k][v];
                        next[u][v] = next[u][k];
                    }
                }
            }
        }

        /*
         * Ciclo utilizzato per accertarsi della presenza di cicli negativi.
         */
        for (u = 0; u < n; u++) {
            if (d[u][u] < 0.0) {
                System.out.println("Attenzione presenza di archi con peso negativo!");
                break;
            }
        }

    }

    /*
     * Stampa di tutti i cammini minimi rispetto alla costruzione del metodo
     * precedente shortestPaths() in cui e' dato un nodo source.
     */
    public void print_paths() {
        int i, j;
        for (i = 0; i < n; i++) {
            System.out.println();
            System.out.println("   s    d         dist                path");
            System.out.println("---- ---- ------------ -------------------");
            for (j = 0; j < n; j++) {
                System.out.print(i + "\t" + j + "\t  " + d[i][j] + "\t");
                print_path(i, j);
                System.out.println();
            }
        }
    }

    /*
     * Stampa del cammino minom che collega il nodo u e il nodo v, se esiste.
     */
    protected void print_path(int u, int v) {
        if ((u != v) && (next[u][v] < 0)) {
            System.out.print("Attenzione impossibile raggiungere il nodo di destinazione!");
        } else {
            System.out.print(u);
            while (u != v) {
                u = next[u][v];
                System.out.print("->" + u);
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
}