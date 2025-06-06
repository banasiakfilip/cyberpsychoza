package com.symulacja;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

/**
 * Główna klasa aplikacji JavaFX. Odpowiada za uruchomienie
 * interfejsu graficznego symulacji.
 */
public class App extends Application {

    /**
     * Metoda startowa aplikacji JavaFX. Ładuje główny widok FXML,
     * ustawia scenę i wyświetla główne okno aplikacji.
     * @param stage Główny kontener (okno) aplikacji.
     * @throws IOException Jeśli wystąpi błąd podczas ładowania pliku FXML.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(App.class.getResource("main_view.fxml")));
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root, 1000, 700);
        stage.setTitle("Symulacja 'W Wirze': Upadek Społeczeństwa Cyber");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Główna metoda uruchomieniowa programu.
     * Wywołuje {@link #launch(String...)} w celu uruchomienia aplikacji JavaFX.
     * @param args Argumenty wiersza poleceń (nieużywane).
     */
    public static void main(String[] args) {
        launch(args);
    }
}