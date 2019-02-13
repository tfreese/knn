package de.freese.knn.utils.image;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.ConvolveOp;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.Kernel;
import java.awt.image.PixelGrabber;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.GrayFilter;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 * Nützliches für die Bildverarbeitung.
 *
 * @author Thomas Freese
 */
public final class ImageUtils
{
    /**
     * Leeres Icon Rechteck ohne Pixel, - Größe 16 x 16 Pixel ist Standard.
     *
     * @author Thomas Freese
     */
    private static class EmptyIcon extends ImageIcon
    {
        /**
         *
         */
        private static final long serialVersionUID = 102999713634663152L;

        /**
         *
         */
        private final int height;

        /**
         *
         */
        private final int width;

        /**
         * Creates a new {@link EmptyIcon} object.
         */
        public EmptyIcon()
        {
            this(16, 16);
        }

        /**
         * Creates a new {@link EmptyIcon} object.
         *
         * @param width int
         * @param height int
         */
        public EmptyIcon(final int width, final int height)
        {
            super();

            this.width = width;
            this.height = height;

            setImage(new ImageIcon(new byte[]
            {
                    0, 0
            }).getImage());
        }

        /**
         * @see javax.swing.ImageIcon#getIconHeight()
         */
        @Override
        public int getIconHeight()
        {
            return this.height;
        }

        /**
         * @see javax.swing.ImageIcon#getIconWidth()
         */
        @Override
        public int getIconWidth()
        {
            return this.width;
        }

        /**
         * @see javax.swing.ImageIcon#paintIcon(java.awt.Component, java.awt.Graphics, int, int)
         */
        @Override
        public synchronized void paintIcon(final Component c, final Graphics g, final int x, final int y)
        {
            // NOOP
        }
    }

    /**
     * Icon, wenn kein Icon da ist Rechteck mit Kreuz drin (Rot) - Größe 16 x 16 Pixel.
     *
     * @author Thomas Freese
     */
    private static class MissingIcon extends ImageIcon
    {
        /**
         *
         */
        private static final long serialVersionUID = -3986977626709987448L;

        /**
         * Creates a new {@link MissingIcon} object.
         */
        public MissingIcon()
        {
            super();

            setImage(new ImageIcon(new byte[]
            {
                    0, 0
            }).getImage());
        }

        /**
         * @see javax.swing.ImageIcon#getIconHeight()
         */
        @Override
        public int getIconHeight()
        {
            return 16;
        }

        /**
         * @see javax.swing.ImageIcon#getIconWidth()
         */
        @Override
        public int getIconWidth()
        {
            return 16;
        }

        /**
         * @see javax.swing.ImageIcon#paintIcon(java.awt.Component, java.awt.Graphics, int, int)
         */
        @Override
        public synchronized void paintIcon(final Component c, final Graphics g, final int x, final int y)
        {
            Graphics2D g2d = (Graphics2D) g.create();

            g2d.addRenderingHints(ImageUtils.RENDERING_HINTS);

            g.setColor(Color.RED);
            g.translate(x, y);
            g.drawLine(2, 2, 13, 13);
            g.drawLine(13, 2, 2, 13);
            g.drawRect(2, 2, 11, 11);
            g.translate(-x, -y);
        }
    }

    /**
     * Icon für ein Pfeil Zeichen.
     *
     * @author Thomas Freese
     */
    private static class Triangle extends ImageIcon
    {
        /**
         *
         */
        private static final long serialVersionUID = 6491045895051309036L;

        /**
         *
         */
        private final int direction;

        /**
         *
         */
        private final Color foreground;

        /**
         *
         */
        private final int height;

        /**
         *
         */
        private final int width;

        /**
         * Creates a new {@link Triangle} object.<br>
         * Defaults: Widht = 16, Height = 16, ForeGround = Black
         *
         * @param direction int, [SwingConstants.NORTH, SwingConstants.SOUTH, SwingConstants.EAST, SwingConstants.WEST]
         */
        public Triangle(final int direction)
        {
            this(16, 16, direction, Color.BLACK);
        }

