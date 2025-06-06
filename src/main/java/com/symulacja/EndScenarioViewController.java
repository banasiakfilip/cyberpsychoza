package com.symulacja;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Kontroler dla widoku FXML wyświetlanego na końcu scenariusza symulacji.
 */
public class EndScenarioViewController {

    @FXML
    private Label endScenarioTextLabel;

    @FXML
    private Button okButton;

    /**
     * Ustawia tekst opisujący scenariusz zakończenia symulacji.
     * Tekst zostanie wyświetlony w etykiecie {@code endScenarioTextLabel}.
     * @param text Tekst do wyświetlenia.
     */
    public void setScenarioText(String text) {
        if (endScenarioTextLabel != null) {
            endScenarioTextLabel.setText(text);
        }
    }

    /**
     * Obsługuje akcję kliknięcia przycisku "OK".
     * Zamyka okno (Stage) zawierające ten widok.
     */
    @FXML
    private void handleOkAction() {
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }
}