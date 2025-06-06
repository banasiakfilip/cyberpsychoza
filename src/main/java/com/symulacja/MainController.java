package com.symulacja;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Główny kontroler interfejsu użytkownika (GUI) dla aplikacji symulacyjnej.
 * Zarządza interakcjami użytkownika, inicjalizacją symulacji,
 * aktualizacją interfejsu w czasie rzeczywistym oraz wyświetlaniem wyników.
 */
public class MainController {

    // Pola FXML - powiązane z elementami UI z pliku main_view.fxml
    @FXML private TextField populacjaTextField;
    @FXML private TextField elitaTextField;
    @FXML private TextField klasaSredniaTextField;
    @FXML private TextField implantyTextField;
    @FXML private TextField stabilizatoryTextField;
    @FXML private TextField policjaTextField;
    @FXML private TextField epokiTextField;
    @FXML private Label epokaLabel;
    @FXML private Label currentPopulationLabel;
    @FXML private Label eventLabel;
    @FXML private ProgressBar degeneracjaProgressBar;
    @FXML private Canvas populationCanvas;
    @FXML private Button startButton;
    @FXML private TextArea eventLogTextArea;

    private Symulacja symulacja;
    private int liczbaEpokDoSymulacji; // Całkowita liczba epok do przeprowadzenia w symulacji
    // private int currentEpokaNumber = 0; // Numer bieżącej epoki jest pobierany z obiektu symulacji

    private static final long OPOZNIENIE_EPOKI_MS = 1000; // Opóźnienie między epokami w milisekundach
    private static final String CSV_FILE_PATH = "RaportCSV.csv"; // Ścieżka do pliku raportu CSV

    /**
     * Metoda inicjalizacyjna kontrolera, wywoływana automatycznie po załadowaniu pliku FXML.
     * Ustawia domyślne wartości w polach tekstowych interfejsu oraz inicjalizuje etykiety i progress bar.
     */
    @FXML
    public void initialize() {
        // Ustawienie domyślnych wartości parametrów symulacji
        populacjaTextField.setText("100000");
        elitaTextField.setText("10"); // %
        klasaSredniaTextField.setText("30"); // %
        implantyTextField.setText("50"); // %
        stabilizatoryTextField.setText("70"); // %
        policjaTextField.setText("80"); // %
        epokiTextField.setText("10"); // liczba epok

        // Inicjalizacja etykiet i paska postępu
        epokaLabel.setText("Epoka: 0");
        currentPopulationLabel.setText("Populacja: 0");
        eventLabel.setText("Oczekiwanie na symulację...");
        degeneracjaProgressBar.setProgress(0);
        drawPopulationDistribution(0, 0, 0); // Wyczyść canvas

        if (eventLogTextArea != null) {
            eventLogTextArea.setText("Dziennik zdarzeń symulacji:\n");
        }
    }

