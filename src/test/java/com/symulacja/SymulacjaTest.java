package com.symulacja;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SymulacjaTest {

    private Symulacja symulacja;

    @BeforeEach
    void setUp() {
        symulacja = new Symulacja();
        // Ustawienie standardowych parametrów przed każdym testem
        symulacja.ustawParametryPoczatkowe(1000, 100, 300, 600, 50, 50, 50);
    }

    @Test
    @DisplayName("Sprawdź warunek końca: Max Degeneracja")
    void sprawdzWarunkiKonca_MaxDegeneracja() {
        Epoka epokaZMaxDegeneracja = new Epoka(10);
        epokaZMaxDegeneracja.setDegeneracja(95); // Powyżej progu 90
        symulacja.setAktualnaEpoka(epokaZMaxDegeneracja);

        assertTrue(symulacja.sprawdzWarunkiKonca());
        assertNotNull(symulacja.getScenariuszKonca());
        assertTrue(symulacja.getScenariuszKonca().contains("maksymalny poziom degeneracji"));
    }

    @Test
    @DisplayName("Sprawdź warunek końca: Populacja poniżej progu krytycznego")
    void sprawdzWarunkiKonca_PopulacjaKrytyczna() {
        // Próg krytyczny dla 1000 to 200 (20%)
        symulacja.getPopulacja().setElita(50);
        symulacja.getPopulacja().setKlasaSrednia(50);
        symulacja.getPopulacja().setSlumsy(50); // Razem 150 < 200

        assertTrue(symulacja.sprawdzWarunkiKonca());
        assertTrue(symulacja.getScenariuszKonca().contains("Populacja spadła poniżej krytycznego poziomu"));
    }

    @Test
    @DisplayName("Sprawdź warunek końca: Elita zniszczona")
    void sprawdzWarunkiKonca_BrakElity() {
        symulacja.getPopulacja().setElita(0); // Poniżej progu 1
        assertTrue(symulacja.sprawdzWarunkiKonca());
        assertTrue(symulacja.getScenariuszKonca().contains("Elita została zniszczona"));
    }

    @Test
    @DisplayName("Sprawdź warunek końca: Upadek policji")
    void sprawdzWarunkiKonca_UpadekPolicji() {
        symulacja.getPolicja().setSila(5); // Poniżej progu 10
        assertTrue(symulacja.sprawdzWarunkiKonca());
        assertTrue(symulacja.getScenariuszKonca().contains("Siły porządkowe upadły"));
    }

    @Test
    @DisplayName("Mechanizm korupcji powinien zmniejszyć elitę, gdy jest jej za dużo")
    void wykonajCyklSymulacji_KorupcjaElity() {
        // Ustaw stan, w którym elita stanowi > 30% populacji
        symulacja.ustawParametryPoczatkowe(1000, 400, 300, 300, 50, 50, 50); // 40% elity
        int poczatkowaLiczbaElity = symulacja.getPopulacja().getElita();

        symulacja.wykonajCyklSymulacji();

        int koncowaLiczbaElity = symulacja.getPopulacja().getElita();

        // Oczekujemy, że ubędzie 2% z 400, czyli 8. Sprawdzamy czy ubyło (bo mogą dojść ofiary zdarzenia).
        assertTrue(koncowaLiczbaElity < poczatkowaLiczbaElity);
        assertNotNull(symulacja.getKomunikatDodatkowyOskutkachEpoki());
        assertTrue(symulacja.getKomunikatDodatkowyOskutkachEpoki().contains("Wewnętrzne tarcia i korupcja"));
    }

    @Test
    @DisplayName("Numer epoki powinien się zwiększyć po wykonaniu cyklu")
    void wykonajCyklSymulacji_InkrementujeEpoke() {
        int numerPoczatkowy = symulacja.getAktualnaEpoka().getNumerEpoki();
        assertEquals(0, numerPoczatkowy);

        symulacja.wykonajCyklSymulacji();

        int numerKoncowy = symulacja.getAktualnaEpoka().getNumerEpoki();
        assertEquals(1, numerKoncowy);
    }
}