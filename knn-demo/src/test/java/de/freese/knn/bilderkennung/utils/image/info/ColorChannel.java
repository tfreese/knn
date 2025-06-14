// Created: 02.08.2009
package de.freese.knn.bilderkennung.utils.image.info;

import java.awt.Color;

/**
 * Enums für verschiedene Farbkanäle.
 *
 * @author Thomas Freese
 */
public enum ColorChannel {
    ALPHA(24, Color.GRAY),

    BLUE(0, Color.BLUE),

    GREEN(8, Color.GREEN),

    RED(16, Color.RED);

    private final int bitOperator;

    private final Color color;

    ColorChannel(final int bitOperator, final Color color) {
        this.bitOperator = bitOperator;
        this.color = color;
    }

    /**
     * Bit-Operator für das Auslesen des konkreten Farbwertes:<br>
     * int pixel = BufferedImage.getRGB(x, y);<br>
     * int alpha = (pixel >> 24) & 0xFF;<br>
     * int red = (pixel1 >> 16) & 0xFF;<br>
     * int green = (pixel >> 8) & 0xFF;<br>
     * int blue = (pixel >> 0) & 0xFF;
     */
    public int getBitOperator() {
        return bitOperator;
    }

    public Color getColor() {
        return color;
    }

    /**
     * Liefert den Farbwert aus dem Pixel.
     */
    public int getValue(final int pixel) {
        return (pixel >> getBitOperator()) & 0xFF;
    }
}
