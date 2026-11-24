/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package practica_6_algoritmos;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;

/**
 *
 * @author enriq
 */
public class Practica_6_Algoritmos {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Path archivo = Path.of("datos", "billboard_hot_100_spotify_audio_features_enriched.csv");
        if (args.length > 0) {
            archivo = Path.of(args[0]);
        }

        try {
            DatosMusicales datos = new LectorCsv().leer(archivo);
            System.out.println("Filas: " + datos.totalFilas());
            System.out.println("Columnas: " + datos.totalColumnas());

            mostrarTiempos(datos);
        } catch (IOException error) {
            System.err.println("No se pudo cargar el CSV " + archivo + ": " + error.getMessage());
        }
    }

    private static void mostrarTiempos(DatosMusicales datos) {
        MedidorOrdenamiento medidor = new MedidorOrdenamiento();
        Ordenamientos.Metodo[] metodos = Ordenamientos.Metodo.values();
        int[] tamanos = {1_000, 5_000, 10_000};

        System.out.printf("%-27s %7s", "Columna", "Filas");
        for (Ordenamientos.Metodo metodo : metodos) {
            System.out.printf(" %22s", metodo);
        }
        System.out.println("  (ms)");

        for (int columna = 0; columna < datos.totalColumnas(); columna++) {
            int cantidadAnterior = 0;
            for (int tamano : tamanos) {
                int cantidad = Math.min(tamano, datos.totalFilas());
                if (cantidad == cantidadAnterior) {
                    break;
                }
                var resultados = medidor.medir(datos, columna, cantidad, metodos);
                System.out.printf("%-27s %7d", datos.nombreColumna(columna), cantidad);
                for (MedidorOrdenamiento.Resultado resultado : resultados) {
                    System.out.printf(Locale.ROOT, " %22.3f", resultado.milisegundos());
                }
                System.out.println();
                cantidadAnterior = cantidad;
            }
        }
    }
}
