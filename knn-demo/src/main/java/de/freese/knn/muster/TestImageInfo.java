/**
 * Created: 12.06.2011
 */

package de.freese.knn.muster;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

import de.freese.base.core.image.info.ImageInfo;

/**
 * TestKlasse für {@link ImageInfo}.
 * 
 * @author Thomas Freese
 */
public class TestImageInfo
{
	/**
	 * @param args String[]
	 * @throws Exception falls was schief geht.
	 */
	public static void main(final String[] args) throws Exception
	{
		String fileName = null;
		// fileName = "Einbahn.gif";
		// fileName = "Stop.gif";
		// fileName = "Seaside.jpg";
		fileName = "BigBrother.jpg";
		// fileName = "Sylvester.jpg";
		// fileName = "winnt.bmp";
		ImageInfo imageInfo = new ImageInfo(fileName);
		imageInfo.calculate();
		System.out.println(imageInfo.toString());

		imageInfo.getInfoVector();

		// imageInfo = new ImageInfo("Einbahn.gif");
		// imageInfo.calculate();
		// System.out.println(imageInfo.toString());

		JFrame frame = new JFrame(fileName);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.add(new JLabel(new ImageIcon(imageInfo.createCoOccurenceMatrixImage())));
		frame.setSize(550, 550);
		frame.setVisible(true);
	}
}
