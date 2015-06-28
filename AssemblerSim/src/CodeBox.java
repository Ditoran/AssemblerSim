import java.awt.Container;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

public class CodeBox extends JDialog {

	private static final long serialVersionUID = 4732494351145169728L;
//
	private JEditorPane codeTextArea;
	private JScrollPane codeScrollBox;
	private JTextArea helpTextArea = new JTextArea();
	private JScrollPane helpScrollBox = new JScrollPane(helpTextArea);
	private JButton close = new JButton();
	private JPopupMenu jpm = new JPopupMenu();
	private JMenuItem JPopupCut = new JMenuItem("Ausschneiden");
	private JMenuItem JPopupCopy = new JMenuItem("Kopieren");
	private JMenuItem JPopupPaste = new JMenuItem("Einfügen");
	private JMenuItem JPopupDelete = new JMenuItem("Löschen");
	private JMenuItem JPopupSelectAll = new JMenuItem(
			"Alles markieren");
	AssemblerSim a;

	public CodeBox(AssemblerSim a) {
		this.a = a;
		setTitle("Codebox");
		int width = 1000;
		int height = 580;
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				close.doClick();
			}
		});
		setSize(width, height);
		setLocationRelativeTo(null);
		setResizable(false);
		Container cp = getContentPane();
		cp.setLayout(null);
		codeTextArea = (JEditorPane) new Syntax(12).getEditPane();
		codeTextArea.setText(a.codebox.getText());
		codeScrollBox = new JScrollPane(codeTextArea);
		// Kontextmenü Start
		codeTextArea.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent evt) {
				if (evt.isPopupTrigger()) {
					jpm.show(evt.getComponent(), evt.getX(), evt.getY());
				}
			}
		});			
		JPopupCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
		JPopupCut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				codeTextArea.cut();
			}
		});
		jpm.add(JPopupCut);

		JPopupCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
		JPopupCopy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				codeTextArea.copy();
			}
		});
		jpm.add(JPopupCopy);

		JPopupPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
		JPopupPaste.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				codeTextArea.paste();
			}
		});
		jpm.add(JPopupPaste);

		JPopupDelete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		JPopupDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				try {
					codeTextArea.replaceSelection("");
				} catch (Exception e) {
				}
			}
		});
		jpm.add(JPopupDelete);

		jpm.addSeparator();

		JPopupSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));
		JPopupSelectAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				codeTextArea.selectAll();
			}
		});
		jpm.add(JPopupSelectAll);

		// Kontextmenï¿½ Ende

		codeTextArea.setText(a.codebox.getText());
		codeScrollBox.setBounds(10, 20, 400, 490);
		cp.add(codeScrollBox);

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					getClass().getResourceAsStream("txt/docs.txt"), "UTF-8"));
			helpTextArea.read(br, "");
			helpTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		helpScrollBox.setBounds(410, 20, 565, 490);
		cp.add(helpScrollBox);
		helpTextArea.setEditable(false);

		close.setBounds(400, 525, 190, 20);
		close.setMargin(new Insets(5, 5, 5, 5));
		close.setText("Übernehmen & Schließen");
		cp.add(close);
		close.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				setCodeBox();
				dispose();
			}
		});

		setModal(true);
		setVisible(true);
	}

	public void setCodeBox() {
		String temp = codeTextArea.getText();
		a.codebox.setText(temp);
		a.codebox.setCaretPosition(0);
	}
}