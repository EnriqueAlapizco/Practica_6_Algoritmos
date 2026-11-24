# Práctica 6 — Ordenamientos

## Conjunto de datos

Usamos la versión 3 de [Billboard Hot 100 & Spotify Audio Features](https://www.kaggle.com/datasets/luthfimuthathohirin/billboard-hot-100-spotify-audio-features/versions/3), publicada por Luthfi Muthathohirin en Kaggle con licencia CC0.

1. Descarga y descomprime el archivo de Kaggle.
2. Coloca `billboard_hot_100_spotify_audio_features_enriched.csv` en la carpeta `datos` del proyecto. El CSV se obtiene desde la fuente original.

El CSV verificado contiene **32,503 filas de datos y 33 columnas**. Está en UTF-8, usa coma como separador y encierra entre comillas campos que contienen comas. Tiene campos vacíos y valores repetidos en `song_id`, por lo que se conservarán todas las filas al cargarlo.

| Tipo | Ejemplos |
| --- | --- |
| Texto y categorías | `song`, `performer`, `spotify_genre`, `musical_era` |
| Números | `danceability`, `energy`, `tempo`, `peak_position` |
| Booleano | `spotify_track_explicit` |
| Fechas | `first_week`, `last_week` |

El explorador de Kaggle muestra una tabla de registros y un resumen por columna. Para la interfaz, la referencia son los histogramas numéricos, las frecuencias de categorías y el conteo de valores faltantes.

## Ejecutar la carga

Abre el proyecto en NetBeans con JDK 24 y presiona F6. La consola muestra el total de filas y columnas, seguido por los tiempos en milisegundos para los seis métodos. Se prueban las primeras 1,000, 5,000 y 10,000 filas de cada columna. Los valores vacíos se conservan y quedan al final del arreglo ordenado.

Cada método recibe una copia de la misma muestra. La copia se crea antes de iniciar el cronómetro y `System.nanoTime()` mide una ejecución del ordenamiento. Los tiempos pueden variar entre ejecuciones.

La selección directa sigue el pseudocódigo de la diapositiva 11 de `Complejidad-Ordenamientos2026.pdf`. Shell sigue los saltos y los intercambios de la diapositiva 15.

SHA-256 del CSV verificado: `be3c7c02b24d2f671b029e338e191a46c5a8354a6abd205d2efad8f461cae7fb`.
