package com.symulacja;

/**
 * Reprezentuje zdarzenie w symulacji, które rozwija się przez wiele epok i może mieć różne etapy.
 * Śledzi postęp zdarzenia i liczbę epok pozostałych do następnego etapu.
 */
public class AktywneZdarzenieWieloetapowe {
    private final String idSekwencji; // Np. "ARTEFAKT_BADANIE"
    private int aktualnyEtap;
    private int epokiDoNastepnegoEtapu;

    /**
     * Konstruktor dla aktywnego zdarzenia wieloetapowego.
     * @param idSekwencji Unikalny identyfikator sekwencji zdarzenia.
     * @param aktualnyEtap Numer bieżącego etapu zdarzenia.
     * @param epokiDoNastepnegoEtapu Liczba epok pozostałych do zakończenia bieżącego etapu lub przejścia do następnego.
     */
    public AktywneZdarzenieWieloetapowe(String idSekwencji, int aktualnyEtap, int epokiDoNastepnegoEtapu) {
        this.idSekwencji = idSekwencji;
        this.aktualnyEtap = aktualnyEtap;
        this.epokiDoNastepnegoEtapu = epokiDoNastepnegoEtapu;
    }

    /**
     * Zwraca identyfikator sekwencji zdarzenia.
     * @return Identyfikator sekwencji.
     */
    public String getIdSekwencji() {
        return idSekwencji;
    }

    /**
     * Zwraca numer aktualnego etapu zdarzenia.
     * @return Numer aktualnego etapu.
     */
    public int getAktualnyEtap() {
        return aktualnyEtap;
    }

    /**
     * Ustawia numer aktualnego etapu zdarzenia.
     * @param aktualnyEtap Nowy numer etapu.
     */
    public void setAktualnyEtap(int aktualnyEtap) {
        this.aktualnyEtap = aktualnyEtap;
    }

    /**
     * Zwraca liczbę epok pozostałych do następnego etapu zdarzenia.
     * @return Liczba epok do następnego etapu.
     */
    public int getEpokiDoNastepnegoEtapu() {
        return epokiDoNastepnegoEtapu;
    }

    /**
     * Ustawia liczbę epok pozostałych do następnego etapu zdarzenia.
     * @param epokiDoNastepnegoEtapu Nowa liczba epok.
     */
    public void setEpokiDoNastepnegoEtapu(int epokiDoNastepnegoEtapu) {
        this.epokiDoNastepnegoEtapu = epokiDoNastepnegoEtapu;
    }

    /**
     * Dekrementuje licznik epok pozostałych do następnego etapu.
     * Licznik nie spadnie poniżej zera.
     */
    public void dekrementujEpoki() {
        if (this.epokiDoNastepnegoEtapu > 0) {
            this.epokiDoNastepnegoEtapu--;
        }
    }
}