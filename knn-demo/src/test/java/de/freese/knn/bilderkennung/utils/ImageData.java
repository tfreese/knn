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
        final URL url = ClassLoader.getSystemResource(fileName);
        final BufferedImage bufferedImage = ImageIO.read(url);

        sourceImage = ImageUtils.scaleImageAbsolut(bufferedImage, 100, 100);
    }

    /**
     * Liefert das Schwarzweissbild.
     */
    public BufferedImage getBlackWhiteImage() {
        if (blackWhiteImage == null) {
            blackWhiteImage = ImageUtils.toBlackWhiteImage(getEdgeImage());
        }

        return blackWhiteImage;
    }

    /**
     * Liefert das Kantenbild.
     */
    public BufferedImage getEdgeImage() {
        if (edgeImage == null) {
            edgeImage = ImageUtils.toEdgeImage(getSourceImage());
        }

        return edgeImage;
    }

    /**
     * Liefert die Pixel des Kantenbildes als double[].<br>
     * Schwarze Pixel haben den Wert -1, Weiße die +1.
     */
    public double[] getPixels() {
        if (pixels == null) {
            final BufferedImage image = getBlackWhiteImage();

            final int width = image.getWidth();
            final int height = image.getHeight();

            pixels = new double[width * height];
            Arrays.fill(pixels, -1.0D);

            final int rgbWhite = Color.WHITE.getRGB();

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    final int pixel = image.getRGB(x, y);

                    if (pixel == rgbWhite) {
                        pixels[x + (width * y)] = 1.0D;
                    }
                }
            }
        }

        return pixels;
    }

    /**
     * Liefert das OriginalBild.
     */
    public BufferedImage getSourceImage() {
        return sourceImage;
    }
}
