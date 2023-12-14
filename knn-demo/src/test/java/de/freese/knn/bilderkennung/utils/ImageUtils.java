package de.freese.knn.bilderkennung.utils;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.PixelGrabber;
import java.util.HashMap;
import java.util.Map;

/**
 * Nützliches für die Bildverarbeitung.
 *
 * @author Thomas Freese
 */
public final class ImageUtils {
    /**
     * Alle Icons in hoher Qualität.
     */
    public static final Map<Key, Object> RENDERING_HINTS = new HashMap<>();

    static {
        ImageUtils.RENDERING_HINTS.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    /**
     * Liefert true, wenn das {@link Image} transparente Pixel enthält.
     */
    public static boolean hasAlpha(final Image image) {
        if (image instanceof BufferedImage bi) {
            return bi.getColorModel().hasAlpha();
        }

        final PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);

        try {
            pg.grabPixels();
        }
        catch (InterruptedException ex) {
            // Ignore
        }

        final ColorModel cm = pg.getColorModel();

        return cm.hasAlpha();
    }

    /**
     * Skaliert das Bild auf eine feste Größe.
     */
    public static BufferedImage scaleImage(final Image src, final double scaleX, final double scaleY) {
        final BufferedImage bufferedImage = ImageUtils.toBufferedImage(src);
        final AffineTransform tx = new AffineTransform();
        tx.scale(scaleX, scaleY);

        // tx.shear(shiftx, shifty);
        // tx.translate(x, y);
        // tx.rotate(radians, origin.getWidth()/2, origin.getHeight()/2);

        final RenderingHints hints = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        // hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        final AffineTransformOp op = new AffineTransformOp(tx, hints);

        return op.filter(bufferedImage, null);
    }

    /**
     * Skaliert das Bild auf eine feste Größe.
     */
    public static BufferedImage scaleImageAbsolut(final Image src, final int width, final int height) {
        final BufferedImage bufferedImage = ImageUtils.toBufferedImage(src);

        final double scaleX = ((double) width) / bufferedImage.getWidth();
        final double scaleY = ((double) height) / bufferedImage.getHeight();

        return ImageUtils.scaleImage(bufferedImage, scaleX, scaleY);
    }

    /**
     * Liefert das Schwarzweiss Bild.
     */
    public static BufferedImage toBlackWhiteImage(final Image image) {
        final BufferedImage bufferedImage = ImageUtils.toBufferedImage(image);

        final RenderingHints hints = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        BufferedImageOp op = null;

        // final int width = getSourceImage().getWidth();
        // final int height = getSourceImage().getHeight();
        // final ColorModel colorModel = ColorModel.getRGBdefault();
        //
        // final BufferedImage blackWhiteImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        // final byte[] data = new byte[256];
        // Arrays.fill(data, (byte) 255);
        // data[0] = (byte)0;
        // data[254] = (byte)255;
        // data[253] = (byte)255;
        // data[252] = (byte)255;
        // data[251] = (byte)255;
        // data[250] = (byte)255;
        //
        // final IndexColorModel colorModel = new IndexColorModel(2, 256, data, data, data);
        //
        // blackWhiteImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_INDEXED, colorModel);
        //
        // final Graphics2D g2d = this.blackWhiteImage.createGraphics();
        // g2d.drawRenderedImage(getEdgeImage(), null);
        // g2d.dispose();
        //
        //
        // final short[] red = new short[256];
        // final short[] green = new short[256];
        // final short[] blue = new short[256];
        //
        // for (int i = 0; i < 255; i++)
        // {
        // red[i]=255;
        // green[i]=255;
        // blue[i]=255;
        // }
        //
        // red[0]=0;
        // green[0]=0;
        // blue[0]=0;
        //
        // final short[][] data = new short[][] {
        // red, green, blue
        // };
        //
        // final LookupTable lookupTable = new ShortLookupTable(0, data);
        // op = new LookupOp(lookupTable, hints);
        //
        //
        // float[] factors = new float[] {
        // 1000f, 1000f, 1000f
        // };
        // float[] offsets = new float[] {
        // 0.0f, 0.0f, 0.0f
        // };
        // BufferedImageOp op = new RescaleOp(factors, offsets, hints);
        op = new BlackWhiteOp(hints, 0);

        return op.filter(bufferedImage, null);
    }

    /**
     * Konvertiert ein {@link Image} in ein {@link BufferedImage}.
     */
    public static BufferedImage toBufferedImage(final Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }

        // final Image mImage = new ImageIcon(image).getImage();

        final boolean hasAlpha = ImageUtils.hasAlpha(image);

        BufferedImage bimage = null;
        final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

        try {
            int transparency = Transparency.OPAQUE;

            if (hasAlpha) {
                transparency = Transparency.BITMASK;
            }

            final GraphicsDevice gs = ge.getDefaultScreenDevice();
            final GraphicsConfiguration gc = gs.getDefaultConfiguration();

            bimage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null), transparency);
        }
        catch (HeadlessException ex) {
            // Keine GUI vorhanden
        }

        if (bimage == null) {
            int type = BufferedImage.TYPE_INT_RGB;

            if (hasAlpha) {
                type = BufferedImage.TYPE_INT_ARGB;
            }

            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        }

        final Graphics g = bimage.createGraphics();

        g.drawImage(image, 0, 0, null);
        g.dispose();

        return bimage;
    }

    /**
     * Liefert das Kanten Bild.
     */
    public static BufferedImage toEdgeImage(final Image image) {
        final BufferedImage bufferedImage = ImageUtils.toBufferedImage(image);

        // Sobel Operator, horizontal & vertikal
        final float[] matrix = new float[]{0.0f, -1.0f, 0.0f, -1.0f, 4.0f, -1.0f, 0.0f, -1.0f, 0.0f};

        // // Sobel Operator, horizontal
        // final float[] matrix = new float[] {1.0f, 2.0f, 1.0f, 0.0f, 0.0f, 0.0f, -1.0f, -2.0f, -1.0f};
        // // Sobel Operator, vertikal
        // final float[] matrix = new float[] {1.0f, 0.0f, -1.0f, 2.0f, 0.0f, -2.0f, 1.0f, 0.0f, -1.0f};
        // // Sobel Operator, diagonal
        // final float[] matrix = {-1.0f, -2.0f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 2.0f, 1.0f};

        final Kernel kernel = new Kernel(3, 3, matrix);
        final RenderingHints hints = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        final ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, hints);

        return op.filter(bufferedImage, null);
    }

    private ImageUtils() {
        super();
    }
}
