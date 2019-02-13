/**
 * Created: 02.08.2009
 */
package de.freese.knn.utils.image.info;

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
public class ImageInfo
{
    /**
     *
     */
    private boolean calculated = false;

    /**
     *
     */
    private final List<ImageColorChannelInfo> channelInfos = new ArrayList<>();

    /**
     *
     */
    private double[] outputVector = null;

    /**
     * Erstellt ein neues {@link ImageInfo} Object.
     * 
     * @param fileName String
     * @throws Exception Falls was schief geht.
     */
    public ImageInfo(final String fileName) throws Exception
    {
        super();

        // URL url = ClassLoader.getSystemClassLoader().getResource(fileName);
        URL url = ClassLoader.getSystemResource(fileName);
        BufferedImage source = ImageIO.read(url);

        // In RGB umwandeln
        int w = source.getWidth();
        int h = source.getHeight();
        BufferedImage target = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = target.createGraphics();
        g.drawRenderedImage(source, null);
        g.dispose();

        // target = ImageData.scaleImage(target, 500, 500);
        // target = ImageData.createGrayImage(target);

        // ImageIO.write(target, "gif", new File("Thomas.gif"));
        // JFrame frame = new JFrame();
        // frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        // frame.add(new JLabel(new ImageIcon(target)));
        // frame.setVisible(true);

        this.channelInfos.add(new ImageColorChannelInfo(target, ColorChannel.ALPHA));
        this.channelInfos.add(new ImageColorChannelInfo(target, ColorChannel.RED));
        this.channelInfos.add(new ImageColorChannelInfo(target, ColorChannel.GREEN));
        this.channelInfos.add(new ImageColorChannelInfo(target, ColorChannel.BLUE));
    }

    /**
     * Berechnen aller Werte der Farbkanäle.
     */
    public void calculate()
    {
        if (this.calculated)
        {
            return;
        }

        synchronized (this)
        {
            if (this.calculated)
            {
                return;
            }

            for (ImageColorChannelInfo channelInfo : this.channelInfos)
            {
                channelInfo.calculate();
            }

            this.calculated = true;
        }
    }

    /**
     * @return {@link BufferedImage}
     */
    public BufferedImage createCoOccurenceMatrixImage()
    {
        BufferedImage bufferedImage = new BufferedImage(510, 510, BufferedImage.TYPE_INT_RGB);

        for (ImageColorChannelInfo channelInfo : this.channelInfos)
        {
            ColorChannel colorChannel = channelInfo.getColorChannel();

            int xOffset = 0;
            int yOffset = 0;

            if (ColorChannel.RED.equals(colorChannel))
            {
                xOffset = 255;
            }
            else if (ColorChannel.GREEN.equals(colorChannel))
            {
                xOffset = 0;
                yOffset = 255;
            }
            else if (ColorChannel.BLUE.equals(colorChannel))
            {
                xOffset = 255;
                yOffset = 255;
            }

            for (int x = xOffset; x < (255 + xOffset); x++)
            {
                for (int y = yOffset; y < (255 + yOffset); y++)
                {
                    int value = channelInfo.getCoOccurenceMatrix()[x - xOffset][y - yOffset];

                    if (value > 0)
                    {
                        bufferedImage.setRGB(x, y, colorChannel.getColor().getRGB());
                    }
                }
            }
        }

        return bufferedImage;
    }

    /**
     * @return {@link List}
     */
    public List<ImageColorChannelInfo> getChannelInfos()
    {
        return Collections.unmodifiableList(this.channelInfos);
    }

    /**
     * Liefert die gesammelten Daten aller Farbkanäle.<br>
     * 1. Minimaler Farbwert<br>
     * 2. Maximaler Farbwert<br>
     * 3. Mittlerer Farbwert<br>
     * 4. Entropie<br>
     * 5. Uniformitaet<br>
     * 6. Unaehnlichkeit<br>
     * 7. Inverse Differenz<br>
     * 8. Inverses Differenzmoment<br>
     * 9. Kontrast<br>
     * 
     * @return double[]
     */
    public double[] getInfoVector()
    {
        if (this.outputVector == null)
        {
            this.outputVector = new double[4 * 9];

            calculate();

            int i = 0;

            // Co-Occurence-Matrix direkt verwenden
            // ImageColorChannelInfo channelInfo = this.channelInfos.get(1);
            // int[][] matrix = channelInfo.getCoOccurenceMatrix();
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

            for (ImageColorChannelInfo channelInfo : this.channelInfos)
            {
                this.outputVector[i++] = channelInfo.getMinimalerFarbwert();
                this.outputVector[i++] = channelInfo.getMaximalerFarbwert();
                this.outputVector[i++] = channelInfo.getMittlererFarbwert();
                this.outputVector[i++] = channelInfo.getEntrophie();
                this.outputVector[i++] = channelInfo.getUniformitaet();
                this.outputVector[i++] = channelInfo.getUnaehnlichkeit();
                this.outputVector[i++] = channelInfo.getInverseDifferenz();
                this.outputVector[i++] = channelInfo.getInversesDifferenzMoment();
                this.outputVector[i++] = channelInfo.getKontrast();
            }

            // Normalisieren, da grosse Werte dabei sind (Energie)
            // double max = Double.MIN_VALUE;
            // double min = Double.MAX_VALUE;
            //
            // for (double value : this.outputVector)
            // {
            // if (value <= 255)
            // {
            // continue;
            // }
            //
            // max = Math.max(max, value);
            // min = Math.min(min, value);
            // }
            //
            // double maxNorm = 500;
            // double minNorm = 0;
            //
            // for (int j = 0; j < this.outputVector.length; j++)
            // {
            // if (this.outputVector[j] <= 255)
            // {
            // continue;
            // }
            //
            // this.outputVector[j] =
            // ExtMath.normalize(this.outputVector[j], max, min, maxNorm, minNorm);
            // }
        }

        return this.outputVector;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("ImageInfo...\n");

        for (ImageColorChannelInfo channelInfo : this.channelInfos)
        {
            sb.append(channelInfo.toString());
            sb.append("\n");
        }

        return sb.toString();
    }
}
