// 12.07.2008
package de.freese.knn.bilderkennung.utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Arrays;

import javax.imageio.ImageIO;

/**
 * Enthält die Bildinformationen.
 *
 * @author Thomas Freese
 */
public class ImageData {
    private final BufferedImage sourceImage;
    private BufferedImage blackWhiteImage;
    private BufferedImage edgeImage;
    private double[] pixels;

    public ImageData(final String fileName) throws Exception {
        super();

        // String[] formats = ImageIO.getWriterFormatNames();
        URL url = ClassLoader.getSystemResource(fileName);
        BufferedImage bufferedImage = ImageIO.read(url);

        bufferedImage = ImageUtils.scaleImageAbsolut(bufferedImage, 100, 100);

        this.sourceImage = bufferedImage;
    }

    /**
     * Liefert das Schwarzweiss Bild.
     */
    public BufferedImage getBlackWhiteImage() {
        if (this.blackWhiteImage == null) {
            this.blackWhiteImage = ImageUtils.toBlackWhiteImage(getEdgeImage());
        }

        return this.blackWhiteImage;
    }

    /**
     * Liefert das Kantenbild.
     */
    public BufferedImage getEdgeImage() {
        if (this.edgeImage == null) {
            this.edgeImage = ImageUtils.toEdgeImage(getSourceImage());
        }

        return this.edgeImage;
    }

    /**
     * Liefert die Pixel des Kantenbildes als double[].<br>
     * Schwarze Pixel haben den Wert -1, Weiße die +1.
     */
    public double[] getPixels() {
        if (this.pixels == null) {
            BufferedImage image = getBlackWhiteImage();

            int width = image.getWidth();
            int height = image.getHeight();

            this.pixels = new double[width * height];
            Arrays.fill(this.pixels, -1.0D);

            int rgbWhite = Color.WHITE.getRGB();

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int pixel = image.getRGB(x, y);

                    if (pixel == rgbWhite) {
                        this.pixels[x + (width * y)] = 1.0D;
                    }
                }
            }
        }

        return this.pixels;
    }

    /**
     * Liefert das OriginalBild.
     */
    public BufferedImage getSourceImage() {
        return this.sourceImage;
    }
}
