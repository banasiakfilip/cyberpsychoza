package com.symulacja;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PopulacjaTest {

    @Test
    void testKonstruktorPoprawnieInicjalizujeWartosci() {
        Populacja populacja = new Populacja(1000, 50, 70);
        assertEquals(1000, populacja.getPoczatkowaLiczebnosc());
        assertEquals(1000, populacja.getCalkowitaLiczebnosc());
        assertEquals(50, populacja.getPoziomImplantow());
        assertEquals(70, populacja.getDostepDoStabilizatorow());
    }

    @Test
    void testZmniejszanieGrupSpolecznych() {
        Populacja populacja = new Populacja(1000, 0, 0);
        populacja.setElita(100);
        populacja.setKlasaSrednia(300);
        populacja.setSlumsy(600);

        populacja.zmniejszElita(20);
        assertEquals(80, populacja.getElita());
        assertEquals(980, populacja.getCalkowitaLiczebnosc());

        populacja.zmniejszKlasaSrednia(50);
        assertEquals(250, populacja.getKlasaSrednia());
        assertEquals(930, populacja.getCalkowitaLiczebnosc());

        populacja.zmniejszSlumsy(100);
        assertEquals(500, populacja.getSlumsy());
        assertEquals(830, populacja.getCalkowitaLiczebnosc());
    }

    @Test
    void testZmniejszanieGrupSpolecznychNieSchodziPonizejZera() {
        Populacja populacja = new Populacja(100, 0, 0);
        populacja.setElita(10);

        populacja.zmniejszElita(20); // Próba odjęcia więcej niż jest
        assertEquals(0, populacja.getElita());
        assertEquals(0, populacja.getCalkowitaLiczebnosc());
    }

    @Test
    void testSetteryOgraniczajaWartosciProcentowe() {
        Populacja populacja = new Populacja(100, 0, 0);

        populacja.setPoziomImplantow(150);
        assertEquals(100, populacja.getPoziomImplantow());

        populacja.setPoziomImplantow(-50);
        assertEquals(0, populacja.getPoziomImplantow());

        populacja.setDostepDoStabilizatorow(110);
        assertEquals(100, populacja.getDostepDoStabilizatorow());

        populacja.setDostepDoStabilizatorow(-10);
        assertEquals(0, populacja.getDostepDoStabilizatorow());
    }
}