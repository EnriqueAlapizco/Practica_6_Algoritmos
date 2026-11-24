package practica_6_algoritmos;

import java.time.LocalDate;
import java.util.List;

final class DatosMusicales {

    enum Tipo {
        TEXTO, NUMERO, BOOLEANO, FECHA
    }

    private final String[] encabezados;
    private final List<String[]> filas;

    DatosMusicales(String[] encabezados, List<String[]> filas) {
        this.encabezados = encabezados;
        this.filas = filas;
    }

    int totalFilas() {
        return filas.size();
    }

    int totalColumnas() {
        return encabezados.length;
    }

    String nombreColumna(int columna) {
        return encabezados[columna];
    }

    String valor(int fila, int columna) {
        return filas.get(fila)[columna];
    }

    Tipo tipoColumna(int columna) {
        return switch (nombreColumna(columna)) {
            case "spotify_track_duration_ms", "danceability", "energy", "key",
                    "loudness", "mode", "speechiness", "acousticness", "instrumentalness",
                    "liveness", "valence", "tempo", "time_signature", "spotify_track_popularity",
                    "peak_position", "weeks_on_chart", "confidence_score" -> Tipo.NUMERO;
            case "spotify_track_explicit" -> Tipo.BOOLEANO;
            case "first_week", "last_week" -> Tipo.FECHA;
            default -> Tipo.TEXTO;
        };
    }

    Comparable<?>[] arregloColumna(int columna, int cantidad) {
        if (cantidad < 0 || cantidad > totalFilas()) {
            throw new IllegalArgumentException("Tamaño de muestra fuera del rango de filas.");
        }

        Tipo tipo = tipoColumna(columna);
        Comparable<?>[] arreglo = new Comparable<?>[cantidad];
        for (int fila = 0; fila < cantidad; fila++) {
            String texto = valor(fila, columna);
            if (!texto.isBlank()) {
                arreglo[fila] = switch (tipo) {
                    case TEXTO -> texto;
                    case NUMERO -> Double.valueOf(texto);
                    case BOOLEANO -> Boolean.valueOf(texto);
                    case FECHA -> LocalDate.parse(texto);
                };
            }
        }
        return arreglo;
    }
}
