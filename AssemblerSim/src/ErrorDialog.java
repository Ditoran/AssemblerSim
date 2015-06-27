import java.awt.Color;
import java.awt.Container;
import javax.swing.JDialog;
import javax.swing.JLabel;

public class ErrorDialog extends JDialog {

	private static final long serialVersionUID = 4732494351145169728L;

	private JLabel numberErrorLabel = new JLabel();
	private JLabel programStoppedLabel = new JLabel();

	public ErrorDialog() {

		setTitle("Error");
		int width = 200;
		int height = 120;
		setSize(width, height);
		setLocationRelativeTo(null);

		Container cp = getContentPane();
		cp.setLayout(null);
		setModal(true);
		numberErrorLabel.setText("Number Error!");
		numberErrorLabel.setBounds(60, 30, 100, 30);
		cp.add(numberErrorLabel);
		programStoppedLabel.setText("Thread Stopped!");
		programStoppedLabel.setBounds(60, 60, 100, 30);
		cp.add(programStoppedLabel);
		cp.setBackground(Color.WHITE);
		setVisible(true);
	}

}