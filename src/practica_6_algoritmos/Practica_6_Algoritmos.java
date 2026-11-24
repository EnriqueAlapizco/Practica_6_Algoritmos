/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package practica_6_algoritmos;

import java.io.IOException;
import java.nio.file.Path;

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

            probarOrdenamientos(datos);
        } catch (IOException error) {
            System.err.println("No se pudo cargar el CSV " + archivo + ": " + error.getMessage());
        }
    }

    private static void probarOrdenamientos(DatosMusicales datos) {
        for (int columna = 0; columna < datos.totalColumnas(); columna++) {
            if (datos.nombreColumna(columna).equals("tempo")) {
                int cantidad = Math.min(datos.totalFilas(), 1_000);
                Comparable<?>[] muestra = datos.arregloColumna(columna, cantidad);
                var comparador = datos.comparadorColumna(columna);
                System.out.println("Demostración con tempo: " + cantidad + " filas");

                for (Ordenamientos.Metodo metodo : Ordenamientos.Metodo.values()) {
                    Comparable<?>[] copia = muestra.clone();
                    Ordenamientos.ordenar(copia, comparador, metodo);
                    System.out.println(metodo + ": " + copia.length + " valores ordenados.");
                }
                return;
            }
        }
    }
}
