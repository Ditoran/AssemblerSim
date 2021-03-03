package de.assemblersim.application.cloud;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

public class Cloud {

	private String username = "Admin";
	private String password = "test123";
	private boolean loggedIn = false;

	private CloudFileModel fileModel = new CloudFileModel();

	private static Cloud instance;

	private Cloud() {

	}

	public static Cloud getInstance() {
		if (instance == null) {
			instance = new Cloud();
		}
		return instance;
	}

	private String httpGet(URL url) {
		String response = "";
		try {

			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
			InputStreamReader isr = new InputStreamReader(connection.getInputStream());
			BufferedReader br = new BufferedReader(isr);
			String line = "";

			while ((line = br.readLine()) != null) {
				response += line;
			}

		} catch (Exception e) {
			System.err.println("Verbindung zur Cloud fehlgeschlagen!");
		}
		return response;
	}

	private String httpPost(URL url, Map<String, String> postDataParams) {
		String response = "";
		try {

			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoInput(true);
			connection.setDoOutput(true);

			OutputStream os = connection.getOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
			writer.write(getPostDataString(postDataParams));

			writer.flush();
			writer.close();
			os.close();
			int responseCode = connection.getResponseCode();

			if (responseCode == HttpsURLConnection.HTTP_OK) {
				InputStreamReader isr = new InputStreamReader(connection.getInputStream());
				BufferedReader br = new BufferedReader(isr);
				String line = "";

				while ((line = br.readLine()) != null) {
					response += line;
				}
			} else {
				response = "";

			}

		} catch (Exception e) {
			System.err.println("Verbindung zur Cloud fehlgeschlagen!");
		}
		return response;
	}

	private String getPostDataString(Map<String, String> params) throws UnsupportedEncodingException {
		StringBuilder result = new StringBuilder();
		boolean first = true;
		for (Map.Entry<String, String> entry : params.entrySet()) {
			if (first)
				first = false;
			else
				result.append("&");

			result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
			result.append("=");
			result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
		}

		return result.toString();
	}

	public boolean login(String username, String password) {
		if (this.loggedIn) {
			return true;
		}

		this.username = username;
		this.password = password;
		try {
			Map<String, String> params = new HashMap<>();
			params.put("username", this.username);
			params.put("password", this.password);
			URL url = new URL("http://assemblersim.de/cloud/api/login/");
			String response = httpPost(url, params);
			System.out.println(response);
			JSONObject json = new JSONObject(response);
			if (json.getString("status").equals("ok")) {
				this.loggedIn = true;
				System.out.println("logged in");
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		initialize();

		return this.loggedIn;
	}

	public void initialize() {
		File f = new File("res/cloud/");
		if (!f.exists()) {
			f.mkdirs();
		}
		// delete all files for a clean cloud folder
		for (File fileInFolder : f.listFiles()) {
			fileInFolder.delete();
		}
		list();
		Iterator<CloudFile> i = fileModel.getFiles().iterator();
		while (i.hasNext()) {
			CloudFile cf = (CloudFile) i.next();
			System.out.println(cf.title);
			File file = new File("res/cloud/" + cf.getTitle());
			FileWriter fw;
			BufferedWriter br;

			try {
				fw = new FileWriter(file);
				br = new BufferedWriter(fw);
				br.write(cf.content);
				br.flush();
				br.close();
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void list() {

		if (!this.loggedIn) {
			return;
		}

		try {
			Map<String, String> params = new HashMap<>();
			params.put("username", this.username);
			params.put("password", this.password);
			URL url = new URL("http://assemblersim.de/cloud/api/list/");
			String response = httpPost(url, params);
			System.out.println("response: " + response);
			JSONObject json = new JSONObject(response);
			JSONArray array = json.getJSONArray("files");
			Iterator<Object> i = array.iterator();
			while (i.hasNext()) {
				JSONObject obj = (JSONObject) i.next();
				CloudFile file = new CloudFile(obj.get("hash").toString(), obj.get("title").toString(),
						obj.get("content").toString(), obj.get("created").toString(), obj.get("modified").toString());
				fileModel.addFile(file);
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public String getUsername() {
		return this.username;
	}

	public String getPassword() {
		return this.password;
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public CloudFileModel getCloudFileModel() {
		return this.fileModel;
	}

}
