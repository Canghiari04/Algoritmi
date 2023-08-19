/*
 * Nome: Matteo
 * Cognome: Canghiari
 * Email: matteo.canghiari@studio.unibo.it
 * Matricola: 1032059
 * 
 * Considerazioni; 
 * L'implementazione, oltre alla corretta lettura del file, richiede l'uso della tecnica Divide-et-Impera affinche' sia possibile comparare
 * quali valori dell'array I[i] risultino essere maggiori della soglia k. Il costo computazionale medio dell'algoritmo utilizzato,
 * basato sulla ricerca binaria, risulta essere O(log n), dove comunque non e' detto che sia necessario scorrere l'intera struttura dati.
 * 
 * Si possono stabilire casi, legati alla soglia stabilita e ai valori contenuti nell'array ordinato, in cui il costo computazionale potrebbe
 * ottenere un giovamento oppure un peggioramento:
 * - se tutti i valori dell'array risultano (> della soglia k), l'algoritmo richiede O(1) iterazioni, in quanto terminera' immediatamente il ciclo while;
 * - mentre, se tutti i valori dell'array risultano (< della soglia k), allora saranno richiesti O(n) passi.
 */

import java.io.*;
import java.util.*;

public class Es3 {
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        /*
         * Richiesto, qualora lanciato il file .java, il nome del .txt per permettere la
         * lettura delle variabili e delle strtture dati in questione.
         */
        if (args.length != 1) {
            System.out.println("Specificare il nome del file!");
            System.exit(1);
        }

        Promotion example = new Promotion(args[0]);

        System.out.println("Il numero di dipendenti soggetti alla promozione: " + "\n"
                + example.countPromotions(example.getArray(), example.getK(), 0, (example.getLength() - 1)));
    }
}

class Promotion {
    private int n;
    private int i = 0;
    private double k;
    private double[] I;

    public Promotion(String nameFile) {
        File file = new File(nameFile);
        scanFile(file);
    }

    public int getLength() {
        return n;
    }

    public double getK() {
        return k;
    }

    public double[] getArray() {
        return I;
    }

    public void scanFile(File file) {
        try {
            Scanner scanFile = new Scanner(file);

            while (scanFile.hasNextLine()) {
                String line = scanFile.nextLine();

                if (line.equals("# numero di dipendenti")) {
                    line = scanFile.nextLine();

                    /*
                     * Lettura della dimensione della struttura dati e inizializzazione dell'array
                     */
                    n = Integer.parseInt(line);
                    I = new double[n];
                } else if (line.equals("# vettore I")) {
                    line = scanFile.nextLine();

                    /*
                     * Inserimento degli indici di competenza di ogni dipendente all'interno
                     * dell'array.
                     */
                    while (!line.isEmpty()) {
                        I[i] = Double.parseDouble(line);
                        i++;

                        line = scanFile.nextLine();
                    }
                } else if (line.equals("# soglia k")) {
                    line = scanFile.nextLine();

                    /*
                     * Lettura del valore soglia affinche' sia possibile stabilire quali dipendenti
                     * siano soggetti ad una premiazione.
                     */
                    k = Double.parseDouble(line);
                }
            }
            scanFile.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /*
     * L'algoritmo countPromotions() viene invocato con countPromotions(I, k, 0,
     * (I.length - 1)), essendo (I.length - 1) la totalita' dei dipendenti.
     * Ad ogni passo vengono osservate tali condizioni:
     * 
     * - se i > j, stabilisce la conclusione della ricerca binaria;
     * 
     * - se i ≤ j, allora viene stabilito l'indice medio dell'elemento posto
     * nell'array, distinguendo due casi, per cui in questo momento interviene la
     * tecnica del Divide-et-Impera:
     * 
     * - se l'elemento I[m] ≤ k, ossia della soglia, allora il conteggio prosegue
     * considerando esclusivamente i valori posti alla destra di I[m];
     * 
     * - se l'elemento I[m] > k, ossia della soglia, allora il conteggio si
     * ripercuote
     * su tutti gli elementi allocati rispettivamente da posizione I[m...j], dato
     * l'ordinamento
     * crescente dell'array, concludendo la funzione ricorsiva.
     * 
     */
    public int countPromotions(double[] I, double k, int i, int j) {
        if (i > j) {
            return 0;
        } else {
            /*
             * Serve per overflow, dato che gli indici potrebbero sorpassare la soglia
             * consentita, ossia la rappresentazione in double.
             */
            int m = i + (j - i) / 2;
            if (I[m] <= k) {
                return countPromotions(I, k, m + 1, j);
            } else {
                /*
                 * L'incremento e' dovuto all'uso degli indici rispetto a strutture dati
                 * statiche,
                 * quale array.
                 * 
                 * In relazione al metodo countPromotions() e' posta come parametro la
                 * differenza (m - 1), passo voluto per valorizzare la prima condizione
                 * della funzione (i > j) e pertanto concludere l'esecuzione.
                 */
                return ((j - m) + 1) + countPromotions(I, k, i, m - 1);
            }
        }
    }
}