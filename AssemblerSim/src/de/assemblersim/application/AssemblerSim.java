package de.assemblersim.application;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import de.assemblersim.application.cloud.Cloud;
import de.assemblersim.application.cloud.CloudFile;
import de.assemblersim.application.cloud.CloudFileChooser;
import de.assemblersim.application.display.Display;
import de.assemblersim.application.easteregg.TrollingWindow;

/****************************************************************
 * @author Dominik Jahnke
 * 
 *         Funktionen bis jetzt: - mov - setb - clr - Unterstuetzung von Marken
 *         - sjmp - djnz - cjne - inc - dec - rl - rr - jz - jnz - jb - jnb -
 *         mul - div - cpl - add - swap - Tabellen/Datenbanken - anl - orl - xrl
 *         - xch - xchd
 * 
 * 
 *         Kleinere Features: - Leerzeichen am Anfang und Kommentare im Code
 *         werden entfernt - P1 - interner RAM - Interrupts mit P3.2 und P3.3 -
 *         Poti - Timer 0 & Timer 1 - DATA und EQU (Register und Konstanten
 *         benennen)
 * 
 * 
 *         Zu dem RAM-Speicher:
 * 
 *         Felder gehen von 48 bis 127 (30h bis 7Fh) -> 80 Speicherzellen
 * 
 * 
 *         To Do:
 * 
 *         -push -pop -mehrzeilige Tabellen -cloud upload
 * 
 ****************************************************************/

@SuppressWarnings("serial")
public class AssemblerSim extends JFrame implements Runnable {

	private boolean taster1, taster2 = false;
	boolean segmentOn = true;
	private int i = 0; // Pointer for main loop
	private boolean running = true; // Wenn false, bricht Thread ab
	int loading = 0;

	private String programversion = "20150926";
	private String filepath = System.getProperty("user.dir");
	private String filename = "";
	String buffer = "";
	private List<String> logfile = new ArrayList<String>();
	// Settings
	private boolean useCodeCleaner;
	private boolean cpucheckoff;
	private boolean printHeader = false;
	private boolean trayIcon = true;
	private boolean toolbarVisible = true;
	private String theme = "";
	int speed = 0;
	long cpuSpeed = 0;
	Cloud cloud = Cloud.getInstance();

	// Anfang Attribute
	ExtendedJEditorPane codebox;
	private JScrollPane codeboxScrollPane;
	JSpinner fontsizeSpinner = null;
	private JPopupMenu codeboxPopupMenu = new JPopupMenu();
	private JPopupMenu displayPopupMenu = new JPopupMenu();
	private JMenuItem JPopupCut = new JMenuItem("Ausschneiden");
	private JMenuItem JPopupCopy = new JMenuItem("Kopieren");
	private JMenuItem JPopupPaste = new JMenuItem("Einf\u00fcgen");
	private JMenuItem JPopupDelete = new JMenuItem("L\u00f6schen");
	private JMenuItem JPopupSelectAll = new JMenuItem("Alles markieren");
	private JMenuItem JPopupCommentSelection = new JMenuItem("Auskommentieren");
	private JMenuItem JPopupClearDisplay = new JMenuItem("Display l\u00f6schen");
	JPanel toolbarPane;
	SevenSegment anzeige;
	Display display;
	Poti poti;
	UndoRedoManager undomgr = new UndoRedoManager();

	private Thread thread1;
	private boolean waitingprog = false;

	// Timer & Interrupt Bits

	boolean ea = false;
	boolean ex0 = false;
	boolean ex1 = false;
	boolean et0 = false;
	boolean et1 = false;

	// TMOD Bit

	boolean gate1 = false;
	boolean ct1 = false;
	boolean m1_1 = false; // M1 Timer 1
	boolean m0_1 = false;// M0 Timer 1
	boolean gate0 = false;
	boolean ct0 = false;
	boolean m1_0 = false; // M1 Timer 0
	boolean m0_0 = false;// M0 Timer 0

	// TCON Bit

	boolean tf1 = false;
	boolean tr1 = false;
	boolean tf0 = false;
	boolean tr0 = false;
	boolean ie1 = false;
	boolean it1 = false;
	boolean ie0 = false;
	boolean it0 = false;

	String th1 = "0";
	String tl1 = "0";
	String th0 = "0";
	String tl0 = "0";

	int startValueTimer0 = 0;
	int startValueTimer1 = 0;

	int currentValueTimer0 = 0;
	int currentValueTimer1 = 0;

	// Ende Timer & Interrupt Bits

	private String dptr = "";

	private boolean pause = false;
	private boolean debugMode = false;

	String[][] source;

