// Created: 02.08.2009
package de.freese.knn.bilderkennung.utils.image.info;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

/**
 * Klasse für Bildinformationen.
 *
 * @author Thomas Freese
 */
public class ImageInfo {
    private final List<ImageColorChannelInfo> channelInfos = new ArrayList<>();

    private BufferedImage coOccurrenceMatrixImage;
    private double[] infoVector;
    private double[] infoVectorReScaled;

    public ImageInfo(final String fileName) throws Exception {
        super();

        // URL url = ClassLoader.getSystemClassLoader().getResource(fileName);
        final URL url = ClassLoader.getSystemResource(fileName);
        final BufferedImage source = ImageIO.read(url);

        // In RGB umwandeln
        final int w = source.getWidth();
        final int h = source.getHeight();
        final BufferedImage target = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g = target.createGraphics();
        g.drawRenderedImage(source, null);
        g.dispose();

        channelInfos.add(new ImageColorChannelInfo(target, ColorChannel.ALPHA));
        channelInfos.add(new ImageColorChannelInfo(target, ColorChannel.RED));
        channelInfos.add(new ImageColorChannelInfo(target, ColorChannel.GREEN));
        channelInfos.add(new ImageColorChannelInfo(target, ColorChannel.BLUE));
    }

    public BufferedImage createCoOccurrenceMatrixImage() {
        if (coOccurrenceMatrixImage == null) {
            coOccurrenceMatrixImage = new BufferedImage(510, 510, BufferedImage.TYPE_INT_RGB);

            for (ImageColorChannelInfo channelInfo : channelInfos) {
                final ColorChannel colorChannel = channelInfo.getColorChannel();

                int xOffset = 0;
                int yOffset = 0;

                if (ColorChannel.RED.equals(colorChannel)) {
                    xOffset = 255;
                }
                else if (ColorChannel.GREEN.equals(colorChannel)) {
                    xOffset = 0;
                    yOffset = 255;
                }
                else if (ColorChannel.BLUE.equals(colorChannel)) {
                    xOffset = 255;
                    yOffset = 255;
                }

                for (int x = xOffset; x < (255 + xOffset); x++) {
                    for (int y = yOffset; y < (255 + yOffset); y++) {
                        final int value = channelInfo.getCoOccurrenceMatrix()[x - xOffset][y - yOffset];

                        if (value > 0) {
                            coOccurrenceMatrixImage.setRGB(x, y, colorChannel.getColor().getRGB());
                        }
                    }
                }
            }
        }

        return coOccurrenceMatrixImage;
    }

    public List<ImageColorChannelInfo> getChannelInfos() {
        return Collections.unmodifiableList(channelInfos);
    }

    /**
     * Liefert die gesammelten Daten aller Farbkanäle.<br>
     * 1. Minimaler Farbwert<br>
     * 2. Maximaler Farbwert<br>
     * 3. Mittlerer Farbwert<br>
     * 4. Entropie<br>
     * 5. Uniformität<br>
     * 6. Unähnlichkeit<br>
     * 7. Inverse Differenz<br>
     * 8. Inverses Differenzmoment<br>
     * 9. Kontrast<br>
     */
    public double[] getInfoVector() {
        if (infoVector == null) {
            infoVector = new double[9 * channelInfos.size()];

            int i = 0;

            // Co-Occurrence-Matrix direkt verwenden
            // final ImageColorChannelInfo channelInfo = channelInfos.get(1);
            // int[][] matrix = channelInfo.getCoOccurrenceMatrix();
            //
            // for (int x = 0; x < 255; x++)
            // {
            // for (int y = 0; y < 255; y++)
            // {
            // if (matrix[x][y] > 0)
            // {
            // outputVector[i] = 1;
            // }
            //
            // i++;
            // }
            // }

            for (ImageColorChannelInfo channelInfo : channelInfos) {
                infoVector[i++] = channelInfo.getMinimalerFarbwert();
                infoVector[i++] = channelInfo.getMaximalerFarbwert();
                infoVector[i++] = channelInfo.getMittlererFarbwert();
                infoVector[i++] = channelInfo.getEntropie();
                infoVector[i++] = channelInfo.getUniformitaet();
                infoVector[i++] = channelInfo.getUnaehnlichkeit();
                infoVector[i++] = channelInfo.getInverseDifferenz();
                infoVector[i++] = channelInfo.getInversesDifferenzMoment();
                infoVector[i++] = channelInfo.getKontrast();
            }
        }

        return infoVector;
    }

    /**
     * Liefert die gesammelten Daten aller Farbkanäle.<br>
     * Die Daten sind hier re-skaliert da grosse Werte dabei sind, wie Uniformität (Energie) und Kontrast.
     */
    public double[] getInfoVectorReScaled() {
        if (infoVectorReScaled == null) {
            infoVectorReScaled = new double[getInfoVector().length];

            double min = Double.MAX_VALUE;
            double max = Double.MIN_VALUE;

            for (int i = 0; i < getInfoVector().length; i++) {
                double value = getInfoVector()[i];

                // Sehr große Werte häppchenweise herunterrechnen.
                if (value > 1_000_000D) {
                    value /= 1_000D;
                }

                if (value > 1_000_000D) {
                    value /= 100D;
                }

                if (value > 1_000_000D) {
                    value /= 10D;
                }

                if (value > 500_000D) {
                    value /= 2D;
                }

                infoVectorReScaled[i] = value;

                min = Math.min(min, value);
                max = Math.max(max, value);
            }

            // double minNorm = 0.0D;
            // double maxNorm = 500_000D;
            //
            // for (int i = 0; i < infoVectorReScaled.length; i++)
            // {
            // // infoVectorReScaled[i] = ExtMath.reScale(infoVectorReScaled[i], min, max, minNorm, maxNorm);
            //
            // infoVectorReScaled[i] = minNorm + (((infoVectorReScaled[i] - min) * (maxNorm - minNorm)) / (max - min));
            // }
        }

        return infoVectorReScaled;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        for (ImageColorChannelInfo channelInfo : channelInfos) {
            sb.append(channelInfo.toString());
            sb.append("\n");
        }

        return sb.toString();
    }
}
