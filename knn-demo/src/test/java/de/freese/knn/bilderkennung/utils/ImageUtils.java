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
public final class ImageUtils
{
    /**
     * Alle Icons in hoher Qualität.
     */
    public static final Map<Key, Object> RENDERING_HINTS = new HashMap<>();

    static
    {
        ImageUtils.RENDERING_HINTS.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    /**
     * Liefert true, wenn das {@link Image} transparente Pixel enthält.
     *
     * @param image {@link Image}
     *
     * @return boolean
     */
    public static boolean hasAlpha(final Image image)
    {
        if (image instanceof BufferedImage bimage)
        {
            return bimage.getColorModel().hasAlpha();
        }

        PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);

        try
        {
            pg.grabPixels();
        }
        catch (InterruptedException ex)
        {
            // Ignore
        }

        ColorModel cm = pg.getColorModel();

        return cm.hasAlpha();
    }

    /**
     * Skaliert das Bild auf eine feste Größe.
     *
     * @param src {@link Image}
     * @param scaleX double
     * @param scaleY double
     *
     * @return {@link BufferedImage}
     */
    public static BufferedImage scaleImage(final Image src, final double scaleX, final double scaleY)
    {
        BufferedImage bufferedImage = ImageUtils.toBufferedImage(src);
        AffineTransform tx = new AffineTransform();
        tx.scale(scaleX, scaleY);

        // tx.shear(shiftx, shifty);
        // tx.translate(x, y);
        // tx.rotate(radians, origin.getWidth()/2, origin.getHeight()/2);

        RenderingHints hints = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        // hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        AffineTransformOp op = new AffineTransformOp(tx, hints);

        return op.filter(bufferedImage, null);
    }

    /**
     * Skaliert das Bild auf eine feste Größe.
     *
     * @param src {@link BufferedImage}
     * @param width int
     * @param height int
     *
     * @return {@link BufferedImage}
     */
    public static BufferedImage scaleImageAbsolut(final Image src, final int width, final int height)
    {
        BufferedImage bufferedImage = ImageUtils.toBufferedImage(src);

        double scaleX = ((double) width) / bufferedImage.getWidth();
        double scaleY = ((double) height) / bufferedImage.getHeight();

        return ImageUtils.scaleImage(bufferedImage, scaleX, scaleY);
    }

    /**
     * Liefert das Schwarzweiss Bild.
     *
     * @param image {@link Image}
     *
     * @return {@link BufferedImage}
     */
    public static BufferedImage toBlackWhiteImage(final Image image)
    {
        BufferedImage bufferedImage = ImageUtils.toBufferedImage(image);

        RenderingHints hints = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        BufferedImageOp op = null;

        // int width = getSourceImage().getWidth();
        // int height = getSourceImage().getHeight();
        // ColorModel colorModel = ColorModel.getRGBdefault();
        //
        // BufferedImage blackWhiteImage = new BufferedImage(width, height,
        // BufferedImage.TYPE_INT_RGB);
        // byte[] data = new byte[256];
        // Arrays.fill(data, (byte) 255);
        // data[0] = (byte)0;
        // data[254] = (byte)255;
        // data[253] = (byte)255;
        // data[252] = (byte)255;
        // data[251] = (byte)255;
        // data[250] = (byte)255;
        //
        // IndexColorModel colorModel = new IndexColorModel(2, 256, data, data, data);
        //
        // blackWhiteImage = new BufferedImage(width, height,
        // BufferedImage.TYPE_BYTE_INDEXED, colorModel);
        //
        // Graphics2D g2d = this.blackWhiteImage.createGraphics();
        // g2d.drawRenderedImage(getEdgeImage(), null);
        // g2d.dispose();
        //
        //
        // short[] red = new short[256];
        // short[] green = new short[256];
        // short[] blue = new short[256];
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
        // short[][] data = new short[][] {
        // red, green, blue
        // };
        //
        // LookupTable lookupTable = new ShortLookupTable(0, data);
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
     *
     * @param image {@link Image}
     *
     * @return {@link BufferedImage}
     */
    public static BufferedImage toBufferedImage(final Image image)
    {
        if (image instanceof BufferedImage)
        {
            return (BufferedImage) image;
        }

        // Image m_Image = new ImageIcon(image).getImage();

        boolean hasAlpha = ImageUtils.hasAlpha(image);

        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

        try
        {
            int transparency = Transparency.OPAQUE;

            if (hasAlpha)
            {
                transparency = Transparency.BITMASK;
            }

            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();

            bimage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null), transparency);
        }
        catch (HeadlessException ex)
        {
            // Keine GUI vorhanden
        }

        if (bimage == null)
        {
            int type = BufferedImage.TYPE_INT_RGB;

            if (hasAlpha)
            {
                type = BufferedImage.TYPE_INT_ARGB;
            }

            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        }

        Graphics g = bimage.createGraphics();

        g.drawImage(image, 0, 0, null);
        g.dispose();

        return bimage;
    }

    /**
     * Liefert das Kanten Bild.
     *
     * @param image {@link Image}
     *
     * @return {@link BufferedImage}
     */
    public static BufferedImage toEdgeImage(final Image image)
    {
        BufferedImage bufferedImage = ImageUtils.toBufferedImage(image);

        // Sobel Operator, horizontal & vertikal
        float[] matrix = new float[]
                {
                        0.0f, -1.0f, 0.0f, -1.0f, 4.0f, -1.0f, 0.0f, -1.0f, 0.0f
                };

        // // Sobel Operator, horizontal
        // float[] matrix = new float[]
        // {
        // 1.0f, 2.0f, 1.0f, 0.0f, 0.0f, 0.0f, -1.0f, -2.0f, -1.0f
        // };
        // // Sobel Operator, vertikal
        // float[] matrix = new float[]
        // {
        // 1.0f, 0.0f, -1.0f, 2.0f, 0.0f, -2.0f, 1.0f, 0.0f, -1.0f
        // };
        // // Sobel Operator, diagonal
        // float[] matrix =
        // {
        // -1.0f, -2.0f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 2.0f, 1.0f
        // };

        Kernel kernel = new Kernel(3, 3, matrix);
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, hints);

        return op.filter(bufferedImage, null);
    }
}
