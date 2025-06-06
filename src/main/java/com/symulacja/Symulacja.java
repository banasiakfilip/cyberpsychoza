package com.symulacja;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

/**
 * Główna klasa zarządzająca logiką symulacji społeczeństwa cybernetycznego.
 * Odpowiada za inicjalizację parametrów, przeprowadzanie kolejnych epok (cykli),
 * generowanie zdarzeń, aktualizację stanu populacji i policji,
 * oraz sprawdzanie warunków końca symulacji.
 */
public class Symulacja {
    private Epoka aktualnaEpoka;
    private String scenariuszKonca;
    private Populacja populacja;
    private Policja policja;
    private final List<Epoka> epoki;
    private final RaportCSV raportCSV;
    private Zdarzenie ostatnieZdarzenie;
    private final List<AktywneZdarzenieWieloetapowe> aktywneZdarzeniaWieloetapowe;

    private String komunikatDodatkowyOskutkachEpoki;

    private int calkowitaSmiertelnosc = 0;
    private int numerEpoki;

    private static final int MAKS_DEGENERACJA = 90;
    private static final int MIN_ELITA = 1;
    private int progKrytycznyPopulacji;

    private static final double PROG_PROC_WYSOKIEJ_ELITY = 0.30;
    private static final double STOPIEN_REDUKCJI_ELITY_PRZEZ_KORUPCJE = 0.02;

    private static final String SEKWENCJA_ARTEFAKTU = "ARTEFAKT_BADANIE";

    /**
     * Konstruktor klasy Symulacja.
     */
    public Symulacja() {
        this.epoki = new ArrayList<>();
        this.raportCSV = new RaportCSV("RaportCSV.csv");
        this.raportCSV.zapiszNaglowek();
        this.aktywneZdarzeniaWieloetapowe = new ArrayList<>();
    }

    /**
     * Ustawia parametry początkowe dla nowej symulacji.
     * @param poczatkowaLiczebnoscPopulacji Całkowita początkowa liczba ludności.
     * @param elita Początkowa liczba członków elity.
     * @param klasaSrednia Początkowa liczba członków klasy średniej.
     * @param slumsy Początkowa liczba mieszkańców slumsów.
     * @param poczatkowyPoziomImplantow Procentowy poziom implantacji (0-100).
     * @param poczatkowyDostepDoStabilizatorow Procentowy dostęp do stabilizatorów (0-100).
     * @param poczatkowaSilaPolicji Procentowa siła policji (0-100).
     */
    public void ustawParametryPoczatkowe(int poczatkowaLiczebnoscPopulacji, int elita, int klasaSrednia, int slumsy,
                                         int poczatkowyPoziomImplantow, int poczatkowyDostepDoStabilizatorow, int poczatkowaSilaPolicji) {
        this.populacja = new Populacja(poczatkowaLiczebnoscPopulacji, poczatkowyPoziomImplantow, poczatkowyDostepDoStabilizatorow);
        this.populacja.setElita(elita);
        this.populacja.setKlasaSrednia(klasaSrednia);
        this.populacja.setSlumsy(slumsy);
        this.policja = new Policja(poczatkowaSilaPolicji);
        this.progKrytycznyPopulacji = (int) (poczatkowaLiczebnoscPopulacji * 0.20);

        this.aktywneZdarzeniaWieloetapowe.clear();
        this.numerEpoki = 0;
        this.scenariuszKonca = null;
        this.epoki.clear();
        this.calkowitaSmiertelnosc = 0;
        this.komunikatDodatkowyOskutkachEpoki = null;

        this.aktualnaEpoka = new Epoka(0);
        this.aktualnaEpoka.setDegeneracja(5);
    }

