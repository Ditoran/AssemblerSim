import java.awt.Container;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class DocsDialog extends JDialog {

	private static final long serialVersionUID = 4732494351145169728L;

	JTextArea helpTextArea = new JTextArea();
	JScrollPane helpScrollBox = new JScrollPane(helpTextArea);
	JButton close = new JButton("Schlie\u00dfen");

	public DocsDialog() {
		setTitle("Befehlssatz");
		int width = 600;
		int height = 590;
		setSize(width, height);
		setLocationRelativeTo(null);
		setResizable(false);
		Container cp = getContentPane();
		cp.setLayout(null);

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
		helpScrollBox.setBounds(10, 20, 575, 500);
		cp.add(helpScrollBox);
		helpTextArea.setEditable(false);
		close.setBounds(250, 535, 100, 20);
		close.setMargin(new Insets(5, 5, 5, 5));
		cp.add(close);
		close.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		setVisible(true);
	}

}