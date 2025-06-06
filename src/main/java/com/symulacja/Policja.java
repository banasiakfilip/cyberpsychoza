package com.symulacja;

/**
 * Klasa reprezentująca siłę policji w społeczeństwie.
 * Jej siła wpływa na zdolność do redukcji negatywnych skutków zdarzeń
 * i poziomu degeneracji.
 */
public class Policja {
    private int sila; // Poziom siły policji w procentach (0-100)

    /**
     * Konstruktor klasy Policja.
     * @param poczatkowaSila Początkowy poziom siły policji (0-100).
     */
    public Policja(int poczatkowaSila) {
        this.sila = Math.max(0, Math.min(100, poczatkowaSila)); // Ensure initial value is within bounds
    }

    /**
     * Zwraca aktualny poziom siły policji.
     * @return Siła policji (0-100).
     */
    public int getSila() {
        return sila;
    }

    /**
     * Ustawia nowy poziom siły policji.
     * Wartość jest ograniczana do zakresu 0-100.
     * @param sila Nowy poziom siły policji (0-100).
     */
    public void setSila(int sila) {
        this.sila = Math.max(0, Math.min(100, sila));
    }
}