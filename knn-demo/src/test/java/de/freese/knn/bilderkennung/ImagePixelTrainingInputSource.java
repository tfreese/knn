// Created: 15.07.2008
package de.freese.knn.bilderkennung;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.freese.knn.bilderkennung.utils.ImageData;
import de.freese.knn.net.trainer.TrainingInputSource;

/**
 * TrainingInputSource für die Mustererkennung von Bildern.
 *
 * @author Thomas Freese
 */
public class ImagePixelTrainingInputSource implements TrainingInputSource
{
    private final List<ImageData> imageDataList = new ArrayList<>();

    public ImagePixelTrainingInputSource() throws Exception
    {
        super();

        this.imageDataList.add(new ImageData("Ampel.gif"));
        this.imageDataList.add(new ImageData("Bahnkreuz.gif"));
        this.imageDataList.add(new ImageData("BigBrother.jpg"));
        this.imageDataList.add(new ImageData("Einbahn.gif"));
        this.imageDataList.add(new ImageData("Klippe.gif"));
        this.imageDataList.add(new ImageData("Seaside.jpg"));
        this.imageDataList.add(new ImageData("Stop.gif"));
        this.imageDataList.add(new ImageData("Sylvester.jpg"));
        this.imageDataList.add(new ImageData("winnt.bmp"));
    }

    /**
     * Liefert die Liste der Bilderdaten.
     */
    public List<ImageData> getImageData()
    {
        return this.imageDataList;
    }

    /**
     * @see de.freese.knn.net.trainer.TrainingInputSource#getInputAt(int)
     */
    @Override
    public double[] getInputAt(final int index)
    {
        ImageData imageData = this.imageDataList.get(index);

        return imageData.getPixels();
    }

    /**
     * @see de.freese.knn.net.trainer.TrainingInputSource#getOutputAt(int)
     */
    @Override
    public double[] getOutputAt(final int index)
    {
        double[] output = new double[getSize()];
        Arrays.fill(output, 0.0D);

        output[index] = 1.0D;

        return output;
    }

    /**
     * @see de.freese.knn.net.trainer.TrainingInputSource#getSize()
     */
    @Override
    public int getSize()
    {
        return this.imageDataList.size();
    }
}