    /**
     * Wykonuje jeden cykl (epokę) symulacji.
     */
    public void wykonajCyklSymulacji() {
        numerEpoki++;
        Epoka poprzedniStanEpoki = this.aktualnaEpoka;
        aktualnaEpoka = new Epoka(numerEpoki);

        this.ostatnieZdarzenie = null;
        this.komunikatDodatkowyOskutkachEpoki = null;
        boolean zdarzenieEtapoweWystapilo = false;

        Iterator<AktywneZdarzenieWieloetapowe> iterator = aktywneZdarzeniaWieloetapowe.iterator();
        while (iterator.hasNext()) {
            AktywneZdarzenieWieloetapowe aktywneZdarzenie = iterator.next();
            aktywneZdarzenie.dekrementujEpoki();
            if (aktywneZdarzenie.getEpokiDoNastepnegoEtapu() <= 0) {
                if (SEKWENCJA_ARTEFAKTU.equals(aktywneZdarzenie.getIdSekwencji())) {
                    if (aktywneZdarzenie.getAktualnyEtap() == 1) {
                        this.ostatnieZdarzenie = ZdarzenieLosowe.stworzPrzelomArtefaktu();
                        iterator.remove();
                        zdarzenieEtapoweWystapilo = true;
                        break;
                    }
                }
            }
        }

        if (!zdarzenieEtapoweWystapilo) {
            this.ostatnieZdarzenie = ZdarzenieLosowe.generujLosoweZdarzenie(populacja, policja, poprzedniStanEpoki.getDegeneracja(), numerEpoki);
        }

        if (this.ostatnieZdarzenie != null && ZdarzenieLosowe.ID_ODKRYTO_ARTEFAKT.equals(this.ostatnieZdarzenie.getTyp())) {
            boolean czySekwencjaAktywna = aktywneZdarzeniaWieloetapowe.stream()
                    .anyMatch(azw -> SEKWENCJA_ARTEFAKTU.equals(azw.getIdSekwencji()));
            if (!czySekwencjaAktywna) {
                aktywneZdarzeniaWieloetapowe.add(new AktywneZdarzenieWieloetapowe(SEKWENCJA_ARTEFAKTU, 1, 5));
            }
        }

        int smiertelnoscBiezacejEpoki = 0;

        if (this.ostatnieZdarzenie != null) {
            int nowaDegeneracja = poprzedniStanEpoki.getDegeneracja() + this.ostatnieZdarzenie.getWplywNaDegeneracje() + 1;
            nowaDegeneracja = Math.max(0, Math.min(100, nowaDegeneracja));
            aktualnaEpoka.setDegeneracja(nowaDegeneracja);

            double redukcjaDegeneracjiPolicja = policja.getSila() * 0.25;
            aktualnaEpoka.setDegeneracja(Math.max(0, (int)(aktualnaEpoka.getDegeneracja() - redukcjaDegeneracjiPolicja)));

            policja.setSila(policja.getSila() + this.ostatnieZdarzenie.getWplywNaSilePolicji());
            populacja.setPoziomImplantow(populacja.getPoziomImplantow() + this.ostatnieZdarzenie.getWplywNaPoziomImplantow());
            // NOWA LINIA - AKTUALIZACJA DOSTĘPU DO STABILIZATORÓW
            populacja.setDostepDoStabilizatorow(populacja.getDostepDoStabilizatorow() + this.ostatnieZdarzenie.getWplywNaDostepDoStabilizatorow());


            int ofiaryElitaZdarzenie = this.ostatnieZdarzenie.getOfiaryElita();
            int ofiaryKlasaSredniaZdarzenie = this.ostatnieZdarzenie.getOfiaryKlasaSrednia();
            int ofiarySlumsyZdarzenie = this.ostatnieZdarzenie.getOfiarySlumsy();

            ofiaryElitaZdarzenie -= (int)(ofiaryElitaZdarzenie * (populacja.getPoziomImplantow() / 100.0));
            ofiaryKlasaSredniaZdarzenie -= (int)(ofiaryKlasaSredniaZdarzenie * (populacja.getDostepDoStabilizatorow() / 100.0));
            ofiarySlumsyZdarzenie -= (int)(ofiarySlumsyZdarzenie * (populacja.getDostepDoStabilizatorow() / 100.0));

            double redukcjaOfiarPolicja = policja.getSila() / 200.0;
            ofiaryElitaZdarzenie = Math.max(0, (int)(ofiaryElitaZdarzenie * (1.0 - redukcjaOfiarPolicja)));
            ofiaryKlasaSredniaZdarzenie = Math.max(0, (int)(ofiaryKlasaSredniaZdarzenie * (1.0 - redukcjaOfiarPolicja)));
            ofiarySlumsyZdarzenie = Math.max(0, (int)(ofiarySlumsyZdarzenie * (1.0 - redukcjaOfiarPolicja)));

            populacja.zmniejszElita(Math.max(0, ofiaryElitaZdarzenie));
            populacja.zmniejszKlasaSrednia(Math.max(0, ofiaryKlasaSredniaZdarzenie));
            populacja.zmniejszSlumsy(Math.max(0, ofiarySlumsyZdarzenie));

            smiertelnoscBiezacejEpoki = Math.max(0, ofiaryElitaZdarzenie) + Math.max(0, ofiaryKlasaSredniaZdarzenie) + Math.max(0, ofiarySlumsyZdarzenie);
        } else {
            int nowaDegeneracja = poprzedniStanEpoki.getDegeneracja() + 1;
            nowaDegeneracja = Math.max(0, Math.min(100, nowaDegeneracja));
            aktualnaEpoka.setDegeneracja(nowaDegeneracja);
            double redukcjaDegeneracjiPolicja = policja.getSila() * 0.25;
            aktualnaEpoka.setDegeneracja(Math.max(0, (int)(aktualnaEpoka.getDegeneracja() - redukcjaDegeneracjiPolicja)));
        }

        if (populacja.getCalkowitaLiczebnosc() > 0 &&
                ((double)populacja.getElita() / populacja.getCalkowitaLiczebnosc()) > PROG_PROC_WYSOKIEJ_ELITY) {
            int ofiaryKorupcjiElity = (int) (populacja.getElita() * STOPIEN_REDUKCJI_ELITY_PRZEZ_KORUPCJE);
            if (ofiaryKorupcjiElity > 0) {
                populacja.zmniejszElita(ofiaryKorupcjiElity);
                smiertelnoscBiezacejEpoki += ofiaryKorupcjiElity;
                this.komunikatDodatkowyOskutkachEpoki = "Wewnętrzne tarcia i korupcja w przerośniętej Elicie pochłonęły " + ofiaryKorupcjiElity + " jej członków.";
            }
        }

        aktualnaEpoka.setSmiertelnosc(smiertelnoscBiezacejEpoki);
        calkowitaSmiertelnosc += smiertelnoscBiezacejEpoki;

        raportCSV.zapiszDaneTicku("Epoka: " + numerEpoki,
                aktualnaEpoka.getDegeneracja(),
                aktualnaEpoka.getSmiertelnosc(),
                populacja,
                policja);

        epoki.add(aktualnaEpoka);
    }