	String[][] ramdata = new String[][] { { "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "" },
			{ "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "" },
			{ "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "" },
			{ "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "" },
			{ "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "" } };
	final String[][] ramcellnames = new String[][] {
			{ "30h", "31h", "32h", "33h", "34h", "35h", "36h", "37h", "38h", "39h", "3Ah", "3Bh", "3Ch", "3Dh", "3Eh",
					"3Fh" },
			{ "40h", "41h", "42h", "43h", "44h", "45h", "46h", "47h", "48h", "49h", "4Ah", "4Bh", "4Ch", "4Dh", "4Eh",
					"4Fh" },
			{ "50h", "51h", "52h", "53h", "54h", "55h", "56h", "57h", "58h", "59h", "5Ah", "5Bh", "5Ch", "5Dh", "5Eh",
					"5Fh" },
			{ "60h", "61h", "62h", "63h", "64h", "65h", "66h", "67h", "68h", "69h", "6Ah", "6Bh", "6Ch", "6Dh", "6Eh",
					"6Fh" },
			{ "70h", "71h", "72h", "73h", "74h", "75h", "76h", "77h", "78h", "79h", "7Ah", "7Bh", "7Ch", "7Dh", "7Eh",
					"7Fh" } };

	private JRadioButton[] leds = { new JRadioButton(), new JRadioButton(), new JRadioButton(), new JRadioButton(),
			new JRadioButton(), new JRadioButton(), new JRadioButton(), new JRadioButton() };

	private JTextField[] register = { new JTextField("0"), new JTextField("0"), new JTextField("0"),
			new JTextField("0"), new JTextField("0"), new JTextField("0"), new JTextField("0"), new JTextField("0") };

	private JToggleButton[] schalter = { new JToggleButton(), new JToggleButton(), new JToggleButton(),
			new JToggleButton(), new JToggleButton(), new JToggleButton(), new JToggleButton(), new JToggleButton() };

	private JTextField A = new JTextField("0");
	private JTextField B = new JTextField("0");
	private JButton jButton_run = new JButton();
	private JButton jButton_stop = new JButton();
	private JButton jButton_close = new JButton();
	private JButton jButton_reset = new JButton();
	private JButton jButton_taster1 = new JButton();
	private JButton jButton_taster2 = new JButton();
	private JLabel jLabel_leds = new JLabel();
	private JLabel jLabel_taster = new JLabel();
	private JLabel jLabel_poti = new JLabel();
	private JButton jButton_debug = new JButton();
	private JButton jButton_ram = new JButton();

	JButton toolbarResumeDebug;

	private JLabel label_R0 = new JLabel();
	private JLabel label_R1 = new JLabel();
	private JLabel label_R2 = new JLabel();
	private JLabel label_R3 = new JLabel();
	private JLabel label_R4 = new JLabel();
	private JLabel label_R5 = new JLabel();
	private JLabel label_R6 = new JLabel();
	private JLabel label_R7 = new JLabel();
	private JLabel label_P2_0 = new JLabel();
	private JLabel label_P2_1 = new JLabel();
	private JLabel label_P2_2 = new JLabel();
	private JLabel label_P2_3 = new JLabel();
	private JLabel label_P2_4 = new JLabel();
	private JLabel label_P2_5 = new JLabel();
	private JLabel label_P2_6 = new JLabel();
	private JLabel label_P2_7 = new JLabel();
	private JLabel label_A = new JLabel();
	private JLabel label_B = new JLabel();
	private JLabel label_debug = new JLabel();
	private JLabel label_schalter = new JLabel();
	private JSlider jSlider_speed = new JSlider();
	private JLabel label_speed_value = new JLabel();
	private JLabel label_speed_text = new JLabel();
	private JLabel label_ram = new JLabel();
	private JLabel label_currentcell = new JLabel();
	private JTable ramTable = new JTable(ramdata,
			new String[] { "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "" });

	Container cp;

	public AssemblerSim(String title) {
		// Frame-Initialisierung
		super(title);

		// ============================================================
		try {
			InputStream is = getClass()
					.getResourceAsStream(Splash.class.getProtectionDomain().getCodeSource().getLocation().getFile());
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			StringBuffer sb = new StringBuffer();
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		// ============================================================

		File jarFile;
		try {
			jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
		} catch (Exception e) {
			return;
		}
		if (jarFile.getName().endsWith(".jar")) {
			try {
				System.setErr(new PrintStream(new FileOutputStream(System.getProperty("user.dir") + "/error.log")));
			} catch (FileNotFoundException ex) {
				ex.printStackTrace();
			}
		}

		// Lade Einstellungen
		try {
			Properties props = new Properties();
			props.load(new FileInputStream("res/settings.ini"));
			useCodeCleaner = Boolean.parseBoolean(props.getProperty("codecleaner"));
			cpucheckoff = Boolean.parseBoolean(props.getProperty("cpucheckoff"));
			trayIcon = Boolean.parseBoolean(props.getProperty("trayIcon"));
			theme = props.getProperty("theme");
			printHeader = Boolean.parseBoolean(props.getProperty("printHeader"));
			props.setProperty("programversion", programversion);
			props.setProperty("theme", theme);
			props.setProperty("trayIcon", Boolean.toString(trayIcon));
			props.setProperty("printHeader", Boolean.toString(printHeader));
			FileWriter fr = new FileWriter("res/settings.ini");
			BufferedWriter bw = new BufferedWriter(fr);
			props.store(bw, null);
		} catch (FileNotFoundException e1) {
			writeLog("Settings file not found");
		} catch (Exception e1) {
			writeLog(e1.getMessage());
			e1.printStackTrace();
		}

		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if (theme.equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
			// If Nimbus is not available, you can set the GUI to another look
			// and feel.
		}
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (JOptionPane.showConfirmDialog(null, "Beenden?\nAlle ungesicherten Daten gehen verloren.", "",
						JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					System.exit(0);
				}
			}
		});
		final int frameWidth = 869;
		final int frameHeight = 442;
		setSize(471, 492);
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (d.width - 669) / 2;
		int y = (d.height - 442) / 2;
		setLocation(x, y);
		Image icon = new ImageIcon(this.getClass().getResource("/icons/icon.png")).getImage();
		setIconImage(icon);
		setResizable(false);
		setBackground(new Color(238, 238, 238));
		cp = getContentPane();
		cp.setLayout(null);
		cp.setFocusable(true);
		cp.addKeyListener(new Keyboard());

		codebox = new Syntax(12).getEditPane();

		codeboxScrollPane = new JScrollPane(codebox);
		codeboxScrollPane.setBounds(32, 40, 290, 237);
		cp.add(codeboxScrollPane);

		// restores text after restart
		File f = new File(".temprestart");
		if (f.exists()) {
			FileReader fr;
			try {
				fr = new FileReader(f);
				codebox.read(fr, "");
				fr.close();
				f.delete();
			} catch (Exception e2) {

			}

		}

		// SiebensegmentAnzeige
		anzeige = new SevenSegment();
		anzeige.setBounds(400, 270, 150, 105);
		cp.add(anzeige);

		// LCD Anzeige

		display = new Display();
		display.setBounds(344, 40, 292, 57);
		cp.add(display);

		// Poti Anzeige

		poti = new Poti();
		poti.setBounds(570, 140, 70, 70);
		cp.add(poti);

		// Display Kontextmenue Start
		display.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent evt) {
				if (evt.isPopupTrigger()) {
					displayPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
				}
			}
		});

		JPopupClearDisplay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				display.deleteAllLines();
			}
		});
		displayPopupMenu.add(JPopupClearDisplay);

		// Codebox Kontextmenue Start
		codebox.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent evt) {
				if (evt.isPopupTrigger()) {
					codeboxPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
				}
			}
		});

		JPopupCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
		JPopupCut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				codebox.cut();
				undomgr.addToQueue(codebox.getText());
			}
		});
		codeboxPopupMenu.add(JPopupCut);

		JPopupCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
		JPopupCopy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				codebox.copy();
			}
		});
		codeboxPopupMenu.add(JPopupCopy);

		JPopupPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
		JPopupPaste.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				codebox.paste();
				undomgr.addToQueue(codebox.getText());
			}
		});
		codeboxPopupMenu.add(JPopupPaste);

		JPopupDelete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		JPopupDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				try {
					codebox.replaceSelection("");
					undomgr.addToQueue(codebox.getText());
				} catch (Exception e) {
				}
			}
		});
		codeboxPopupMenu.add(JPopupDelete);

		JPopupSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));
		JPopupSelectAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				codebox.selectAll();
			}
		});
		codeboxPopupMenu.add(JPopupSelectAll);

		codeboxPopupMenu.addSeparator();

		JPopupCommentSelection.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, InputEvent.CTRL_MASK));
		JPopupCommentSelection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (codebox.getSelectedText() == null) {
					return;
				}
				String[] temp = codebox.getSelectedText().split("\n");
				String replacement = "";
				for (int i = 0; i < temp.length; i++) {
					if (!temp[i].startsWith(";")) {
						temp[i] = ";" + temp[i];
					} else {
						temp[i] = temp[i].substring(1);
					}

					replacement += temp[i];
					if (i < temp.length - 1) {
						replacement += "\n";
					}
				}

				codebox.replaceSelection(replacement);
			}
		});
		codeboxPopupMenu.add(JPopupCommentSelection);

		// Kontextmenue Ende

		// MENUBAR ANFANG
		JMenuBar mbar = new JMenuBar();
		setJMenuBar(mbar);
		// File Menue
		JMenu filemenu = new JMenu("Datei");
		mbar.add(filemenu);
		final JMenuItem newdoc = new JMenuItem("Neu");
		filemenu.add(newdoc);
		newdoc.setIcon(new ImageIcon(this.getClass().getResource("/icons/new.gif")));
		newdoc.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
		newdoc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (JOptionPane.showConfirmDialog(null,
						"Neues Dokument erstellen?\nAlle ungesicherten Daten gehen verloren.", "",
						JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					codebox.setText("");
					undomgr.clearQueue();
				}
			}
		});
		final JMenu open = new JMenu("\u00d6ffnen");
		open.setIcon(new ImageIcon(this.getClass().getResource("/icons/open.gif")));
		filemenu.add(open);
		final JMenuItem openFromFile = new JMenuItem("Datei");
		open.add(openFromFile);
		openFromFile.setIcon(new ImageIcon(this.getClass().getResource("/icons/open.gif")));
		openFromFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		openFromFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				JFileChooser chooseopen = new JFileChooser(filepath);
				chooseopen.setAcceptAllFileFilterUsed(false);
				chooseopen.addChoosableFileFilter(new FileNameExtensionFilter("Assembler File (.asm)", "asm"));
				chooseopen.addChoosableFileFilter(new FileNameExtensionFilter("Ride IDE File (.a51)", "a51"));
				chooseopen.showOpenDialog(null);
				try {
					if (chooseopen.getSelectedFile() != null) {
						filepath = chooseopen.getSelectedFile().getPath();
						filename = chooseopen.getSelectedFile().getName();
						System.out.println(chooseopen.getSelectedFile());
						codebox.read(new FileReader(chooseopen.getSelectedFile()), "");
						setTitle(chooseopen.getSelectedFile().getName() + " | AssemblerSim");
						undomgr.clearQueue();
					}
				} catch (IOException e) {
					writeLog(e.getMessage());
				}
			}
		});
		final JMenuItem openFromCloud = new JMenuItem("Cloud");
		open.add(openFromCloud);
		openFromCloud.setIcon(new ImageIcon(this.getClass().getResource("/icons/cloud.gif")));
		openFromCloud.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (!Cloud.getInstance().isLoggedIn()) {
					JPanel panel = new JPanel(new BorderLayout(5, 5));

					JPanel label = new JPanel(new GridLayout(0, 1, 2, 2));
					label.add(new JLabel("Benutzername", SwingConstants.RIGHT));
					label.add(new JLabel("Password", SwingConstants.RIGHT));
					panel.add(label, BorderLayout.WEST);

					JPanel controls = new JPanel(new GridLayout(0, 1, 2, 2));
					JTextField username = new JTextField(Cloud.getInstance().getUsername());
					username.requestFocusInWindow();
					controls.add(username);
					JPasswordField password = new JPasswordField(Cloud.getInstance().getPassword());
					controls.add(password);
					panel.add(controls, BorderLayout.CENTER);

					JOptionPane.showMessageDialog(null, panel, "", JOptionPane.QUESTION_MESSAGE);

					cloud.login(username.getText(), String.valueOf(password.getPassword()));
				}

				CloudFileChooser chooser = new CloudFileChooser();

				chooser.showOpenDialog();
				CloudFile selectedFile = chooser.getSelectedFile();
				if (selectedFile != null) {
					// filepath = chooseopen.getSelectedFile().getPath();
					filename = selectedFile.getTitle();
					codebox.setText(selectedFile.getContent());
					setTitle(selectedFile.getTitle() + " | AssemblerSim");
					undomgr.clearQueue();
				}
			}
		});
		final JMenu save = new JMenu("Speichern");
		save.setIcon(new ImageIcon(this.getClass().getResource("/icons/save.gif")));
		filemenu.add(save);
		final JMenuItem saveInFile = new JMenuItem("Datei");
		save.add(saveInFile);
		saveInFile.setIcon(new ImageIcon(this.getClass().getResource("/icons/save.gif")));
		saveInFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		saveInFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				JFileChooser choosesave;
				if (filepath.equals("")) {
					choosesave = new JFileChooser();
				} else {
					choosesave = new JFileChooser(filepath);
				}
				choosesave.setAcceptAllFileFilterUsed(false);
				FileFilter filter = new FileNameExtensionFilter("Assembler File (.asm)", "asm");
				choosesave.addChoosableFileFilter(filter);
				choosesave.setSelectedFile(new File(filename));
				choosesave.showSaveDialog(null);

				try {
					File f = new File(choosesave.getSelectedFile(), "");

					if (!f.getName().endsWith(".asm")) {
						f = new File(f.getAbsolutePath() + ".asm");
					}

					// Pruefe, ob Datei schon existiert
					if (new File(f.getAbsolutePath()).exists() && !f.getName().equals(".asm")) {
						int response = JOptionPane.showConfirmDialog(null,
								f.getName() + " existiert bereits. \u00DCberschreiben?", "Fortfahren",
								JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
						if (response == JOptionPane.NO_OPTION) {
							return;
						} else if (response == JOptionPane.CLOSED_OPTION) {
							return;
						}
					}

					FileWriter fw = new FileWriter(f);
					fw.write(codebox.getText());
					fw.close();
					filepath = choosesave.getSelectedFile().getPath();
					filename = choosesave.getSelectedFile().getName();

					if (choosesave.getSelectedFile().getName().endsWith(".asm")) {
						setTitle(choosesave.getSelectedFile().getName() + " | AssemblerSim");
					} else {
						setTitle(choosesave.getSelectedFile().getName() + ".asm | AssemblerSim");
					}

				} catch (Exception e) {
				}
			}
		});
		final JMenuItem saveInCloud = new JMenuItem("Cloud");
		save.add(saveInCloud);
		saveInCloud.setIcon(new ImageIcon(this.getClass().getResource("/icons/cloud.gif")));
		saveInCloud.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (!Cloud.getInstance().isLoggedIn()) {
					JPanel panel = new JPanel(new BorderLayout(5, 5));

					JPanel label = new JPanel(new GridLayout(0, 1, 2, 2));
					label.add(new JLabel("Benutzername", SwingConstants.RIGHT));
					label.add(new JLabel("Password", SwingConstants.RIGHT));
					panel.add(label, BorderLayout.WEST);

					JPanel controls = new JPanel(new GridLayout(0, 1, 2, 2));
					JTextField username = new JTextField(Cloud.getInstance().getUsername());
					username.requestFocusInWindow();
					controls.add(username);
					JPasswordField password = new JPasswordField(Cloud.getInstance().getPassword());
					controls.add(password);
					panel.add(controls, BorderLayout.CENTER);

					JOptionPane.showMessageDialog(null, panel, "", JOptionPane.QUESTION_MESSAGE);
					cloud.login(username.getText(), String.valueOf(password.getPassword()));
				}

				JFileChooser filechooser = new JFileChooser("res/cloud/");
				filechooser.showSaveDialog(null);
			}
		});
		final JMenuItem printdoc = new JMenuItem("Drucken");
		filemenu.add(printdoc);
		printdoc.setIcon(new ImageIcon(this.getClass().getResource("/icons/print.gif")));
		printdoc.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK));
		printdoc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				String temp = codebox.getText();
				codebox.setEditorKit(new Syntax(10).getEditorKit());
				codebox.setText(temp);
				new CodePrinter().print(codebox, getTitle(), printHeader);
				codebox.setEditorKit(new Syntax((int) fontsizeSpinner.getValue()).getEditorKit());
				codebox.repaint();
				codebox.setText(temp);
				codebox.repaint();
			}
		});
		filemenu.addSeparator();

		JMenu samplesmenu = new JMenu("Beispiele");
		samplesmenu.setIcon(new ImageIcon(this.getClass().getResource("/icons/samples.gif")));
		filemenu.add(samplesmenu);

		JMenuItem sample1 = new JMenuItem("7-Segment-Anzeige Zähler mit DB");
		sample1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					BufferedReader br = new BufferedReader(new InputStreamReader(
							getClass().getResourceAsStream("/samples/7-Segment-Anzeige Zähler mit DB.asm"), "UTF-8"));
					codebox.read(br, "");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		samplesmenu.add(sample1);

		JMenuItem sample2 = new JMenuItem("7-Segment-Anzeige Zähler rückwärts mit DB");
		sample2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					BufferedReader br = new BufferedReader(new InputStreamReader(
							getClass().getResourceAsStream("/samples/7-Segment-Anzeige Zähler rückwärts mit DB.asm"),
							"UTF-8"));
					codebox.read(br, "");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		samplesmenu.add(sample2);

		JMenuItem sample3 = new JMenuItem("BCD");
		sample3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					BufferedReader br = new BufferedReader(
							new InputStreamReader(getClass().getResourceAsStream("/samples/BCD.asm"), "UTF-8"));
					codebox.read(br, "");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		samplesmenu.add(sample3);

		JMenuItem sample4 = new JMenuItem("Counter Display");
		sample4.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					BufferedReader br = new BufferedReader(new InputStreamReader(
							getClass().getResourceAsStream("/samples/Counter Display.asm"), "UTF-8"));
					codebox.read(br, "");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		samplesmenu.add(sample4);

		JMenuItem sample5 = new JMenuItem("Display eigenes Zeichen");
		sample5.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					BufferedReader br = new BufferedReader(new InputStreamReader(
							getClass().getResourceAsStream("/samples/Display eigenes Zeichen.asm"), "UTF-8"));
					codebox.read(br, "");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		samplesmenu.add(sample5);

		JMenuItem sample6 = new JMenuItem("Displayansteuerung 1");
		sample6.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					BufferedReader br = new BufferedReader(new InputStreamReader(
							getClass().getResourceAsStream("/samples/Displayansteuerung 1.asm"), "UTF-8"));
					codebox.read(br, "");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		samplesmenu.add(sample6);

		JMenuItem sample7 = new JMenuItem("Drückspiel");
		sample7.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					BufferedReader br = new BufferedReader(
							new InputStreamReader(getClass().getResourceAsStream("/samples/Drückspiel.asm"), "UTF-8"));
					codebox.read(br, "");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		samplesmenu.add(sample7);

		JMenuItem sample8 = new JMenuItem("Fibonacci-Zahlen");
		sample8.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					BufferedReader br = new BufferedReader(new InputStreamReader(
							getClass().getResourceAsStream("/samples/Fibonacci-Zahlen.asm"), "UTF-8"));
					codebox.read(br, "");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		samplesmenu.add(sample8);

		JMenuItem sample9 = new JMenuItem("Interrupt 1");
		sample9.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					BufferedReader br = new BufferedReader(
							new InputStreamReader(getClass().getResourceAsStream("/samples/Interrupt 1.asm"), "UTF-8"));
					codebox.read(br, "");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		samplesmenu.add(sample9);

		JMenuItem sample10 = new JMenuItem("Interrupt 2");
		sample10.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					BufferedReader br = new BufferedReader(
							new InputStreamReader(getClass().getResourceAsStream("/samples/Interrupt 2.asm"), "UTF-8"));
					codebox.read(br, "");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		samplesmenu.add(sample10);

		JMenuItem sample11 = new JMenuItem("Lauflicht mit Stop");
		sample11.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					BufferedReader br = new BufferedReader(new InputStreamReader(
							getClass().getResourceAsStream("/samples/Lauflicht mit Stop.asm"), "UTF-8"));
					codebox.read(br, "");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		samplesmenu.add(sample11);

		JMenuItem sample12 = new JMenuItem("Potentiometer");
		sample12.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					BufferedReader br = new BufferedReader(new InputStreamReader(
							getClass().getResourceAsStream("/samples/Potentiometer.asm"), "UTF-8"));
					codebox.read(br, "");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		samplesmenu.add(sample12);

		JMenuItem sample13 = new JMenuItem("Timer LED Zähler");
		sample13.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					BufferedReader br = new BufferedReader(new InputStreamReader(
							getClass().getResourceAsStream("/samples/Timer LED Zähler.asm"), "UTF-8"));
					codebox.read(br, "");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		samplesmenu.add(sample13);

		JMenuItem sample14 = new JMenuItem("Timer Uhr");
		sample14.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					BufferedReader br = new BufferedReader(
							new InputStreamReader(getClass().getResourceAsStream("/samples/Timer Uhr.asm"), "UTF-8"));
					codebox.read(br, "");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		samplesmenu.add(sample14);

		JMenuItem sample15 = new JMenuItem("Zahlen sortieren");
		sample15.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					BufferedReader br = new BufferedReader(new InputStreamReader(
							getClass().getResourceAsStream("/samples/Zahlen sortieren.asm"), "UTF-8"));
					codebox.read(br, "");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		samplesmenu.add(sample15);

		JMenuItem sample16 = new JMenuItem("Zahlenspeicher");
		sample16.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					BufferedReader br = new BufferedReader(new InputStreamReader(
							getClass().getResourceAsStream("/samples/Zahlenspeicher.asm"), "UTF-8"));
					codebox.read(br, "");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		samplesmenu.add(sample16);

		samplesmenu.addSeparator();
		JMenuItem loadNewSamples = new JMenuItem("Beispiele herunterladen");
		loadNewSamples.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "Die Funktion ist noch nicht verfügbar", "",
						JOptionPane.INFORMATION_MESSAGE);
			}
		});
		samplesmenu.add(loadNewSamples);

		filemenu.addSeparator();
		JMenuItem restart = new JMenuItem("Neustarten");
		filemenu.add(restart);
		restart.setIcon(new ImageIcon(this.getClass().getResource("/icons/restart.gif")));
		restart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (JOptionPane.showConfirmDialog(null, "Neustarten?\nDer bisherige Programmcode bleibt erhalten.", "",
						JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					restart();
				}
			}
		});
		JMenuItem exit = new JMenuItem("Beenden");
		filemenu.add(exit);
		exit.setIcon(new ImageIcon(this.getClass().getResource("/icons/close.gif")));
		exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (JOptionPane.showConfirmDialog(null, "Beenden?\nAlle ungesicherten Daten gehen verloren.", "",
						JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					System.exit(0);
				}
			}
		});
		// Edit Menue
		JMenu editmenu = new JMenu("Bearbeiten");
		mbar.add(editmenu);
		final JMenuItem menuundo = new JMenuItem("Rückgängig");
		editmenu.add(menuundo);
		menuundo.setIcon(new ImageIcon(this.getClass().getResource("/icons/undo.gif")));
		menuundo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));
		menuundo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				undomgr.addToQueue(codebox.getText());
				undomgr.undo();
				int caretPos = codebox.getCaretPosition();
				codebox.setText(undomgr.undo());
				try {
					codebox.setCaretPosition(caretPos);
				} catch (IllegalArgumentException e) {

				}
			}
		});
		final JMenuItem menuredo = new JMenuItem("Wiederholen");
		editmenu.add(menuredo);
		menuredo.setIcon(new ImageIcon(this.getClass().getResource("/icons/redo.gif")));
		menuredo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_MASK));
		menuredo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				int caretPos = codebox.getCaretPosition();
				codebox.setText(undomgr.redo());
				codebox.setCaretPosition(caretPos);
			}
		});
		editmenu.addSeparator();
		final JMenuItem menucut = new JMenuItem("Ausschneiden");
		editmenu.add(menucut);
		menucut.setIcon(new ImageIcon(this.getClass().getResource("/icons/cut.gif")));
		menucut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
		menucut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				codebox.cut();
				undomgr.addToQueue(codebox.getText());
			}
		});
		final JMenuItem menucopy = new JMenuItem("Kopieren");
		editmenu.add(menucopy);
		menucopy.setIcon(new ImageIcon(this.getClass().getResource("/icons/copy.gif")));
		menucopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
		menucopy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				codebox.copy();
			}
		});
		final JMenuItem menupaste = new JMenuItem("Einfügen");
		editmenu.add(menupaste);
		menupaste.setIcon(new ImageIcon(this.getClass().getResource("/icons/paste.gif")));
		menupaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
		menupaste.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				codebox.paste();
				undomgr.addToQueue(codebox.getText());
			}
		});
		JMenuItem menudelete = new JMenuItem("Löschen");
		editmenu.add(menudelete);
		menudelete.setIcon(new ImageIcon(this.getClass().getResource("/icons/delete.gif")));
		menudelete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		menudelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				codebox.replaceSelection("");
				undomgr.addToQueue(codebox.getText());
			}
		});
		editmenu.addSeparator();
		JMenuItem menuselectall = new JMenuItem("Alles markieren");
		editmenu.add(menuselectall);
		menuselectall.setIcon(new ImageIcon(this.getClass().getResource("/icons/selectall.gif")));
		menuselectall.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));
		menuselectall.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				codebox.selectAll();
			}
		});
		// View Menue
		JMenu view = new JMenu("Ansicht");
		mbar.add(view);
		JMenuItem viewLargeCodebox = new JMenuItem("Codebox");
		view.add(viewLargeCodebox);
		viewLargeCodebox.setIcon(new ImageIcon(this.getClass().getResource("/icons/codebox.gif")));
		viewLargeCodebox.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_MASK));
		viewLargeCodebox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				new CodeBox(getAssemblerSim());
			}
		});
		JCheckBoxMenuItem viewToolbar = new JCheckBoxMenuItem("Toolbar");
		view.add(viewToolbar);
		viewToolbar.setSelected(toolbarVisible);
		viewToolbar.setIcon(new ImageIcon(this.getClass().getResource("/icons/toolbar.gif")));
		viewToolbar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_MASK));
		viewToolbar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				toolbarPane.setVisible(!toolbarPane.isVisible());
				repaint();
			}
		});
		JCheckBoxMenuItem viewSevenSegment = new JCheckBoxMenuItem("Segmentanzeige");
		view.add(viewSevenSegment);
		viewSevenSegment.setSelected(true);
		viewSevenSegment.setIcon(new ImageIcon(this.getClass().getResource("/icons/sevensegment.gif")));
		viewSevenSegment.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_MASK));
		viewSevenSegment.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (segmentOn) {
					segmentOn = false;
					anzeige.reset();
				} else {
					segmentOn = true;
					boolean[] tempanzeige = { leds[0].isSelected(), leds[1].isSelected(), leds[2].isSelected(),
							leds[3].isSelected(), leds[4].isSelected(), leds[5].isSelected(), leds[6].isSelected(),
							leds[7].isSelected() };
					anzeige.anzeige1 = tempanzeige;
					// anzeige.anzeige2 = tempanzeige;
					anzeige.repaint();
				}
			}
		});
		// Tools Menue
		JMenu tools = new JMenu("Werkzeuge");
		mbar.add(tools);
		JMenuItem resetram = new JMenuItem("RAM zurücksetzen");
		tools.add(resetram);
		resetram.setIcon(new ImageIcon(this.getClass().getResource("/icons/resetram.gif")));
		resetram.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				for (int k = 0; k < ramdata.length; k++) {
					for (int l = 0; l < ramdata[0].length; l++) {
						ramdata[k][l] = "";
					}
				}
				ramTable.repaint();

			}
		});
		tools.addSeparator();
		JMenuItem settings = new JMenuItem("Einstellungen");
		tools.add(settings);
		settings.setIcon(new ImageIcon(this.getClass().getResource("/icons/settings.gif")));
		settings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				new SettingsDialog(getAssemblerSim());
			}
		});
		// Info Menue
		JMenu infomenu = new JMenu("Hilfe");
		mbar.add(infomenu);
		JMenuItem update = new JMenuItem("Kein Update");
		update.setEnabled(false);
		infomenu.add(update);
		update.setIcon(new ImageIcon(this.getClass().getResource("/icons/update.gif")));
		update.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				String[] run = { "java", "-jar", "Updater.jar" };
				try {
					Runtime.getRuntime().exec(run);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				System.exit(0);
			}
		});
		infomenu.addSeparator();
		JMenuItem docs = new JMenuItem("Befehlssatz");
		infomenu.add(docs);
		docs.setIcon(new ImageIcon(this.getClass().getResource("/icons/docs.gif")));
		docs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_MASK));
		docs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				new DocsDialog();
			}
		});
		JMenuItem doku = new JMenuItem("Dokumentation");
		infomenu.add(doku);
		doku.setIcon(new ImageIcon(this.getClass().getResource("/icons/docu.gif")));
		doku.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				try {
					Desktop.getDesktop().browse(new URI("http://wiki.assemblersim.de"));
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		JMenuItem bugupload = new JMenuItem("Fehler melden");
		infomenu.add(bugupload);
		bugupload.setIcon(new ImageIcon(this.getClass().getResource("/icons/reportbug.gif")));
		bugupload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				new BugreportDialog(codebox.getText(), programversion);
			}
		});
		infomenu.addSeparator();
		JMenuItem donation = new JMenuItem("Spenden");
		infomenu.add(donation);
		donation.setIcon(new ImageIcon(this.getClass().getResource("/icons/donate.gif")));
		donation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				new Donation();
			}
		});
		JMenuItem changelog = new JMenuItem("Changelog");
		infomenu.add(changelog);
		changelog.setIcon(new ImageIcon(this.getClass().getResource("/icons/changelog.gif")));
		changelog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				new ChangelogDialog();
			}
		});
		JMenuItem about = new JMenuItem("Über...");
		infomenu.add(about);
		about.setIcon(new ImageIcon(this.getClass().getResource("/icons/about.gif")));
		about.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		about.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				new AboutDialog(programversion);
			}
		});
		// MENUBAR ENDE

		// TOOLBAR ANFANG

		toolbarPane = new JPanel();
		toolbarPane.setBounds(1, -4, 900, 31);
		cp.add(toolbarPane);
		JToolBar toolbar = new JToolBar("Toolbar");
		toolbar.setFloatable(false);
		toolbar.add(new JLabel(new ImageIcon(this.getClass().getResource("/icons/separator.gif"))));
		JButton toolbarNew = new JButton();
		toolbarNew.setToolTipText("Neu");
		toolbarNew.setMargin(new Insets(0, 4, 0, 4));
		toolbarNew.setBorder(BorderFactory.createEmptyBorder());
		toolbarNew.setIcon(new ImageIcon(this.getClass().getResource("/icons/new.gif")));
		toolbarNew.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				newdoc.doClick();
			}
		});
		toolbar.add(toolbarNew);
		toolbar.addSeparator(new Dimension(4, 16));
		JButton toolbarOpen = new JButton();
		toolbarOpen.setToolTipText("Öffnen");
		toolbarOpen.setMargin(new Insets(0, 4, 0, 4));
		toolbarOpen.setBorder(BorderFactory.createEmptyBorder());
		toolbarOpen.setIcon(new ImageIcon(this.getClass().getResource("/icons/open.gif")));
		toolbarOpen.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				openFromFile.doClick();
			}
		});
		toolbar.add(toolbarOpen);
		toolbar.addSeparator(new Dimension(4, 16));
		JButton toolbarSave = new JButton();
		toolbarSave.setToolTipText("Speichern");
		toolbarSave.setMargin(new Insets(0, 4, 0, 4));
		toolbarSave.setBorder(BorderFactory.createEmptyBorder());
		toolbarSave.setIcon(new ImageIcon(this.getClass().getResource("/icons/save.gif")));
		toolbarSave.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				saveInFile.doClick();
			}
		});
		toolbar.add(toolbarSave);
		toolbar.addSeparator(new Dimension(4, 16));
		JButton toolbarPrint = new JButton();
		toolbarPrint.setToolTipText("Drucken");
		toolbarPrint.setMargin(new Insets(0, 4, 0, 4));
		toolbarPrint.setBorder(BorderFactory.createEmptyBorder());
		toolbarPrint.setIcon(new ImageIcon(this.getClass().getResource("/icons/print.gif")));
		toolbarPrint.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				printdoc.doClick();
			}
		});
		toolbar.add(toolbarPrint);
		toolbar.add(new JLabel(new ImageIcon(this.getClass().getResource("/icons/separator.gif"))));
		JButton toolbarCut = new JButton();
		toolbarCut.setToolTipText("Ausschneiden");
		toolbarCut.setMargin(new Insets(0, 4, 0, 4));
		toolbarCut.setBorder(BorderFactory.createEmptyBorder());
		toolbarCut.setIcon(new ImageIcon(this.getClass().getResource("/icons/cut.gif")));
		toolbarCut.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				menucut.doClick();
			}
		});
		toolbar.add(toolbarCut);
		toolbar.addSeparator(new Dimension(4, 16));
		JButton toolbarCopy = new JButton();
		toolbarCopy.setToolTipText("Kopieren");
		toolbarCopy.setMargin(new Insets(0, 4, 0, 4));
		toolbarCopy.setBorder(BorderFactory.createEmptyBorder());
		toolbarCopy.setIcon(new ImageIcon(this.getClass().getResource("/icons/copy.gif")));
		toolbarCopy.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				menucopy.doClick();
			}
		});
		toolbar.add(toolbarCopy);
		toolbar.addSeparator(new Dimension(4, 16));
		JButton toolbarPaste = new JButton();
		toolbarPaste.setToolTipText("Einfügen");
		toolbarPaste.setMargin(new Insets(0, 4, 0, 4));
		toolbarPaste.setBorder(BorderFactory.createEmptyBorder());
		toolbarPaste.setIcon(new ImageIcon(this.getClass().getResource("/icons/paste.gif")));
		toolbarPaste.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				menupaste.doClick();
			}
		});
		toolbar.add(toolbarPaste);
		toolbar.add(new JLabel(new ImageIcon(this.getClass().getResource("/icons/separator.gif"))));
		JButton toolbarUndo = new JButton();
		toolbarUndo.setToolTipText("Rückgängig");
		toolbarUndo.setMargin(new Insets(0, 4, 0, 4));
		toolbarUndo.setBorder(BorderFactory.createEmptyBorder());
		toolbarUndo.setIcon(new ImageIcon(this.getClass().getResource("/icons/undo.gif")));
		toolbarUndo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				menuundo.doClick();
			}
		});
		toolbar.add(toolbarUndo);
		toolbar.addSeparator(new Dimension(4, 16));
		JButton toolbarRedo = new JButton();
		toolbarRedo.setToolTipText("Wiederholen");
		toolbarRedo.setMargin(new Insets(0, 4, 0, 4));
		toolbarRedo.setBorder(BorderFactory.createEmptyBorder());
		toolbarRedo.setIcon(new ImageIcon(this.getClass().getResource("/icons/redo.gif")));
		toolbarRedo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				menuredo.doClick();
			}
		});
		toolbar.add(toolbarRedo);
		toolbar.add(new JLabel(new ImageIcon(this.getClass().getResource("/icons/separator.gif"))));
		fontsizeSpinner = new JSpinner(new SpinnerNumberModel(12, 8, 24, 1));
		fontsizeSpinner.setToolTipText("Schriftgröße");
		fontsizeSpinner.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				String temp = codebox.getText();
				codebox.setEditorKit(new Syntax((int) fontsizeSpinner.getValue()).getEditorKit());
				codebox.repaint();
				codebox.setText(temp);
				codebox.repaint();
				codebox.setCaretPosition(0);
			}
		});
		toolbar.add(fontsizeSpinner);
		toolbar.add(new JLabel(new ImageIcon(this.getClass().getResource("/icons/separator.gif"))));
		toolbar.addSeparator(new Dimension(4, 16));
		toolbarResumeDebug = new JButton();
		toolbarResumeDebug.setToolTipText("Debug Fortsetzen");
		toolbarResumeDebug.setMargin(new Insets(0, 4, 0, 4));
		toolbarResumeDebug.setBorder(BorderFactory.createEmptyBorder());
		toolbarResumeDebug.setIcon(new ImageIcon(this.getClass().getResource("/icons/resume.gif")));
		toolbarResumeDebug.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (debugMode) {
					pause = false;
				} else {
					debugMode = true;
					jButton_run.doClick();
				}
			}
		});
		toolbar.add(toolbarResumeDebug);

		toolbar.addSeparator(new Dimension(610, 16));

		toolbarPane.add(toolbar);
		toolbarPane.setVisible(toolbarVisible);

		// TOOLBAR ENDE

		jButton_run.setBounds(32, 292, 90, 42);
		jButton_run.setText("Start");
		jButton_run.setFocusable(false);
		jButton_run.setMargin(new Insets(2, 2, 2, 2));
		jButton_run.setBackground(Color.GREEN);
		jButton_run.setIcon(new ImageIcon(this.getClass().getResource("/icons/run.gif")));
		cp.add(jButton_run);
		jButton_stop.setBounds(132, 292, 90, 42);
		jButton_stop.setText("Stop");
		jButton_stop.setFocusable(false);
		jButton_stop.setMargin(new Insets(2, 2, 2, 2));
		jButton_stop.setBackground(Color.RED);
		jButton_stop.setEnabled(false);
		jButton_stop.setIcon(new ImageIcon(this.getClass().getResource("/icons/reset.gif")));
		cp.add(jButton_stop);
		jButton_close.setBounds(232, 292, 90, 42);
		jButton_close.setText("Beenden");
		jButton_close.setFocusable(false);
		jButton_close.setMargin(new Insets(2, 2, 2, 2));
		jButton_close.setIcon(new ImageIcon(this.getClass().getResource("/icons/close.gif")));
		cp.add(jButton_close);
		jButton_reset.setBounds(360, 305, 16, 16);
		jButton_reset.setFocusable(false);
		jButton_reset.setMargin(new Insets(2, 2, 2, 2));
		jButton_reset.setBackground(Color.RED);
		jButton_reset.setToolTipText("Reset");
		jButton_reset.setIcon(new ImageIcon(this.getClass().getResource("/icons/reset_button.gif")));
		cp.add(jButton_reset);
		jButton_taster1.setBounds(496, 198, 41, 33);
		jButton_taster1.setText("P3.2");
		jButton_taster1.setFocusable(false);
		jButton_taster1.setEnabled(false);
		jButton_taster1.setMargin(new Insets(2, 2, 2, 2));
		jButton_taster1.setToolTipText("Taste: \u2192 ");
		cp.add(jButton_taster1);
		jButton_taster2.setBounds(440, 198, 41, 33);
		jButton_taster2.setText("P3.3");
		jButton_taster2.setFocusable(false);
		jButton_taster2.setEnabled(false);
		jButton_taster2.setMargin(new Insets(2, 2, 2, 2));
		jButton_taster2.setToolTipText("Taste: \u2190");
		cp.add(jButton_taster2);
		jButton_debug.setBounds(648, 128, 25, 81);
		jButton_debug.setText("\u300B");
		jButton_debug.setFocusable(false);
		jButton_debug.setMargin(new Insets(2, 2, 2, 2));
		jButton_debug.setToolTipText(
				"<html><div align=\"center\">Debug anzeigen<br />Geschwindigkeit des Programms wird reduziert</div></html>");
		cp.add(jButton_debug);
		jButton_ram.setBounds(250, 378, 81, 25);
		jButton_ram.setText("\uFE3E"); // FE3D
		jButton_ram.setFocusable(false);
		jButton_ram.setMargin(new Insets(2, 2, 2, 2));
		jButton_ram.setToolTipText("RAM anzeigen");
		cp.add(jButton_ram);
		leds[0].setBounds(344, 252, 20, 20);
		leds[0].setOpaque(false);
		leds[0].setEnabled(false);
		leds[0].setIcon(new ImageIcon(this.getClass().getResource("/icons/led_off.gif")));
		leds[0].setDisabledSelectedIcon(new ImageIcon(this.getClass().getResource("/icons/led.gif")));
		cp.add(leds[0]);
		label_P2_0.setBounds(370, 252, 30, 20);
		label_P2_0.setText("P2.0");
		cp.add(label_P2_0);
		leds[1].setBounds(344, 236, 20, 20);
		leds[1].setText("P2.1");
		leds[1].setOpaque(false);
		leds[1].setEnabled(false);
		leds[1].setIcon(new ImageIcon(this.getClass().getResource("/icons/led_off.gif")));
		leds[1].setDisabledSelectedIcon(new ImageIcon(this.getClass().getResource("/icons/led.gif")));
		cp.add(leds[1]);
		label_P2_1.setBounds(370, 236, 30, 20);
		label_P2_1.setText("P2.1");
		cp.add(label_P2_1);
		leds[2].setBounds(344, 220, 60, 20);
		leds[2].setOpaque(false);
		leds[2].setEnabled(false);
		leds[2].setIcon(new ImageIcon(this.getClass().getResource("/icons/led_off.gif")));
		leds[2].setDisabledSelectedIcon(new ImageIcon(this.getClass().getResource("/icons/led.gif")));
		cp.add(leds[2]);
		label_P2_2.setBounds(370, 220, 30, 20);
		label_P2_2.setText("P2.2");
		cp.add(label_P2_2);
		leds[3].setBounds(344, 204, 60, 20);
		leds[3].setOpaque(false);
		leds[3].setEnabled(false);
		leds[3].setIcon(new ImageIcon(this.getClass().getResource("/icons/led_off.gif")));
		leds[3].setDisabledSelectedIcon(new ImageIcon(this.getClass().getResource("/icons/led.gif")));
		cp.add(leds[3]);
		label_P2_3.setBounds(370, 204, 30, 20);
		label_P2_3.setText("P2.3");
		cp.add(label_P2_3);
		leds[4].setBounds(344, 188, 60, 20);
		leds[4].setOpaque(false);
		leds[4].setEnabled(false);
		leds[4].setIcon(new ImageIcon(this.getClass().getResource("/icons/led_off.gif")));
		leds[4].setDisabledSelectedIcon(new ImageIcon(this.getClass().getResource("/icons/led.gif")));
		cp.add(leds[4]);
		label_P2_4.setBounds(370, 188, 30, 20);
		label_P2_4.setText("P2.4");
		cp.add(label_P2_4);
		leds[5].setBounds(344, 172, 60, 20);
		leds[5].setOpaque(false);
		leds[5].setEnabled(false);
		leds[5].setIcon(new ImageIcon(this.getClass().getResource("/icons/led_off.gif")));
		leds[5].setDisabledSelectedIcon(new ImageIcon(this.getClass().getResource("/icons/led.gif")));
		cp.add(leds[5]);
		label_P2_5.setBounds(370, 172, 30, 20);
		label_P2_5.setText("P2.5");
		cp.add(label_P2_5);
		leds[6].setBounds(344, 156, 60, 20);
		leds[6].setOpaque(false);
		leds[6].setEnabled(false);
		leds[6].setIcon(new ImageIcon(this.getClass().getResource("/icons/led_off.gif")));
		leds[6].setDisabledSelectedIcon(new ImageIcon(this.getClass().getResource("/icons/led.gif")));
		cp.add(leds[6]);
		label_P2_6.setBounds(370, 156, 30, 20);
		label_P2_6.setText("P2.6");
		cp.add(label_P2_6);
		leds[7].setBounds(344, 140, 60, 20);
		leds[7].setOpaque(false);
		leds[7].setEnabled(false);
		leds[7].setIcon(new ImageIcon(this.getClass().getResource("/icons/led_off.gif")));
		leds[7].setDisabledSelectedIcon(new ImageIcon(this.getClass().getResource("/icons/led.gif")));
		cp.add(leds[7]);
		label_P2_7.setBounds(370, 140, 30, 20);
		label_P2_7.setText("P2.7");
		cp.add(label_P2_7);
		jLabel_leds.setBounds(352, 108, 52, 25);
		jLabel_leds.setText("LED P2");
		cp.add(jLabel_leds);
		jLabel_taster.setBounds(468, 170, 48, 25);
		jLabel_taster.setText("Buttons");
		cp.add(jLabel_taster);
		jLabel_poti.setBounds(560, 105, 98, 25);
		jLabel_poti.setText("Potentiometer");
		cp.add(jLabel_poti);
		label_debug.setBounds(739, 40, 52, 25);
		label_debug.setText("Debug");
		cp.add(label_debug);
		register[0].setBounds(716, 72, 90, 25);
		cp.add(register[0]);
		register[1].setBounds(716, 96, 90, 25);
		cp.add(register[1]);
		register[2].setBounds(716, 120, 90, 25);
		cp.add(register[2]);
		register[3].setBounds(716, 144, 90, 25);
		cp.add(register[3]);
		register[4].setBounds(716, 168, 90, 25);
		cp.add(register[4]);
		register[5].setBounds(716, 192, 90, 25);
		cp.add(register[5]);
		register[6].setBounds(716, 216, 90, 25);
		cp.add(register[6]);
		register[7].setBounds(716, 240, 90, 25);
		cp.add(register[7]);
		A.setBounds(716, 280, 90, 25);
		cp.add(A);
		B.setBounds(716, 304, 90, 25);
		cp.add(B);
		label_R0.setBounds(692, 72, 20, 25);
		label_R0.setText("R0");
		cp.add(label_R0);
		label_R1.setBounds(692, 96, 20, 25);
		label_R1.setText("R1");
		cp.add(label_R1);
		label_R2.setBounds(692, 120, 20, 25);
		label_R2.setText("R2");
		cp.add(label_R2);
		label_R3.setBounds(692, 144, 20, 25);
		label_R3.setText("R3");
		cp.add(label_R3);
		label_R4.setBounds(692, 168, 20, 25);
		label_R4.setText("R4");
		cp.add(label_R4);
		label_R5.setBounds(692, 192, 20, 25);
		label_R5.setText("R5");
		cp.add(label_R5);
		label_R6.setBounds(692, 216, 20, 25);
		label_R6.setText("R6");
		cp.add(label_R6);
		label_R7.setBounds(692, 240, 20, 25);
		label_R7.setText("R7");
		cp.add(label_R7);
		label_A.setBounds(700, 280, 12, 25);
		label_A.setText("A");
		cp.add(label_A);
		label_B.setBounds(700, 304, 12, 25);
		label_B.setText("B");
		cp.add(label_B);
		label_schalter.setBounds(460, 108, 80, 20);
		label_schalter.setText("Switch P1");
		cp.add(label_schalter);
		register[0].setFocusable(false);
		register[1].setFocusable(false);
		register[2].setFocusable(false);
		register[3].setFocusable(false);
		register[4].setFocusable(false);
		register[5].setFocusable(false);
		register[6].setFocusable(false);
		register[7].setFocusable(false);
		A.setFocusable(false);
		B.setFocusable(false);

		schalter[0].setBounds(450, 135, 10, 10);
		cp.add(schalter[0]);
		schalter[1].setBounds(460, 135, 10, 10);
		cp.add(schalter[1]);
		schalter[2].setBounds(470, 135, 10, 10);
		cp.add(schalter[2]);
		schalter[3].setBounds(480, 135, 10, 10);
		cp.add(schalter[3]);
		schalter[4].setBounds(490, 135, 10, 10);
		cp.add(schalter[4]);
		schalter[5].setBounds(500, 135, 10, 10);
		cp.add(schalter[5]);
		schalter[6].setBounds(510, 135, 10, 10);
		cp.add(schalter[6]);
		schalter[7].setBounds(520, 135, 10, 10);
		cp.add(schalter[7]);
		schalter[0].setToolTipText("P1.0");
		schalter[1].setToolTipText("P1.1");
		schalter[2].setToolTipText("P1.2");
		schalter[3].setToolTipText("P1.3");
		schalter[4].setToolTipText("P1.4");
		schalter[5].setToolTipText("P1.5");
		schalter[6].setToolTipText("P1.6");
		schalter[7].setToolTipText("P1.7");
		schalter[0].setFocusable(false);
		schalter[1].setFocusable(false);
		schalter[2].setFocusable(false);
		schalter[3].setFocusable(false);
		schalter[4].setFocusable(false);
		schalter[5].setFocusable(false);
		schalter[6].setFocusable(false);
		schalter[7].setFocusable(false);

		jSlider_speed.setBounds(140, 350, 100, 27);
		jSlider_speed.setMinorTickSpacing(10);
		jSlider_speed.setMajorTickSpacing(50);
		jSlider_speed.setFocusable(false);
		jSlider_speed.setValue(0);
		jSlider_speed.setInverted(true);
		jSlider_speed.setToolTipText("<html>Geschwindigkeit<br />0 = langsam, 100 = schnell</html>");
		jSlider_speed.setBackground(null);
		cp.add(jSlider_speed);

		label_speed_value.setBounds(250, 355, 30, 15);
		label_speed_value.setText(Integer.toString(100));
		cp.add(label_speed_value);

		label_speed_text.setBounds(32, 355, 150, 15);
		label_speed_text.setText("Geschwindigkeit");
		cp.add(label_speed_text);

		label_ram.setBounds(280, 420, 40, 15);
		label_ram.setText("RAM");
		cp.add(label_ram);

		label_currentcell.setBounds(550, 523, 75, 15);
		label_currentcell.setText("");
		cp.add(label_currentcell);

		ramTable.setBounds(32, 440, 600, 80);
		ramTable.setBackground(null);
		ramTable.setFocusable(false);
		ramTable.setRowSelectionAllowed(false);
		ramTable.setEnabled(false);
		ramTable.repaint();
		cp.add(ramTable);
		ramTable.setBorder(BorderFactory.createLineBorder(null, 1));
		ramTable.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				int row = ramTable.rowAtPoint(e.getPoint());
				int column = ramTable.columnAtPoint(e.getPoint());
				ramTable.setRowSelectionInterval(row, row);
				ramTable.setColumnSelectionInterval(row, column);
				label_currentcell.setText("Aktuell: " + ramcellnames[row][column]);
			}
		});

		jButton_run.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {

				if (jButton_run.getText().equals("Start")) {
					jButton_run.setText("Pause");
					jButton_run.setIcon(new ImageIcon(this.getClass().getResource("/icons/pause.gif")));
					codebox.setFocusable(false);
					jButton_stop.setEnabled(true);
					running = true;
					startThread1();
				} else if (jButton_run.getText().equals("Fortsetzen")) {
					pause = false;
					jButton_run.setText("Pause");
					jButton_run.setIcon(new ImageIcon(this.getClass().getResource("/icons/pause.gif")));
				} else if (jButton_run.getText().equals("Pause")) {
					pause = true;
					jButton_run.setText("Fortsetzen");
					jButton_run.setIcon(new ImageIcon(this.getClass().getResource("/icons/run.gif")));
				}
			}
		});

		jButton_stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				pause = false;
				debugMode = false;
				stopThread1();
				jButton_run.setText("Start");
				jButton_run.setIcon(new ImageIcon(this.getClass().getResource("/icons/run.gif")));

			}
		});

		jButton_close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (JOptionPane.showConfirmDialog(null, "Beenden?\nAlle ungesicherten Daten gehen verloren.", "",
						JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					System.exit(0);
				}

			}
		});
		jButton_reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jButton_stop.doClick(1);
				jButton_run.doClick(1);
			}
		});
		jButton_debug.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (jButton_debug.getText().equals("\u300B")) {
					setSize(frameWidth, getHeight());
					jButton_debug.setText("\u300A");
					jButton_debug.setBounds(848, 128, 25, 81);
				} else {
					setSize(frameWidth - 200, getHeight());
					jButton_debug.setText("\u300B");
					jButton_debug.setBounds(648, 128, 25, 81);
				}
			}// end of if-else
		});
		jButton_ram.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (jButton_ram.getText().equals("\uFE3D")) {
					setSize(getWidth(), frameHeight);
					jButton_ram.setText("\uFE3E");
					jButton_ram.setBounds(250, 378, 81, 25);
				} else {
					setSize(getWidth(), frameHeight + 150);
					jButton_ram.setText("\uFE3D");
					jButton_ram.setBounds(250, 528, 81, 15);
				}
			}// end of if-else
		});

		jSlider_speed.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				speed = jSlider_speed.getValue();
				label_speed_value.setText(Integer.toString(Math.abs(speed - 100)));
			}
		});

		codebox.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER
						|| e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
					undomgr.addToQueue(codebox.getText());

				}
			}
		});

		// Ende Komponenten
		pack();
		cpuspeed();
		addSysTray();
		setVisible(true);
		setSize(frameWidth - 200, frameHeight);
		// Hier Update Anfrage aufrufen

		if (new Update(programversion).checkForUpdate()) {
			update.setEnabled(true);
			update.setText("Neues Update");
			int n = JOptionPane.showConfirmDialog(null, "Neues Update verf\u00fcgbar. Installieren?", "",
					JOptionPane.YES_NO_OPTION);
			if (n == 0) {
				String[] run = { "java", "-jar", "Updater.jar" };
				try {
					Runtime.getRuntime().exec(run);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				System.exit(0);
			}
		}

	} // end of public AssemblerSim

	public class Keyboard implements KeyListener {
		public void keyTyped(KeyEvent e) {

		}

		public void keyReleased(KeyEvent e) {
			e.getKeyCode();

			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				jButton_taster2.setEnabled(false);
				taster2 = false;
				System.out.println("P3.3");
				// Externer Interrupt 1 mit Flankensteuerung loest aus
				if (ex1 == true && ea == true && it1 == true) {
					waitingprog = true;
					runningInterrupt(source, "0013h");
					waitingprog = false;
				}

			}
			if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				jButton_taster1.setEnabled(false);
				taster1 = false;
				System.out.println("P3.2");
				// Externer Interrupt 0 mit Flankensteuerung loest aus
				if (ex0 == true && ea == true && it0 == true) {
					waitingprog = true;
					runningInterrupt(source, "0003h");
					waitingprog = false;
				}

			}

		}

		public void keyPressed(KeyEvent e) {
			e.getKeyCode();

			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				jButton_taster2.setEnabled(true);
				taster2 = true;
				if (ex1 == true && ea == true && it1 == false) {
					waitingprog = true;
					runningInterrupt(source, "0013h");
					waitingprog = false;
				}
			}
			if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				jButton_taster1.setEnabled(true);
				taster1 = true;
				// Pause fuer Taster-Interrupt
				if (ex0 == true && ea == true && it0 == false) {
					waitingprog = true;
					runningInterrupt(source, "0003h");
					waitingprog = false;
				}
			}
		}

	}

	// Anfang Methoden

	public void functionMov(String[] runArgument) {
		// 1 = Ziel, 2 = Wert

		if (runArgument[1].equals("dptr")) {
			dptr = runArgument[2].substring(1);
			return;
		}

		if (runArgument[1].equals("TMOD")) {
			// 00010000b
			gate1 = binaryToBoolean(runArgument[2].charAt(1));
			ct1 = binaryToBoolean(runArgument[2].charAt(2));
			m1_1 = binaryToBoolean(runArgument[2].charAt(3));
			m0_1 = binaryToBoolean(runArgument[2].charAt(4));
			gate0 = binaryToBoolean(runArgument[2].charAt(5));
			ct0 = binaryToBoolean(runArgument[2].charAt(6));
			m1_0 = binaryToBoolean(runArgument[2].charAt(7));
			m0_0 = binaryToBoolean(runArgument[2].charAt(8));
			return;
		}

		if (runArgument[1].equals("TCON")) {
			tf1 = binaryToBoolean(runArgument[2].charAt(1));
			tr1 = binaryToBoolean(runArgument[2].charAt(2));
			tf0 = binaryToBoolean(runArgument[2].charAt(3));
			tr0 = binaryToBoolean(runArgument[2].charAt(4));
			ie1 = binaryToBoolean(runArgument[2].charAt(5));
			it1 = binaryToBoolean(runArgument[2].charAt(6));
			ie0 = binaryToBoolean(runArgument[2].charAt(7));
			it0 = binaryToBoolean(runArgument[2].charAt(8));
			return;
		}

		if (runArgument[1].startsWith("T")) {
			if (runArgument[1].startsWith("TH1")) {
				if (runArgument[2].endsWith("b")) {
					runArgument[2] = runArgument[2].substring(1, runArgument[2].length() - 1);
					th1 = Integer.toHexString(Integer.parseInt(runArgument[2], 2));
				} else if (runArgument[2].endsWith("h")) {
					runArgument[2] = runArgument[2].substring(1, runArgument[2].length() - 1);
					th1 = runArgument[2];
				} else {
					runArgument[2] = runArgument[2].substring(1, runArgument[2].length() - 1);
					th1 = Integer.toHexString(Integer.parseInt(runArgument[2]));
				}
			} else if (runArgument[1].startsWith("TL1")) {
				if (runArgument[2].endsWith("b")) {
					runArgument[2] = runArgument[2].substring(1, runArgument[2].length() - 1);
					tl1 = Integer.toHexString(Integer.parseInt(runArgument[2], 2));
				} else if (runArgument[2].endsWith("h")) {
					runArgument[2] = runArgument[2].substring(1, runArgument[2].length() - 1);
					tl1 = runArgument[2];
				} else {
					runArgument[2] = runArgument[2].substring(1, runArgument[2].length() - 1);
					tl1 = Integer.toHexString(Integer.parseInt(runArgument[2]));
				}
			} else if (runArgument[1].startsWith("TH0")) {
				if (runArgument[2].endsWith("b")) {
					runArgument[2] = runArgument[2].substring(1, runArgument[2].length() - 1);
					th0 = Integer.toHexString(Integer.parseInt(runArgument[2], 2));
				} else if (runArgument[2].endsWith("h")) {
					runArgument[2] = runArgument[2].substring(1, runArgument[2].length() - 1);
					th0 = runArgument[2];
				} else {
					runArgument[2] = runArgument[2].substring(1, runArgument[2].length() - 1);
					th0 = Integer.toHexString(Integer.parseInt(runArgument[2]));
				}
			} else if (runArgument[1].startsWith("TL0")) {
				if (runArgument[2].endsWith("b")) {
					runArgument[2] = runArgument[2].substring(1, runArgument[2].length() - 1);
					tl0 = Integer.toHexString(Integer.parseInt(runArgument[2], 2));
				} else if (runArgument[2].endsWith("h")) {
					runArgument[2] = runArgument[2].substring(1, runArgument[2].length() - 1);
					tl0 = runArgument[2];
				} else {
					runArgument[2] = runArgument[2].substring(1, runArgument[2].length() - 1);
					tl0 = Integer.toHexString(Integer.parseInt(runArgument[2]));
				}
			}
			if (tl1.length() == 1) {
				tl1 = "0" + tl1;
			}
			if (tl1.length() == 3 && tl1.charAt(0) == '0') {
				tl1 = tl1.substring(1);
			}
			String temp = th1 + tl1;
			startValueTimer1 = Integer.parseInt(temp, 16);
			currentValueTimer1 = startValueTimer1;
			if (tl0.length() <= 1) {
				tl0 = "0" + tl0;
			}
			if (tl0.length() == 3 && tl0.charAt(0) == '0') {
				tl0 = tl0.substring(1);
			}
			temp = th0 + tl0;
			startValueTimer0 = Integer.parseInt(temp, 16);
			currentValueTimer0 = startValueTimer0;
			return;
		}

		// Unterscheidung zwischen binaer, hex und dezimal
		char hexOrBin = runArgument[2].charAt(runArgument[2].length() - 1);
		// Hilfsvariable zur ueberpruefung, ob eine Konstante vorliegt oder
		// welches Register/Adresse
		char whichAddress = runArgument[2].charAt(0);
		// ueberpruefen ob der Wert eine Konstante oder eine Adresse/Register
		// ist
		if (whichAddress != '#') {
			// Moeglichkeiten A, B, P2, Rn
			if (whichAddress == 'A') {
				runArgument[2] = A.getText();
			} else if (whichAddress == 'B') {
				runArgument[2] = B.getText();
			} else if (whichAddress == 'P') { // Unterscheidung P1/P2

				if (runArgument[2].charAt(1) == '1') {
					runArgument[2] = p1ToNumber();
				} else if (runArgument[2].charAt(1) == '2') {
					runArgument[2] = p2ToNumber();
				}

			} else if (whichAddress == 'R') {
				// Holt den Wert aus dem Textfeld Rn
				runArgument[2] = register[Integer.parseInt(String.valueOf(runArgument[2].charAt(1)))].getText();
			} else if (whichAddress == '@') {
				// Position in RAM ermitteln
				int x = (Integer.parseInt(register[Integer.parseInt(runArgument[2].substring(2))].getText()) - 48) % 16;
				int y = (Integer.parseInt(register[Integer.parseInt(runArgument[2].substring(2))].getText()) - 48) / 16;
				runArgument[2] = ramdata[y][x];
			}
		} else {
			switch (hexOrBin) {
			case 'b':
				runArgument[2] = runArgument[2].substring(1, runArgument[2].length() - 1);
				runArgument[2] = Integer.toString(Integer.parseInt(runArgument[2], 2));
				break;
			case 'h':
				runArgument[2] = runArgument[2].substring(1, runArgument[2].length() - 1);
				runArgument[2] = Integer.toString(Integer.parseInt(runArgument[2], 16));
				break;
			case '\'': // Für Display, wenn bei charaus ein Buchstabe in den
				// Akku muss
				// Buchstabe wird laut ASCII Tabelle in Zahl umgewandelt
				runArgument[2] = runArgument[2].substring(2, runArgument[2].length() - 1);
				runArgument[2] = Integer.toString((int) runArgument[2].charAt(0));
				break;
			default:
				runArgument[2] = runArgument[2].substring(1);
			} // end of switch
		} // end of if-else

		functionWrite(runArgument[1], runArgument[2]);
	}

	// Methode, um den Wert, der vom Befehl MOV kommt, richtig abzulegen
	public void functionWrite(String ziel, String wert) {
		// x und y falls @R0, R1 usw.
		int x, y;
		switch (ziel) {
		case "R0":
			register[0].setText(wert);
			digitWatcherRegister(0);
			break;
		case "R1":
			register[1].setText(wert);
			digitWatcherRegister(1);
			break;
		case "R2":
			register[2].setText(wert);
			digitWatcherRegister(2);
			break;
		case "R3":
			register[3].setText(wert);
			digitWatcherRegister(3);
			break;
		case "R4":
			register[4].setText(wert);
			digitWatcherRegister(4);
			break;
		case "R5":
			register[5].setText(wert);
			digitWatcherRegister(5);
			break;
		case "R6":
			register[6].setText(wert);
			digitWatcherRegister(6);
			break;
		case "R7":
			register[7].setText(wert);
			digitWatcherRegister(7);
			break;
		case "@R0":
			x = (Integer.parseInt(register[Integer.parseInt(ziel.substring(2))].getText()) - 48) % 16;
			y = (Integer.parseInt(register[Integer.parseInt(ziel.substring(2))].getText()) - 48) / 16;
			ramdata[y][x] = wert;
			ramTable.repaint();
			break;
		case "@R1":
			x = (Integer.parseInt(register[Integer.parseInt(ziel.substring(2))].getText()) - 48) % 16;
			y = (Integer.parseInt(register[Integer.parseInt(ziel.substring(2))].getText()) - 48) / 16;
			ramdata[y][x] = wert;
			ramTable.repaint();
			break;
		case "@R2":
			x = (Integer.parseInt(register[Integer.parseInt(ziel.substring(2))].getText()) - 48) % 16;
			y = (Integer.parseInt(register[Integer.parseInt(ziel.substring(2))].getText()) - 48) / 16;
			ramdata[y][x] = wert;
			ramTable.repaint();
			break;
		case "@R3":
			x = (Integer.parseInt(register[Integer.parseInt(ziel.substring(2))].getText()) - 48) % 16;
			y = (Integer.parseInt(register[Integer.parseInt(ziel.substring(2))].getText()) - 48) / 16;
			ramdata[y][x] = wert;
			ramTable.repaint();
			break;
		case "@R4":
			x = (Integer.parseInt(register[Integer.parseInt(ziel.substring(2))].getText()) - 48) % 16;
			y = (Integer.parseInt(register[Integer.parseInt(ziel.substring(2))].getText()) - 48) / 16;
			ramdata[y][x] = wert;
			ramTable.repaint();
			break;
		case "@R5":
			x = (Integer.parseInt(register[Integer.parseInt(ziel.substring(2))].getText()) - 48) % 16;
			y = (Integer.parseInt(register[Integer.parseInt(ziel.substring(2))].getText()) - 48) / 16;
			ramdata[y][x] = wert;
			ramTable.repaint();
			break;
		case "@R6":
			x = (Integer.parseInt(register[Integer.parseInt(ziel.substring(2))].getText()) - 48) % 16;
			y = (Integer.parseInt(register[Integer.parseInt(ziel.substring(2))].getText()) - 48) / 16;
			ramdata[y][x] = wert;
			ramTable.repaint();
			break;
		case "@R7":
			x = (Integer.parseInt(register[Integer.parseInt(ziel.substring(2))].getText()) - 48) % 16;
			y = (Integer.parseInt(register[Integer.parseInt(ziel.substring(2))].getText()) - 48) / 16;
			ramdata[y][x] = wert;
			ramTable.repaint();
			break;
		case "A":
			A.setText(wert);
			digitWatcherA();
			break;
		case "B":
			B.setText(wert);
			digitWatcherB();
			break;
		case "P2":
			functionSetP2(Integer.toBinaryString(Integer.parseInt(wert)));
			break;
		default:

		} // end of switch
	}

	public void functionMovc(String[][] source, String[] temp) {
		// Suche Tabelle
		int tempjump = 0;
		String marke = dptr;
		marke += ":";
		for (int k = 0; k < source.length; k++) {
			if (source[k][0].equals(marke)) {
				tempjump = k;
			} // end of if
		} // end of for
		tempjump++;
		String[] tabellendaten = source[tempjump][1].split(",");
		// Unterscheidung biaer/hex/dezimal
		if (tabellendaten[Integer.parseInt(A.getText())].endsWith("b")) {
			A.setText(Integer.toString(Integer.parseInt(tabellendaten[Integer.parseInt(A.getText())].substring(0,
					tabellendaten[Integer.parseInt(A.getText())].length() - 1), 2)));
		} else if (tabellendaten[Integer.parseInt(A.getText())].endsWith("h")) {
			A.setText(Integer.toString(Integer.parseInt(tabellendaten[Integer.parseInt(A.getText())].substring(0,
					tabellendaten[Integer.parseInt(A.getText())].length() - 1), 16)));
		} else {
			A.setText(tabellendaten[Integer.parseInt(A.getText())]);
		}

	}

	// bekommt Zahl als Binaerstring
	public void functionSetP2(String binaryInput) {

		for (int m = 0; m < 8; m++) {
			leds[m].setSelected(false);
			if (segmentOn) {
				anzeige.anzeige1[m] = false;
			}
		}

		int temp_number = 0;
		for (int i = binaryInput.length() - 1; i >= 0; i--) {
			if (binaryInput.charAt(i) == '1') {
				leds[temp_number].setSelected(true);
				if (segmentOn) {
					anzeige.anzeige1[temp_number] = true;
					// anzeige.anzeige2[temp_number] = true;
				}
			} // end of if
			temp_number++;
		} // end of for

		anzeige.repaint();
	}

	// Ermittelt aus P2 die angezeigte Dezimalzahl als String
	public String p2ToNumber() {
		String result = "";
		for (int i = 0; i < 8; i++) {
			if (leds[i].isSelected()) {
				result = "1" + result;
			} else {
				result = "0" + result;
			} // end of if-else
		} // end of for
		result = Integer.toString(Integer.parseInt(result, 2));
		return result;
	}

	// Ermittelt aus P1 die angezeigte Dezimalzahl als String
	public String p1ToNumber() {
		String result = "";
		for (int i = 0; i < 8; i++) {
			if (schalter[i].isSelected()) {
				result = "1" + result;
			} else {
				result = "0" + result;
			} // end of if-else

		} // end of for
		result = Integer.toString(Integer.parseInt(result, 2));
		return result;
	}

	public void functionXch(String[] runArgument) {
		int wert = 0;
		if (runArgument[2].charAt(0) == '@') {
			int x = (Integer.parseInt(register[Integer.parseInt(runArgument[2].substring(2))].getText()) - 48) % 16;
			int y = (Integer.parseInt(register[Integer.parseInt(runArgument[2].substring(2))].getText()) - 48) / 16;
			wert = Integer.parseInt(ramdata[y][x]);
			ramdata[y][x] = A.getText();
			ramTable.repaint();
		} else if (runArgument[2].charAt(0) == 'R') {
			wert = Integer.parseInt(register[Integer.parseInt(String.valueOf(runArgument[2].charAt(1)))].getText());
			register[Integer.parseInt(String.valueOf(runArgument[2].charAt(1)))].setText(A.getText());
		}

		A.setText(Integer.toString(wert));
	}

	public void functionXchd(String[] runArgument) {
		int x = (Integer.parseInt(register[Integer.parseInt(runArgument[2].substring(2))].getText()) - 48) % 16;
		int y = (Integer.parseInt(register[Integer.parseInt(runArgument[2].substring(2))].getText()) - 48) / 16;
		String resultSpeicher = Integer.toBinaryString(Integer.parseInt(ramdata[y][x]));
		String resultAkku = Integer.toBinaryString(Integer.parseInt(A.getText()));

		// Falls nicht 8 Bit -> mit Nullen auffuellen
		for (int i = resultSpeicher.length(); i < 8; i++) {
			resultSpeicher = "0" + resultSpeicher;
		}
		for (int i = resultAkku.length(); i < 8; i++) {
			resultAkku = "0" + resultAkku;
		}

		String akkuNibble1 = resultAkku.substring(0, 4);
		String akkuNibble2 = resultAkku.substring(4, 8);
		String speicherNibble1 = resultSpeicher.substring(0, 4);
		String speicherNibble2 = resultSpeicher.substring(4, 8);
		resultAkku = akkuNibble1 + speicherNibble2;
		resultSpeicher = speicherNibble1 + akkuNibble2;
		A.setText(Integer.toString(Integer.parseInt(resultAkku, 2)));
		ramdata[y][x] = Integer.toString(Integer.parseInt(resultSpeicher, 2));
		ramTable.repaint();
	}

	public void functionSetb(String[] runArgument) {
		// 1 = Ziel
		if (runArgument[1].charAt(0) == 'P') {
			// LED
			leds[Integer.parseInt(String.valueOf(runArgument[1].charAt(3)))].setSelected(true);
			anzeige.anzeige1[Integer.parseInt(String.valueOf(runArgument[1].charAt(3)))] = true;
			anzeige.repaint();
		} else {
			// Interrupt Enable Register & Flankensteuerung & Einsprungsadressen
			// der Interrupts
			if (runArgument[1].substring(0, 2).equals("EA")) {
				// Enable all Interrupts
				ea = true;
			} else if (runArgument[1].substring(0, 2).equals("EX")) {
				// Enable External Interrupts
				if (runArgument[1].charAt(2) == '0') {
					ex0 = true;
				} else if (runArgument[1].charAt(2) == '1') {
					ex1 = true;
				}
			} else if (runArgument[1].substring(0, 2).equals("IE")) {
				// Ausloese-Bit
				if (runArgument[1].charAt(2) == '0') {
					ie0 = true;
				} else if (runArgument[1].charAt(2) == '1') {
					ie1 = true;
				}
			} else if (runArgument[1].substring(0, 2).equals("IT")) {
				// Flankensteuerung
				if (runArgument[1].charAt(2) == '0') {
					it0 = true;
				} else if (runArgument[1].charAt(2) == '1') {
					it1 = true;
				}
			} else if (runArgument[1].substring(0, 2).equals("ET")) {
				// Timer einschalten
				if (runArgument[1].charAt(2) == '0') {
					et0 = true;
				} else if (runArgument[1].charAt(2) == '1') {
					et1 = true;
				}
			} else if (runArgument[1].substring(0, 2).equals("TR")) {
				// Timer einschalten
				if (runArgument[1].charAt(2) == '0') {
					tr0 = true;
				} else if (runArgument[1].charAt(2) == '1') {
					tr1 = true;
				}
			}
		}

	}

	public void functionClr(String[] runArgument) {
		// 1 = Ziel //Einzelne LED oder Akku kann geloescht werden
		if (runArgument[1].charAt(0) == 'P') {
			// LED
			leds[Integer.parseInt(String.valueOf(runArgument[1].charAt(3)))].setSelected(false);
			anzeige.anzeige1[Integer.parseInt(String.valueOf(runArgument[1].charAt(3)))] = false;
			anzeige.repaint();
		} else if (runArgument[1].charAt(0) == 'A') {
			// Akku
			A.setText("");
		} else {
			// Interrupt Enable Register & Flankensteuerung & Einsprungsadressen
			// der Interrupts
			if (runArgument[1].substring(0, 2).equals("EA")) {
				// Enable all Interrupts
				ea = false;
			} else if (runArgument[1].substring(0, 2).equals("EX")) {
				// Enable External Interrupts
				if (runArgument[1].charAt(2) == '0') {
					ex0 = false;
				} else if (runArgument[1].charAt(2) == '1') {
					ex1 = false;
				}
			} else if (runArgument[1].substring(0, 2).equals("IE")) {
				// Ausloese-Bit
				if (runArgument[1].charAt(2) == '0') {
					ie0 = false;
				} else if (runArgument[1].charAt(2) == '1') {
					ie1 = false;
				}
			} else if (runArgument[1].substring(0, 2).equals("IT")) {
				// Flankensteuerung
				if (runArgument[1].charAt(2) == '0') {
					it0 = false;
				} else if (runArgument[1].charAt(2) == '1') {
					it1 = false;
				}
			} else if (runArgument[1].substring(0, 2).equals("ET")) {
				// Timer einschalten
				if (runArgument[1].charAt(2) == '0') {
					et0 = false;
				} else if (runArgument[1].charAt(2) == '1') {
					et1 = false;
				}
			} else if (runArgument[1].substring(0, 2).equals("TR")) {
				// Timer einschalten
				if (runArgument[1].charAt(2) == '0') {
					tr0 = false;
				} else if (runArgument[1].charAt(2) == '1') {
					tr1 = false;
				}
			}
		}
	}

	public void functionCpl(String[] runArgument) {
		// Unterscheidung Akku oder einzelnes Bit
		if (runArgument[1].charAt(0) == 'A') {// Akku
			// Beim Akku muss erst das Bit aufgefuellt werden
			String akku = Integer.toBinaryString(Integer.parseInt(A.getText()));
			for (int i = akku.length(); i < 8; i++) {
				akku = "0" + akku;
			}
			// String einzelne Bits komplementieren
			String result = "";
			for (int i = 0; i < 8; i++) {
				if (akku.charAt(i) == '0') {
					result += '1';
				} else if (akku.charAt(i) == '1') {
					result += '0';
				}
			}
			A.setText(Integer.toString(Integer.parseInt(result, 2)));

		} else if (runArgument[1].charAt(0) == 'P') {// Einzelnes Bit von P2
			boolean status = leds[Integer.parseInt(String.valueOf(runArgument[1].charAt(3)))].isSelected();
			if (status) {
				leds[Integer.parseInt(String.valueOf(runArgument[1].charAt(3)))].setSelected(false);
			} else {
				leds[Integer.parseInt(String.valueOf(runArgument[1].charAt(3)))].setSelected(true);
			}
		} else {
			// Interrupt Enable Register & Flankensteuerung & Einsprungsadressen
			// der Interrupts
			if (runArgument[1].substring(0, 2).equals("EA")) {
				// Enable all Interrupts
				ea = !ea;
			} else if (runArgument[1].substring(0, 2).equals("EX")) {
				// Enable External Interrupts
				if (runArgument[1].charAt(2) == '0') {
					ex0 = !ex0;
				} else if (runArgument[1].charAt(2) == '1') {
					ex1 = !ex1;
				}
			} else if (runArgument[1].substring(0, 2).equals("IE")) {
				// Ausloese-Bit
				if (runArgument[1].charAt(2) == '0') {
					ie0 = !ie0;
				} else if (runArgument[1].charAt(2) == '1') {
					ie1 = !ie1;
				}
			} else if (runArgument[1].substring(0, 2).equals("IT")) {
				// Flankensteuerung
				if (runArgument[1].charAt(2) == '0') {
					it0 = !it0;
				} else if (runArgument[1].charAt(2) == '1') {
					it1 = !it1;
				}
			}
		}
	}

	public void functionAnl(String[] runArgument) {

		// Hinterer Teil: Konstante/Register/Adresse/Akku -> In Binaerzahl
		// umwandeln und bereitstellen
		char konstante = runArgument[2].charAt(0);
		if (konstante == '#') {
			// Unterscheidung Hex, Bin und Dezimal
			char hexOrBin = runArgument[2].charAt(runArgument[2].length() - 1);
			if (hexOrBin == 'b') {
				runArgument[2] = runArgument[2].substring(1, runArgument[2].length() - 1);
			} else if (hexOrBin == 'h') {
				runArgument[2] = Integer
						.toBinaryString(Integer.parseInt(runArgument[2].substring(1, runArgument[2].length() - 1), 16));
			} else {
				runArgument[2] = Integer.toBinaryString(Integer.parseInt(runArgument[2].substring(1)));
			}
		} else if (konstante == 'R') {
			runArgument[2] = Integer.toBinaryString(
					Integer.parseInt(register[Integer.parseInt(String.valueOf(runArgument[2].charAt(1)))].getText()));
		} else if (konstante == 'P') {
			char whichP = runArgument[2].charAt(1);
			if (whichP == '1') {
				runArgument[2] = Integer.toBinaryString(Integer.parseInt(p1ToNumber()));
			} else if (whichP == '2') {
				runArgument[2] = Integer.toBinaryString(Integer.parseInt(p2ToNumber()));
			}
		} else if (konstante == 'A') {
			runArgument[2] = Integer.toBinaryString(Integer.parseInt(A.getText()));
		} else if (konstante == '@') {
			int x = (Integer.parseInt(register[Integer.parseInt(runArgument[2].substring(2))].getText()) - 48) % 16;
			int y = (Integer.parseInt(register[Integer.parseInt(runArgument[2].substring(2))].getText()) - 48) / 16;
			int wert = Integer.parseInt(ramdata[y][x]);
			runArgument[2] = Integer.toBinaryString(wert);
		}

		// Byte auffuellen

		for (int j = runArgument[2].length(); j < 8; j++) {
			runArgument[2] = "0" + runArgument[2];
		}

		// Ziel und Zahl im Ziel binaer umwandeln und bereitstellen
		String result = "";
		if (runArgument[1].equals("A")) {
			runArgument[1] = Integer.toBinaryString(Integer.parseInt(A.getText()));
			// Byte auffuellen
			for (int j = runArgument[1].length(); j < 8; j++) {
				runArgument[1] = "0" + runArgument[1];
			}
			for (int b = 0; b < 8; b++) {
				if (runArgument[2].charAt(b) == '1' && runArgument[1].charAt(b) == '1') {
					result = result + "1";
				} else {
					result = result + "0";
				}
			}
			A.setText(Integer.toString(Integer.parseInt(result, 2)));
		} else if (runArgument[1].equals("P2")) {
			runArgument[1] = Integer.toBinaryString(Integer.parseInt(p1ToNumber()));
			// Byte auffuellen
			for (int j = runArgument[1].length(); j < 8; j++) {
				runArgument[1] = "0" + runArgument[1];
			}

			for (int b = 0; b < 8; b++) {
				if (runArgument[2].charAt(b) == '1' && runArgument[1].charAt(b) == '1') {
					result = result + "1";
				} else {
					result = result + "0";
				}
			}
			functionSetP2(result);
		}
	}

	public void functionOrl(String[] runArgument) {

		// Hinterer Teil: Konstante/Register/Adresse/Akku -> In Binaerzahl
		// umwandeln und bereitstellen
		char konstante = runArgument[2].charAt(0);
		if (konstante == '#') {
			// Unterscheidung Hex, Bin und Dezimal
			char hexOrBin = runArgument[2].charAt(runArgument[2].length() - 1);
			if (hexOrBin == 'b') {
				runArgument[2] = runArgument[2].substring(1, runArgument[2].length() - 1);
			} else if (hexOrBin == 'h') {
				runArgument[2] = Integer
						.toBinaryString(Integer.parseInt(runArgument[2].substring(1, runArgument[2].length() - 1), 16));
			} else {
				runArgument[2] = Integer.toBinaryString(Integer.parseInt(runArgument[2].substring(1)));
			}
		} else if (konstante == 'R') {
			runArgument[2] = Integer.toBinaryString(
					Integer.parseInt(register[Integer.parseInt(String.valueOf(runArgument[2].charAt(1)))].getText()));
		} else if (konstante == 'P') {
			char whichP = runArgument[2].charAt(1);
			if (whichP == '1') {
				runArgument[2] = Integer.toBinaryString(Integer.parseInt(p1ToNumber()));
			} else if (whichP == '2') {
				runArgument[2] = Integer.toBinaryString(Integer.parseInt(p2ToNumber()));
			}
		} else if (konstante == 'A') {
			runArgument[2] = Integer.toBinaryString(Integer.parseInt(A.getText()));
		} else if (konstante == '@') {
			int x = (Integer.parseInt(register[Integer.parseInt(runArgument[2].substring(2))].getText()) - 48) % 16;
			int y = (Integer.parseInt(register[Integer.parseInt(runArgument[2].substring(2))].getText()) - 48) / 16;
			int wert = Integer.parseInt(ramdata[y][x]);
			runArgument[2] = Integer.toBinaryString(wert);
		}

		// Byte auffuellen

		for (int j = runArgument[2].length(); j < 8; j++) {
			runArgument[2] = "0" + runArgument[2];
		}

		// Ziel und Zahl im Ziel binaer umwandeln und bereitstellen
		String result = "";
		if (runArgument[1].equals("A")) {
			runArgument[1] = Integer.toBinaryString(Integer.parseInt(A.getText()));
			// Byte auffuellen
			for (int j = runArgument[1].length(); j < 8; j++) {
				runArgument[1] = "0" + runArgument[1];
			}
			for (int b = 0; b < 8; b++) {
				if (runArgument[2].charAt(b) == '1' || runArgument[1].charAt(b) == '1') {
					result = result + "1";
				} else {
					result = result + "0";
				}
			}
			A.setText(Integer.toString(Integer.parseInt(result, 2)));
		} else if (runArgument[1].equals("P2")) {
			runArgument[1] = Integer.toBinaryString(Integer.parseInt(p1ToNumber()));
			// Byte auffuellen
			for (int j = runArgument[1].length(); j < 8; j++) {
				runArgument[1] = "0" + runArgument[1];
			}

			for (int b = 0; b < 8; b++) {
				if (runArgument[2].charAt(b) == '1' || runArgument[1].charAt(b) == '1') {
					result = result + "1";
				} else {
					result = result + "0";
				}
			}
			functionSetP2(result);
		}
	}

	public void functionXrl(String[] runArgument) {

		// Hinterer Teil: Konstante/Register/Adresse/Akku -> In Binaerzahl
		// umwandeln und bereitstellen
		char konstante = runArgument[2].charAt(0);
		if (konstante == '#') {
			// Unterscheidung Hex, Bin und Dezimal
			char hexOrBin = runArgument[2].charAt(runArgument[2].length() - 1);
			if (hexOrBin == 'b') {
				runArgument[2] = runArgument[2].substring(1, runArgument[2].length() - 1);
			} else if (hexOrBin == 'h') {
				runArgument[2] = Integer
						.toBinaryString(Integer.parseInt(runArgument[2].substring(1, runArgument[2].length() - 1), 16));
			} else {
				runArgument[2] = Integer.toBinaryString(Integer.parseInt(runArgument[2].substring(1)));
			}
		} else if (konstante == 'R') {
			runArgument[2] = Integer.toBinaryString(
					Integer.parseInt(register[Integer.parseInt(String.valueOf(runArgument[2].charAt(1)))].getText()));
		} else if (konstante == 'P') {
			char whichP = runArgument[2].charAt(1);
			if (whichP == '1') {
				runArgument[2] = Integer.toBinaryString(Integer.parseInt(p1ToNumber()));
			} else if (whichP == '2') {
				runArgument[2] = Integer.toBinaryString(Integer.parseInt(p2ToNumber()));
			}
		} else if (konstante == 'A') {
			runArgument[2] = Integer.toBinaryString(Integer.parseInt(A.getText()));
		} else if (konstante == '@') {
			int x = (Integer.parseInt(register[Integer.parseInt(runArgument[2].substring(2))].getText()) - 48) % 16;
			int y = (Integer.parseInt(register[Integer.parseInt(runArgument[2].substring(2))].getText()) - 48) / 16;
			int wert = Integer.parseInt(ramdata[y][x]);
			runArgument[2] = Integer.toBinaryString(wert);
		}

		// Byte auffuellen

		for (int j = runArgument[2].length(); j < 8; j++) {
			runArgument[2] = "0" + runArgument[2];
		}

		// Ziel und Zahl im Ziel binaer umwandeln und bereitstellen
		String result = "";
		if (runArgument[1].equals("A")) {
			runArgument[1] = Integer.toBinaryString(Integer.parseInt(A.getText()));
			// Byte auffuellen
			for (int j = runArgument[1].length(); j < 8; j++) {
				runArgument[1] = "0" + runArgument[1];
			}
			for (int b = 0; b < 8; b++) {
				if (runArgument[2].charAt(b) == runArgument[1].charAt(b)) {
					result = result + "0";
				} else {
					result = result + "1";
				}
			}
			A.setText(Integer.toString(Integer.parseInt(result, 2)));
		} else if (runArgument[1].equals("P2")) {
			runArgument[1] = Integer.toBinaryString(Integer.parseInt(p1ToNumber()));
			// Byte auffuellen
			for (int j = runArgument[1].length(); j < 8; j++) {
				runArgument[1] = "0" + runArgument[1];
			}

			for (int b = 0; b < 8; b++) {
				if (runArgument[2].charAt(b) == runArgument[1].charAt(b)) {
					result = result + "0";
				} else {
					result = result + "1";
				}
			}
			functionSetP2(result);
		}
	}

	public int functionSjmp(String[][] source, String[] temp, int currentline) {
		// 1 = Ziel
		int tempjump = 0;
		String marke = temp[1];
		marke += ":";
		for (int k = 0; k < source.length; k++) {
			if (source[k][0].equals(marke)) {
				tempjump = k;
			} // end of if
		} // end of for
		return tempjump;
	}

	public int functionJz(String[][] source, String[] temp, int currentline) {
		int tempjump = 0;
		if (A.getText().equals("0")) {
			String marke = temp[1];
			marke += ":";
			for (int k = 0; k < source.length; k++) {
				if (source[k][0].equals(marke)) {
					tempjump = k;
				} // end of if
			} // end of for
		} else {
			tempjump = currentline;
		} // end of if-else
		return tempjump;
	}

	public int functionJnz(String[][] source, String[] temp, int currentline) {
		int tempjump = 0;
		if (!A.getText().equals("0")) {
			String marke = temp[1];
			marke += ":";
			for (int k = 0; k < source.length; k++) {
				if (source[k][0].equals(marke)) {
					tempjump = k;
				} // end of if
			} // end of for
		} else {
			tempjump = currentline;
		} // end of if-else
		return tempjump;
	}

	public int functionJb(String[][] source, String[] temp, int currentline) {
		// 1 = taster, 2 = Sprungziel
		int tempjump = currentline;

		if (temp[1].startsWith("P2")) { // Bits der LEDs werden abgefragt
			int whichLed = Integer.parseInt(String.valueOf(temp[1].charAt(3)));
			if (leds[whichLed].isSelected() == true) {
				String marke = temp[2];
				marke += ":";
				for (int k = 0; k < source.length; k++) {
					if (source[k][0].equals(marke)) {
						tempjump = k;
					} // end of if
				} // end of for
			} // end of if

		} else if (temp[1].startsWith("P1")) {
			int whichSwitch = Integer.parseInt(String.valueOf(temp[1].charAt(3)));
			if (schalter[whichSwitch].isSelected() == true) {
				String marke = temp[2];
				marke += ":";
				for (int k = 0; k < source.length; k++) {
					if (source[k][0].equals(marke)) {
						tempjump = k;
					} // end of if
				} // end of for
			} // end of if
		}

		else if (temp[1].startsWith("P3")) { // Bits der taster werden abgefragt
			if (temp[1].equals("P3.2")) {
				if (taster1) {
					String marke = temp[2];
					marke += ":";
					for (int k = 0; k < source.length; k++) {
						if (source[k][0].equals(marke)) {
							tempjump = k;
						} // end of if
					} // end of for
				} // end of if
			} else if (temp[1].equals("P3.3")) {
				if (taster2) {
					String marke = temp[2];
					marke += ":";
					for (int k = 0; k < source.length; k++) {
						if (source[k][0].equals(marke)) {
							tempjump = k;
						} // end of if
					} // end of for
				} // end of if
			}
		}
		return tempjump;
	}

	public int functionJnb(String[][] source, String[] temp, int currentline) {
		// 1 = taster, 2 = Sprungziel
		int tempjump = currentline;

		if (temp[1].startsWith("P2")) { // Bits der LEDs werden abgefragt
			int whichLed = Integer.parseInt(String.valueOf(temp[1].charAt(3)));
			if (leds[whichLed].isSelected() == false) {
				String marke = temp[2];
				marke += ":";
				for (int k = 0; k < source.length; k++) {
					if (source[k][0].equals(marke)) {
						tempjump = k;
					} // end of if
				} // end of for
			} // end of if

		} else if (temp[1].startsWith("P1")) {
			int whichSwitch = Integer.parseInt(String.valueOf(temp[1].charAt(3)));
			if (schalter[whichSwitch].isSelected() == false) {
				String marke = temp[2];
				marke += ":";
				for (int k = 0; k < source.length; k++) {
					if (source[k][0].equals(marke)) {
						tempjump = k;
					} // end of if
				} // end of for
			} // end of if
		}

		else if (temp[1].startsWith("P3")) { // Bits der taster werden abgefragt
			if (temp[1].equals("P3.2")) {
				if (!taster1) {
					String marke = temp[2];
					marke += ":";
					for (int k = 0; k < source.length; k++) {
						if (source[k][0].equals(marke)) {
							tempjump = k;
						} // end of if
					} // end of for
				} // end of if
			} else if (temp[1].equals("P3.3")) {
				if (!taster2) {
					String marke = temp[2];
					marke += ":";
					for (int k = 0; k < source.length; k++) {
						if (source[k][0].equals(marke)) {
							tempjump = k;
						} // end of if
					} // end of for
				} // end of if
			}
		}
		return tempjump;
	}

	public int functionDjnz(String[][] source, String[] temp, int currentline) {
		// 1 = Wert, 2 = Sprungziel | Rn und P2 koennen abgerufen werden
		int tempjump = 0;
		// Register bzw Adresse ermitteln
		int whichRegister = 0;
		if (temp[1].charAt(0) == 'R') { // Register
			// Holt Registernummer
			whichRegister = Integer.parseInt(Character.toString(temp[1].charAt(1)));
			// Register mit 1 subtrahieren
			register[whichRegister].setText(Integer.toString(Integer.parseInt(register[whichRegister].getText()) - 1));
			digitWatcherRegister(whichRegister);
			// ggf. springen
			if (!register[whichRegister].getText().equals("0")) {
				String marke = temp[2];
				marke += ":";
				for (int k = 0; k < source.length; k++) {
					if (source[k][0].equals(marke)) {
						tempjump = k;
					} // end of if
				} // end of for
			} else {
				tempjump = currentline;
			} // end of if-else
		} else if (temp[1].charAt(0) == 'P') { // P2
			// P2 mit 1 subtrahieren
			functionSetP2(Integer.toBinaryString(Integer.parseInt(p2ToNumber()) - 1));
			// ggf. springen
			if (!p2ToNumber().equals("0")) {
				String marke = temp[2];
				marke += ":";
				for (int k = 0; k < source.length; k++) {
					if (source[k][0].equals(marke)) {
						tempjump = k;
					} // end of if
				} // end of for
			} else {
				tempjump = currentline;
			} // end of if-else
		}
		return tempjump;
	}

	public int functionCjne(String[][] source, String[] temp, int currentline) {
		// 1 = Register/Adresse, 2 = Konstante, 3 = Sprungziel
		String konstantetemp = temp[2].substring(1);
		// Hex, Bin, oder Dezimal Unterscheidung
		if (konstantetemp.endsWith("b")) {
			konstantetemp = konstantetemp.substring(0, konstantetemp.length() - 1);
			konstantetemp = Integer.toString(Integer.parseInt(konstantetemp, 2));
		} else if (konstantetemp.endsWith("h")) {
			konstantetemp = konstantetemp.substring(0, konstantetemp.length() - 1);
			konstantetemp = Integer.toString(Integer.parseInt(konstantetemp, 16));
		}
		int konstante = Integer.parseInt(konstantetemp);
		int tempjump = 0;
		if (temp[1].charAt(0) == 'R') { // Register
			if (Integer
					.parseInt(register[Integer.parseInt(String.valueOf(temp[1].charAt(1)))].getText()) != konstante) {
				String marke = temp[3];
				marke += ":";
				for (int k = 0; k < source.length; k++) {
					if (source[k][0].equals(marke)) {
						tempjump = k;
					} // end of if
				} // end of for
			} else {
				tempjump = currentline;
			} // end of if-else
		} else if (temp[1].charAt(0) == 'A') { // Akku
			if (Integer.parseInt(A.getText()) != konstante) {
				String marke = temp[3];
				marke += ":";
				for (int k = 0; k < source.length; k++) {
					if (source[k][0].equals(marke)) {
						tempjump = k;
					} // end of if
				} // end of for
			} else {
				tempjump = currentline;
			} // end of if-else
		} else if (temp[1].charAt(0) == '@') { // Indir. Adresse
			int x = (Integer.parseInt(register[Integer.parseInt(temp[1].substring(2))].getText()) - 48) % 16;
			int y = (Integer.parseInt(register[Integer.parseInt(temp[1].substring(2))].getText()) - 48) / 16;
			int wert = Integer.parseInt(ramdata[y][x]);
			if (wert != konstante) {
				String marke = temp[3];
				marke += ":";
				for (int k = 0; k < source.length; k++) {
					if (source[k][0].equals(marke)) {
						tempjump = k;
					} // end of if
				} // end of for
			} else {
				tempjump = currentline;
			} // end of if-else
		}
		return tempjump;
	}

	public void functionInc(String[] temp) {
		if (temp[1].charAt(0) == 'R') { // Register
			register[Integer.parseInt(String.valueOf(temp[1].charAt(1)))].setText(Integer.toString(
					Integer.parseInt(register[Integer.parseInt(String.valueOf(temp[1].charAt(1)))].getText()) + 1));
			digitWatcherRegister(Integer.parseInt(String.valueOf(temp[1].charAt(1))));
		} else if (temp[1].charAt(0) == 'A') { // Akku
			A.setText(Integer.toString(Integer.parseInt(A.getText()) + 1));
			digitWatcherA();
		} else if (temp[1].charAt(0) == 'P') {
			if ((Integer.parseInt(p2ToNumber()) + 1) < 256) {
				functionSetP2(Integer.toBinaryString(Integer.parseInt(p2ToNumber()) + 1));
			} else if ((Integer.parseInt(p2ToNumber()) + 1) == 256) {
				functionSetP2(Integer.toBinaryString(0));
			}
		} else if (temp[1].charAt(0) == '@') {
			int x = (Integer.parseInt(register[Integer.parseInt(temp[1].substring(2))].getText()) - 48) % 16;
			int y = (Integer.parseInt(register[Integer.parseInt(temp[1].substring(2))].getText()) - 48) / 16;
			int wert = Integer.parseInt(ramdata[y][x]) + 1;
			wert = digitWatcherRam(wert);
			ramdata[y][x] = Integer.toString(wert);
			ramTable.repaint();
		}
	}

	public void functionDec(String[] temp) {
		if (temp[1].charAt(0) == 'R') { // Register
			register[Integer.parseInt(String.valueOf(temp[1].charAt(1)))].setText(Integer.toString(
					Integer.parseInt(register[Integer.parseInt(String.valueOf(temp[1].charAt(1)))].getText()) - 1));
			digitWatcherRegister(Integer.parseInt(String.valueOf(temp[1].charAt(1))));
		} else if (temp[1].charAt(0) == 'A') { // Akku
			A.setText(Integer.toString(Integer.parseInt(A.getText()) - 1));
			digitWatcherA();
		} else if (temp[1].charAt(0) == 'P') {
			if ((Integer.parseInt(p2ToNumber()) - 1) >= 0) {
				functionSetP2(Integer.toBinaryString(Integer.parseInt(p2ToNumber()) - 1));
			} else if ((Integer.parseInt(p2ToNumber()) - 1) == (-1)) {
				functionSetP2(Integer.toBinaryString(255));
			}
		} else if (temp[1].charAt(0) == '@') {
			int x = (Integer.parseInt(register[Integer.parseInt(temp[1].substring(2))].getText()) - 48) % 16;
			int y = (Integer.parseInt(register[Integer.parseInt(temp[1].substring(2))].getText()) - 48) / 16;
			int wert = Integer.parseInt(ramdata[y][x]) - 1;
			wert = digitWatcherRam(wert);
			ramdata[y][x] = Integer.toString(wert);
			ramTable.repaint();
		}
	}

	public void functionAdd(String[] temp) {

		// ueberprueft zuerst ob eine konstante oder ein Register/Adresse/RAM
		// zum
		// Akku addiert wird
		char hexOrBin = temp[2].charAt(temp[2].length() - 1);
		char whichAddress = temp[2].charAt(0);
		if (whichAddress != '#') {// Adresse/Register
			if (whichAddress == 'R') {
				A.setText(Integer.toString(
						Integer.parseInt(register[Integer.parseInt(String.valueOf(temp[2].charAt(1)))].getText())
								+ Integer.parseInt(A.getText())));
				digitWatcherA();
			} else if (whichAddress == 'P') {
				if (temp[2].charAt(1) == '1') {
					A.setText(Integer.toString(Integer.parseInt(A.getText()) + Integer.parseInt(p1ToNumber())));
					digitWatcherA();
				} else if (temp[2].charAt(1) == '2') {
					A.setText(Integer.toString(Integer.parseInt(A.getText()) + Integer.parseInt(p2ToNumber())));
					digitWatcherA();
				}
			} else if (whichAddress == '@') {
				int x = (Integer.parseInt(register[Integer.parseInt(temp[2].substring(2))].getText()) - 48) % 16;
				int y = (Integer.parseInt(register[Integer.parseInt(temp[2].substring(2))].getText()) - 48) / 16;
				int wert = Integer.parseInt(ramdata[y][x]) + Integer.parseInt(A.getText());
				A.setText(Integer.toString(wert));
				digitWatcherA();
			}
		} else {// Konstante
			switch (hexOrBin) {
			case 'b':
				A.setText(Integer.toString(Integer.parseInt(temp[2].substring(1, temp[2].length() - 1), 2)
						+ Integer.parseInt(A.getText())));
				digitWatcherA();
				break;
			case 'h':
				A.setText(Integer.toString(Integer.parseInt(temp[2].substring(1, temp[2].length() - 1), 16)
						+ Integer.parseInt(A.getText())));
				digitWatcherA();
				break;
			default:
				A.setText(Integer.toString(
						Integer.parseInt(temp[2].substring(1, temp[2].length())) + Integer.parseInt(A.getText())));
				digitWatcherA();
			}

		} // end of switch
	}

	public void functionSwap() {
		String result = Integer.toBinaryString(Integer.parseInt(A.getText()));
		// Falls nicht 8 Bit -> mit Nullen auffuellen
		for (int i = result.length(); i < 8; i++) {
			result = "0" + result;
		}
		String nibble1 = result.substring(0, 4);
		String nibble2 = result.substring(4, 8);
		result = nibble2 + nibble1;
		A.setText(Integer.toString(Integer.parseInt(result, 2)));
	}

	public void functionMul(String[] temp) {
		int a = Integer.parseInt(A.getText());
		int b = Integer.parseInt(B.getText());
		int mul = a * b;
		String result = Integer.toBinaryString(mul);

		String tempstring = "";
		while (result.length() < 16) {
			tempstring = result;
			result = "0" + tempstring;
		}
		B.setText(Integer.toString(Integer.parseInt(result.substring(0, 8), 2)));
		A.setText(Integer.toString(Integer.parseInt(result.substring(8, 16), 2)));
	}

	public void functionDiv(String[] temp) {
		int a = Integer.parseInt(A.getText());
		int b = Integer.parseInt(B.getText());
		int div = a / b;
		int modulo = a % b;
		A.setText(Integer.toString(div));
		B.setText(Integer.toString(modulo));
	}

	public void functionRL() {
		String temp = Integer.toBinaryString(Integer.parseInt(A.getText()));
		String fuehrendeNullen = "";
		if (temp.length() < 8) {
			for (int i = 0; i < 8 - temp.length(); i++) {
				fuehrendeNullen += "0";
			} // end of for
			fuehrendeNullen += temp;
			temp = fuehrendeNullen;
		}
		String result = temp.substring(1) + String.valueOf(temp.charAt(0));
		A.setText(Integer.toString(Integer.parseInt(result, 2)));
	}

	public void functionRR() {
		String temp = Integer.toBinaryString(Integer.parseInt(A.getText()));
		String fuehrendeNullen = "";
		if (temp.length() < 8) {
			for (int i = 0; i < 8 - temp.length(); i++) {
				fuehrendeNullen += "0";
			} // end of for
			fuehrendeNullen += temp;
			temp = fuehrendeNullen;
		}
		String result = String.valueOf(temp.charAt(7)) + temp.substring(0, 7);
		A.setText(Integer.toString(Integer.parseInt(result, 2)));
	}

	@SuppressWarnings("static-access")
	public void functionLcall(String[][] source, String[] temp) {

		boolean isAKeyword = lcallForDisplay(temp);

		if (isAKeyword) {
			return;
		}

		if (temp[1].equals("Ain0")) {
			A.setText(Integer.toString(poti.getValue()));
			repaint();
			return;
		}

		int zeileunterprogramm = 0; // aktuelle Zeile
		// 1 = Zielmarke
		String marke = temp[1];
		marke += ":";
		// Suche Startzeile des Unterprogramms
		int unterprogrammstartzeile = 0;
		for (int k = 0; k < source.length; k++) {
			if (source[k][0].equals(marke)) {
				unterprogrammstartzeile = k;
				break;
			} // end of if
		} // end of for

		// Suche letzte Zeile des Unterprogramms
		int unterprogrammendzeile = 0;
		for (int m = unterprogrammstartzeile; m < source.length; m++) {
			if (source[m][0].equals("ret")) {
				unterprogrammendzeile = m;
				break;
			} // end of if
		} // end of for

		String[][] sourceunterprogramm = source;

		boolean returned = false;
		for (zeileunterprogramm = unterprogrammstartzeile; zeileunterprogramm <= unterprogrammendzeile; zeileunterprogramm++) {
			temp = InputToSource.getLine(sourceunterprogramm, zeileunterprogramm); // externes
																					// Auslesen
																					// der
																					// Zeile

			synchronized (thread1) {
				if (debugMode) {
					codebox.setLineHighlight(zeileunterprogramm + 1);
					codebox.setCaretLine(zeileunterprogramm + 1);
				}
				while (waitingprog) {
					try {
						thread1.sleep(1);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				while (pause) {
					try {
						thread1.sleep(1);
					} catch (Exception e) {
					}
				}
			}
			timerCountdown();

			switch (temp[0]) {
			case "mov":
				functionMov(temp);
				break;
			case "movc":
				functionMovc(source, temp);
				break;
			case "setb":
				functionSetb(temp);
				break;
			case "clr":
				functionClr(temp);
				break;
			case "cpl":
				functionCpl(temp);
				break;
			case "anl":
				functionAnl(temp);
				break;
			case "orl":
				functionOrl(temp);
				break;
			case "xrl":
				functionXrl(temp);
				break;
			case "xch":
				functionXch(temp);
				break;
			case "xchd":
				functionXchd(temp);
				break;
			case "sjmp":
				zeileunterprogramm = functionSjmp(sourceunterprogramm, temp, zeileunterprogramm);
				break;
			case "jz":
				zeileunterprogramm = functionJz(sourceunterprogramm, temp, zeileunterprogramm);
				break;
			case "jnz":
				zeileunterprogramm = functionJnz(sourceunterprogramm, temp, zeileunterprogramm);
				break;
			case "jb":
				zeileunterprogramm = functionJb(source, temp, zeileunterprogramm);
				break;
			case "jnb":
				zeileunterprogramm = functionJnb(source, temp, zeileunterprogramm);
				break;
			case "djnz":
				zeileunterprogramm = functionDjnz(sourceunterprogramm, temp, zeileunterprogramm);
				break;
			case "cjne":
				zeileunterprogramm = functionCjne(sourceunterprogramm, temp, zeileunterprogramm);
				break;
			case "inc":
				functionInc(temp);
				break;
			case "dec":
				functionDec(temp);
				break;
			case "add":
				functionAdd(temp);
				break;
			case "swap":
				functionSwap();
				break;
			case "mul":
				functionMul(temp);
				break;
			case "div":
				functionDiv(temp);
				break;
			case "rl":
				functionRL();
				break;
			case "rr":
				functionRR();
				break;
			case "lcall":
				functionLcall(sourceunterprogramm, temp);
				break;
			case "ret":
				returned = true;
				break;
			default: // Sprungmarke
				break;
			} // end of switch

			try {
				Thread.sleep(speed * 10);
			} catch (Exception ie) {
			}

			if (returned) {
				break;
			} // end of if
			if (!running) {
				break;
			}
			if (debugMode) {
				pause = true;
			}
		}
	}

	public void runningInterrupt(String[][] source, String whichInterrupt) {
		// erhaelt hier die Info, durch was Interrupt ausgeloest wurde
		// 1 = Ziel
		int interruptCodeStart = 0;
		int interruptCodeEnd = 0;

		String marke = whichInterrupt;
		for (int k = 0; k < source.length; k++) {
			if (source[k][0].equals("org") && source[k][1].equals(marke)) {
				interruptCodeStart = k;
				break;
			} // end of if
		} // end of for

		for (int k = interruptCodeStart; k < source.length; k++) {
			if (source[k][0].equals("reti")) {
				interruptCodeEnd = k;
				break;
			} // end of if
		} // end of for

		boolean returned = false;
		String[] temp = { "", "", "", "" };
		for (int currentLine = interruptCodeStart + 1; currentLine <= interruptCodeEnd; currentLine++) {
			timerCountdown();

			temp = InputToSource.getLine(source, currentLine);
			switch (temp[0]) {
			case "mov":
				functionMov(temp);
				break;
			case "movc":
				functionMovc(source, temp);
				break;
			case "setb":
				functionSetb(temp);
				break;
			case "clr":
				functionClr(temp);
				break;
			case "cpl":
				functionCpl(temp);
				break;
			case "anl":
				functionAnl(temp);
				break;
			case "orl":
				functionOrl(temp);
				break;
			case "xrl":
				functionXrl(temp);
				break;
			case "xch":
				functionXch(temp);
				break;
			case "xchd":
				functionXchd(temp);
				break;
			case "sjmp":
				currentLine = functionSjmp(source, temp, currentLine);
				break;
			case "jz":
				currentLine = functionJz(source, temp, currentLine);
				break;
			case "jnz":
				currentLine = functionJnz(source, temp, currentLine);
				break;
			case "jb":
				currentLine = functionJb(source, temp, currentLine);
				break;
			case "jnb":
				currentLine = functionJnb(source, temp, currentLine);
				break;
			case "djnz":
				currentLine = functionDjnz(source, temp, currentLine);
				break;
			case "cjne":
				currentLine = functionCjne(source, temp, currentLine);
				break;
			case "inc":
				functionInc(temp);
				break;
			case "dec":
				functionDec(temp);
				break;
			case "add":
				functionAdd(temp);
				break;
			case "swap":
				functionSwap();
				break;
			case "mul":
				functionMul(temp);
				break;
			case "div":
				functionDiv(temp);
				break;
			case "rl":
				functionRL();
				break;
			case "rr":
				functionRR();
				break;
			case "lcall":
				functionLcall(source, temp);
				break;
			case "reti":
				returned = true;
				break;

			default: // Sprungmarke
				break;
			} // end of switch

			try {
				Thread.sleep(speed * 10);
			} catch (Exception ie) {
			}

			if (returned) {
				break;
			} // end of if
			if (!running) {
				break;
			} // end of if
			if (debugMode) {
				pause = true;
			}
		}
	}

	public void runningTimer(String[][] source, String whichTimer) {
		// erhaelt hier die Info, durch was Interrupt ausgeloest wurde
		// 1 = Ziel
		int timerCodeStart = 0;
		int timerCodeEnd = 0;

		String marke = whichTimer;
		for (int k = 0; k < source.length; k++) {
			if (source[k][0].equals("org") && source[k][1].equals(marke)) {
				timerCodeStart = k;
				break;
			} // end of if
		} // end of for

		for (int k = timerCodeStart; k < source.length; k++) {
			if (source[k][0].equals("reti")) {
				timerCodeEnd = k;
				break;
			} // end of if
		} // end of for

		boolean returned = false;
		String[] temp = { "", "", "", "" };
		for (int currentLine = timerCodeStart + 1; currentLine <= timerCodeEnd; currentLine++) {
			temp = InputToSource.getLine(source, currentLine);
			switch (temp[0]) {
			case "mov":
				functionMov(temp);
				break;
			case "movc":
				functionMovc(source, temp);
				break;
			case "setb":
				functionSetb(temp);
				break;
			case "clr":
				functionClr(temp);
				break;
			case "cpl":
				functionCpl(temp);
				break;
			case "anl":
				functionAnl(temp);
				break;
			case "orl":
				functionOrl(temp);
				break;
			case "xrl":
				functionXrl(temp);
				break;
			case "xch":
				functionXch(temp);
				break;
			case "xchd":
				functionXchd(temp);
				break;
			case "sjmp":
				currentLine = functionSjmp(source, temp, currentLine);
				break;
			case "jz":
				currentLine = functionJz(source, temp, currentLine);
				break;
			case "jnz":
				currentLine = functionJnz(source, temp, currentLine);
				break;
			case "jb":
				currentLine = functionJb(source, temp, currentLine);
				break;
			case "jnb":
				currentLine = functionJnb(source, temp, currentLine);
				break;
			case "djnz":
				currentLine = functionDjnz(source, temp, currentLine);
				break;
			case "cjne":
				currentLine = functionCjne(source, temp, currentLine);
				break;
			case "inc":
				functionInc(temp);
				break;
			case "dec":
				functionDec(temp);
				break;
			case "add":
				functionAdd(temp);
				break;
			case "swap":
				functionSwap();
				break;
			case "mul":
				functionMul(temp);
				break;
			case "div":
				functionDiv(temp);
				break;
			case "rl":
				functionRL();
				break;
			case "rr":
				functionRR();
				break;
			case "lcall":
				functionLcall(source, temp);
				break;
			case "reti":
				returned = true;
				break;

			default: // Sprungmarke
				break;
			} // end of switch

			try {
				Thread.sleep(speed * 10);
			} catch (Exception ie) {
			}

			if (returned) {
				break;
			} // end of if
			if (!running) {
				break;
			} // end of if
			if (debugMode) {
				pause = true;
			}
		}
	}

	public void startThread1() {
		thread1 = new Thread(this);
		thread1.start();
	}

	public void stopThread1() {
		thread1.interrupt();
		codebox.setFocusable(true);
		jButton_run.setEnabled(true);
		jButton_stop.setEnabled(false);
		running = false;
		resetAll();
	}

	private AssemblerSim getAssemblerSim() {
		return this;
	}

	public boolean lcallForDisplay(String[] temp) {
		boolean ret = false;
		if (temp[1].equals("initLCD")) {
			display.setInitialized(true);
			ret = true;
		} else if (temp[1].equals("loeschen")) {
			display.deleteAllLines();
			ret = true;
		} else if (temp[1].equals("loeschzeile1")) {
			display.deleteLine1();
			ret = true;
		} else if (temp[1].equals("loeschzeile2")) {
			display.deleteLine2();
			ret = true;
		} else if (temp[1].equals("cursorpos")) {
			display.setCursorPos(Integer.parseInt(A.getText()));
			ret = true;
		} else if (temp[1].equals("textaus")) {
			display.setText(source, dptr);
			ret = true;
		} else if (temp[1].equals("textzeile1")) {
			display.setTextLine1(source, dptr);
			ret = true;
		} else if (temp[1].equals("textzeile2")) {
			display.setTextLine2(source, dptr);
			ret = true;
		} else if (temp[1].equals("zifferaus")) {
			display.setNumber(Integer.parseInt(A.getText()));
			ret = true;
		} else if (temp[1].equals("charaus")) {
			display.setChar(Integer.parseInt(A.getText()));
			ret = true;
		} else if (temp[1].equals("definiereZeichen")) {
			display.loadToRam(Integer.parseInt(A.getText()), source, dptr);
			ret = true;
		}

		return ret;
	}

	public void resetAll() {
		// Hauptthread beenden lassen
		try {
			Thread.sleep(50);
		} catch (Exception e) {
		}
		for (int i = 0; i < 8; i++) {
			register[i].setText("");
			leds[i].setSelected(false);
		} // end of for
		A.setText("");
		B.setText("");
		ea = false;
		ex0 = false;
		ex1 = false;
		ie0 = false;
		ie1 = false;
		it0 = false;
		it1 = false;
		anzeige.reset();
	}

	public void writeLog(String line) {

		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		Date currentTime = new Date();
		line = formatter.format(currentTime) + " | " + line;
		logfile.add(0, line);
		System.err.println(line);
	}

	private void addSysTray() {
		if (!trayIcon) {
			return;
		}
		if (!SystemTray.isSupported()) {
			return;
		}
		final PopupMenu popup = new PopupMenu();

		final TrayIcon trayIcon = new TrayIcon(
				new ImageIcon(this.getClass().getResource("/icons/icon.gif")).getImage());
		SystemTray tray = SystemTray.getSystemTray();

		// Create a popup menu components
		MenuItem aboutItem = new MenuItem("\u00dcber");
		MenuItem start = new MenuItem("Starte Programm");
		MenuItem stop = new MenuItem("Stoppe Programm");
		MenuItem exitItem = new MenuItem("Beenden");

		// Add components to popup menu
		popup.add(start);
		popup.add(stop);
		popup.addSeparator();
		popup.add(aboutItem);
		popup.addSeparator();
		popup.add(exitItem);
		trayIcon.setPopupMenu(popup);

		try {
			tray.add(trayIcon);
		} catch (AWTException e) {
			return;
		}

		aboutItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new AboutDialog(programversion);
			}
		});

		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jButton_run.doClick();
				trayIcon.displayMessage("AssemblerSim", "Programm gestartet", TrayIcon.MessageType.NONE);
			}
		});

		stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jButton_stop.doClick();
				trayIcon.displayMessage("AssemblerSim", "Programm gestoppt", TrayIcon.MessageType.NONE);
			}
		});
		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jButton_close.doClick();
			}
		});
	}

	// Die DigitWatcher ueberpruefen, ob der Wert in den Registern ueber oder
	// unter
	// 255 bzw. 0 liegen und korrigieren
	// P2 muss direkt in inc und dec geprueft werden, weil man es nicht auf 256
	// steigen lassen kann (globale Variable hilft spaeter vielleicht)

	public boolean binaryToBoolean(char bin) {
		if (bin == '1') {
			return true;
		} else {
			return false;
		}
	}

	public void digitWatcherRegister(int whichRegister) {
		/*
		 * Throwable t = new Throwable(); StackTraceElement[] ste = t.getStackTrace();
		 * for (int i = 0; i < ste.length; i++) {
		 * System.out.println(ste[i].getMethodName()); }
		 */

		if (Integer.parseInt(register[whichRegister].getText()) < 0) {
			register[whichRegister]
					.setText(Integer.toString(Integer.parseInt(register[whichRegister].getText()) + 256));
		} else if (Integer.parseInt(register[whichRegister].getText()) > 255) {
			register[whichRegister]
					.setText(Integer.toString(Integer.parseInt(register[whichRegister].getText()) - 256));
		}

	}

	public void digitWatcherA() {
		/*
		 * Throwable t = new Throwable(); StackTraceElement[] ste = t.getStackTrace();
		 * for (int i = 0; i < ste.length; i++) {
		 * System.out.println(ste[i].getMethodName()); }
		 */

		if (Integer.parseInt(A.getText()) < 0) {
			A.setText(Integer.toString(Integer.parseInt(A.getText()) + 256));
		} else if (Integer.parseInt(A.getText()) > 255) {
			A.setText(Integer.toString(Integer.parseInt(A.getText()) - 256));
		}
	}

	public void digitWatcherB() {
		/*
		 * Throwable t = new Throwable(); StackTraceElement[] ste = t.getStackTrace();
		 * for (int i = 0; i < ste.length; i++) {
		 * System.out.println(ste[i].getMethodName()); }
		 */

		if (Integer.parseInt(B.getText()) < 0) {
			B.setText(Integer.toString(Integer.parseInt(B.getText()) + 256));
		} else if (Integer.parseInt(B.getText()) > 255) {
			B.setText(Integer.toString(Integer.parseInt(B.getText()) - 256));
		}
	}

	public int digitWatcherRam(int value) {
		if (value < 0) {
			value += 256;
		} else if (value > 255) {
			value -= 256;
		}
		return value;
	}

	// Hauptthread thread1
	@SuppressWarnings("static-access")
	public void run() {
		String get = codebox.getText();
		if (get.equals("assisim") || get.equals("easteregg") || get.equals("easter egg")) {
			setTitle("Assi Sim");
			get = "mov dptr, #text1\nlcall textzeile1\nende:\nsjmp ende\ntext1:\nDB 'Assi Sim started',0";
			repaint();
			new TrollingWindow().start();
		}
		String[] input = get.split("\n", 0);
		final int lines = input.length;

		// Loesche Kommentare aus Code
		for (int k = 0; k < lines; k++) {
			input[k] = input[k].trim(); // Unnoetige Leerzeichen entfernen
			if (input[k].startsWith(";")) {
				// input[k] = "";
			}
			if (input[k].indexOf(";") != -1) {
				input[k] = input[k].substring(0, input[k].indexOf(";"));
				input[k] = input[k].trim();
			}
		}

		source = InputToSource.get(input, lines);

		// CodeChecker
		if (useCodeCleaner) {

			CodeChecker codechecker = new CodeChecker(source);
			if (!codechecker.isValidCode()) {
				running = false;
				stopThread1();
			}
			source = codechecker.getSource();
		}
		while (running) {

			String[] temp = new String[4];
			// Zentrale Schleife fuer den Programmablauf
			for (i = 0; i < lines; i++) {
				temp = InputToSource.getLine(source, i); // externes Auslesen

				synchronized (thread1) {

					if (debugMode) {
						codebox.setLineHighlight(i + 1);
						codebox.setCaretLine(i + 1);
					}
					while (waitingprog) {
						try {
							thread1.sleep(1);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					while (pause) {
						try {
							thread1.sleep(1);
						} catch (Exception e) {
						}
					}
				}
				timerCountdown();

				switch (temp[0]) {
				case "mov":
					functionMov(temp);
					break;
				case "movc":
					functionMovc(source, temp);
					break;
				case "setb":
					functionSetb(temp);
					break;
				case "clr":
					functionClr(temp);
					break;
				case "cpl":
					functionCpl(temp);
					break;
				case "anl":
					functionAnl(temp);
					break;
				case "orl":
					functionOrl(temp);
					break;
				case "xrl":
					functionXrl(temp);
					break;
				case "xch":
					functionXch(temp);
					break;
				case "xchd":
					functionXchd(temp);
					break;
				case "sjmp":
					i = functionSjmp(source, temp, i);
					break;
				case "jz":
					i = functionJz(source, temp, i);
					break;
				case "jnz":
					i = functionJnz(source, temp, i);
					break;
				case "jb":
					i = functionJb(source, temp, i);
					break;
				case "jnb":
					i = functionJnb(source, temp, i);
					break;
				case "djnz":
					i = functionDjnz(source, temp, i);
					break;
				case "cjne":
					i = functionCjne(source, temp, i);
					break;
				case "inc":
					functionInc(temp);
					break;
				case "dec":
					functionDec(temp);
					break;
				case "add":
					functionAdd(temp);
					break;
				case "swap":
					functionSwap();
					break;
				case "mul":
					functionMul(temp);
					break;
				case "div":
					functionDiv(temp);
					break;
				case "rl":
					functionRL();
					break;
				case "rr":
					functionRR();
					break;
				case "lcall":
					functionLcall(source, temp);
					break;
				default: // Sprungmarke
					break;
				} // end of switch

				try {
					Thread.sleep(speed * 10);
				} catch (Exception ie) {
				}

				if (!running) {
					break;

				}

				if (debugMode) {
					pause = true;
				}
			}
			running = false; // bricht ab, falls uebers ende hinausschiesst
		}
		// hier evtl. fuer den Reset die ganzen Zuruecksetzmethoden
	}

	/**
	 * Restarts assemblersim after writing codebox content to temp file.
	 * 
	 * Works only in jar file. No function in SDK
	 */
	public void restart() {
		File f = new File(".temprestart");
		try {
			FileWriter fw = new FileWriter(f);
			fw.write(codebox.getText());
			fw.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		String javaBin = System.getProperty("java.home") + "/bin/java";
		File jarFile;
		try {
			jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
		} catch (Exception e) {
			return;
		}

		/* is it a jar file? */
		if (!jarFile.getName().endsWith(".jar"))
			return; // no, it's a .class probably

		String toExec[] = new String[] { javaBin, "-jar", jarFile.getPath() };
		try {
			@SuppressWarnings("unused")
			Process p = Runtime.getRuntime().exec(toExec);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		System.exit(0);

	}

	public void cpuspeed() {
		if (!cpucheckoff) {
			JOptionPane.showMessageDialog(null, new CpuSpeedPanel());
		}
	}

	public void timerCountdown() {
		if (et0 == true || et1 == true) {
			if (tr0 == true) { // Runterzaehlen Timer 0
				if (currentValueTimer0 == 65535) {
					runningTimer(source, "000Bh");
					if (m0_0 == false && m0_1 == true) {
						currentValueTimer0 = startValueTimer0;
					} else {
						return;
					}
				} else if (currentValueTimer0 > 65535) {
					currentValueTimer0 = startValueTimer0;
					return;
				} else {
					currentValueTimer0 += 1;
					return;
				}
			}
			if (tr1 == true) { // Runterzaehlen Timer 1
				if (currentValueTimer1 == 65535) {
					runningTimer(source, "001Bh");
					if (m1_0 == false && m1_1 == true) {
						currentValueTimer1 = startValueTimer1;
					} else {
						return;
					}
				} else if (currentValueTimer1 > 65535) {
					currentValueTimer1 = startValueTimer1;
					return;
				} else {
					currentValueTimer1 += 1;
					return;
				}
			}
		}
	}

	/*
	 * public static void main(String[] args) {
	 * 
	 * new AssemblerSim("AssemblerSim");
	 * 
	 * } // end of main
	 */
} // end of class AssemblerSim