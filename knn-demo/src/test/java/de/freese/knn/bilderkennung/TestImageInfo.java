// Created: 12.06.2011
package de.freese.knn.bilderkennung;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

import de.freese.knn.bilderkennung.utils.image.info.ImageInfo;

/**
 * TestKlasse für {@link ImageInfo}.
 *
 * @author Thomas Freese
 */
public final class TestImageInfo {
    public static void main(final String[] args) throws Exception {
        String fileName;
        // fileName = "Ampel.gif";
        // fileName = "Bahnkreuz.gif";
        // fileName = "BigBrother.jpg";
        // fileName = "Einbahn.gif";
        // fileName = "Klippe.gif";
        fileName = "Seaside.jpg";
        // fileName = "Stop.gif";
        // fileName = "Sylvester.jpg";
        // fileName = "winnt.bmp";

        ImageInfo imageInfo = new ImageInfo(fileName);
        System.out.println(imageInfo);

        imageInfo.getInfoVectorReScaled();

        JFrame frame = new JFrame(fileName);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.add(new JLabel(new ImageIcon(imageInfo.createCoOccurrenceMatrixImage())));
        frame.setSize(550, 550);
        frame.setVisible(true);
    }

    private TestImageInfo() {
        super();
    }
}