    /**
     * Sprawdza, czy zostały spełnione warunki zakończenia symulacji.
     * @return {@code true} jeśli symulacja powinna się zakończyć, {@code false} w przeciwnym razie.
     */
    public boolean sprawdzWarunkiKonca() {
        if (aktualnaEpoka.getDegeneracja() >= MAKS_DEGENERACJA) {
            scenariuszKonca = "Społeczeństwo osiągnęło maksymalny poziom degeneracji (" + MAKS_DEGENERACJA + "%). Upadek cywilizacji.";
            return true;
        }
        if (populacja.getCalkowitaLiczebnosc() <= this.progKrytycznyPopulacji) {
            scenariuszKonca = "Populacja spadła poniżej krytycznego poziomu (" + this.progKrytycznyPopulacji +
                    " osób, tj. 20% populacji początkowej wynoszącej " + populacja.getPoczatkowaLiczebnosc() + "). Zagłada.";
            return true;
        }
        if (populacja.getElita() < MIN_ELITA) {
            scenariuszKonca = "Elita została zniszczona (mniej niż " + MIN_ELITA + " członków). Brak przywództwa.";
            return true;
        }
        if (populacja.getCalkowitaLiczebnosc() > 0 && populacja.getCalkowitaLiczebnosc() > this.progKrytycznyPopulacji) {
            boolean elitaDominuje = (double)populacja.getElita() / populacja.getCalkowitaLiczebnosc() > 0.80;
            boolean slumsyZnikome = (double)populacja.getSlumsy() / populacja.getCalkowitaLiczebnosc() < 0.05;
            if (elitaDominuje && slumsyZnikome) {
                scenariuszKonca = "Elita przejęła całkowitą kontrolę nad resztkami społeczeństwa, tworząc Cyber-Autokrację. Oporu już nie ma.";
                return true;
            }
        }
        if (policja.getSila() < 10) {
            scenariuszKonca = "Siły porządkowe upadły (siła poniżej 10%). Miasto pogrążyło się w anarchii bezprawia.";
            return true;
        }
        return false;
    }

    /**
     * Zwraca tekstowy opis scenariusza zakończenia symulacji.
     * @return String ze scenariuszem końca lub {@code null}, jeśli symulacja trwa.
     */
    public String getScenariuszKonca() { return scenariuszKonca; }

