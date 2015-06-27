import java.awt.Color;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.DefaultComboBoxModel;

public class BugreportDialog extends JDialog {
	private static final long serialVersionUID = 4732494351145169728L;
	private JTextField author;
	private JTextField summary;
	JComboBox<String> category;
	JComboBox<String> reproducibility;
	JComboBox<String> severity;
	JComboBox<String> priority;
	JComboBox<String> os;
	JComboBox<String> javaversion;
	JTextField product_version;
	JTextArea description;
	JTextArea steps_to_reproduce;

	public BugreportDialog(String code, String progVersion) {
		setTitle("Fehler melden");
		int width = 600;
		int height = 580;
		setSize(width, height);
		setLocationRelativeTo(null);
		setResizable(false);
		setModal(true);
		Container cp = getContentPane();
		cp.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setLayout(null);
		JScrollPane scrollpane = new JScrollPane(panel);
		scrollpane.setBounds(0, 0, 600, 580);
		getContentPane().add(scrollpane);
		
		JLabel category_label = new JLabel("Kategorie");
		category_label.setBounds(22, 13, 111, 20);
		panel.add(category_label);
		
		category = new JComboBox<String>();
		category.setModel(new DefaultComboBoxModel<String>(new String[] {"", "GUI", "Programm", "Webseite"}));
		category.setBounds(143, 10, 125, 25);
		panel.add(category);
		
		JLabel reproducibility_label = new JLabel("Reproduzierbar");
		reproducibility_label.setBounds(321, 13, 105, 20);
		panel.add(reproducibility_label);
		
		reproducibility = new JComboBox<String>();
		reproducibility.setModel(new DefaultComboBoxModel<String>(new String[] {"", "immer", "manchmal", "nicht getestet", "nicht reproduzierbar", "N/A"}));
		reproducibility.setBounds(436, 10, 125, 25);
		panel.add(reproducibility);
				
		JLabel severity_label = new JLabel("Auswirkung");
		severity_label.setBounds(22, 48, 111, 20);
		panel.add(severity_label);
		
		severity = new JComboBox<String>();
		severity.setModel(new DefaultComboBoxModel<String>(new String[] {"", "Trivial", "Textfehler", "kleiner Fehler", "schwerer Fehler", "Programmabsturz"}));
		severity.setBounds(143, 45, 125, 25);
		panel.add(severity);
		
		JLabel priority_label = new JLabel("Priorit\u00E4t");
		priority_label.setBounds(321, 48, 105, 20);
		panel.add(priority_label);
		
		priority = new JComboBox<String>();
		priority.setModel(new DefaultComboBoxModel<String>(new String[] {"", "keine", "niedrig", "normal", "hoch", "dringend"}));
		priority.setBounds(436, 45, 125, 25);
		priority.setSelectedIndex(3);
		panel.add(priority);
		
		JLabel os_label = new JLabel("Betriebssystem");
		os_label.setBounds(22, 83, 111, 20);
		panel.add(os_label);
		
		os = new JComboBox<String>();
		os.setModel(new DefaultComboBoxModel<String>(new String[] {"", "Linux", "Mac OS", "Windows"}));
		os.setBounds(143, 80, 125, 25);
		panel.add(os);
		
		//get the os
		if (System.getProperty("os.name").startsWith("Win")) {
			os.setSelectedIndex(3);
		}
		
		JLabel javaversion_label = new JLabel("Javaversion");
		javaversion_label.setBounds(321, 83, 105, 20);
		panel.add(javaversion_label);
		
		javaversion = new JComboBox<String>();
		javaversion.setModel(new DefaultComboBoxModel<String>(new String[] {"", "Java 8", "Java 7", "Java 6"}));
		javaversion.setBounds(436, 80, 125, 25);
		panel.add(javaversion);
		
		//get java version:
		if (System.getProperty("java.version").startsWith("1.7")) {
			javaversion.setSelectedIndex(2);
		} else if (System.getProperty("java.version").startsWith("1.6")) {
			javaversion.setSelectedIndex(3);
		} else if(System.getProperty("java.version").startsWith("1.8")) {
		    javaversion.setSelectedIndex(1);
		}
		
		JLabel product_version_label = new JLabel("Produktversion");
		product_version_label.setBounds(22, 118, 111, 20);
		panel.add(product_version_label);
		
		product_version = new JTextField(progVersion);
		product_version.setBounds(143, 115, 125, 25);
		product_version.setEnabled(false);
		product_version.setDisabledTextColor(Color.black);
		panel.add(product_version);
		
		JLabel author_label = new JLabel("Benutzername");
		author_label.setBounds(321, 118, 105, 20);
		panel.add(author_label);
		
		author = new JTextField();
		author.setBounds(436, 115, 125, 25);
		panel.add(author);
		author.setColumns(10);
		author.setText(System.getProperty("user.name"));
		
		JLabel summary_label = new JLabel("Zusammenfassung");
		summary_label.setBounds(22, 153, 111, 20);
		panel.add(summary_label);
		
		summary = new JTextField();
		summary.setBounds(143, 150, 418, 25);
		panel.add(summary);
		summary.setColumns(10);
		
		JLabel description_label = new JLabel("Beschreibung");
		description_label.setBounds(22, 188, 111, 20);
		panel.add(description_label);
		
		description = new JTextArea();
		description.setLineWrap(true);
		description.setWrapStyleWord(true);
		
		JScrollPane description_scroll = new JScrollPane(description);
		description_scroll.setBounds(143, 185, 418, 150);
		panel.add(description_scroll);
				
		JLabel steps_to_reproduce_label = new JLabel("<html>Schritte zur<br>Reproduktion</html>");
		steps_to_reproduce_label.setBounds(22, 353, 111, 30);
		panel.add(steps_to_reproduce_label);
		
		steps_to_reproduce = new JTextArea();
		File jarFile = null;
	    try{  
	        jarFile = new File  
	        (getClass().getProtectionDomain()  
	        .getCodeSource().getLocation().toURI());  
	    } catch(Exception e) {  
	         
	    }
		if (jarFile.getName().endsWith(".jar")) {
			try {
				@SuppressWarnings("resource")
				String errorfilecontent = new Scanner(new File(System.getProperty("user.dir") + "/error.log")).useDelimiter("\\A").next();
				if(!errorfilecontent.isEmpty()){
					steps_to_reproduce.append("Fehlermeldung:"
						+ System.lineSeparator() + "==============" + System.lineSeparator() + System.lineSeparator());
					steps_to_reproduce.append(errorfilecontent);
					steps_to_reproduce.append(System.lineSeparator());
					steps_to_reproduce.append("==============================");
					steps_to_reproduce.append(System.lineSeparator() + System.lineSeparator());
				}
				
			} catch (Exception e1) {
				e1.printStackTrace();
			}			
		}
		steps_to_reproduce.append(code);
		steps_to_reproduce.setLineWrap(true);
		steps_to_reproduce.setWrapStyleWord(true);
		
		JScrollPane steps_to_reproduce_scroll = new JScrollPane(steps_to_reproduce);
		steps_to_reproduce_scroll.setBounds(143, 350, 418, 150);
		panel.add(steps_to_reproduce_scroll);
		
		JButton btnAbsenden = new JButton("Absenden");
		btnAbsenden.setBounds(175, 515, 100, 25);
		btnAbsenden.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String id = sendForm();
				if (JOptionPane.showConfirmDialog(null,
						"Die Meldung wurde erfolgreich abgeschickt.\n"+
						"Ihr wurde die ID #" + id + " zugeteilt. " +
						"Nach einer Prüfung wird die Meldung veröffentlicht.\n" +
						"Jetzt im Browser öffnen?",
						"", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					try {
						Desktop.getDesktop().browse(new URI("http://assemblersim.de/"+
							"bugtracker/show-report.php?id=" + id));
					} catch (IOException | URISyntaxException e) {
						e.printStackTrace();
					}
					dispose();
				} else {
					dispose();
				}
			}
		});
		panel.add(btnAbsenden);
		
		JButton btnSchlieen = new JButton("Schlie\u00DFen");
		btnSchlieen.setBounds(325, 515, 100, 25);
		btnSchlieen.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (JOptionPane.showConfirmDialog(null,
						"Wirklich abbrechen?",
						"", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					dispose();
				}

			}
		});
		panel.add(btnSchlieen);
		
		setVisible(true);
	}

	public String sendForm() {
		String resultid = "";
		int version_id = 0;
		String[] version_history = {"20131109", "20131110", "20131116", "20131123", "20131206", "20131223", "20140217", "20140303", "20150326"};
		for (int i = 0; i < version_history.length; i++) {
			if (product_version.getText().equals(version_history[i])) {
				version_id = i+1;
				break;
			}
		}
		try {
			String body = "category=" + URLEncoder.encode(Integer.toString(category.getSelectedIndex()), "UTF-8")
					+ "&" + "reproducibility=" + URLEncoder.encode(Integer.toString(reproducibility.getSelectedIndex()), "UTF-8")
					+ "&" + "severity=" + URLEncoder.encode(Integer.toString(severity.getSelectedIndex()), "UTF-8")
					+ "&" + "priority=" + URLEncoder.encode(Integer.toString(priority.getSelectedIndex()), "UTF-8")
					+ "&" + "os=" + URLEncoder.encode(Integer.toString(os.getSelectedIndex()), "UTF-8")
					+ "&" + "javaversion=" + URLEncoder.encode(Integer.toString(javaversion.getSelectedIndex()), "UTF-8")
					+ "&" + "product_version=" + URLEncoder.encode(Integer.toString(version_id), "UTF-8")
					+ "&" + "author=" + URLEncoder.encode(author.getText(), "UTF-8")
					+ "&" + "summary=" + URLEncoder.encode(summary.getText(), "UTF-8")
					+ "&" + "description=" + URLEncoder.encode(description.getText(), "UTF-8")
					+ "&" + "steps_to_reproduce=" + URLEncoder.encode(steps_to_reproduce.getText(), "UTF-8")
					+ "&" + "additional_info=" + URLEncoder.encode("", "UTF-8")
					+ "&" + "submit=" + URLEncoder.encode("Bericht absenden", "UTF-8");
			URL url = new URL("http://assemblersim.de/bugtracker/report-new-bug.php");
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			connection.setRequestProperty("Content-Length",
					String.valueOf(body.length()));
			OutputStreamWriter writer = new OutputStreamWriter(
					connection.getOutputStream());
			writer.write(body);
			writer.flush();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			
		    StringBuilder websitesource = new StringBuilder();
		    
			for (String line; (line = reader.readLine()) != null;) {
				websitesource.append(line);
			}
			writer.close();
			reader.close();
			
			String re1=".*?";	// Non-greedy match on filler
		    String re2="\\d+";	// Uninteresting: int
		    String re3=".*?";	// Non-greedy match on filler
		    String re4="\\d+";	// Uninteresting: int
		    String re5=".*?";	// Non-greedy match on filler
		    String re6="\\d+";	// Uninteresting: int
		    String re7=".*?";	// Non-greedy match on filler
		    String re8="\\d+";	// Uninteresting: int
		    String re9=".*?";	// Non-greedy match on filler
		    String re10="\\d+";	// Uninteresting: int
		    String re11=".*?";	// Non-greedy match on filler
		    String re12="\\d+";	// Uninteresting: int
		    String re13=".*?";	// Non-greedy match on filler
		    String re14="\\d+";	// Uninteresting: int
		    String re15=".*?";	// Non-greedy match on filler
		    String re16="(\\d+)";	// Integer Number 1

		    Pattern p = Pattern.compile(re1+re2+re3+re4+re5+re6+re7+re8+re9+re10+re11+re12+re13+re14+re15+re16,Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		    Matcher m = p.matcher(websitesource.toString());
			if (m.find()){
				String string1=m.group(1);
				resultid = string1;
		    }
			m = null;
		} catch (Exception e) {
			
		}
		return resultid;
	}
	
	public static boolean isNumeric(String str){
		try {
			@SuppressWarnings("unused")
			double d = Double.parseDouble(str);
		} catch(NumberFormatException nfe){
			return false;
		}
		return true;
	}
}
