package practica_6_algoritmos;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

record ResumenColumna(int validos, int vacios, int distintos, List<Frecuencia> frecuencias) {

    record Frecuencia(String etiqueta, int cantidad) {
    }

    static ResumenColumna calcular(DatosMusicales datos, int columna) {
        Map<Comparable<?>, Integer> conteos = new LinkedHashMap<>();
        int vacios = 0;
        for (Comparable<?> valor : datos.arregloColumna(columna, datos.totalFilas())) {
            if (valor == null) {
                vacios++;
            } else {
                conteos.merge(valor, 1, Integer::sum);
            }
        }

        List<Frecuencia> frecuencias = switch (datos.tipoColumna(columna)) {
            case NUMERO -> intervalos(conteos);
            case FECHA -> decadas(conteos);
            default -> categorias(conteos);
        };
        return new ResumenColumna(datos.totalFilas() - vacios, vacios, conteos.size(), frecuencias);
    }

    private static List<Frecuencia> intervalos(Map<Comparable<?>, Integer> conteos) {
        double minimo = Double.POSITIVE_INFINITY;
        double maximo = Double.NEGATIVE_INFINITY;
        int especiales = 0;
        int finitos = 0;
        for (var entrada : conteos.entrySet()) {
            double valor = (Double) entrada.getKey();
            if (Double.isFinite(valor)) {
                minimo = Math.min(minimo, valor);
                maximo = Math.max(maximo, valor);
                finitos += entrada.getValue();
            } else {
                especiales += entrada.getValue();
            }
        }
        List<Frecuencia> resultado = new ArrayList<>();
        if (finitos > 0 && minimo == maximo) {
            resultado.add(new Frecuencia(formato(minimo), finitos));
        } else if (finitos > 0) {
            int[] rangos = new int[10];
            double amplitud = (maximo - minimo) / rangos.length;
            for (var entrada : conteos.entrySet()) {
                double valor = (Double) entrada.getKey();
                if (Double.isFinite(valor)) {
                    int intervalo = Math.min((int) ((valor - minimo) / amplitud), rangos.length - 1);
                    rangos[intervalo] += entrada.getValue();
                }
            }
            for (int i = 0; i < rangos.length; i++) {
                double inferior = minimo + i * amplitud;
                double superior = i == rangos.length - 1 ? maximo : minimo + (i + 1) * amplitud;
                resultado.add(new Frecuencia(formato(inferior) + " a " + formato(superior), rangos[i]));
            }
        }
        if (especiales > 0) {
            resultado.add(new Frecuencia("Valores no finitos", especiales));
        }
        return resultado;
    }

    private static String formato(double valor) {
        return BigDecimal.valueOf(valor).round(new MathContext(7)).stripTrailingZeros().toPlainString();
    }

    private static List<Frecuencia> decadas(Map<Comparable<?>, Integer> conteos) {
        Map<Integer, Integer> grupos = new TreeMap<>();
        for (var entrada : conteos.entrySet()) {
            int anio = ((LocalDate) entrada.getKey()).getYear();
            int decada = Math.floorDiv(anio, 10) * 10;
            grupos.merge(decada, entrada.getValue(), Integer::sum);
        }
        List<Frecuencia> resultado = new ArrayList<>();
        grupos.forEach((decada, cantidad) ->
                resultado.add(new Frecuencia(decada + " a " + (decada + 9), cantidad)));
        return resultado;
    }

    private static List<Frecuencia> categorias(Map<Comparable<?>, Integer> conteos) {
        var entradas = new ArrayList<>(conteos.entrySet());
        entradas.sort(Collections.reverseOrder(Map.Entry.comparingByValue()));
        List<Frecuencia> resultado = new ArrayList<>();
        int restantes = 0;
        for (var entrada : entradas) {
            if (resultado.size() < 8) {
                resultado.add(new Frecuencia(entrada.getKey().toString(), entrada.getValue()));
            } else {
                restantes += entrada.getValue();
            }
        }
        if (restantes > 0) {
            resultado.add(new Frecuencia("Otras categorías", restantes));
        }
        return resultado;
    }
}