    /**
     * Generuje raport końcowy symulacji w formie tekstowej.
     * @return String zawierający sformatowany raport końcowy.
     */
    public String getRaportKoncowy() {
        StringBuilder sb = new StringBuilder();
        String separator = ConsoleColors.PURPLE_BOLD_BRIGHT + "--------------------------------\n" + ConsoleColors.RESET;
        sb.append(separator);
        sb.append(ConsoleColors.PURPLE_BOLD_BRIGHT + "   RAPORT KOŃCOWY SYMULACJI\n" + ConsoleColors.RESET);
        sb.append(separator);
        sb.append(ConsoleColors.WHITE_BOLD_BRIGHT).append("Całkowita liczba epok: ").append(numerEpoki).append(ConsoleColors.RESET).append("\n");
        sb.append(ConsoleColors.WHITE_BOLD_BRIGHT).append("Pozostała populacja: ").append(populacja.getCalkowitaLiczebnosc()).append(ConsoleColors.RESET).append("\n");
        sb.append(ConsoleColors.WHITE_BOLD_BRIGHT).append("  - Elita: ").append(populacja.getElita()).append(ConsoleColors.RESET).append("\n");
        sb.append(ConsoleColors.WHITE_BOLD_BRIGHT).append("  - Klasa Średnia: ").append(populacja.getKlasaSrednia()).append(ConsoleColors.RESET).append("\n");
        sb.append(ConsoleColors.WHITE_BOLD_BRIGHT).append("  - Slumsy: ").append(populacja.getSlumsy()).append(ConsoleColors.RESET).append("\n");
        sb.append(ConsoleColors.WHITE_BOLD_BRIGHT).append("Poziom implantów: ").append(populacja.getPoziomImplantow()).append("%").append(ConsoleColors.RESET).append("\n");
        sb.append(ConsoleColors.WHITE_BOLD_BRIGHT).append("Dostęp do stabilizatorów: ").append(populacja.getDostepDoStabilizatorow()).append("%").append(ConsoleColors.RESET).append("\n"); // Ta wartość teraz powinna się zmieniać
        sb.append(ConsoleColors.WHITE_BOLD_BRIGHT).append("Siła policji: ").append(policja.getSila()).append("%").append(ConsoleColors.RESET).append("\n");
        sb.append(separator);

        if (scenariuszKonca != null) {
            sb.append(ConsoleColors.RED_BOLD_BRIGHT).append("\nSCENARIUSZ ZAKOŃCZENIA: ").append(scenariuszKonca).append(ConsoleColors.RESET);
        } else {
            sb.append(ConsoleColors.YELLOW_BOLD).append("\n  Symulacja zakończona naturalnie (osiągnięto limit epok).").append(ConsoleColors.RESET);
        }
        sb.append("\n").append(separator);
        return sb.toString();
    }


    /**
     * Zwraca obiekt reprezentujący aktualny stan populacji.
     * @return Obiekt {@link Populacja}.
     */
    public Populacja getPopulacja() { return populacja; }

    /**
     * Zwraca obiekt reprezentujący aktualny stan sił policji.
     * @return Obiekt {@link Policja}.
     */
    public Policja getPolicja() { return policja; }

    /**
     * Zwraca obiekt reprezentujący dane dla bieżącej epoki.
     * @return Obiekt {@link Epoka}.
     */
    public Epoka getAktualnaEpoka() { return aktualnaEpoka; }

    /**
     * Ustawia obiekt bieżącej epoki.
     * @param aktualnaEpoka Nowy obiekt bieżącej epoki.
     */
    public void setAktualnaEpoka(Epoka aktualnaEpoka) { this.aktualnaEpoka = aktualnaEpoka; }


    /**
     * Zwraca ostatnie zdarzenie, które wystąpiło w symulacji.
     * @return Obiekt {@link Zdarzenie} lub {@code null}.
     */
    public Zdarzenie getOstatnieZdarzenie() { return ostatnieZdarzenie; }

    /**
     * Zwraca dodatkowy komunikat tekstowy opisujący specyficzne skutki ostatniej epoki.
     * @return String z komunikatem lub {@code null}.
     */
    public String getKomunikatDodatkowyOskutkachEpoki() {
        return komunikatDodatkowyOskutkachEpoki;
    }

    /**
     * Zwraca całkowitą liczbę ofiar śmiertelnych od początku symulacji.
     * @return Całkowita śmiertelność.
     */
    public int getCalkowitaSmiertelnosc() { return calkowitaSmiertelnosc; }


    /**
     * Zwraca listę wszystkich przetworzonych epok w symulacji.
     * @return Lista obiektów {@link Epoka}.
     */
    public List<Epoka> getEpoki() { return epoki; }
}