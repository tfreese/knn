/**
 * Created: 02.08.2009
 */
package de.freese.knn.utils.image.info;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

/**
 * Klasse für Informationen eines Bild-Farbkanals.
 * 
 * @author Thomas Freese
 */
public class ImageColorChannelInfo
{
	/**
	 *
	 */
	private final BufferedImage bufferedImage;

	/**
	 *
	 */
	private final ColorChannel colorChannel;

	/**
	 *
	 */
	private int[][] coOccurenceMatrix = null;

	/**
	 *
	 */
	private double uniformitaet = -1.0D;

	/**
	 *
	 */
	private double unaehnlichkeit = -1.0D;

	/**
	 *
	 */
	private double entrophie = -1.0D;

	/**
	 *
	 */
	private int farbTiefe = -1;

	/**
	 *
	 */
	private int[] histogramm = null;

	/**
	 *
	 */
	private double inverseDifferenz = -1.0D;

	/**
	 *
	 */
	private double inversesDifferenzMoment = -1.0D;

	/**
	 *
	 */
	private double kontrast = -1.0D;

	/**
	 *
	 */
	private int maximalerFarbwert = -1;

	/**
	 *
	 */
	private int minimalerFarbwert = -1;

	/**
	 *
	 */
	private int mittlererFarbwert = -1;

	/**
	 * Erstellt ein neues {@link ImageColorChannelInfo} Object.
	 * 
	 * @param bufferedImage {@link BufferedImage}
	 * @param colorChannel {@link ColorChannel}
	 */
	public ImageColorChannelInfo(final BufferedImage bufferedImage, final ColorChannel colorChannel)
	{
		super();

		this.bufferedImage = bufferedImage;
		this.colorChannel = colorChannel;
	}

	/**
	 * Berechnen aller Werte des Farbkanals.
	 */
	public void calculate()
	{
		ColorModel colorModel = this.bufferedImage.getColorModel();
		int width = this.bufferedImage.getWidth();
		int height = this.bufferedImage.getHeight();

		double pixelSize = colorModel.getPixelSize();
		double colorBands = colorModel.getNumComponents();
		this.farbTiefe = (int) Math.pow(2.0, pixelSize / colorBands);
		this.coOccurenceMatrix = new int[this.farbTiefe][this.farbTiefe];
		this.histogramm = new int[this.farbTiefe];

		this.minimalerFarbwert = 0;
		this.maximalerFarbwert = 0;
		this.mittlererFarbwert = 0;

		// Co-Occurence-Matrix berechnen
		for (int x = 0; x < (width - 1); x++)
		{
			for (int y = 0; y < height; y++)
			{
				int pixel1 = this.bufferedImage.getRGB(x, y);
				int pixel2 = this.bufferedImage.getRGB(x + 1, y);

				int color1 = this.colorChannel.getValue(pixel1);
				int color2 = this.colorChannel.getValue(pixel2);

				this.coOccurenceMatrix[color1][color2]++;

				this.minimalerFarbwert = Math.min(this.minimalerFarbwert, color1);
				this.maximalerFarbwert = Math.max(this.maximalerFarbwert, color1);
				this.mittlererFarbwert += color1;
				this.histogramm[color1]++;
			}
		}

		// Letzte Pixelzeile fuer Histogramm nicht vegessen.
		for (int y = 0; y < height; y++)
		{
			int pixel = this.bufferedImage.getRGB(width - 1, y);

			int color = this.colorChannel.getValue(pixel);

			this.minimalerFarbwert = Math.min(this.minimalerFarbwert, color);
			this.maximalerFarbwert = Math.max(this.maximalerFarbwert, color);
			this.mittlererFarbwert += color;
			this.histogramm[color]++;
		}

		this.mittlererFarbwert /= (width * height);

		// Weitere Paramerter
		this.entrophie = 0.0D;
		this.uniformitaet = 0.0D;
		this.unaehnlichkeit = 0.0D;
		this.inverseDifferenz = 0.0D;
		this.inversesDifferenzMoment = 0.0D;
		this.kontrast = 0.0D;

		for (int x = 0; x < this.farbTiefe; ++x)
		{
			for (int y = 0; y < this.farbTiefe; ++y)
			{
				final double c = this.coOccurenceMatrix[x][y];
				final double d = x - y;

				if (c != 0.0)
				{
					this.entrophie += (c * Math.log(c));
				}

				this.uniformitaet += (c * c);
				this.unaehnlichkeit += c * Math.abs(d);
				this.inverseDifferenz += (c / (1.0 + Math.abs(d)));
				this.inversesDifferenzMoment += (c / (1.0 + (d * d)));
				this.kontrast += (c * (d * d));
			}
		}
	}

	/**
	 * @return {@link ColorChannel}
	 */
	public ColorChannel getColorChannel()
	{
		return this.colorChannel;
	}

	/**
	 * Liefert die "Grauwertübergangsmatrix".<br>
	 * Zählt wie oft Farbwerte nebeneinander auftreten.
	 * 
	 * @return int[][]
	 */
	public int[][] getCoOccurenceMatrix()
	{
		return this.coOccurenceMatrix;
	}

	/**
	 * @return double
	 */
	public double getEntrophie()
	{
		return this.entrophie;
	}

	/**
	 * @return int
	 */
	public int getFarbTiefe()
	{
		return this.farbTiefe;
	}

	/**
	 * @return int[]
	 */
	public int[] getHistogramm()
	{
		return this.histogramm;
	}

	/**
	 * Homogenität.
	 * 
	 * @return double
	 */
	public double getInverseDifferenz()
	{
		return this.inverseDifferenz;
	}

	/**
	 * @return double
	 */
	public double getInversesDifferenzMoment()
	{
		return this.inversesDifferenzMoment;
	}

	/**
	 * @return double
	 */
	public double getKontrast()
	{
		return this.kontrast;
	}

	/**
	 * @return int
	 */
	public int getMaximalerFarbwert()
	{
		return this.maximalerFarbwert;
	}

	/**
	 * @return int
	 */
	public int getMinimalerFarbwert()
	{
		return this.minimalerFarbwert;
	}

	/**
	 * @return int
	 */
	public int getMittlererFarbwert()
	{
		return this.mittlererFarbwert;
	}

	/**
	 * @return double
	 */
	public double getUnaehnlichkeit()
	{
		return this.unaehnlichkeit;
	}

	/**
	 * Energie.
	 * 
	 * @return double
	 */
	public double getUniformitaet()
	{
		return this.uniformitaet;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("ChannelInfo: ").append(getColorChannel()).append("\n");
		sb.append("Minimaler Farbwert: ").append(getMinimalerFarbwert()).append("\n");
		sb.append("Maximaler Farbwert: ").append(getMaximalerFarbwert()).append("\n");
		sb.append("Mittlerer Farbwert: ").append(getMittlererFarbwert()).append("\n");
		sb.append("Entropie: ").append(getEntrophie()).append("\n");
		sb.append("Uniformität: ").append(getUniformitaet()).append("\n");
		sb.append("Unähnlichkeit: ").append(getUnaehnlichkeit()).append("\n");
		sb.append("Inverse Differenz: ").append(getInverseDifferenz()).append("\n");
		sb.append("Inverses Differenz Moment: ").append(getInversesDifferenzMoment()).append("\n");
		sb.append("Kontrast: ").append(getKontrast()).append("\n");

		return sb.toString();
	}
}
