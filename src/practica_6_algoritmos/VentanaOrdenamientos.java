package practica_6_algoritmos;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import static javafx.geometry.Orientation.VERTICAL;

public class VentanaOrdenamientos extends Application {

    @Override
    public void start(Stage ventana) {
        Path archivo = Path.of(getParameters().getRaw().getFirst());
        if (Files.notExists(archivo)) {
            FileChooser selector = new FileChooser();
            selector.setTitle("Selecciona el CSV de Billboard");
            selector.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV", "*.csv"));
            var elegido = selector.showOpenDialog(ventana);
            if (elegido == null) {
                return;
            }
            archivo = elegido.toPath();
        }

        try {
            DatosMusicales datos = new LectorCsv().leer(archivo);
            ventana.setTitle("Práctica 6 · Ordenamientos");
            ventana.setMinWidth(850);
            ventana.setMinHeight(620);
            ventana.setScene(crearEscena(datos));
            ventana.show();
        } catch (IOException | IllegalArgumentException error) {
            mostrarError("No se pudo abrir el CSV", error);
        }
    }

    private Scene crearEscena(DatosMusicales datos) {
        Label titulo = new Label("Billboard Hot 100");
        titulo.getStyleClass().add("titulo");
        Label detalle = new Label(String.format("Spotify Audio Features  ·  %,d registros  ·  %d columnas",
                datos.totalFilas(), datos.totalColumnas()));
        detalle.getStyleClass().add("detalle");
        VBox encabezado = new VBox(4, titulo, detalle);
        encabezado.getStyleClass().add("encabezado");

        Tab explorar = new Tab("Conjunto de datos", explorador(datos));
        Tab comparar = new Tab("Comparar ordenamientos", comparador(datos));
        TabPane pestanas = new TabPane(explorar, comparar);
        pestanas.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        BorderPane raiz = new BorderPane(pestanas);
        raiz.setTop(encabezado);
        Scene escena = new Scene(raiz, 1160, 780);
        escena.getStylesheets().add(getClass().getResource("interfaz.css").toExternalForm());
        return escena;
    }

    private SplitPane explorador(DatosMusicales datos) {
        TableView<Integer> tabla = new TableView<>();
        tabla.setId("tablaDatos");
        tabla.setFixedCellSize(27);
        tabla.setPlaceholder(new Label("El CSV no contiene registros."));
        for (int i = 0; i < datos.totalColumnas(); i++) {
            int columna = i;
            TableColumn<Integer, String> encabezado = new TableColumn<>(datos.nombreColumna(i));
            encabezado.setPrefWidth(Math.max(115, datos.nombreColumna(i).length() * 8 + 25));
            encabezado.setSortable(false);
            encabezado.setCellValueFactory(celda ->
                    new ReadOnlyStringWrapper(datos.valor(celda.getValue(), columna)));
            tabla.getColumns().add(encabezado);
        }
        List<Integer> filas = new ArrayList<>(datos.totalFilas());
        for (int fila = 0; fila < datos.totalFilas(); fila++) {
            filas.add(fila);
        }
        tabla.getItems().setAll(filas);

        ComboBox<String> columna = selectorColumnas(datos);
        columna.setId("columnaResumen");
        Label resumen = new Label();
        resumen.setId("resumenColumna");
        HBox controles = new HBox(14, campo("Distribución por columna", columna), resumen);
        controles.setAlignment(Pos.BOTTOM_LEFT);

        BarChart<Number, String> grafica = grafica("Registros");
        grafica.setId("graficaDistribucion");
        grafica.setMinHeight(300);
        VBox distribucion = new VBox(10, controles, grafica);
        distribucion.getStyleClass().add("panel");
        VBox.setVgrow(grafica, Priority.ALWAYS);
        columna.setOnAction(evento -> mostrarDistribucion(datos, columna, resumen, grafica));
        mostrarDistribucion(datos, columna, resumen, grafica);

        SplitPane division = new SplitPane(tabla, distribucion);
        division.setOrientation(VERTICAL);
        division.setDividerPositions(0.46);
        division.setPadding(new Insets(12));
        return division;
    }