        /**
         * Creates a new {@link Triangle} object.
         *
         * @param width int
         * @param height int
         * @param direction int, [SwingConstants.NORTH, SwingConstants.SOUTH, SwingConstants.EAST, SwingConstants.WEST]
         * @param foreground {@link Color}
         * @throws IllegalArgumentException Falls Direction ungültig
         */
        public Triangle(final int width, final int height, final int direction, final Color foreground)
        {
            super();

            this.width = width;
            this.height = height;
            this.direction = direction;
            this.foreground = foreground;

            if ((this.direction != SwingConstants.NORTH) && (this.direction != SwingConstants.SOUTH) && (this.direction != SwingConstants.EAST)
                    && (this.direction != SwingConstants.WEST))
            {
                throw new IllegalArgumentException("Only SwingConstants.NORTH, SOUTH, EAST, WEST supported !");
            }

            setImage(new ImageIcon(new byte[]
            {
                    0, 0
            }).getImage());
        }

        /**
         * @see javax.swing.ImageIcon#getIconHeight()
         */
        @Override
        public int getIconHeight()
        {
            return this.height;
        }

        /**
         * @see javax.swing.ImageIcon#getIconWidth()
         */
        @Override
        public int getIconWidth()
        {
            return this.width;
        }

        /**
         * @see javax.swing.ImageIcon#paintIcon(java.awt.Component, java.awt.Graphics, int, int)
         */
        @Override
        public synchronized void paintIcon(final Component c, final Graphics g, final int x, final int y)
        {
            Graphics2D g2d = (Graphics2D) g.create();

            g2d.addRenderingHints(ImageUtils.RENDERING_HINTS);
            g2d.setColor(this.foreground);
            int centerX = this.width / 2;
            int centerY = this.height / 2;
            int[] xPoints;
            int[] yPoints;

            if (this.direction == SwingConstants.NORTH)
            {
                xPoints = new int[]
                {
                        x, x + centerX, x + (centerX * 2)
                };
                yPoints = new int[]
                {
                        y + (centerY * 2), y, y + (centerY * 2)
                };
            }
            else if (this.direction == SwingConstants.SOUTH)
            {
                xPoints = new int[]
                {
                        x, x + centerX, x + (centerX * 2)
                };
                yPoints = new int[]
                {
                        y, y + (centerY * 2), y
                };
            }
            else if (this.direction == SwingConstants.WEST)
            {
                xPoints = new int[]
                {
                        x + (centerX * 2), x, x + (centerX * 2)
                };
                yPoints = new int[]
                {
                        y, y + centerY, y + (centerY * 2)
                };
            }
            else if (this.direction == SwingConstants.EAST)
            {
                xPoints = new int[]
                {
                        x, x + (centerX * 2), x, x
                };
                yPoints = new int[]
                {
                        y, y + centerY, y + (centerY * 2)
                };
            }
            else
            {
                throw new IllegalStateException();
            }

            g2d.fillPolygon(xPoints, yPoints, 3);
        }
    }

    /**
     * Alle Icons in hoher Qualität.
     */
    public static final Map<Key, Object> RENDERING_HINTS = new HashMap<>();

    static
    {
        ImageUtils.RENDERING_HINTS.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    /**
     * Konvertiert ein {@link BufferedImage} in ein {@link Image}.
     *
     * @param bufferedImage {@link BufferedImage}
     * @return {@link Image}
     */
    public static Image convertToImage(final BufferedImage bufferedImage)
    {
        return Toolkit.getDefaultToolkit().createImage(bufferedImage.getSource());
    }

    /**
     * Encode the image in a specific format.
     *
     * @param image The image to be encoded.
     * @param format {@link ImageFormat}
     * @return The byte[] that is the encoded image.
     * @throws IOException Falls was schief geht
     */
    public static byte[] encode(final Image image, final ImageFormat format) throws IOException
    {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream())
        {
            ImageUtils.writeImage(image, format, baos);

            baos.flush();

            return baos.toByteArray();
        }
    }

    /**
     * Erzeugt ein leeres Icon.
     *
     * @return {@link ImageIcon}, Ein 16x16 Pixel Icon ohne Inhalt
     */
    public static ImageIcon getEmptyIcon()
    {
        return new EmptyIcon();
    }

    /**
     * Erzeugt ein leeres Icon der Größe x mal y.
     *
     * @param width int Breite
     * @param height int Höhe
     * @return {@link ImageIcon}, Ein Icon ohne Inhalt
     */
    public static ImageIcon getEmptyIcon(final int width, final int height)
    {
        return new EmptyIcon(width, height);
    }

