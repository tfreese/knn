// Created: 17.07.2011
package de.freese.knn.net.neuron;

import de.freese.knn.net.function.Function;
import de.freese.knn.net.layer.InputLayer;
import de.freese.knn.net.layer.OutputLayer;

/**
 * Interface eines Neurons.
 *
 * @author Thomas Freese
 */
public interface Neuron
{
    /**
     * Liefert die Aktivierungsfunktion des Neurons.
     *
     * @return {@link Function}
     */
    Function getFunction();

    /**
     * Liefert den Bias Wert um Ueberanpassungen zu vermeiden.
     *
     * @return double
     */
    double getInputBIAS();

    /**
     * Liefert die Anzahl von Eingaengen.
     *
     * @return int
     */
    int getInputSize();

    /**
     * Liefert das Eingangsgewicht.<br>
     * Der {@link InputLayer} hat keine Eingangsgewichte !
     *
     * @param index int
     *
     * @return double
     */
    double getInputWeight(int index);

    /**
     * Liefert die Position des Neurons im Layer.
     *
     * @return int
     */
    int getLayerIndex();

    /**
     * Liefert die Anzahl von Ausgaengen.
     *
     * @return int
     */
    int getOutputSize();

    /**
     * Liefert das Ausgangsgewicht.<br>
     * Der {@link OutputLayer} hat keine Ausgangsgewichte !
     *
     * @param index int
     *
     * @return double
     */
    double getOutputWeight(int index);

    /**
     * Liefert den Bias Gewicht um Ueberanpassungen zu vermeiden.<br>
     * Der {@link InputLayer} hat keine BIAS Gewichte !
     *
     * @param value double
     */
    void setInputBIAS(double value);

    /**
     * Setzt das Eingangsgewicht.<br>
     * Der {@link InputLayer} hat keine Eingangsgewichte !
     *
     * @param index int
     * @param weight double
     */
    void setInputWeight(int index, double weight);

    /**
     * Setzt das Ausgangsgewicht.<br>
     * Der {@link OutputLayer} hat keine Ausgangsgewichte !
     *
     * @param index int
     * @param weight double
     */
    void setOutputWeight(int index, double weight);
}
