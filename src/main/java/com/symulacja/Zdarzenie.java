package com.symulacja;

/**
 * Interfejs Zdarzenie definiuje wspólne metody dla wszystkich typów zdarzeń
 * mogących wystąpić w symulacji. Umożliwia polimorficzne traktowanie
 * różnych konkretnych zdarzeń, które wpływają na stan symulacji.
 */
public interface Zdarzenie {

    /**
     * Zwraca typ (nazwę) zdarzenia.
     * @return String reprezentujący typ zdarzenia.
     */
    String getTyp();

    /**
     * Zwraca wpływ zdarzenia na poziom degeneracji społeczeństwa.
     * Wartość dodatnia zwiększa degenerację, ujemna zmniejsza.
     * @return Wartość całkowita wpływu na degenerację.
     */
    int getWplywNaDegeneracje();

    /**
     * Zwraca liczbę ofiar w grupie Elity spowodowaną przez zdarzenie.
     * @return Liczba ofiar w Elity.
     */
    int getOfiaryElita();

    /**
     * Zwraca liczbę ofiar w grupie Klasy Średniej spowodowaną przez zdarzenie.
     * @return Liczba ofiar w Klasy Średniej.
     */
    int getOfiaryKlasaSrednia();

    /**
     * Zwraca liczbę ofiar w grupie Slumsów spowodowaną przez zdarzenie.
     * @return Liczba ofiar w Slumsach.
     */
    int getOfiarySlumsy();

    /**
     * Zwraca wpływ zdarzenia na siłę policji.
     * Wartość dodatnia zwiększa siłę, ujemna zmniejsza.
     * @return Wartość całkowita wpływu na siłę policji.
     */
    int getWplywNaSilePolicji();

    /**
     * Zwraca wpływ zdarzenia na poziom implantów w społeczeństwie.
     * Wartość dodatnia zwiększa poziom, ujemna zmniejsza.
     * @return Wartość całkowita wpływu na poziom implantów.
     */
    int getWplywNaPoziomImplantow();

    /**
     * Zwraca wpływ zdarzenia na dostęp do stabilizatorów w społeczeństwie.
     * Wartość dodatnia zwiększa dostęp, ujemna zmniejsza.
     * @return Wartość całkowita wpływu na dostęp do stabilizatorów.
     */
    int getWplywNaDostepDoStabilizatorow(); // NOWA METODA
}