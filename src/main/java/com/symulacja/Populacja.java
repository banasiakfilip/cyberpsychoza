package com.symulacja;

/**
 * Klasa Populacja modeluje społeczeństwo, dzieląc je na trzy grupy:
 * Elitę, Klasę Średnią i Slumsy. Zarządza liczebnością tych grup
 * oraz globalnym poziomem implantów i dostępem do stabilizatorów.
 */
public class Populacja {
    private int calkowitaLiczebnosc;
    private int elita;
    private int klasaSrednia;
    private int slumsy;
    private int poziomImplantow; // Procent osób z implantami (0-100)
    private int dostepDoStabilizatorow; // Procent dostępu do stabilizatorów (0-100)
    private final int poczatkowaLiczebnosc;

    /**
     * Konstruktor klasy Populacja.
     * @param poczatkowaLiczebnosc Początkowa całkowita liczebność populacji.
     * @param poziomImplantow Początkowy procentowy poziom implantów (0-100).
     * @param dostepDoStabilizatorow Początkowy procentowy dostęp do stabilizatorów (0-100).
     */
    public Populacja(int poczatkowaLiczebnosc, int poziomImplantow, int dostepDoStabilizatorow) {
        this.poczatkowaLiczebnosc = poczatkowaLiczebnosc;
        this.calkowitaLiczebnosc = poczatkowaLiczebnosc;
        this.poziomImplantow = Math.max(0, Math.min(100, poziomImplantow));
        this.dostepDoStabilizatorow = Math.max(0, Math.min(100, dostepDoStabilizatorow));
        // Początkowy podział (elita, klasaSrednia, slumsy) zostanie ustawiony przez settery w Symulacji
    }

    /**
     * Zmniejsza liczbę osób w slumsach o podaną liczbę ofiar.
     * Liczba osób w slumsach nie spadnie poniżej zera.
     * @param liczbaOfiar Liczba osób do usunięcia ze slumsów.
     */
    public void zmniejszSlumsy(int liczbaOfiar) {
        this.slumsy = Math.max(0, this.slumsy - liczbaOfiar);
        aktualizujCalkowitaLiczebnosc();
    }

    /**
     * Zmniejsza liczbę osób w klasie średniej o podaną liczbę ofiar.
     * Liczba osób w klasie średniej nie spadnie poniżej zera.
     * @param liczbaOfiar Liczba osób do usunięcia z klasy średniej.
     */
    public void zmniejszKlasaSrednia(int liczbaOfiar) {
        this.klasaSrednia = Math.max(0, this.klasaSrednia - liczbaOfiar);
        aktualizujCalkowitaLiczebnosc();
    }

    /**
     * Zmniejsza liczbę osób w elicie o podaną liczbę ofiar.
     * Liczba osób w elicie nie spadnie poniżej zera.
     * @param liczbaOfiar Liczba osób do usunięcia z elity.
     */
    public void zmniejszElita(int liczbaOfiar) {
        this.elita = Math.max(0, this.elita - liczbaOfiar);
        aktualizujCalkowitaLiczebnosc();
    }

    /**
     * Aktualizuje całkowitą liczebność populacji na podstawie sumy poszczególnych grup społecznych.
     * Metoda prywatna, wywoływana po każdej zmianie liczebności którejkolwiek z grup.
     */
    private void aktualizujCalkowitaLiczebnosc() {
        this.calkowitaLiczebnosc = this.elita + this.klasaSrednia + this.slumsy;
    }

    /**
     * Zwraca początkową liczebność populacji ustawioną przy tworzeniu obiektu.
     * @return Początkowa liczebność populacji.
     */
    public int getPoczatkowaLiczebnosc() {
        return poczatkowaLiczebnosc;
    }

    /**
     * Zwraca aktualną całkowitą liczebność populacji.
     * @return Całkowita liczebność populacji.
     */
    public int getCalkowitaLiczebnosc() {
        return calkowitaLiczebnosc;
    }

    /**
     * Zwraca aktualną liczebność elity.
     * @return Liczebność elity.
     */
    public int getElita() {
        return elita;
    }

    /**
     * Ustawia liczebność elity. Wartość nie może być ujemna.
     * @param elita Nowa liczebność elity.
     */
    public void setElita(int elita) {
        this.elita = Math.max(0, elita);
        aktualizujCalkowitaLiczebnosc();
    }

    /**
     * Zwraca aktualną liczebność klasy średniej.
     * @return Liczebność klasy średniej.
     */
    public int getKlasaSrednia() {
        return klasaSrednia;
    }

    /**
     * Ustawia liczebność klasy średniej. Wartość nie może być ujemna.
     * @param klasaSrednia Nowa liczebność klasy średniej.
     */
    public void setKlasaSrednia(int klasaSrednia) {
        this.klasaSrednia = Math.max(0, klasaSrednia);
        aktualizujCalkowitaLiczebnosc();
    }

    /**
     * Zwraca aktualną liczebność slumsów.
     * @return Liczebność slumsów.
     */
    public int getSlumsy() {
        return slumsy;
    }

    /**
     * Ustawia liczebność slumsów. Wartość nie może być ujemna.
     * @param slumsy Nowa liczebność slumsów.
     */
    public void setSlumsy(int slumsy) {
        this.slumsy = Math.max(0, slumsy);
        aktualizujCalkowitaLiczebnosc();
    }

    /**
     * Zwraca procentowy poziom implantów w populacji (0-100).
     * @return Poziom implantów.
     */
    public int getPoziomImplantow() {
        return poziomImplantow;
    }

    /**
     * Ustawia procentowy poziom implantów w populacji. Wartość jest ograniczana do zakresu 0-100.
     * @param poziomImplantow Nowy poziom implantów.
     */
    public void setPoziomImplantow(int poziomImplantow) {
        this.poziomImplantow = Math.max(0, Math.min(100, poziomImplantow));
    }

    /**
     * Zwraca procentowy dostęp do stabilizatorów w populacji (0-100).
     * @return Dostęp do stabilizatorów.
     */
    public int getDostepDoStabilizatorow() {
        return dostepDoStabilizatorow;
    }

    /**
     * Ustawia procentowy dostęp do stabilizatorów w populacji. Wartość jest ograniczana do zakresu 0-100.
     * @param dostepDoStabilizatorow Nowy dostęp do stabilizatorów.
     */
    public void setDostepDoStabilizatorow(int dostepDoStabilizatorow) {
        this.dostepDoStabilizatorow = Math.max(0, Math.min(100, dostepDoStabilizatorow));
    }
}