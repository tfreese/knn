/**
 * 15.07.2008
 */
package de.freese.knn.muster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.freese.knn.muster.imageop.ImageData;
import de.freese.knn.net.trainer.ITrainingInputSource;

/**
 * TrainingInputSource für die Mustererkennung von Bildern.
 * 
 * @author Thomas Freese
 */
public class ImagePixelTrainingInputSource implements ITrainingInputSource
{
	/**
	 *
	 */
	private final List<ImageData> imageData = new ArrayList<>();

	/**
	 * Creates a new {@link ImagePixelTrainingInputSource} object.
	 * 
	 * @throws Exception falls was schief geht.
	 */
	public ImagePixelTrainingInputSource() throws Exception
	{
		super();

		this.imageData.add(new ImageData("Ampel.gif"));
		this.imageData.add(new ImageData("Bahnkreuz.gif"));
		// this.imageData.add(new ImageData("BigBrother.jpg"));
		this.imageData.add(new ImageData("Einbahn.gif"));
		this.imageData.add(new ImageData("Klippe.gif"));
		// this.imageData.add(new ImageData("Seaside.jpg"));
		this.imageData.add(new ImageData("Stop.gif"));
		// this.imageData.add(new ImageData("Sylvester.jpg"));
		// this.imageData.add(new ImageData("winnt.bmp"));
	}

	/**
	 * Liefert die Liste der Bilderdaten.
	 * 
	 * @return {@link List}
	 */
	public List<ImageData> getImageData()
	{
		return this.imageData;
	}

	/**
	 * @see de.freese.knn.net.trainer.ITrainingInputSource#getInputAt(int)
	 */
	@Override
	public double[] getInputAt(final int index)
	{
		ImageData imageData = this.imageData.get(index);

		return imageData.getPixels();
	}

	/**
	 * @see de.freese.knn.net.trainer.ITrainingInputSource#getOutputAt(int)
	 */
	@Override
	public double[] getOutputAt(final int index)
	{
		double[] output = new double[10];
		Arrays.fill(output, 0.0D);

		output[index] = 1.0D;

		return output;
	}

	/**
	 * @see de.freese.knn.net.trainer.ITrainingInputSource#getSize()
	 */
	@Override
	public int getSize()
	{
		return this.imageData.size();
	}
}
