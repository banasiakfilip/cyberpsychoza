package com.symulacja;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Klasa odpowiedzialna za generowanie i zapisywanie raportu z przebiegu symulacji
 * do pliku w formacie CSV.
 */
public class RaportCSV {
    private final String nazwaPliku;
    private boolean naglowekZapisany;

    /**
     * Konstruktor klasy RaportCSV.
     * Przy tworzeniu obiektu, plik o podanej nazwie jest czyszczony (jeśli istniał).
     * @param nazwaPliku Nazwa pliku CSV, do którego będą zapisywane dane.
     */
    public RaportCSV(String nazwaPliku) {
        this.nazwaPliku = nazwaPliku;
        this.naglowekZapisany = false;
        // Czyszczenie pliku przy inicjalizacji raportu
        try (PrintWriter writer = new PrintWriter(new FileWriter(nazwaPliku, false))) {
            writer.print(""); // Nadpisuje plik pustą zawartością
        } catch (IOException e) {
            System.err.println(ConsoleColors.RED + "Błąd podczas inicjalizacji (czyszczenia) pliku CSV: " + e.getMessage() + ConsoleColors.RESET);
        }
    }

    /**
     * Zapisuje nagłówek do pliku CSV, jeśli nie został jeszcze zapisany.
     * Nagłówek zawiera nazwy kolumn dla danych z każdej epoki.
     */
    public void zapiszNaglowek() {
        if (!naglowekZapisany) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(nazwaPliku, true))) {
                writer.println("Epoka;Degeneracja;SmiertelnoscWTicku;CalkowitaLiczebnosc;Elita;KlasaSrednia;Slumsy;SilaPolicji;PoziomImplantow;DostepDoStabilizatorow");
                naglowekZapisany = true;
            } catch (IOException e) {
                System.err.println(ConsoleColors.RED + "Błąd podczas zapisu nagłówka do pliku CSV: " + e.getMessage() + ConsoleColors.RESET);
            }
        }
    }

    /**
     * Zapisuje dane z pojedynczej epoki (ticku) symulacji do pliku CSV.
     * @param epokaLabel Etykieta identyfikująca epokę (np. "Epoka: 1").
     * @param degeneracja Poziom degeneracji w danej epoce.
     * @param smiertelnoscWTicku Liczba ofiar śmiertelnych w danej epoce.
     * @param populacja Obiekt populacji zawierający dane o liczebności grup społecznych, implantach i stabilizatorach.
     * @param policja Obiekt policji zawierający dane o sile policji.
     */
    public void zapiszDaneTicku(String epokaLabel, int degeneracja, int smiertelnoscWTicku, Populacja populacja, Policja policja) {
        if (!naglowekZapisany) {
            zapiszNaglowek(); // Upewnij się, że nagłówek jest zapisany przed danymi
        }
        try (PrintWriter writer = new PrintWriter(new FileWriter(nazwaPliku, true))) {
            writer.printf("%s;%d;%d;%d;%d;%d;%d;%d;%d;%d\n",
                    epokaLabel,
                    degeneracja,
                    smiertelnoscWTicku,
                    populacja.getCalkowitaLiczebnosc(),
                    populacja.getElita(),
                    populacja.getKlasaSrednia(),
                    populacja.getSlumsy(),
                    policja.getSila(),
                    populacja.getPoziomImplantow(),
                    populacja.getDostepDoStabilizatorow());
        } catch (IOException e) {
            System.err.println(ConsoleColors.RED + "Błąd podczas zapisu danych ticku do pliku CSV: " + e.getMessage() + ConsoleColors.RESET);
        }
    }
}