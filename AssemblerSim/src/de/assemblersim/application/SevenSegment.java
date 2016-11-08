package de.assemblersim.application;
import java.awt.*;
import javax.swing.JPanel;

/**
 * Sieben-Segment-Anzeige
 * 
 * @ Dominik Jahnke @ 02.03.2013
 * 
 * Wird über die Arrays "anzeige1" und "anzeige2" gesteuert.
 * 
 * Anordnung der Segmente:
 * 
 * 5 6 4 7 3 1 2 0
 */

public class SevenSegment extends JPanel {

	private static final long serialVersionUID = 1L;

	// Größe der Anzeige
	final int SIZE = 3;
	// Koordinaten der einzelnen Segmente
	int[][] x1 = { { 21, 23, 23, 21, 19, 19 }, { 9, 19, 21, 19, 9, 7 },
			{ 7, 9, 9, 7, 5, 5 }, { 21, 23, 23, 21, 19, 19 },
			{ 9, 19, 21, 19, 9, 7 }, { 7, 9, 9, 7, 5, 5 },
			{ 9, 19, 21, 19, 9, 7 } };

	int[][] y1 = { { 17, 19, 29, 31, 29, 19 }, { 29, 29, 31, 33, 33, 31 },
			{ 17, 19, 29, 31, 29, 19 }, { 3, 5, 15, 17, 15, 5 },
			{ 1, 1, 3, 5, 5, 3 }, { 3, 5, 15, 17, 15, 5 },
			{ 15, 15, 17, 19, 19, 17 } };

	int[][] x2 = { { 43, 45, 45, 43, 41, 41 }, { 31, 41, 43, 41, 31, 29 },
			{ 29, 31, 31, 29, 27, 27 }, { 43, 45, 45, 43, 41, 41 },
			{ 31, 41, 43, 41, 31, 29 }, { 29, 31, 31, 29, 27, 27 },
			{ 31, 41, 43, 41, 31, 29 } };

	int[][] y2 = { { 17, 19, 29, 31, 29, 19 }, { 29, 29, 31, 33, 33, 31 },
			{ 17, 19, 29, 31, 29, 19 }, { 3, 5, 15, 17, 15, 5 },
			{ 1, 1, 3, 5, 5, 3 }, { 3, 5, 15, 17, 15, 5 },
			{ 15, 15, 17, 19, 19, 17 } };

	// Status Anzeige für die einzelnen Segmente
	// Wird dann später im Objekt direkt geändert

	boolean[] anzeige1 = { false, false, false, false, false, false, false,
			false };
	boolean[] anzeige2 = { false, false, false, false, false, false, false,
			false };

	Color background = new Color(238, 238, 238);

	public SevenSegment() {

		setVisible(true);

	}

	public void paintComponent(Graphics g) {
		g.setColor(background);
		g.fillRect(0, 0, 50 * SIZE, 45 * SIZE);// Hintergrund grau färben

		//Zeichne erste Zahl

		// 7 Polygone jeweils für die Segmente
		Polygon[] segment1 = new Polygon[7];
		for (int i = 0; i < 7; i++) {
			segment1[i] = new Polygon();
		}
		// Den Polygonen werden die Eckpunkte zugeordnet
		for (int i = 0; i < 7; i++) {
			for (int k = 0; k < 6; k++) {
				segment1[i].addPoint(x1[i][k] * SIZE, y1[i][k] * SIZE);
			} // end of for
		} // end of for
			// Polygone werden gezeichnet
		for (int m = 1; m < 8; m++) {
			if (anzeige1[m]) {// aktiv -> rot
				g.setColor(Color.red);
			} else { // inaktiv ->grau
				g.setColor(Color.lightGray);
			}

			g.fillPolygon(segment1[m - 1]); // Zeichne das Polygon mit der
											// entsprechenden Farbe
			g.setColor(Color.black);
			g.drawPolygon(segment1[m - 1]); // Polygon schwarz umrahmen

		}

		//Zeichne zweite Zahl
		Polygon[] segment2 = new Polygon[7];
		for (int i = 0; i < 7; i++) {
			segment2[i] = new Polygon();
		}

		for (int i = 0; i < 7; i++) {
			for (int k = 0; k < 6; k++) {
				segment2[i].addPoint(x2[i][k] * SIZE, y2[i][k] * SIZE);
			} // end of for
		} // end of for

		for (int m = 1; m < 8; m++) {
			if (anzeige2[m]) {
				g.setColor(Color.red);
			} else {
				g.setColor(Color.lightGray);
			}

			g.fillPolygon(segment2[m - 1]);
			g.setColor(Color.black);
			g.drawPolygon(segment2[m - 1]);

		}

		// Zeichne die Punkte der Segmente

		if (anzeige1[0]) {
			g.setColor(Color.red);
			g.fillRect(24 * SIZE, 30 * SIZE, 6, 6);
			g.setColor(Color.black);
			g.drawRect(24 * SIZE, 30 * SIZE, 6, 6);
		} else {
			g.setColor(Color.lightGray);
			g.fillRect(24 * SIZE, 30 * SIZE, 6, 6);
			g.setColor(Color.black);
			g.drawRect(24 * SIZE, 30 * SIZE, 6, 6);
		}
		if (anzeige2[0]) {
			g.setColor(Color.red);
			g.fillRect(46 * SIZE, 30 * SIZE, 6, 6);
			g.setColor(Color.black);
			g.drawRect(46 * SIZE, 30 * SIZE, 6, 6);
		} else {
			g.setColor(Color.lightGray);
			g.fillRect(46 * SIZE, 30 * SIZE, 6, 6);
			g.setColor(Color.black);
			g.drawRect(46 * SIZE, 30 * SIZE, 6, 6);
		}
		// repaint();
	}

	public void reset() {
		for (int i = 0; i < 8; i++) {
			anzeige1[i] = false;
			anzeige2[i] = false;
		}
		this.repaint();
	}

	public void setColor(Color color) {
		this.background = color;
	}
} // end of class SiebenSegment

