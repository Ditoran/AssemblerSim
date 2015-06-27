// Imports for the GUI classes.
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

/**
 * JKnob.java - A knob component. The knob can be rotated by dragging a spot on
 * the knob around in a circle. The knob will report its position in radians
 * when asked.
 * 
 * @author Grant William Braught
 * @author Dickinson College
 * @version 12/4/2000
 */

@SuppressWarnings("serial")
public class Poti extends JComponent implements MouseListener, MouseMotionListener {

	private static final int radius = 30;
	private static final int spotRadius = 5;

	private int value = 0;

	private double theta;
	private Color knobColor;
	private Color spotColor;

	private boolean pressedOnSpot;

	/**
	 * No-Arg constructor that initializes the position of the knob to 0 radians
	 * (Up).
	 */
	public Poti() {
		this(0);
	}

	/**
	 * Constructor that initializes the position of the knob to the specified
	 * angle in radians.
	 * 
	 * @param initAngle
	 *            the initial angle of the knob.
	 */
	public Poti(double initTheta) {
		this(initTheta, Color.red, Color.black);
	}

	/**
	 * Constructor that initializes the position of the knob to the specified
	 * position and also allows the colors of the knob and spot to be specified.
	 * 
	 * @param initAngle
	 *            the initial angle of the knob.
	 * @param initColor
	 *            the color of the knob.
	 * @param initSpotColor
	 *            the color of the spot.
	 */
	public Poti(double initTheta, Color initKnobColor, Color initSpotColor) {

		theta = initTheta;
		pressedOnSpot = false;
		knobColor = initKnobColor;
		spotColor = initSpotColor;

		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}

	/**
	 * Paint the JKnob on the graphics context given. The knob is a filled
	 * circle with a small filled circle offset within it to show the current
	 * angular position of the knob.
	 * 
	 * @param g
	 *            The graphics context on which to paint the knob.
	 */
	public void paint(Graphics g) {

		// Draw the knob.
		g.setColor(knobColor);
		g.fillOval(0, 0, 2 * radius, 2 * radius);

		// Find the center of the spot.
		Point pt = getSpotCenter();
		int xc = (int) pt.getX();
		int yc = (int) pt.getY();

		// Draw the spot.
		g.setColor(spotColor);
		g.fillOval(xc - spotRadius, yc - spotRadius, 2 * spotRadius,
				2 * spotRadius);
	}

	/**
	 * Return the ideal size that the knob would like to be.
	 * 
	 * @return the preferred size of the JKnob.
	 */
	public Dimension getPreferredSize() {
		return new Dimension(2 * radius, 2 * radius);
	}

	/**
	 * Return the minimum size that the knob would like to be. This is the same
	 * size as the preferred size so the knob will be of a fixed size.
	 * 
	 * @return the minimum size of the JKnob.
	 */
	public Dimension getMinimumSize() {
		return new Dimension(2 * radius, 2 * radius);
	}

	/**
	 * Get the current anglular position of the knob.
	 * 
	 * @return the current anglular position of the knob.
	 */
	public double getAngle() {
		return theta;
	}

	/**
	 * Calculate the x, y coordinates of the center of the spot.
	 * 
	 * @return a Point containing the x,y position of the center of the spot.
	 */
	private Point getSpotCenter() {

		// Calculate the center point of the spot RELATIVE to the
		// center of the of the circle.

		int r = radius - spotRadius;

		int xcp = (int) (r * Math.sin(theta));
		int ycp = (int) (r * Math.cos(theta));

		// Adjust the center point of the spot so that it is offset
		// from the center of the circle. This is necessary becasue
		// 0,0 is not actually the center of the circle, it is the
		// upper left corner of the component!
		int xc = radius + xcp;
		int yc = radius - ycp;

		// Create a new Point to return since we can't
		// return 2 values!
		return new Point(xc, yc);
	}

	/**
	 * Determine if the mouse click was on the spot or not. If it was return
	 * true, otherwise return false.
	 * 
	 * @return true if x,y is on the spot and false if not.
	 */
	private boolean isOnSpot(Point pt) {
		return (pt.distance(getSpotCenter()) < spotRadius);
	}

	// Methods from the MouseListener interface.

	/**
	 * Empy method because nothing happens on a click.
	 * 
	 * @param e
	 *            reference to a MouseEvent object describing the mouse click.
	 */

	public void mousePressed(MouseEvent e) {

		Point mouseLoc = e.getPoint();
		pressedOnSpot = isOnSpot(mouseLoc);
	}

	/**
	 * When the button is released, the dragging of the spot is disabled.
	 * 
	 * @param e
	 *            reference to a MouseEvent object describing the mouse release.
	 */
	public void mouseReleased(MouseEvent e) {
		pressedOnSpot = false;
	}

	// Methods from the MouseMotionListener interface.

	/**
	 * Empty method because nothing happens when the mouse is moved if it is not
	 * being dragged.
	 * 
	 * @param e
	 *            reference to a MouseEvent object describing the mouse move.
	 */

	public void mouseDragged(MouseEvent e) {
		if (pressedOnSpot) {

			int mx = e.getX();
			int my = e.getY();

			// Compute the x, y position of the mouse RELATIVE
			// to the center of the knob.
			int mxp = mx - radius;
			int myp = radius - my;

			// Compute the new angle of the knob from the
			// new x and y position of the mouse.
			// Math.atan2(...) computes the angle at which
			// x,y lies from the positive y axis with cw rotations
			// being positive and ccw being negative.
			theta = Math.atan2(mxp, myp);
			double temp = theta;
			// int temp = (int)(theta*100000);
			if (temp < 0) {
				temp = Math.abs((Math.abs(temp)) - Math.PI) + Math.PI;
			}
			temp = temp * 1000;
			int number = (int) (temp / 24.55);
			this.setValue(number);

			repaint();
		}
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}