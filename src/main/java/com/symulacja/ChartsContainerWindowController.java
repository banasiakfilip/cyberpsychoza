package com.symulacja;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.GridPane;
import java.util.List;

/**
 * Kontroler dla okna FXML wyświetlającego wykresy danych symulacji.
 * Odpowiada za dynamiczne tworzenie i umieszczanie wykresów w siatce (GridPane).
 */
public class ChartsContainerWindowController {

    @FXML
    private GridPane chartsGridPane; // Siatka, w której będą umieszczane wykresy

    /**
     * Metoda inicjalizacyjna kontrolera, wywoływana po załadowaniu pliku FXML.
     * Obecnie pusta, może służyć do przyszłej konfiguracji.
     */
    @FXML
    public void initialize() {
        // Potencjalna przyszła inicjalizacja
    }

    /**
     * Ustawia dane dla wykresów i generuje je w siatce.
     * Dla każdej serii danych tworzony jest osobny wykres liniowy.
     * @param allSeries Lista serii danych ({@link XYChart.Series}) do wyświetlenia na wykresach.
     */
    public void setChartsData(List<XYChart.Series<Number, Number>> allSeries) {
        chartsGridPane.getChildren().clear(); // Wyczyść poprzednie wykresy

        String[] chartTitles = {
                "Całkowita Populacja",
                "Poziom Degeneracji",
                "Siła Policji"
        };
        String[] yAxisLabels = {
                "Populacja",
                "Degeneracja (%)",
                "Siła Policji (%)"
        };

        for (int i = 0; i < allSeries.size(); i++) {
            if (i >= chartTitles.length || i >= yAxisLabels.length) {
                System.err.println("Ostrzeżenie: Niezgodna liczba serii danych (" + allSeries.size() +
                        ") z liczbą zdefiniowanych tytułów/etykiet wykresów (" + chartTitles.length + "). Pomijanie dodatkowych serii.");
                continue;
            }

            NumberAxis xAxis = new NumberAxis();
            xAxis.setLabel("Epoka");
            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel(yAxisLabels[i]);

            LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
            chart.setTitle(chartTitles[i]);
            chart.setCreateSymbols(false); // Nie pokazuj punktów na liniach
            chart.setLegendVisible(false); // Ukryj legendę dla pojedynczej serii

            chart.getData().add(allSeries.get(i));

            // Układ 2xN, wykresy są dodawane do siatki
            // Pierwsza kolumna: i % 2 == 0, Druga kolumna: i % 2 == 1
            // Pierwszy wiersz: i / 2 == 0, Drugi wiersz: i / 2 == 1, itd.
            GridPane.setConstraints(chart, i % 2, i / 2);
            chartsGridPane.getChildren().add(chart);
        }
    }
}