    /**
     * Erzeugt ein Missing-Icon.
     *
     * @return {@link ImageIcon}, Ein 16x16 Pixel Icon (Roter Kasten mit rotem X)
     */
    public static ImageIcon getMissingIcon()
    {
        return new MissingIcon();
    }

    /**
     * Liefert ein TriangleIcon mit einer Größe von 16x16 Pixel und schwarzem Vordergrund.
     *
     * @param direction int, [SwingConstants.NORTH, SwingConstants.SOUTH, SwingConstants.EAST, SwingConstants.WEST]
     * @return {@link ImageIcon}
     */
    public static ImageIcon getTriangleIcon(final int direction)
    {
        return new Triangle(direction);
    }

    /**
     * Liefert ein TriangleIcon.
     *
     * @param width int
     * @param height int
     * @param direction int, [SwingConstants.NORTH, SwingConstants.SOUTH, SwingConstants.EAST, SwingConstants.WEST]
     * @param foreground int
     * @return {@link ImageIcon}
     */
    public static ImageIcon getTriangleIcon(final int width, final int height, final int direction, final Color foreground)
    {
        return new Triangle(width, height, direction, foreground);
    }

    /**
     * Liefert true, wenn das {@link Image} transparente Pixel enthält.
     *
     * @param image {@link Image}
     * @return boolean
     */
    public static boolean hasAlpha(final Image image)
    {
        if (image instanceof BufferedImage)
        {
            BufferedImage bimage = (BufferedImage) image;

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
     * Vereint zwei Bilder in einem. dabei wird das zweite über das erste Bild gelegt.
     *
     * @param image {@link Image}
     * @param overlay {@link Image}
     * @return {@link BufferedImage}
     */
    public static BufferedImage merge(final Image image, final Image overlay)
    {
        int w = Math.max(image.getWidth(null), overlay.getWidth(null));
        int h = Math.max(image.getHeight(null), overlay.getHeight(null));

        BufferedImage merged = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        Graphics g = merged.getGraphics();
        g.drawImage(image, 0, 0, null);
        g.drawImage(overlay, 0, 0, null);

        return merged;
    }

    /**
     * Vereint zwei Bilder in einem, dabei wird das zweite über das erste Bild gelegt.
     *
     * @param image {@link ImageIcon}
     * @param overlay {@link ImageIcon}
     * @return {@link BufferedImage}
     */
    public static BufferedImage merge(final ImageIcon image, final ImageIcon overlay)
    {
        return ImageUtils.merge(image.getImage(), overlay.getImage());
    }

    /**
     * Skaliert das Bild auf eine feste Größe.
     *
     * @param src {@link Image}
     * @param scaleX double
     * @param scaleY double
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
     * Skaliert das Image auf die gewählte Größe. <br>
     * Ist entweder die Breite oder die Höhe <code>null</code> so wird der Wert anhand des Seitenverhältnisses des Originals berechnet. <br>
     * Sind beide von <code>null</code> verschieden, wird das Seitenverhältnis ignoriert!
     *
     * @param src {@link Image}
     * @param width {@link Integer} darf <code>null</code> sein.
     * @param height {@link Integer} darf <code>null</code> sein.
     * @return {@link BufferedImage}
     */
    public static BufferedImage scaleImageRelative(final Image src, final Integer width, final Integer height)
    {
        if ((width == null) && (height == null))
        {
            throw new IllegalArgumentException("Either width or height must not be null!");
        }

        double w = width == null ? 1.0D : width.intValue();
        double h = height == null ? 1.0D : height.intValue();

        BufferedImage bufferedImage = ImageUtils.toBufferedImage(src);

        // Seitenverhaeltnis
        double aspectRatio = ((double) bufferedImage.getWidth()) / ((double) bufferedImage.getHeight());

        double scaleX = (width == null ? aspectRatio * h : width.doubleValue()) / bufferedImage.getWidth();
        double scaleY = (height == null ? w / aspectRatio : height.doubleValue()) / bufferedImage.getHeight();

        return ImageUtils.scaleImage(bufferedImage, scaleX, scaleY);
    }

    /**
     * @param image {@link Image}
     * @return {@link BufferedImage}
     */
    public static BufferedImage scaleImageTo16x16(final Image image)
    {
        return ImageUtils.scaleImageAbsolut(image, 16, 16);
    }

    /**
     * Liefert das Schwarzweiss Bild.
     *
     * @param image {@link Image}
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
     * Kopiert ein Icon in eine Image-Kopie.
     *
     * @param icon {@link Icon}
     * @return {@link BufferedImage}
     */
    public static BufferedImage toBufferedImage(final Icon icon)
    {
        if (icon instanceof ImageIcon)
        {
            ImageIcon imageIcon = (ImageIcon) icon;
            Image image = imageIcon.getImage();

            if (image instanceof BufferedImage)
            {
                return (BufferedImage) image;
            }
        }

        BufferedImage returnImage = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = returnImage.getGraphics();
        JLabel dummyLabel = new JLabel();

        icon.paintIcon(dummyLabel, g, 0, 0);
        g.dispose();

        return returnImage;
    }

    /**
     * Konvertiert ein {@link Image} in ein {@link BufferedImage}.
     *
     * @param image {@link Image}
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

    /**
     * Graut das Icon aus.
     *
     * @param icon {@link Icon}
     * @return {@link Icon}
     */
    public static Icon toGrayIcon(final Icon icon)
    {
        BufferedImage bufferedImage = ImageUtils.toBufferedImage(icon);
        Image result = ImageUtils.toGrayImage(bufferedImage);

        return new ImageIcon(result);
    }

    /**
     * Graut das Icon aus.
     *
     * @param imageIcon ImageIcon
     * @param percent int
     * @return {@link ImageIcon}
     */
    public static ImageIcon toGrayIcon(final ImageIcon imageIcon, final int percent)
    {
        Image image = ImageUtils.toGrayImage(imageIcon.getImage(), percent);

        return new ImageIcon(image);
    }

    /**
     * Erzeugt ein Graustufenbild.
     *
     * @param bufferedImage {@link BufferedImage}
     * @return {@link BufferedImage}
     */
    public static BufferedImage toGrayImage(final BufferedImage bufferedImage)
    {
        ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        ColorConvertOp op = new ColorConvertOp(colorSpace, hints);

        return op.filter(bufferedImage, null);
    }

    /**
     * Graut das Bild aus.
     *
     * @param image ImageIcon
     * @param percent int, 0-100%
     * @return {@link Image}
     */
    public static Image toGrayImage(final Image image, final int percent)
    {
        ImageFilter filter = new GrayFilter(true, percent);
        ImageProducer prod = new FilteredImageSource(image.getSource(), filter);

        return Toolkit.getDefaultToolkit().createImage(prod);
    }

    /**
     * Liefert das eschaerfte Bild.
     *
     * @param image {@link Image}
     * @return {@link BufferedImage}
     */
    public static BufferedImage toSharpenImage(final Image image)
    {
        BufferedImage bufferedImage = ImageUtils.toBufferedImage(image);

        float[] matrix = new float[]
        {
                0.0f, -1.0f, 0.0f, -1.0f, 5.0f, -1.0f, 0.0f, -1.0f, 0.0f
        };

        // Kantenglaettung
        // float[] matrix =
        // new float[]
        // {
        // 1.0f / 9.0f,
        // 1.0f / 9.0f,
        // 1.0f / 9.0f,
        // 1.0f / 9.0f,
        // 1.0f / 9.0f,
        // 1.0f / 9.0f,
        // 1.0f / 9.0f,
        // 1.0f / 9.0f,
        // 1.0f / 9.0f
        // };
        Kernel kernel = new Kernel(3, 3, matrix);
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, hints);

        return op.filter(bufferedImage, null);
    }

    /**
     * Encode the image in a specific format and write it to an OutputStream.
     *
     * @param image {@link Image}
     * @param format {@link ImageFormat}
     * @param outputStream {@link OutputStream}
     * @throws IOException Falls was schief geht
     */
    public static void writeImage(final Image image, final ImageFormat format, final OutputStream outputStream) throws IOException
    {
        BufferedImage bufferedImage = ImageUtils.toBufferedImage(image);

        ImageIO.write(bufferedImage, format.name(), outputStream);
    }
}
