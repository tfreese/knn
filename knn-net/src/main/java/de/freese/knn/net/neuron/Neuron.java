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
     */
    Function getFunction();

    /**
     * Liefert den Bias Wert, um Überanpassungen zu vermeiden.
     */
    double getInputBIAS();

    /**
     * Liefert die Anzahl von Eingängen.
     */
    int getInputSize();

    /**
     * Liefert das Eingangsgewicht.<br>
     * Der {@link InputLayer} hat keine Eingangsgewichte !
     */
    double getInputWeight(int index);

    /**
     * Liefert die Position des Neurons im Layer.
     */
    int getLayerIndex();

    /**
     * Liefert die Anzahl von Ausgängen.
     */
    int getOutputSize();

    /**
     * Liefert das Ausgangsgewicht.<br>
     * Der {@link OutputLayer} hat keine Ausgangsgewichte !
     */
    double getOutputWeight(int index);

    /**
     * Liefert den Bias Gewicht, um Überanpassungen zu vermeiden.<br>
     * Der {@link InputLayer} hat keine BIAS Gewichte !
     */
    void setInputBIAS(double value);

    /**
     * Setzt das Eingangsgewicht.<br>
     * Der {@link InputLayer} hat keine Eingangsgewichte !
     */
    void setInputWeight(int index, double weight);

    /**
     * Setzt das Ausgangsgewicht.<br>
     * Der {@link OutputLayer} hat keine Ausgangsgewichte !
     */
    void setOutputWeight(int index, double weight);
}
