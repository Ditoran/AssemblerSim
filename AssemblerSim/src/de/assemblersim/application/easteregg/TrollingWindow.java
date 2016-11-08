package de.assemblersim.application.easteregg;

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
		addComponentListener(new ComponentAdapter() {
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

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		final boolean isTranslucencySupported = gd.isWindowTranslucencySupported(TRANSLUCENT);

		if (!gd.isWindowTranslucencySupported(PERPIXEL_TRANSPARENT)) {
			System.err.println("Shaped windows are not supported");
			System.exit(0);
		}

		if (!isTranslucencySupported) {
			System.out.println("Translucency is not supported, creating an opaque window");
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				TrollingWindow sw = new TrollingWindow();

				if (isTranslucencySupported) {
					sw.setOpacity(0.1f);
				}
				sw.setVisible(true);
			}
		});

	}
}