    /**
     * Rozpoczyna proces symulacji po kliknięciu przycisku "Start".
     * Parsuje dane wejściowe, waliduje je, inicjalizuje obiekt {@link Symulacja}
     * i uruchamia symulację w nowym wątku.
     */
    @FXML
    private void startSimulation() {
        try {
            // Parsowanie danych wejściowych z pól tekstowych
            int poczatkowaLiczebnoscPopulacji = Integer.parseInt(populacjaTextField.getText());
            int podzialElitaProc = Integer.parseInt(elitaTextField.getText());
            int podzialKlasaSredniaProc = Integer.parseInt(klasaSredniaTextField.getText());
            int poczatkowyPoziomImplantow = Integer.parseInt(implantyTextField.getText());
            int poczatkowyDostepDoStabilizatorow = Integer.parseInt(stabilizatoryTextField.getText());
            int poczatkowaSilaPolicji = Integer.parseInt(policjaTextField.getText());
            this.liczbaEpokDoSymulacji = Integer.parseInt(epokiTextField.getText());

            // Walidacja danych wejściowych
            if (poczatkowaLiczebnoscPopulacji <= 0 || podzialElitaProc < 0 || podzialKlasaSredniaProc < 0 ||
                    (podzialElitaProc + podzialKlasaSredniaProc) > 100 ||
                    poczatkowyPoziomImplantow < 0 || poczatkowyPoziomImplantow > 100 ||
                    poczatkowyDostepDoStabilizatorow < 0 || poczatkowyDostepDoStabilizatorow > 100 ||
                    poczatkowaSilaPolicji < 0 || poczatkowaSilaPolicji > 100 ||
                    liczbaEpokDoSymulacji <= 0) {
                showAlert("Błąd danych wejściowych", "Wprowadzone wartości są nieprawidłowe. Sprawdź, czy liczby są dodatnie, a procenty mieszczą się w zakresie 0-100 i sumują się poprawnie.");
                return;
            }

            // Obliczanie liczebności poszczególnych grup na podstawie procentów
            int elitaLiczba = (int)(poczatkowaLiczebnoscPopulacji * podzialElitaProc / 100.0);
            int klasaSredniaLiczba = (int)(poczatkowaLiczebnoscPopulacji * podzialKlasaSredniaProc / 100.0);
            int slumsyLiczba = poczatkowaLiczebnoscPopulacji - elitaLiczba - klasaSredniaLiczba;

            if (slumsyLiczba < 0) { // Dodatkowa walidacja, choć poprzednia powinna to wyłapać
                showAlert("Błąd podziału populacji", "Suma procentów Elity i Klasy Średniej nie może przekraczać 100%. To spowodowało ujemną liczbę slumsów.");
                return;
            }

            // Czyszczenie dziennika zdarzeń
            if (eventLogTextArea != null) {
                eventLogTextArea.clear();
                eventLogTextArea.setText("Dziennik zdarzeń symulacji:\n");
            }

            // Inicjalizacja obiektu symulacji
            symulacja = new Symulacja();
            symulacja.ustawParametryPoczatkowe(poczatkowaLiczebnoscPopulacji,
                    elitaLiczba, klasaSredniaLiczba, slumsyLiczba,
                    poczatkowyPoziomImplantow, poczatkowyDostepDoStabilizatorow, poczatkowaSilaPolicji);

            // Blokowanie kontrolek i uruchomienie symulacji w nowym wątku
            startButton.setDisable(true);
            setControlsDisable(true);
            new Thread(this::runSimulationLoop).start(); // Użycie referencji do metody

        } catch (NumberFormatException e) {
            showAlert("Błąd formatu danych", "Wprowadzone wartości muszą być liczbami całkowitymi.");
        } catch (Exception e) { // Ogólny handler dla nieprzewidzianych błędów
            showAlert("Błąd inicjalizacji symulacji", "Wystąpił nieoczekiwany błąd: " + e.getMessage());
            e.printStackTrace(); // Logowanie błędu do konsoli
        }
    }

