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

            int[] tamanos = {1_000, 5_000, 10_000};
            for (int tamano : tamanos) {
                if (tamano <= datos.totalFilas()) {
                    for (int columna = 0; columna < datos.totalColumnas(); columna++) {
                        datos.arregloColumna(columna, tamano);
                    }
                    System.out.println("Muestras de " + tamano + " filas preparadas.");
                }
            }
        } catch (IOException error) {
            System.err.println("No se pudo cargar el CSV " + archivo + ": " + error.getMessage());
        }
    }
}