    private void mostrarDistribucion(DatosMusicales datos, ComboBox<String> columna,
            Label resumen, BarChart<Number, String> grafica) {
        var resultado = ResumenColumna.calcular(datos, columna.getSelectionModel().getSelectedIndex());
        resumen.setText(String.format("%,d válidos  ·  %,d vacíos  ·  %,d distintos",
                resultado.validos(), resultado.vacios(), resultado.distintos()));
        var serie = new XYChart.Series<Number, String>();
        var etiquetas = new HashSet<String>();
        for (var frecuencia : resultado.frecuencias()) {
            String etiqueta = frecuencia.etiqueta();
            String corta = etiqueta.length() <= 40 ? etiqueta : etiqueta.substring(0, 39) + "…";
            String unica = corta;
            int sufijo = 2;
            while (!etiquetas.add(unica)) {
                unica = corta + " (" + sufijo++ + ")";
            }
            agregarBarra(serie, frecuencia.cantidad(), unica,
                    etiqueta + " · " + frecuencia.cantidad() + " registros");
        }
        grafica.getData().clear();
        grafica.getData().add(serie);
    }

    private VBox comparador(DatosMusicales datos) {
        ComboBox<String> columna = selectorColumnas(datos);
        columna.setId("columnaPrueba");
        ComboBox<Integer> tamano = new ComboBox<>();
        tamano.setId("tamanoPrueba");
        for (int opcion : new int[]{1_000, 5_000, 10_000, datos.totalFilas()}) {
            if (opcion > 0 && opcion <= datos.totalFilas() && !tamano.getItems().contains(opcion)) {
                tamano.getItems().add(opcion);
            }
        }
        tamano.getSelectionModel().selectFirst();

        Ordenamientos.Metodo[] metodos = Ordenamientos.Metodo.values();
        ComboBox<String> metodo = new ComboBox<>();
        metodo.setId("metodoPrueba");
        metodo.getItems().add("Todos los métodos");
        for (var opcion : metodos) {
            metodo.getItems().add(opcion.toString());
        }
        metodo.getSelectionModel().selectFirst();

        Button medir = new Button("Medir tiempos");
        medir.setId("medirTiempos");
        medir.getStyleClass().add("accion");
        medir.setDisable(tamano.getValue() == null);
        Label estado = new Label(medir.isDisabled() ? "El CSV no contiene registros."
                : "Elige los datos y presiona Medir tiempos.");
        estado.setId("estadoMedicion");
        HBox controles = new HBox(14, campo("Columna", columna), campo("Número de filas", tamano),
                campo("Método de ordenamiento", metodo), medir);
        controles.setAlignment(Pos.BOTTOM_LEFT);

        BarChart<Number, String> grafica = grafica("Tiempo (ms)");
        TableView<MedidorOrdenamiento.Resultado> tabla = tablaTiempos();
        HBox resultados = new HBox(16, grafica, tabla);
        HBox.setHgrow(grafica, Priority.ALWAYS);
        medir.setOnAction(evento -> {
            int indice = metodo.getSelectionModel().getSelectedIndex();
            Ordenamientos.Metodo[] seleccion = indice == 0 ? metodos
                    : new Ordenamientos.Metodo[]{metodos[indice - 1]};
            int indiceColumna = columna.getSelectionModel().getSelectedIndex();
            int cantidad = tamano.getValue();
            controles.setDisable(true);
            estado.setText("Midiendo " + datos.nombreColumna(indiceColumna) + "…");
            tabla.getItems().clear();
            grafica.getData().clear();

            Task<List<MedidorOrdenamiento.Resultado>> tarea = new Task<>() {
                @Override
                protected List<MedidorOrdenamiento.Resultado> call() {
                    return new MedidorOrdenamiento().medir(datos, indiceColumna, cantidad, seleccion);
                }
            };
            tarea.setOnSucceeded(fin -> {
                var mediciones = tarea.getValue();
                tabla.getItems().setAll(mediciones);
                var serie = new XYChart.Series<Number, String>();
                for (var resultado : mediciones) {
                    String nombre = resultado.metodo().toString();
                    agregarBarra(serie, resultado.milisegundos(), nombre, nombre + " · "
                            + String.format(Locale.ROOT, "%.3f ms", resultado.milisegundos()));
                }
                grafica.getData().add(serie);
                estado.setText(String.format("%s  ·  %,d filas por método",
                        datos.nombreColumna(indiceColumna), cantidad));
                controles.setDisable(false);
            });
            tarea.setOnFailed(fin -> {
                controles.setDisable(false);
                estado.setText("No se pudo completar la medición.");
                mostrarError("Error de medición", tarea.getException());
            });
            Thread hilo = new Thread(tarea, "medicion-ordenamientos");
            hilo.setDaemon(true);
            hilo.start();
        });

        VBox panel = new VBox(14, controles, resultados, estado);
        panel.getStyleClass().add("panel");
        VBox.setVgrow(resultados, Priority.ALWAYS);
        return panel;
    }

