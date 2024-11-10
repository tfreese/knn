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

        this.channelInfos.add(new ImageColorChannelInfo(target, ColorChannel.ALPHA));
        this.channelInfos.add(new ImageColorChannelInfo(target, ColorChannel.RED));
        this.channelInfos.add(new ImageColorChannelInfo(target, ColorChannel.GREEN));
        this.channelInfos.add(new ImageColorChannelInfo(target, ColorChannel.BLUE));
    }

    public BufferedImage createCoOccurrenceMatrixImage() {
        if (this.coOccurrenceMatrixImage == null) {
            this.coOccurrenceMatrixImage = new BufferedImage(510, 510, BufferedImage.TYPE_INT_RGB);

            for (ImageColorChannelInfo channelInfo : this.channelInfos) {
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
                            this.coOccurrenceMatrixImage.setRGB(x, y, colorChannel.getColor().getRGB());
                        }
                    }
                }
            }
        }

        return this.coOccurrenceMatrixImage;
    }

    public List<ImageColorChannelInfo> getChannelInfos() {
        return Collections.unmodifiableList(this.channelInfos);
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
        if (this.infoVector == null) {
            this.infoVector = new double[9 * this.channelInfos.size()];

            int i = 0;

            // Co-Occurrence-Matrix direkt verwenden
            // final ImageColorChannelInfo channelInfo = this.channelInfos.get(1);
            // int[][] matrix = channelInfo.getCoOccurrenceMatrix();
            //
            // for (int x = 0; x < 255; x++)
            // {
            // for (int y = 0; y < 255; y++)
            // {
            // if (matrix[x][y] > 0)
            // {
            // this.outputVector[i] = 1;
            // }
            //
            // i++;
            // }
            // }

            for (ImageColorChannelInfo channelInfo : this.channelInfos) {
                this.infoVector[i++] = channelInfo.getMinimalerFarbwert();
                this.infoVector[i++] = channelInfo.getMaximalerFarbwert();
                this.infoVector[i++] = channelInfo.getMittlererFarbwert();
                this.infoVector[i++] = channelInfo.getEntropie();
                this.infoVector[i++] = channelInfo.getUniformitaet();
                this.infoVector[i++] = channelInfo.getUnaehnlichkeit();
                this.infoVector[i++] = channelInfo.getInverseDifferenz();
                this.infoVector[i++] = channelInfo.getInversesDifferenzMoment();
                this.infoVector[i++] = channelInfo.getKontrast();
            }
        }

        return this.infoVector;
    }

    /**
     * Liefert die gesammelten Daten aller Farbkanäle.<br>
     * Die Daten sind hier re-skaliert da grosse Werte dabei sind, wie Uniformität (Energie) und Kontrast.
     */
    public double[] getInfoVectorReScaled() {
        if (this.infoVectorReScaled == null) {
            this.infoVectorReScaled = new double[getInfoVector().length];

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

                this.infoVectorReScaled[i] = value;

                min = Math.min(min, value);
                max = Math.max(max, value);
            }

            // double minNorm = 0.0D;
            // double maxNorm = 500_000D;
            //
            // for (int i = 0; i < this.infoVectorReScaled.length; i++)
            // {
            // // this.infoVectorReScaled[i] = ExtMath.reScale(this.infoVectorReScaled[i], min, max, minNorm, maxNorm);
            //
            // this.infoVectorReScaled[i] = minNorm + (((this.infoVectorReScaled[i] - min) * (maxNorm - minNorm)) / (max - min));
            // }
        }

        return this.infoVectorReScaled;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        for (ImageColorChannelInfo channelInfo : this.channelInfos) {
            sb.append(channelInfo.toString());
            sb.append("\n");
        }

        return sb.toString();
    }
}
