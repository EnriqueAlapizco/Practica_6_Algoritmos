package practica_6_algoritmos;

import java.io.IOException;
import java.io.PushbackReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

final class LectorCsv {

    DatosMusicales leer(Path archivo) throws IOException {
        try (var lector = new PushbackReader(
                Files.newBufferedReader(archivo, StandardCharsets.UTF_8))) {
            int primero = lector.read();
            if (primero != -1 && primero != '\uFEFF') {
                lector.unread(primero);
            }

            String[] encabezados = leerFila(lector);
            if (encabezados == null) {
                throw new IOException("El archivo CSV está vacío.");
            }

            List<String[]> filas = new ArrayList<>();
            String[] fila = leerFila(lector);
            while (fila != null) {
                if (fila.length != encabezados.length) {
                    throw new IOException("Cantidad de columnas incorrecta en el registro "
                            + (filas.size() + 1) + ".");
                }
                filas.add(fila);
                fila = leerFila(lector);
            }
            return new DatosMusicales(encabezados, filas);
        }
    }

    private String[] leerFila(PushbackReader lector) throws IOException {
        List<String> campos = new ArrayList<>();
        StringBuilder campo = new StringBuilder();
        boolean entreComillas = false;
        int caracter = lector.read();
        if (caracter == -1) {
            return null;
        }

        while (caracter != -1) {
            if (caracter == '"') {
                if (entreComillas) {
                    int siguiente = lector.read();
                    if (siguiente == '"') {
                        campo.append('"');
                    } else {
                        entreComillas = false;
                        if (siguiente != -1) {
                            lector.unread(siguiente);
                        }
                    }
                } else {
                    entreComillas = true;
                }
            } else if (!entreComillas && caracter == ',') {
                campos.add(campo.toString());
                campo.setLength(0);
            } else if (!entreComillas && (caracter == '\n' || caracter == '\r')) {
                if (caracter == '\r') {
                    int siguiente = lector.read();
                    if (siguiente != -1 && siguiente != '\n') {
                        lector.unread(siguiente);
                    }
                }
                break;
            } else {
                campo.append((char) caracter);
            }
            caracter = lector.read();
        }

        if (entreComillas) {
            throw new IOException("Hay un campo sin comillas de cierre.");
        }
        campos.add(campo.toString());
        return campos.toArray(new String[0]);
    }
}
