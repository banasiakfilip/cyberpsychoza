package com.symulacja;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PolicjaTest {
    @Test
    void setSilaShouldClampValueBetween0And100() {
        Policja policja = new Policja(50);

        policja.setSila(120);
        assertEquals(100, policja.getSila(), "Siła powinna być ograniczona do 100");

        policja.setSila(-20);
        assertEquals(0, policja.getSila(), "Siła nie powinna być mniejsza niż 0");

        policja.setSila(75);
        assertEquals(75, policja.getSila(), "Siła powinna być ustawiona na 75");
    }
}