/*
 * Nome: Matteo
 * Cognome: Canghiari
 * Email: matteo.canghiari@studio.unibo.it
 * Matricola: 1032059
 * 
 * Considerazioni; 
 * L'implementazione per calcolare il numero di elementi avviene tramite l'uso dell'algoritmo binarySearch, legato alla tecnica di programmazione
 * divide-et-impera. Infatti la ricerca di elementi che siano maggiori di una certa soglia, avviene tramite la suddivisione in sotto-problemi indipendenti,
 * da cui ne deriva il significato del calcolo dell'indice medio. Rispetto al valore del vettore contenuto nell'indice medio avviene la chiamata ricorsiva 
 * sulla porzione di Array di riferimento, per verificare il numero complessivo di celle che abbiano un valore maggiore della soglia (k).
 * 
 * Il calcolo ricorsivo per indicare quali valori soddisfino la richiesta risulta ((j - m) + 1), dove
 * - j, rappresenta l'indice dell'ultima cella della struttura dati o sottostruttura;
 * - m, indice medio calcolato come (j + i) / 2.
 * Quindi il numero di elementi maggiori della soglia è dato dalla differenza degli indici j e m, e dalla cella posta nell'indice medio, 
 * poichè, come da condizione della selezione, (A[m] > k), per cui avviene la chiamata risorsiva sul sottovettore posto alla sinistra di m.
 * Infine il costo computazionale è pari al limite asintotico superiore O(log n), ossia complessità logaritmica rispetto al caso medio.
 * 
 * Nota: 
 * Il file dato a riga di comando ha la medesima impostazione fornita dalla traccia dell'esame.
 */

import java.util.Locale;
import java.util.Scanner;
import java.io.File;

public class Esercizio3 {
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        if (args.length != 1) {
            System.out.println("Usage: java Esercizio3 Esercizio.txt");
            System.exit(1);
        }

        Promotion promotion = new Promotion(args[0]);
        System.out.println("Il numero di dipendenti soggetti alla promozione: " + "\n"
                + promotion.countPromotions(promotion.I, promotion.k, 0, (promotion.n - 1)));
    }
}

class Promotion {
    int n;
    int i = 0;
    double k;

    double[] I;

    public Promotion(String nameFile) {
        File file = new File(nameFile);
        scanFile(file);
    }

    public void scanFile(File file) {
        try {
            Scanner scanFile = new Scanner(file);

            while (scanFile.hasNextLine()) {
                String line = scanFile.nextLine();

                if (line.equals("# numero di dipendenti")) {
                    line = scanFile.nextLine();

                    /*
                     * Lettura della dimensione della struttura dati e inizializzazione dell'array.
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
        } catch (Exception e) {
            System.out.println("Attenzione lettura errata del file di input!");
            System.exit(0);
        }
    }

    /*
     * Metodo countPromotions, utilizzato per poter calcolare il numero di
     * promozioni rispetto alla soglia data. Il metodo ha un medesimo approccio
     * dell'algoritmo binarySearch, in cui e' risolto solo uno dei due
     * sotto-problemi indipendenti in maniera ricorsiva.
     * 
     * Il calcolo relativo all riga (128) conta gli elementi maggiori della soglia,
     * posti tra l'indice medio (m), calcolato nel vettore oppure nel sotto-vettore,
     * fino al valore contenuto all'estremo della struttura dati, essendo un array
     * ordinato in maniera crescente.
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