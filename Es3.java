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
 * 
 * 
 * 
 * Nota: 
 * Il file dato a riga di comando ha la medesima impostazione fornita dalla traccia dell'esame.
 */

import java.io.*;
import java.util.*;

public class Es3 {
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        if (args.length != 1) {
            System.out.println("Usage: java Esercizio3 Esercizio.txt");
            System.exit(1);
        }

        Promotion promotion = new Promotion(args[0]);
        System.out.println("Il numero di dipendenti soggetti alla promozione: " + "\n"
                + promotion.countPromotions(promotion.getArray(), promotion.getK(), 0, (promotion.getLength() - 1)));
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
     * Metodo countPromotions, utilizzato per poter calcolare il numero di
     * promozioni rispetto alla soglia data. Il metodo ha un medesimo approccio
     * dell'algoritmo binarySearch, in cui Ã¨ risolto solo uno dei due
     * sotto-problemi
     * in maniera ricorsiva.
     * 
     * Il calcolo relativo all riga (125) conta gli elementi maggiori della soglia,
     * posti tra l'indice medio (m), calcolato nel vettore oppure nel sottovettore,
     * fino al valore contenuto all'estremo della struttura dati, essendo un array
     * ordinato in modo crescente.
     */
    public int countPromotions(double[] I, double k, int i, int j) {
        if (i > j) {
            return 0;
        } else {
            int m = (i + j) / 2;
            if (I[m] <= k) {
                return countPromotions(I, k, m + 1, j);
            } else {
                return ((j - m) + 1) + countPromotions(I, k, i, m - 1);
            }
        }
    }
}