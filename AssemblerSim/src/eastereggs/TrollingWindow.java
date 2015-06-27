package eastereggs;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.awt.geom.Rectangle2D;

import static java.awt.GraphicsDevice.WindowTranslucency.*;

@SuppressWarnings("serial")
public class TrollingWindow extends JFrame {
    public TrollingWindow() {
	super("ShapedWindow");
	this.setLayout(null);
	// It is best practice to set the window's shape in
	// the componentResized method. Then, if the window
	// changes size, the shape will be correctly recalculated.
	addComponentListener(new ComponentAdapter() {
	    // Give the window an elliptical shape.
	    // If the window is resized, the shape is recalculated here.
	    @Override
	    public void componentResized(ComponentEvent e) {
		setShape(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
	    }
	});

	setUndecorated(true);
	setBackground(Color.blue);
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	int width = (int) screenSize.getWidth();
	int height = (int) screenSize.getHeight();
	setSize(width, height);
	setLocationRelativeTo(null);
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	JButton close = new JButton("X");
	close.setMargin(new Insets(0, 0, 0, 0));
	close.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 8));
	close.setBounds(getSize().width - 15, 0, 15, 15);
	close.addActionListener(new ActionListener() {

	    @Override
	    public void actionPerformed(ActionEvent arg0) {
		dispose();
	    }
	});
	add(close);
    }

    public void start() {
	// Determine what the GraphicsDevice can support.
	GraphicsEnvironment ge = GraphicsEnvironment
		.getLocalGraphicsEnvironment();
	GraphicsDevice gd = ge.getDefaultScreenDevice();
	final boolean isTranslucencySupported = gd
		.isWindowTranslucencySupported(TRANSLUCENT);

	// If shaped windows aren't supported, exit.
	if (!gd.isWindowTranslucencySupported(PERPIXEL_TRANSPARENT)) {
	    System.err.println("Shaped windows are not supported");
	    System.exit(0);
	}

	// If translucent windows aren't supported,
	// create an opaque window.
	if (!isTranslucencySupported) {
	    System.out
		    .println("Translucency is not supported, creating an opaque window");
	}

	// Create the GUI on the event-dispatching thread
	SwingUtilities.invokeLater(new Runnable() {
	    @Override
	    public void run() {
		TrollingWindow sw = new TrollingWindow();

		// Set the window to 70% translucency, if supported.
		if (isTranslucencySupported) {
		    sw.setOpacity(0.1f);
		}

		// Display the window.
		sw.setVisible(true);
	    }
	});

    }
}