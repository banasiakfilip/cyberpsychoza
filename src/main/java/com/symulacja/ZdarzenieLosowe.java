package com.symulacja;

import java.util.Random;

/**
 * Implementacja interfejsu {@link Zdarzenie}, reprezentująca losowe zdarzenia
 * mogące wystąpić w symulacji. Zawiera logikę generowania różnych typów zdarzeń
 * oraz definiuje ich wpływ na parametry symulacji.
 */
public class ZdarzenieLosowe implements Zdarzenie {
    private final String typ;
    private final int wplywNaDegeneracje;
    private final int ofiaryElita;
    private final int ofiaryKlasaSrednia;
    private final int ofiarySlumsy;
    private final int wplywNaSilePolicji;
    private final int wplywNaPoziomImplantow;
    private final int wplywNaDostepDoStabilizatorow; // NOWE POLE

    // Identyfikatory dla zdarzeń wieloetapowych
    public static final String ID_ODKRYTO_ARTEFAKT = "Odkryto Starożytny Artefakt";
    public static final String ID_PRZELOM_ARTEFAKTU_POZYTYWNY = "Przełom w Bad. Artefaktu (Pozytywny)";
    public static final String ID_PRZELOM_ARTEFAKTU_NEGATYWNY = "Przełom w Bad. Artefaktu (Negatywny)";

    /**
     * Konstruktor dla zdarzenia losowego.
     * @param typ Nazwa/typ zdarzenia.
     * @param wplywNaDegeneracje Zmiana poziomu degeneracji (dodatnia lub ujemna).
     * @param ofiaryElita Liczba ofiar w elicie.
     * @param ofiaryKlasaSrednia Liczba ofiar w klasie średniej.
     * @param ofiarySlumsy Liczba ofiar w slumsach.
     * @param wplywNaSilePolicji Zmiana siły policji.
     * @param wplywNaPoziomImplantow Zmiana poziomu implantów.
     * @param wplywNaDostepDoStabilizatorow Zmiana dostępu do stabilizatorów. // NOWY PARAMETR
     */
    public ZdarzenieLosowe(String typ, int wplywNaDegeneracje,
                           int ofiaryElita, int ofiaryKlasaSrednia, int ofiarySlumsy,
                           int wplywNaSilePolicji, int wplywNaPoziomImplantow,
                           int wplywNaDostepDoStabilizatorow) { // ZMODYFIKOWANY KONSTRUKTOR
        this.typ = typ;
        this.wplywNaDegeneracje = wplywNaDegeneracje;
        this.ofiaryElita = ofiaryElita;
        this.ofiaryKlasaSrednia = ofiaryKlasaSrednia;
        this.ofiarySlumsy = ofiarySlumsy;
        this.wplywNaSilePolicji = wplywNaSilePolicji;
        this.wplywNaPoziomImplantow = wplywNaPoziomImplantow;
        this.wplywNaDostepDoStabilizatorow = wplywNaDostepDoStabilizatorow; // PRZYPISANIE
    }

