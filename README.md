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

## Ejecutar la aplicación

Abre el proyecto en NetBeans con JDK 24 y JavaFX 26 y presiona F6. Si tu SDK de JavaFX está en otra carpeta, agrega `javafx.lib=C:/ruta/javafx-sdk-26/lib` a `nbproject/private/private.properties`.

En **Conjunto de datos** puedes explorar el CSV y consultar la distribución de cada columna. En **Comparar ordenamientos** elige una columna, el tamaño de la muestra y un método o **Todos los métodos**. Presiona **Medir tiempos** para ver la gráfica y los tiempos en milisegundos.

Cada método recibe una copia de la misma muestra. La copia se crea antes de iniciar el cronómetro y `System.nanoTime()` mide una ejecución del ordenamiento. Los valores vacíos quedan al final del arreglo ordenado. Los tiempos pueden variar entre ejecuciones.

La selección directa sigue el pseudocódigo de la diapositiva 11 de `Complejidad-Ordenamientos2026.pdf`. Shell sigue los saltos y los intercambios de la diapositiva 15.

SHA-256 del CSV verificado: `be3c7c02b24d2f671b029e338e191a46c5a8354a6abd205d2efad8f461cae7fb`.
