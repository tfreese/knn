/**
 * Created: 17.07.2011
 */

package de.freese.knn.net.neuron;

import de.freese.knn.net.function.IFunction;
import de.freese.knn.net.layer.input.InputLayer;
import de.freese.knn.net.layer.output.OutputLayer;

/**
 * Interface eines Neurons.
 * 
 * @author Thomas Freese
 */
public interface INeuron
{
	/**
	 * Liefert die Aktivieerungsfunktion des Neurons.
	 * 
	 * @return {@link IFunction}
	 */
	public IFunction getFunction();

	/**
	 * Liefert den Bias Wert um Ueberanpassungen zu vermeiden.
	 * 
	 * @return double
	 */
	public double getInputBIAS();

	/**
	 * Liefert die Anzahl von Eingaengen.
	 * 
	 * @return int
	 */
	public int getInputSize();

	/**
	 * Liefert das Eingangsgewicht.<br>
	 * Der {@link InputLayer} hat keine Eingangsgewichte !
	 * 
	 * @param index int
	 * @return double
	 */
	public double getInputWeight(int index);

	/**
	 * Liefert die Position des Neurons im Layer.
	 * 
	 * @return int
	 */
	public int getLayerIndex();

	/**
	 * Liefert die Anzahl von Ausgaengen.
	 * 
	 * @return int
	 */
	public int getOutputSize();

	/**
	 * Liefert das Ausgangsgewicht.<br>
	 * Der {@link OutputLayer} hat keine Ausgangsgewichte !
	 * 
	 * @param index int
	 * @return double
	 */
	public double getOutputWeight(int index);

	/**
	 * Liefert den Bias Gewicht um Ueberanpassungen zu vermeiden.<br>
	 * Der {@link InputLayer} hat keine BIAS Gewichte !
	 * 
	 * @param value double
	 */
	public void setInputBIAS(double value);

	/**
	 * Setzt das Eingangsgewicht.<br>
	 * Der {@link InputLayer} hat keine Eingangsgewichte !
	 * 
	 * @param index int
	 * @param weight double
	 */
	public void setInputWeight(int index, double weight);

	/**
	 * Setzt das Ausgangsgewicht.<br>
	 * Der {@link OutputLayer} hat keine Ausgangsgewichte !
	 * 
	 * @param index int
	 * @param weight double
	 */
	public void setOutputWeight(int index, double weight);
}