    /**
     * Główna pętla symulacji wykonywana w osobnym wątku.
     * Iteruje przez zadaną liczbę epok, wykonuje cykle symulacji,
     * aktualizuje UI i sprawdza warunki końca.
     */
    private void runSimulationLoop() {
        for (int i = 1; i <= liczbaEpokDoSymulacji; i++) {
            symulacja.wykonajCyklSymulacji();

            // Pobieranie danych do aktualizacji UI
            final Epoka epokaDoLogu = symulacja.getAktualnaEpoka();
            final Zdarzenie zdarzenieDoLogu = symulacja.getOstatnieZdarzenie();
            // Numer epoki jest teraz pobierany bezpośrednio z obiektu epoki
            final String komunikatDodatkowyDoLogu = symulacja.getKomunikatDodatkowyOskutkachEpoki();

            // Aktualizacja UI w wątku JavaFX
            Platform.runLater(() -> updateUI(symulacja.getPopulacja(), symulacja.getPolicja(), epokaDoLogu,
                    zdarzenieDoLogu, komunikatDodatkowyDoLogu));

            // Sprawdzanie warunków końca symulacji
            if (symulacja.sprawdzWarunkiKonca()) {
                final String finalScenariuszKonca = symulacja.getScenariuszKonca();
                Platform.runLater(() -> endSimulation(finalScenariuszKonca));
                return; // Zakończ pętlę symulacji
            }

            // Opóźnienie między epokami
            try {
                Thread.sleep(OPOZNIENIE_EPOKI_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Przywróć flagę przerwania
                System.err.println("Wątek symulacji został przerwany.");
                // Można rozważyć wcześniejsze zakończenie symulacji z odpowiednim komunikatem
                Platform.runLater(() -> endSimulation("Symulacja przerwana przez użytkownika lub błąd wątku."));
                break; // Przerwij pętlę
            }
        }

        // Jeśli pętla zakończyła się naturalnie (osiągnięto limit epok) i nie było innego scenariusza końca
        if (symulacja.getScenariuszKonca() == null) {
            Platform.runLater(() -> endSimulation("Symulacja zakończona naturalnie (osiągnięto limit epok)."));
        }
    }

    /**
     * Aktualizuje elementy interfejsu użytkownika na podstawie bieżącego stanu symulacji.
     * @param populacja Aktualny obiekt populacji.
     * @param policja Aktualny obiekt policji.
     * @param epoka Aktualny obiekt epoki.
     * @param ostatnieZdarzenie Ostatnie zdarzenie, które wystąpiło.
     * @param komunikatDodatkowy Dodatkowy komunikat z symulacji (np. o korupcji).
     */
    private void updateUI(Populacja populacja, Policja policja, Epoka epoka, Zdarzenie ostatnieZdarzenie, String komunikatDodatkowy) {
        String numerEpokiStr = String.valueOf(epoka.getNumerEpoki());
        epokaLabel.setText("Epoka: " + numerEpokiStr);
        currentPopulationLabel.setText("Populacja: " + populacja.getCalkowitaLiczebnosc());
        degeneracjaProgressBar.setProgress(epoka.getDegeneracja() / 100.0); // Degeneracja jest w % (0-100)

        // Logowanie zdarzenia
        if (ostatnieZdarzenie != null) {
            String eventText = "Ostatnie zdarzenie: " + ostatnieZdarzenie.getTyp() +
                    " (Ofiary: E:" + ostatnieZdarzenie.getOfiaryElita() +
                    ", KŚ:" + ostatnieZdarzenie.getOfiaryKlasaSrednia() +
                    ", S:" + ostatnieZdarzenie.getOfiarySlumsy() + ")";
            eventLabel.setText(eventText);

            if (eventLogTextArea != null) {
                String logEntry = "Epoka " + numerEpokiStr + ": " + ostatnieZdarzenie.getTyp() +
                        " (Ofiary: E:" + ostatnieZdarzenie.getOfiaryElita() +
                        ", KŚ:" + ostatnieZdarzenie.getOfiaryKlasaSrednia() +
                        ", S:" + ostatnieZdarzenie.getOfiarySlumsy() + ")\n";
                eventLogTextArea.appendText(logEntry);
            }
        } else {
            eventLabel.setText("Brak specjalnego zdarzenia w tej epoce.");
            if (eventLogTextArea != null && !numerEpokiStr.equals("0")) { // Nie loguj dla epoki 0
                eventLogTextArea.appendText("Epoka " + numerEpokiStr + ": Brak specjalnego zdarzenia.\n");
            }
        }

        // Logowanie dodatkowego komunikatu (np. o korupcji)
        if (komunikatDodatkowy != null && !komunikatDodatkowy.isEmpty() && eventLogTextArea != null) {
            eventLogTextArea.appendText("    -> " + komunikatDodatkowy + "\n"); // Wcięcie dla odróżnienia
        }
        if (eventLogTextArea != null) {
            eventLogTextArea.setScrollTop(Double.MAX_VALUE); // Automatyczne przewijanie do końca
        }

        // Rysowanie rozkładu populacji
        drawPopulationDistribution(populacja.getElita(), populacja.getKlasaSrednia(), populacja.getSlumsy());
    }

    /**
     * Rysuje na płótnie (Canvas) graficzny rozkład populacji na Elitę, Klasę Średnią i Slumsy.
     * Używa różnych kolorów do reprezentacji każdej grupy.
     * @param elita Liczba członków elity.
     * @param klasaSrednia Liczba członków klasy średniej.
     * @param slumsy Liczba mieszkańców slumsów.
     */
    private void drawPopulationDistribution(int elita, int klasaSrednia, int slumsy) {
        GraphicsContext gc = populationCanvas.getGraphicsContext2D();
        double width = populationCanvas.getWidth();
        double height = populationCanvas.getHeight();
        gc.clearRect(0, 0, width, height); // Wyczyść poprzedni rysunek

        double totalPopulation = elita + klasaSrednia + slumsy;

        if (totalPopulation <= 0) { // Jeśli populacja wynosi 0, nic nie rysuj
            return;
        }

        double elitaRatio = (double)elita / totalPopulation;
        double klasaSredniaRatio = (double)klasaSrednia / totalPopulation;
        // double slumsyRatio = (double)slumsy / totalPopulation; // Ostatni segment wypełni resztę

        double currentX = 0;

        // Rysowanie segmentu Elity
        gc.setFill(Color.BLUE);
        double elitaWidth = width * elitaRatio;
        gc.fillRect(currentX, 0, elitaWidth, height);
        currentX += elitaWidth;

        // Rysowanie segmentu Klasy Średniej
        gc.setFill(Color.GREEN);
        double klasaSredniaWidth = width * klasaSredniaRatio;
        gc.fillRect(currentX, 0, klasaSredniaWidth, height);
        currentX += klasaSredniaWidth;

        // Rysowanie segmentu Slumsów (pozostała część)
        gc.setFill(Color.RED);
        double slumsyWidth = width - currentX; // Reszta płótna
        if (slumsyWidth > 0) {
            gc.fillRect(currentX, 0, slumsyWidth, height);
        }
    }

    /**
     * Kończy symulację, wyświetla okno z wynikiem (scenariuszem końca)
     * oraz generuje wykresy podsumowujące. Odblokowuje kontrolki UI.
     * @param scenariuszKonca Tekst opisujący, jak zakończyła się symulacja.
     */
    private void endSimulation(String scenariuszKonca) {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("EndScenarioView.fxml")));
            Parent root = loader.load();

