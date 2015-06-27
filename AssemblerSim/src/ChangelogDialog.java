import java.awt.Container;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ChangelogDialog extends JDialog {

	private static final long serialVersionUID = 4732494351145169728L;

	JTextArea changelogScrollBox = new JTextArea();
	JScrollPane tempScrollBox = new JScrollPane(changelogScrollBox);
	JButton close = new JButton("Schließen");

	public ChangelogDialog() {
		setTitle("Changelog");
		int width = 600;
		int height = 590;
		setSize(width, height);
		setLocationRelativeTo(null);
		setResizable(false);
		Container cp = getContentPane();
		cp.setLayout(null);
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					getClass().getResourceAsStream("txt/changelog.txt"), "UTF-8"));
			changelogScrollBox.read(br, "");
			changelogScrollBox.setFont(new Font("Monospaced", Font.PLAIN, 12));

		} catch (Exception e) {
			e.printStackTrace();
		}
		tempScrollBox.setBounds(10, 20, 575, 500);
		cp.add(tempScrollBox);
		changelogScrollBox.setEditable(false);
		setModal(true);
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