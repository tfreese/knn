// Created: 02.08.2009
package de.freese.knn.bilderkennung.utils.image.info;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

/**
 * Klasse für Informationen eines Bild-Farbkanals.
 *
 * @author Thomas Freese
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
        return this.coOccurrenceMatrix;
    }

    public ColorChannel getColorChannel() {
        return this.colorChannel;
    }

    public double getEntropie() {
        return this.entropie;
    }

    public int getFarbTiefe() {
        return this.farbTiefe;
    }

    public int[] getHistogramm() {
        return this.histogramm;
    }

    /**
     * Homogenität.
     */
    public double getInverseDifferenz() {
        return this.inverseDifferenz;
    }

    public double getInversesDifferenzMoment() {
        return this.inversesDifferenzMoment;
    }

    public double getKontrast() {
        return this.kontrast;
    }

    public int getMaximalerFarbwert() {
        return this.maximalerFarbwert;
    }

    public int getMinimalerFarbwert() {
        return this.minimalerFarbwert;
    }

    public int getMittlererFarbwert() {
        return this.mittlererFarbwert;
    }

    public double getUnaehnlichkeit() {
        return this.unaehnlichkeit;
    }

    /**
     * Energie.
     */
    public double getUniformitaet() {
        return this.uniformitaet;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ChannelInfo: ").append(getColorChannel()).append("\n");
        sb.append("Minimaler Farbwert: ").append(getMinimalerFarbwert()).append("\n");
        sb.append("Maximaler Farbwert: ").append(getMaximalerFarbwert()).append("\n");
        sb.append("Mittlerer Farbwert: ").append(getMittlererFarbwert()).append("\n");
        sb.append("Entropie: ").append(getEntropie()).append("\n");
        sb.append("Uniformität: ").append(getUniformitaet()).append("\n");
        sb.append("Unähnlichkeit: ").append(getUnaehnlichkeit()).append("\n");
        sb.append("Inverse Differenz: ").append(getInverseDifferenz()).append("\n");
        sb.append("Inverses Differenz Moment: ").append(getInversesDifferenzMoment()).append("\n");
        sb.append("Kontrast: ").append(getKontrast()).append("\n");

        return sb.toString();
    }

    /**
     * Berechnen aller Werte des Farbkanals.
     */
    private void calculate() {
        final ColorModel colorModel = this.bufferedImage.getColorModel();
        final int width = this.bufferedImage.getWidth();
        final int height = this.bufferedImage.getHeight();

        final double pixelSize = colorModel.getPixelSize();
        final double colorBands = colorModel.getNumComponents();
        this.farbTiefe = (int) Math.pow(2.0, pixelSize / colorBands);
        this.coOccurrenceMatrix = new int[this.farbTiefe][this.farbTiefe];
        this.histogramm = new int[this.farbTiefe];

        this.minimalerFarbwert = 0;
        this.maximalerFarbwert = 0;
        this.mittlererFarbwert = 0;

        // Co-Occurrence-Matrix berechnen
        for (int x = 0; x < (width - 1); x++) {
            for (int y = 0; y < height; y++) {
                final int pixel1 = this.bufferedImage.getRGB(x, y);
                final int pixel2 = this.bufferedImage.getRGB(x + 1, y);

                final int color1 = this.colorChannel.getValue(pixel1);
                final int color2 = this.colorChannel.getValue(pixel2);

                this.coOccurrenceMatrix[color1][color2]++;

                this.minimalerFarbwert = Math.min(this.minimalerFarbwert, color1);
                this.maximalerFarbwert = Math.max(this.maximalerFarbwert, color1);
                this.mittlererFarbwert += color1;
                this.histogramm[color1]++;
            }
        }

        // Letzte Pixelzeile für Histogramm nicht vergessen.
        for (int y = 0; y < height; y++) {
            final int pixel = this.bufferedImage.getRGB(width - 1, y);

            final int color = this.colorChannel.getValue(pixel);

            this.minimalerFarbwert = Math.min(this.minimalerFarbwert, color);
            this.maximalerFarbwert = Math.max(this.maximalerFarbwert, color);
            this.mittlererFarbwert += color;
            this.histogramm[color]++;
        }

        this.mittlererFarbwert /= width * height;

        // Weitere Parameter
        this.entropie = 0.0D;
        this.uniformitaet = 0.0D;
        this.unaehnlichkeit = 0.0D;
        this.inverseDifferenz = 0.0D;
        this.inversesDifferenzMoment = 0.0D;
        this.kontrast = 0.0D;

        for (int x = 0; x < this.farbTiefe; ++x) {
            for (int y = 0; y < this.farbTiefe; ++y) {
                final double c = this.coOccurrenceMatrix[x][y];
                final double d = x - y;

                if (c != 0.0) {
                    this.entropie += c * Math.log(c);
                }

                this.uniformitaet += c * c;
                this.unaehnlichkeit += c * Math.abs(d);
                this.inverseDifferenz += c / (1.0D + Math.abs(d));
                this.inversesDifferenzMoment += c / (1.0D + (d * d));
                this.kontrast += c * d * d;
            }
        }
    }
}
