import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.text.MessageFormat;

import javax.swing.JEditorPane;

/**
 * CodePrinter prints the content of the codebox without losing the html format.
 *
 * @author Dominik Jahnke
 * @version 1.0
 */

public class CodePrinter {

    static MessageFormat head;
    static MessageFormat bottom;

    /**
     * @param pane the JEditorPane you want to print
     * @param title title of the document
     * @param printHeader
     */
    public void print(JEditorPane pane, String title, boolean printBottom) {
	CharSequence cs = "| AssemblerSim";
	if (title.contains(cs)) {
	    title = title.substring(0, title.length() - 13);
	} else {
	    title = "";
	}
	if (printBottom) {
	    head = new MessageFormat(" ");
	    bottom = new MessageFormat(title + " Seite: {0}");
	} else {
	    head = null;
	    bottom = null;
	}
	PrinterJob pj = PrinterJob.getPrinterJob();
	pj.setJobName(title);
	PageFormat pageFormat = pj.defaultPage();
	Paper paper = pageFormat.getPaper();
	pageFormat.setOrientation(PageFormat.PORTRAIT);
	paper.setSize(8.3 * 72, 11.8 * 72);
	paper.setImageableArea(1.0 * 72, 2.0 * 72, 7.0 * 72, 11.0 * 72);
	pageFormat.setPaper(paper);
	pj.defaultPage(pageFormat);
	if (pj.printDialog()) {
	    pane.repaint();
	    pj.setPrintable(pane.getPrintable(head, bottom), pageFormat);
	    try {
		pj.print();
	    } catch (PrinterException e) {
		e.printStackTrace();
	    }
	}
    }
}