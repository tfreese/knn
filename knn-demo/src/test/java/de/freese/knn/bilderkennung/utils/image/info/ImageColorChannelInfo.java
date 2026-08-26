package de.freese.knn.bilderkennung.utils.image.info;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

/**
 * Klasse für Informationen eines Bild-Farbkanals.
 *
 * @author Thomas Freese
 * @since 02.08.2009
 */
public class ImageColorChannelInfo {
    private final BufferedImage bufferedImage;
    private final ColorChannel colorChannel;

    private int[][] coOccurrenceMatrix;
    private double entropie = -1.0D;
    private int farbTiefe = -1;
    private int[] histogramm;
    private double inverseDifferenz = -1.0D;
    private double inversesDifferenzMoment = -1.0D;
    private double kontrast = -1.0D;
    private int maximalerFarbwert = -1;
    private int minimalerFarbwert = -1;
    private int mittlererFarbwert = -1;
    private double unaehnlichkeit = -1.0D;
    private double uniformitaet = -1.0D;

    public ImageColorChannelInfo(final BufferedImage bufferedImage, final ColorChannel colorChannel) {
        super();

        this.bufferedImage = bufferedImage;
        this.colorChannel = colorChannel;

        calculate();
    }

    /**
     * Liefert die "Grauwertübergangsmatrix".<br>
     * Zählt wie oft Farbwerte nebeneinander auftreten.
     */
    public int[][] getCoOccurrenceMatrix() {
        return coOccurrenceMatrix;
    }

    public ColorChannel getColorChannel() {
        return colorChannel;
    }

    public double getEntropie() {
        return entropie;
    }

    public int getFarbTiefe() {
        return farbTiefe;
    }

    public int[] getHistogramm() {
        return histogramm;
    }

    /**
     * Homogenität.
     */
    public double getInverseDifferenz() {
        return inverseDifferenz;
    }

    public double getInversesDifferenzMoment() {
        return inversesDifferenzMoment;
    }

    public double getKontrast() {
        return kontrast;
    }

    public int getMaximalerFarbwert() {
        return maximalerFarbwert;
    }

    public int getMinimalerFarbwert() {
        return minimalerFarbwert;
    }

    public int getMittlererFarbwert() {
        return mittlererFarbwert;
    }

    public double getUnaehnlichkeit() {
        return unaehnlichkeit;
    }

    /**
     * Energie.
     */
    public double getUniformitaet() {
        return uniformitaet;
    }

    @Override
    public String toString() {
        return "ChannelInfo: " + getColorChannel() + "\n"
                + "Minimaler Farbwert: " + getMinimalerFarbwert() + "\n"
                + "Maximaler Farbwert: " + getMaximalerFarbwert() + "\n"
                + "Mittlerer Farbwert: " + getMittlererFarbwert() + "\n"
                + "Entropie: " + getEntropie() + "\n"
                + "Uniformität: " + getUniformitaet() + "\n"
                + "Unähnlichkeit: " + getUnaehnlichkeit() + "\n"
                + "Inverse Differenz: " + getInverseDifferenz() + "\n"
                + "Inverses Differenz Moment: " + getInversesDifferenzMoment() + "\n"
                + "Kontrast: " + getKontrast() + "\n";
    }

    /**
     * Berechnen aller Werte des Farbkanals.
     */
    private void calculate() {
        final ColorModel colorModel = bufferedImage.getColorModel();
        final int width = bufferedImage.getWidth();
        final int height = bufferedImage.getHeight();

        final double pixelSize = colorModel.getPixelSize();
        final double colorBands = colorModel.getNumComponents();
        farbTiefe = (int) Math.pow(2.0, pixelSize / colorBands);
        coOccurrenceMatrix = new int[farbTiefe][farbTiefe];
        histogramm = new int[farbTiefe];

        minimalerFarbwert = 0;
        maximalerFarbwert = 0;
        mittlererFarbwert = 0;

        // Co-Occurrence-Matrix berechnen
        for (int x = 0; x < (width - 1); x++) {
            for (int y = 0; y < height; y++) {
                final int pixel1 = bufferedImage.getRGB(x, y);
                final int pixel2 = bufferedImage.getRGB(x + 1, y);

                final int color1 = colorChannel.getValue(pixel1);
                final int color2 = colorChannel.getValue(pixel2);

                coOccurrenceMatrix[color1][color2]++;

                minimalerFarbwert = Math.min(minimalerFarbwert, color1);
                maximalerFarbwert = Math.max(maximalerFarbwert, color1);
                mittlererFarbwert += color1;
                histogramm[color1]++;
            }
        }

        // Letzte Pixelzeile für Histogramm nicht vergessen.
        for (int y = 0; y < height; y++) {
            final int pixel = bufferedImage.getRGB(width - 1, y);

            final int color = colorChannel.getValue(pixel);

            minimalerFarbwert = Math.min(minimalerFarbwert, color);
            maximalerFarbwert = Math.max(maximalerFarbwert, color);
            mittlererFarbwert += color;
            histogramm[color]++;
        }

        mittlererFarbwert /= width * height;

        // Weitere Parameter
        entropie = 0.0D;
        uniformitaet = 0.0D;
        unaehnlichkeit = 0.0D;
        inverseDifferenz = 0.0D;
        inversesDifferenzMoment = 0.0D;
        kontrast = 0.0D;

        for (int x = 0; x < farbTiefe; ++x) {
            for (int y = 0; y < farbTiefe; ++y) {
                final double c = coOccurrenceMatrix[x][y];
                final double d = x - y;

                if (c != 0.0) {
                    entropie += c * Math.log(c);
                }

                uniformitaet += c * c;
                unaehnlichkeit += c * Math.abs(d);
                inverseDifferenz += c / (1.0D + Math.abs(d));
                inversesDifferenzMoment += c / (1.0D + (d * d));
                kontrast += c * d * d;
            }
        }
    }
}
