package practica_6_algoritmos;

import java.util.Arrays;
import java.util.Comparator;

final class Ordenamientos {

    enum Metodo {
        SELECCION_DIRECTA("Selección directa"),
        SHELL_SORT("Shell sort"),
        QUICKSORT("Quicksort"),
        MERGE_SORT("Merge sort"),
        ARRAYS_SORT("Arrays.sort()"),
        ARRAYS_PARALLEL_SORT("Arrays.parallelSort()");

        private final String nombre;

        Metodo(String nombre) {
            this.nombre = nombre;
        }

        @Override
        public String toString() {
            return nombre;
        }
    }

    private Ordenamientos() {
    }

    static <T> void ordenar(T[] arreglo, Comparator<? super T> comparador, Metodo metodo) {
        switch (metodo) {
            case SELECCION_DIRECTA -> seleccionDirecta(arreglo, comparador);
            case SHELL_SORT -> shellSort(arreglo, comparador);
            case QUICKSORT -> quicksort(arreglo, comparador, 0, arreglo.length - 1);
            case MERGE_SORT -> mergeSort(arreglo, comparador, arreglo.clone(), 0, arreglo.length - 1);
            case ARRAYS_SORT -> Arrays.sort(arreglo, comparador);
            case ARRAYS_PARALLEL_SORT -> Arrays.parallelSort(arreglo, comparador);
        }
    }

   
    private static <T> void seleccionDirecta(T[] arreglo, Comparator<? super T> comparador) {
        for (int i = 0; i < arreglo.length - 1; i++) {
            T menor = arreglo[i];
            int k = i;
            for (int j = i + 1; j < arreglo.length; j++) {
                if (comparador.compare(arreglo[j], menor) < 0) {
                    menor = arreglo[j];
                    k = j;
                }
            }
            arreglo[k] = arreglo[i];
            arreglo[i] = menor;
        }
    }

    
    private static <T> void shellSort(T[] arreglo, Comparator<? super T> comparador) {
        int salto = arreglo.length + 1;
        while (salto > 1) {
            salto /= 2;
            boolean huboIntercambio;
            do {
                huboIntercambio = false;
                for (int i = 0; i < arreglo.length - salto; i++) {
                    if (comparador.compare(arreglo[i], arreglo[i + salto]) > 0) {
                        intercambiar(arreglo, i, i + salto);
                        huboIntercambio = true;
                    }
                }
            } while (huboIntercambio);
        }
    }

    private static <T> void quicksort(T[] arreglo, Comparator<? super T> comparador,
            int inicio, int fin) {
        while (inicio < fin) {
            int izquierda = inicio;
            int derecha = fin;
            T pivote = arreglo[inicio + (fin - inicio) / 2];

            while (izquierda <= derecha) {
                while (comparador.compare(arreglo[izquierda], pivote) < 0) {
                    izquierda++;
                }
                while (comparador.compare(arreglo[derecha], pivote) > 0) {
                    derecha--;
                }
                if (izquierda <= derecha) {
                    intercambiar(arreglo, izquierda, derecha);
                    izquierda++;
                    derecha--;
                }
            }

            // La parte menor se resuelve con recursión para limitar la pila.
            if (derecha - inicio < fin - izquierda) {
                quicksort(arreglo, comparador, inicio, derecha);
                inicio = izquierda;
            } else {
                quicksort(arreglo, comparador, izquierda, fin);
                fin = derecha;
            }
        }
    }

    private static <T> void mergeSort(T[] arreglo, Comparator<? super T> comparador,
            T[] auxiliar, int inicio, int fin) {
        if (inicio >= fin) {
            return;
        }
        int mitad = inicio + (fin - inicio) / 2;
        mergeSort(arreglo, comparador, auxiliar, inicio, mitad);
        mergeSort(arreglo, comparador, auxiliar, mitad + 1, fin);

        for (int i = inicio; i <= fin; i++) {
            auxiliar[i] = arreglo[i];
        }

        int izquierda = inicio;
        int derecha = mitad + 1;
        int destino = inicio;
        while (izquierda <= mitad && derecha <= fin) {
            if (comparador.compare(auxiliar[izquierda], auxiliar[derecha]) <= 0) {
                arreglo[destino] = auxiliar[izquierda];
                izquierda++;
            } else {
                arreglo[destino] = auxiliar[derecha];
                derecha++;
            }
            destino++;
        }
        while (izquierda <= mitad) {
            arreglo[destino] = auxiliar[izquierda];
            izquierda++;
            destino++;
        }
        while (derecha <= fin) {
            arreglo[destino] = auxiliar[derecha];
            derecha++;
            destino++;
        }
    }

    private static <T> void intercambiar(T[] arreglo, int i, int j) {
        T auxiliar = arreglo[i];
        arreglo[i] = arreglo[j];
        arreglo[j] = auxiliar;
    }
}
