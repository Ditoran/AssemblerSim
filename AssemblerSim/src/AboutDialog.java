import java.awt.Container;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class AboutDialog extends JDialog {

	private static final long serialVersionUID = 4732494351145169728L;

	private JLabel abouttext = new JLabel();
	private JLabel programVersion = new JLabel();
	private JLabel pic = new JLabel();
	private Date date;
	private JTextArea credits = new JTextArea("");
	private JScrollPane scrollCredits;

	public AboutDialog(String programVersion) {

		setTitle("Über AssemblerSim");
		int width = 300;
		int height = 200;
		setSize(width, height);
		setLocationRelativeTo(null);
		setResizable(false);
		Image icon = new ImageIcon(this.getClass()
				.getResource("icons/icon.png")).getImage();
		setIconImage(icon);
		pic.setIcon(new ImageIcon(icon));
		pic.setBounds(15, 5, 64, 64);
		pic.setCursor(new Cursor(Cursor.HAND_CURSOR));
		Container cp = getContentPane();
		cp.setLayout(null);
		setModal(true);
		date = new Date();
		DateFormat df = new SimpleDateFormat("YYYY");
		String copyrightYear = df.format(date);
		abouttext.setText("\u00A9 " + copyrightYear + " Dominik J.");
		abouttext.setBounds(100, 20, 120, 30);
		cp.add(abouttext);
		this.programVersion.setText("Version: " + programVersion);
		this.programVersion.setBounds(90, 35, 150, 30);
		cp.add(this.programVersion);
		scrollCredits = new JScrollPane(credits);
		scrollCredits.setBounds(15, 75, 260, 90);
		credits.setEditable(false);
		credits.setFocusable(false);
		credits.setLineWrap(true);
		credits.setWrapStyleWord(true);
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					getClass().getResourceAsStream("txt/credits.txt"), "UTF-8"));
			credits.read(br, "");
			credits.setFont(new Font("Monospaced", Font.PLAIN, 12));
		} catch (Exception e) {
		}
		cp.add(scrollCredits);
		cp.add(pic);
		this.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {

			}

			@Override
			public void keyReleased(KeyEvent e) {

			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {

					dispose();
				}
			}
		});
		pic.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent arg0) {
				try {
					Desktop.getDesktop().browse(
							new URI("http://assemblersim.de"));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		});
		setVisible(true);
	}

}