            EndScenarioViewController controller = loader.getController();
            controller.setScenarioText(scenariuszKonca);

            Stage endStage = new Stage();
            endStage.initModality(Modality.APPLICATION_MODAL); // Blokuje inne okna aplikacji
            endStage.setTitle("Wynik Symulacji");
            Scene scene = new Scene(root);
            endStage.setScene(scene);
            endStage.showAndWait(); // Czekaj na zamknięcie okna dialogowego

        } catch (IOException e) {
            e.printStackTrace();
            // Wyświetl standardowy alert, jeśli okno FXML się nie załaduje
            showAlert("Wynik Symulacji (Błąd Okna)", scenariuszKonca);
        } finally {
            // Zawsze generuj wykresy i odblokuj UI, nawet jeśli wystąpił błąd z oknem końca
            generateChartsInSingleNewWindow();
            startButton.setDisable(false);
            setControlsDisable(false);
        }
    }

    /**
     * Wyświetla prosty alert informacyjny.
     * @param title Tytuł okna alertu.
     * @param message Treść wiadomości alertu.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null); // Brak nagłówka
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Włącza lub wyłącza możliwość edycji pól tekstowych parametrów symulacji.
     * @param disable {@code true} aby wyłączyć kontrolki, {@code false} aby włączyć.
     */
    private void setControlsDisable(boolean disable) {
        populacjaTextField.setDisable(disable);
        elitaTextField.setDisable(disable);
        klasaSredniaTextField.setDisable(disable);
        implantyTextField.setDisable(disable);
        stabilizatoryTextField.setDisable(disable);
        policjaTextField.setDisable(disable);
        epokiTextField.setDisable(disable);
    }

    /**
     * Generuje wykresy na podstawie danych z pliku CSV raportu
     * i wyświetla je w nowym oknie.
     */
    private void generateChartsInSingleNewWindow() {
        List<XYChart.Series<Number, Number>> allSeries = new ArrayList<>();
        XYChart.Series<Number, Number> populacjaSeries = new XYChart.Series<>();
        populacjaSeries.setName("Całkowita Populacja");
        XYChart.Series<Number, Number> degeneracjaSeries = new XYChart.Series<>();
        degeneracjaSeries.setName("Degeneracja");
        XYChart.Series<Number, Number> silaPolicjiSeries = new XYChart.Series<>();
        silaPolicjiSeries.setName("Siła Policji");

        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE_PATH))) {
            String line;
            boolean firstLine = true; // Flaga do pominięcia nagłówka CSV

            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] data = line.split(";");
                // Oczekiwane kolumny: Epoka;Degeneracja;SmiertelnoscWTicku;CalkowitaLiczebnosc;Elita;KlasaSrednia;Slumsy;SilaPolicji;PoziomImplantow;DostepDoStabilizatorow
                // Indeksy:             0      1            2                  3                     4       5           6        7            8                   9
                if (data.length >= 10) { // Sprawdzenie minimalnej liczby kolumn
                    try {
                        // Parsowanie danych z odpowiednich kolumn
                        int epoka = Integer.parseInt(data[0].replace("Epoka: ", "").trim());
                        int degeneracja = Integer.parseInt(data[1].trim());
                        int calkowitaLiczebnosc = Integer.parseInt(data[3].trim());
                        int silaPolicji = Integer.parseInt(data[7].trim());

                        populacjaSeries.getData().add(new XYChart.Data<>(epoka, calkowitaLiczebnosc));
                        degeneracjaSeries.getData().add(new XYChart.Data<>(epoka, degeneracja));
                        silaPolicjiSeries.getData().add(new XYChart.Data<>(epoka, silaPolicji));

                    } catch (NumberFormatException e) {
                        System.err.println("Błąd parsowania danych w pliku CSV (linia: '" + line + "'): " + e.getMessage());
                        // Można zdecydować, czy pokazać alert użytkownikowi, czy tylko logować
                    }
                } else {
                    System.err.println("Błąd formatu linii w pliku CSV (za mało kolumn): " + line);
                }
            }
        } catch (IOException e) {
            showAlert("Błąd odczytu pliku CSV", "Nie można wczytać danych do wykresów z pliku " + CSV_FILE_PATH + ": " + e.getMessage());
            e.printStackTrace();
            return; // Nie próbuj otwierać okna wykresów, jeśli plik nie został wczytany
        }

        allSeries.add(populacjaSeries);
        allSeries.add(degeneracjaSeries);
        allSeries.add(silaPolicjiSeries);

        // Otwórz okno z wykresami tylko jeśli są jakieś dane
        Platform.runLater(() -> {
            if (!populacjaSeries.getData().isEmpty() || !degeneracjaSeries.getData().isEmpty() || !silaPolicjiSeries.getData().isEmpty()) {
                openChartsContainerWindow(allSeries);
            } else {
                showAlert("Brak danych do wyświetlenia", "Nie udało się wczytać żadnych danych z pliku '" + CSV_FILE_PATH + "' do wygenerowania wykresów.");
            }
        });
    }

    /**
     * Otwiera nowe okno (zdefiniowane w charts_container_window.fxml)
     * i przekazuje do niego przygotowane serie danych do wyświetlenia na wykresach.
     * @param allSeries Lista serii danych ({@link XYChart.Series}) do wyświetlenia.
     */
    private void openChartsContainerWindow(List<XYChart.Series<Number, Number>> allSeries) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("charts_container_window.fxml")));
            VBox root = fxmlLoader.load(); // VBox jest korzeniem w charts_container_window.fxml
            ChartsContainerWindowController controller = fxmlLoader.getController();
            controller.setChartsData(allSeries);

            Stage chartsStage = new Stage();
            chartsStage.setTitle("Wykresy Danych Symulacji");
            chartsStage.setScene(new Scene(root, 1000, 700)); // Rozmiar okna wykresów
            chartsStage.show();
        } catch (IOException e) {
            showAlert("Błąd otwierania okna wykresów", "Nie można otworzyć okna z wykresami: " + e.getMessage());
            e.printStackTrace();
        }
    }
}