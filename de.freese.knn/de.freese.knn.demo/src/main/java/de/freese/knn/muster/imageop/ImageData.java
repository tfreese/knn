/**
 * 12.07.2008
 */
package de.freese.knn.muster.imageop;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.Arrays;

import javax.imageio.ImageIO;

import de.freese.base.core.ImageUtils;

/**
 * Enthält die Bildinformationen.
 * 
 * @author Thomas Freese
 */
public class ImageData
{
	// /**
	// *
	// */
	// private BufferedImage grayImage = null;

	/**
	 *
	 */
	private BufferedImage blackWhiteImage = null;

	/**
	 * 
	 */
	private BufferedImage edgeImage = null;

	// /**
	// *
	// */
	// private BufferedImage sharpenImage = null;

	/**
	 * 
	 */
	private double[] pixels = null;

	/**
	 * 
	 */
	private final BufferedImage sourceImage;

	/**
	 * Creates a new {@link ImageData} object.
	 * 
	 * @param fileName String
	 * @throws Exception Falls was schief geht.
	 */
	public ImageData(final String fileName) throws Exception
	{
		super();

		// String[] formats = ImageIO.getWriterFormatNames();
		URL url = ClassLoader.getSystemResource(fileName);
		BufferedImage bufferedImage = ImageIO.read(url);

		bufferedImage = ImageUtils.scaleImage(bufferedImage, 100, 100);

		// GIF ist das einzige Format was funktioniert
		String[] splits = fileName.split("[.]");
		File file = new File(splits[0] + ".gif");
		ImageIO.write(bufferedImage, "gif", file);

		bufferedImage = ImageIO.read(file);

		this.sourceImage = bufferedImage;

		// int width = this.sourceImage.getWidth();
		// int height = this.sourceImage.getHeight();
		//
		// int[] pixels = new int[width * height];
		//
		// for (int y = 0; y < height; y++)
		// {
		// for (int x = 0; x < width; x++)
		// {
		// int pixel = this.sourceImage.getRGB(x, y);
		//
		// int alpha = (pixel >> 24) & 0xff;
		// int red = (pixel >> 16) & 0xff;
		// int green = (pixel >> 8) & 0xff;
		// int blue = (pixel >> 0) & 0xff;
		//
		// if ((red > 0) && (green > 0) && (blue > 0))
		// {
		// System.out.println(
		// "[" + (x + 1) + "." + (y + 1) + "]. " + pixel + "= " + alpha + ", " + red + ", " + green
		// + ", "
		// + blue
		// );
		//
		// int rgb =
		// ((alpha & 0xFF) << 24) | ((red & 0xFF) << 16) | ((green & 0xFF) << 8) | ((blue & 0xFF) <<
		// 0);
		// }
		// }
		// }
		//
		// PixelGrabber grabber = new PixelGrabber(this.sourceImage, 0, 0, width, height, pixels, 0,
		// width);
		// grabber.grabPixels();
		//
		// for (int i = 0; i < pixels.length; i++)
		// {
		// int pixel = pixels[i];
		//
		// int alpha = (pixel >> 24) & 0xff;
		// int red = (pixel >> 16) & 0xff;
		// int green = (pixel >> 8) & 0xff;
		// int blue = (pixel >> 0) & 0xff;
		//
		// if ((red > 0) && (green > 0) && (blue > 0))
		// {
		// System.out.println(
		// (i + 1) + ". " + pixel + "= " + alpha + ", " + red + ", " + green + ", " + blue + ": "
		// + (pixel & 0xff)
		// );
		//
		// int rgb = ((alpha & 0xFF) << 24) | ((red & 0xFF) << 16) | ((green & 0xFF) << 8) | ((blue
		// & 0xFF) << 0);
		// }
		// }
	}

	/**
	 * Liefert das Schwarzweiss Bild.
	 * 
	 * @return {@link BufferedImage}
	 */
	public BufferedImage getBlackWhiteImage()
	{
		if (this.blackWhiteImage == null)
		{
			this.blackWhiteImage = ImageUtils.toBlackWhiteImage(getEdgeImage());
		}

		return this.blackWhiteImage;
	}

	/**
	 * Liefert das Kantenbild.
	 * 
	 * @return {@link BufferedImage}
	 */
	public BufferedImage getEdgeImage()
	{
		if (this.edgeImage == null)
		{
			this.edgeImage = ImageUtils.toEdgeImage(getSourceImage());
		}

		return this.edgeImage;
	}

	/**
	 * Liefert die Pixel des Kantenbildes als double[].<br>
	 * Schwarze Pixel haben den Wert -1, Weiße die +1.
	 * 
	 * @return double[]
	 */
	public double[] getPixels()
	{
		if (this.pixels == null)
		{
			BufferedImage image = getBlackWhiteImage();

			int width = image.getWidth();
			int height = image.getHeight();

			this.pixels = new double[width * height];
			Arrays.fill(this.pixels, -1.0D);

			int rgbWhite = Color.WHITE.getRGB();

			for (int y = 0; y < height; y++)
			{
				for (int x = 0; x < width; x++)
				{
					int pixel = image.getRGB(x, y);

					if (pixel == rgbWhite)
					{
						this.pixels[x + (width * y)] = 1.0D;
					}
				}
			}
		}

		return this.pixels;
	}

	// /**
	// * @return
	// */
	// private static ColorModel generateColorModel()
	// {
	// // Generate 16-color model
	// byte[] r = new byte[16];
	// byte[] g = new byte[16];
	// byte[] b = new byte[16];
	//
	// r[0] = 0;
	// g[0] = 0;
	// b[0] = 0;
	// r[1] = 0;
	// g[1] = 0;
	// b[1] = (byte) 192;
	// r[2] = 0;
	// g[2] = 0;
	// b[2] = (byte) 255;
	// r[3] = 0;
	// g[3] = (byte) 192;
	// b[3] = 0;
	// r[4] = 0;
	// g[4] = (byte) 255;
	// b[4] = 0;
	// r[5] = 0;
	// g[5] = (byte) 192;
	// b[5] = (byte) 192;
	// r[6] = 0;
	// g[6] = (byte) 255;
	// b[6] = (byte) 255;
	// r[7] = (byte) 192;
	// g[7] = 0;
	// b[7] = 0;
	// r[8] = (byte) 255;
	// g[8] = 0;
	// b[8] = 0;
	// r[9] = (byte) 192;
	// g[9] = 0;
	// b[9] = (byte) 192;
	// r[10] = (byte) 255;
	// g[10] = 0;
	// b[10] = (byte) 255;
	// r[11] = (byte) 192;
	// g[11] = (byte) 192;
	// b[11] = 0;
	// r[12] = (byte) 255;
	// g[12] = (byte) 255;
	// b[12] = 0;
	// r[13] = (byte) 80;
	// g[13] = (byte) 80;
	// b[13] = (byte) 80;
	// r[14] = (byte) 192;
	// g[14] = (byte) 192;
	// b[14] = (byte) 192;
	// r[15] = (byte) 255;
	// g[15] = (byte) 255;
	// b[15] = (byte) 255;
	//
	// return new IndexColorModel(4, 16, r, g, b);
	// }

	/**
	 * Liefert das OriginalBild.
	 * 
	 * @return {@link BufferedImage}
	 */
	public BufferedImage getSourceImage()
	{
		return this.sourceImage;
	}
}