    private TableView<MedidorOrdenamiento.Resultado> tablaTiempos() {
        TableView<MedidorOrdenamiento.Resultado> tabla = new TableView<>();
        tabla.setId("tablaTiempos");
        tabla.setPrefWidth(292);
        tabla.setMinWidth(260);
        tabla.setMaxWidth(310);
        tabla.setFixedCellSize(32);
        tabla.setPlaceholder(new Label("Aquí aparecerán las mediciones."));
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TableColumn<MedidorOrdenamiento.Resultado, String> metodo = new TableColumn<>("Método");
        metodo.setMinWidth(158);
        metodo.setSortable(false);
        metodo.setCellValueFactory(celda -> new ReadOnlyStringWrapper(celda.getValue().metodo().toString()));
        TableColumn<MedidorOrdenamiento.Resultado, String> tiempo = new TableColumn<>("Tiempo (ms)");
        tiempo.setMinWidth(95);
        tiempo.setSortable(false);
        tiempo.setCellValueFactory(celda -> new ReadOnlyStringWrapper(
                String.format(Locale.ROOT, "%.3f", celda.getValue().milisegundos())));
        tabla.getColumns().add(metodo);
        tabla.getColumns().add(tiempo);
        return tabla;
    }

    private static ComboBox<String> selectorColumnas(DatosMusicales datos) {
        ComboBox<String> selector = new ComboBox<>();
        int seleccion = 0;
        for (int i = 0; i < datos.totalColumnas(); i++) {
            String nombre = datos.nombreColumna(i);
            selector.getItems().add(nombre);
            if ("tempo".equals(nombre)) {
                seleccion = i;
            }
        }
        selector.getSelectionModel().select(seleccion);
        return selector;
    }

    private static VBox campo(String texto, ComboBox<?> entrada) {
        Label etiqueta = new Label(texto);
        etiqueta.setLabelFor(entrada);
        return new VBox(4, etiqueta, entrada);
    }

    private static BarChart<Number, String> grafica(String unidad) {
        NumberAxis valores = new NumberAxis();
        valores.setLabel(unidad);
        BarChart<Number, String> grafica = new BarChart<>(valores, new CategoryAxis());
        grafica.setAnimated(false);
        grafica.setLegendVisible(false);
        grafica.setHorizontalGridLinesVisible(false);
        return grafica;
    }

    private static void agregarBarra(XYChart.Series<Number, String> serie,
            Number valor, String etiqueta, String detalle) {
        var barra = new XYChart.Data<Number, String>(valor, etiqueta);
        barra.nodeProperty().addListener((observable, anterior, nodo) -> {
            if (nodo != null) {
                Tooltip.install(nodo, new Tooltip(detalle));
            }
        });
        serie.getData().add(barra);
    }

    private static void mostrarError(String titulo, Throwable error) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setHeaderText(titulo);
        alerta.setContentText(error.getMessage());
        alerta.showAndWait();
    }
}
