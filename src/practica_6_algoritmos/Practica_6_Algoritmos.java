package practica_6_algoritmos;

import java.nio.file.Path;
import javafx.application.Application;

public class Practica_6_Algoritmos {

    public static void main(String[] args) {
        Path archivo = Path.of("datos", "billboard_hot_100_spotify_audio_features_enriched.csv");
        if (args.length > 0) {
            archivo = Path.of(args[0]);
        }
        Application.launch(VentanaOrdenamientos.class, archivo.toString());
    }
}
