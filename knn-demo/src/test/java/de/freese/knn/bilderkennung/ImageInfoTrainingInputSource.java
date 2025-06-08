// Created: 02.08.2009
package de.freese.knn.bilderkennung;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.freese.knn.bilderkennung.utils.image.info.ImageInfo;
import de.freese.knn.net.trainer.TrainingInputSource;

/**
 * @author Thomas Freese
 */
public class ImageInfoTrainingInputSource implements TrainingInputSource {
    private final List<ImageInfo> imageInfos = new ArrayList<>();

    public ImageInfoTrainingInputSource() throws Exception {
        super();

        imageInfos.add(new ImageInfo("Ampel.gif"));
        imageInfos.add(new ImageInfo("Bahnkreuz.gif"));
        imageInfos.add(new ImageInfo("BigBrother.jpg"));
        imageInfos.add(new ImageInfo("Einbahn.gif"));
        imageInfos.add(new ImageInfo("Klippe.gif"));
        imageInfos.add(new ImageInfo("Seaside.jpg"));
        imageInfos.add(new ImageInfo("Stop.gif"));
        imageInfos.add(new ImageInfo("Sylvester.jpg"));
        imageInfos.add(new ImageInfo("winnt.bmp"));
    }

    @Override
    public double[] getInputAt(final int index) {
        return imageInfos.get(index).getInfoVectorReScaled();
    }

    @Override
    public double[] getOutputAt(final int index) {
        final double[] output = new double[getSize()];
        Arrays.fill(output, 0.0D);

        output[index] = 1.0D;

        return output;
    }

    @Override
    public int getSize() {
        return imageInfos.size();
    }
}
