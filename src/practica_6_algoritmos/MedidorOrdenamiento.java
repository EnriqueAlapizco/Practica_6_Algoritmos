package practica_6_algoritmos;

import java.util.ArrayList;
import java.util.List;

final class MedidorOrdenamiento {

    record Resultado(String columna, int cantidad, Ordenamientos.Metodo metodo,
            long nanosegundos) {

        double milisegundos() {
            return nanosegundos / 1_000_000.0;
        }
    }

    List<Resultado> medir(DatosMusicales datos, int columna, int cantidad,
            Ordenamientos.Metodo... metodos) {
        Comparable<?>[] muestra = datos.arregloColumna(columna, cantidad);
        var comparador = datos.comparadorColumna(columna);
        List<Resultado> resultados = new ArrayList<>();

        for (Ordenamientos.Metodo metodo : metodos) {
            Comparable<?>[] copia = muestra.clone();
            long inicio = System.nanoTime();
            Ordenamientos.ordenar(copia, comparador, metodo);
            long duracion = System.nanoTime() - inicio;
            resultados.add(new Resultado(datos.nombreColumna(columna), cantidad, metodo, duracion));
        }
        return resultados;
    }
}