    /**
     * Generuje losowe zdarzenie na podstawie aktualnego stanu symulacji.
     * @param populacja Aktualny stan populacji.
     * @param policja Aktualny stan sił policji.
     * @param degeneracjaPoprzedniejEpoki Poziom degeneracji z poprzedniej epoki.
     * @param numerEpoki Bieżący numer epoki.
     * @return Wygenerowane zdarzenie.
     */
    public static Zdarzenie generujLosoweZdarzenie(Populacja populacja, Policja policja, int degeneracjaPoprzedniejEpoki, int numerEpoki) {
        Random random = new Random();

        // Sprawdzanie warunków dla zdarzeń specjalnych zależnych od stanu
        if (populacja.getPoziomImplantow() > 70 && populacja.getDostepDoStabilizatorow() < 30) {
            if (random.nextInt(100) < 20) { // 20% szans
                return new ZdarzenieLosowe("Niepokoje związane z implantami", 10, 2, 5, 10, -2, -3, -2); // Dodano wpływ na stabilizatory
            }
        }
        if (populacja.getCalkowitaLiczebnosc() > 0 &&
                ((double)populacja.getElita() / populacja.getCalkowitaLiczebnosc()) > 0.50 &&
                degeneracjaPoprzedniejEpoki > 60) {
            if (random.nextInt(100) < 25) { // 25% szans
                return new ZdarzenieLosowe("Propaganda Sukcesu Elity", -3, 0, 0, 0, 3, 0, 2); // Dodano wpływ na stabilizatory
            }
        }

        // Standardowa logika losowania zdarzeń ogólnych
        int los = random.nextInt(100); // 0-99

        int totalPopulation = populacja.getCalkowitaLiczebnosc();
        int slumsyPercentage = (totalPopulation > 0) ? (int)(((double)populacja.getSlumsy() / totalPopulation) * 100) : 0;

        if (slumsyPercentage > 50) los -= 10;
        if (numerEpoki > 5) los -= 5;
        if (numerEpoki > 10) los -= 5;

        // Przykładowe wartości wpływu na dostęp do stabilizatorów
        if (los < 10) {
            return new ZdarzenieLosowe("Rebelia Niewolników Cybernetycznych", 30, 50, 100, 300, -15, -10, -10);
        } else if (los < 20) {
            return new ZdarzenieLosowe("Wybuch Pandemii Nanonawirusa", 25, 20, 80, 250, -5, -10, -8);
        } else if (los < 35) {
            return new ZdarzenieLosowe("Epidemia", 10, 30, 60, 200, 0, -5, -5);
        } else if (los < 50) {
            return new ZdarzenieLosowe("Kryzys energetyczny", 8, 0, 20, 100, -3, 0, -4);
        } else if (los < 60) {
            return new ZdarzenieLosowe(ID_ODKRYTO_ARTEFAKT, -5, 0, 0, 0, 2, 5, 1);
        } else if (los < 70) {
            return new ZdarzenieLosowe("Drobne zamieszki", 3, 5, 10, 30, -1, 0, -1);
        } else if (los < 80) {
            return new ZdarzenieLosowe("Niespodziewane Odkrycie Technologiczne", -10, 0, 0, 0, 5, 10, 5);
        } else if (los < 90) {
            return new ZdarzenieLosowe("Program Socjalny dla Slumsów", -5, 0, 0, -20, 2, 0, 3);
        } else {
            return new ZdarzenieLosowe("Spokojna Epoka", -2, 0, 0, 0, 1, 1, 1);
        }
    }

    /**
     * Tworzy zdarzenie będące wynikiem przełomu w badaniach nad artefaktem.
     * @return Zdarzenie przełomu w badaniach artefaktu.
     */
    public static Zdarzenie stworzPrzelomArtefaktu() {
        Random random = new Random();
        if (random.nextBoolean()) {
            return new ZdarzenieLosowe(ID_PRZELOM_ARTEFAKTU_POZYTYWNY, -20, 0, 0, 0, 5, 15, 10); // Dodano wpływ na stabilizatory
        } else {
            return new ZdarzenieLosowe(ID_PRZELOM_ARTEFAKTU_NEGATYWNY, 30, 10, 20, 50, -10, -5, -7); // Dodano wpływ na stabilizatory
        }
    }

    @Override
    public String getTyp() { return typ; }
    @Override
    public int getWplywNaDegeneracje() { return wplywNaDegeneracje; }
    @Override
    public int getOfiaryElita() { return ofiaryElita; }
    @Override
    public int getOfiaryKlasaSrednia() { return ofiaryKlasaSrednia; }
    @Override
    public int getOfiarySlumsy() { return ofiarySlumsy; }
    @Override
    public int getWplywNaSilePolicji() { return wplywNaSilePolicji; }
    @Override
    public int getWplywNaPoziomImplantow() { return wplywNaPoziomImplantow; }
    @Override
    public int getWplywNaDostepDoStabilizatorow() { return wplywNaDostepDoStabilizatorow; } // NOWA IMPLEMENTACJA
}