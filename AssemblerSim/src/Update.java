import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class Update {
	String programVersion = "";
	String programDir = "";

	public Update(String programVersion) {
		this.programVersion = programVersion;
		programDir = System.getProperty("user.dir");
	}

	public boolean checkForUpdate() {
		URL getLatest;
		int lastestVersion = 0;
		try {
			getLatest = new URL(
					"http://assemblersim.de/currentversion.php");
			URLConnection conn = getLatest.openConnection();
			// open the stream and put it into BufferedReader
			BufferedReader br = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			lastestVersion = Integer.parseInt(br.readLine());
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		if (Integer.parseInt(programVersion) >= lastestVersion) {
			return false;
		}
		return true;
	}

	public void startUpdate() {

		// Lädt Update herunter

		try {
			URL file1 = new URL(
					"http://assemblersim.de/update/AssemblerSim.jar");
			ReadableByteChannel rbc1 = Channels.newChannel(file1.openStream());
			FileOutputStream fos1 = new FileOutputStream("AssemblerSim.update");
			fos1.getChannel().transferFrom(rbc1, 0, 1 << 24);
			fos1.close();
		} catch (Exception e) {
		}

	}

}
