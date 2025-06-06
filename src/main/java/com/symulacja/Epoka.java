package com.symulacja;

/**
 * Klasa reprezentująca pojedynczą epokę (turę) w symulacji.
 * Przechowuje statystyki dotyczące degeneracji i liczby ofiar w danej epoce.
 */
public class Epoka {
    private int numerEpoki;
    private int degeneracja;
    private int smiertelnosc;

    /**
     * Konstruktor klasy Epoka.
     * @param numerEpoki Numer bieżącej epoki.
     */
    public Epoka(int numerEpoki) {
        this.numerEpoki = numerEpoki;
        this.degeneracja = 0;
        this.smiertelnosc = 0;
    }

    /**
     * Metoda przewidziana na przyszłe rozszerzenia, np. do dodatkowych obliczeń
     * statystyk epoki. W obecnej implementacji nie wykonuje żadnych operacji,
     * gdyż wartości są ustawiane bezpośrednio w klasie {@link Symulacja}.
     */
    public void aktualizujStatystyki() {
        // Logika aktualizacji statystyk epoki (jeśli potrzebne są dodatkowe obliczenia)
    }

    /**
     * Zwraca numer epoki.
     * @return numer epoki.
     */
    public int getNumerEpoki() {
        return numerEpoki;
    }

    /**
     * Ustawia numer epoki.
     * @param numerEpoki nowy numer epoki.
     */
    public void setNumerEpoki(int numerEpoki) {
        this.numerEpoki = numerEpoki;
    }

    /**
     * Zwraca poziom degeneracji w tej epoce.
     * @return poziom degeneracji.
     */
    public int getDegeneracja() {
        return degeneracja;
    }

    /**
     * Ustawia poziom degeneracji w tej epoce.
     * @param degeneracja nowy poziom degeneracji.
     */
    public void setDegeneracja(int degeneracja) {
        this.degeneracja = degeneracja;
    }

    /**
     * Zwraca liczbę ofiar śmiertelnych w tej epoce.
     * @return liczba ofiar śmiertelnych.
     */
    public int getSmiertelnosc() {
        return smiertelnosc;
    }

    /**
     * Ustawia liczbę ofiar śmiertelnych w tej epoce.
     * @param smiertelnosc nowa liczba ofiar śmiertelnych.
     */
    public void setSmiertelnosc(int smiertelnosc) {
        this.smiertelnosc = smiertelnosc;
    }
}