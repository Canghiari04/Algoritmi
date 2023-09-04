/*
 * Nome: Matteo
 * Cognome: Canghiari
 * Email: matteo.canghiari@studio.unibo.it
 * Matricola: 1032059
 * 
 * Riflessione:
 * L'implementazione prevede l'uso dell'algoritmo di Floyd-Warshall, affinche' sia possibile costruire una sequenza di nodi il cui costo sia minimo rispetto agli 
 * archi attraversati, ottenendo una soluzione posta per una sorgente multipla. Infatti, per la risoluzione di cammini minimi a sorgente multipla, spesso
 * e' consigliato adoperare l'algoritmo di Floyd-Warshall invece della soluzione proposta da Bellman-Ford, poiche', contrariamente al secondo citato, non avviene 
 * la stessa ripetizione, per (n) volte, dell'algoritmo per la costruzione di cammini minimi rispetto ad ogni coppia di nodi (u, v) qualsiasi.
 * 
 * L'idea pone la propria centralita' attraverso l'uso di una matrice di distanze D(u, v) e una matrice dei padri T(u, v), i quali riferimenti verranno modificati ogni
 * volta individuato un cammino tra i nodi (u, v) qualsiasi di costo minore rispetto al precedente. Anche in questo e' utilizzata la condizione di Bellman, per stabilire
 * se la soluzione ammissibile risulti ottima, attuando passi di rilassamento connessi all'incremento del vincolo (k) per la costruzione della sequenza di nodi.
 * A livello di costo computazionale Floyd-Warshall corrisponde ad un limite asintotico superiore pari a O(n^3) poiche' richiede una concatenazione di tre cicli:
 * quello posto piu' esternamente per incrementare il valore di (k) vincolo, mentre i due interni sono utilizzati per scansionare la coppia di nodi (u, v).
 * 
 * Nota: 
 * Il file dato a riga di comando ha la medesima impostazione fornita dalla traccia dell'esame.
 */

import java.util.Locale;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;
import java.io.File;

public class Esercizio5 {
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        if (args.length != 1) {
            System.out.println("Usage: java Esercizio5 Esercizio4.txt");
        } else {
            AbileneGraphDynamicProgram example = new AbileneGraphDynamicProgram(args[0]);
            example.shortestPaths();
            example.print_paths();
        }
    }
}

class AbileneGraphDynamicProgram {
    /*
     * Variabile n che indica il numero di nodi del grafo.
     */
    int n;

    /*
     * Variabile intera maxCapacity attuata affinche' possa essere stabilito il peso
     * di ogni edge.
     */
    private double maxCapacity = 0;

    /*
     * Variabili utilizzate come di seguito:
     * m --> numero di archi del grafo;
     * next --> matrice dei padri;
     * d --> matrice delle distanze.
     */
    int m;
    int[][] next;
    double[][] d;

    private HashMap<String, Integer> nodes = new HashMap<String, Integer>();
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
        } catch (Exception e) {
            System.out.println("Attenzione lettura errata del file di input!");
            System.exit(0);
        }
    }

    public void shortestPaths() {
        int u, v, k;
        d = new double[n][n];
        next = new int[n][n];

        /*
         * Fase di inizializzazione della matrice di adiacenza per ogni nodo (u)
         * appartenente al grafo.
         */
        for (u = 0; u < n; u++) {
            for (v = 0; v < n; v++) {
                if (u == v) {
                    d[u][v] = 0;
                    next[u][v] = u;
                } else {
                    d[u][v] = Double.POSITIVE_INFINITY;
                    next[u][v] = -1;
                }
            }
        }

        /*
         * Popolamento della matrice contenente la distanza posta tra nodo u e nodo v,
         * senza passare per nodi intermedi.
         */
        for (Edge edge : edges) {
            double w = edge.w;
            u = edge.src;
            v = edge.dst;
            d[u][v] = w;
            next[u][v] = v;
        }

        /*
         * Eseguiti incrementi di k vincolo fino a grandezza massima dei nodi. Ponendo
         * la possibilita' di attraversare un numero crescente di nodi intermedi,
         * affinche' tramite la condizione di Bellman, sia garantita la modifica qualora
         * la nuova sequenza di nodi abbia un costo minore rispetto al cammino costruito
         * precedentemente.
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
                System.out.println("Attenzione presenza di ciclo con peso negativo!");
                break;
            }
        }
    }

    /*
     * Stampa di tutti i cammini minimi rispetto ad una sorgente multipla.
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
        int src;
        int dst;
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

        public String toString() {
            return this.src + " " + this.dst + " " + this.w;
        }
    }
}