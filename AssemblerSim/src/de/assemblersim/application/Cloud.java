package de.assemblersim.application;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class Cloud {
	
	private String username = "Admin";
	private String password = "test123";
	private boolean loggedIn = false;
	
	private List<CloudFile> files = new ArrayList<>();
	
	private static Cloud instance;
	private Cloud(){
		
	}
	
	public static Cloud getInstance(){
		if(instance==null){
			instance = new Cloud();
		}
		return instance;
	}
	
	private String httpGet(URL url){
		String response = "";
		try {
			
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
			InputStreamReader isr = new InputStreamReader(connection.getInputStream());
			BufferedReader br = new BufferedReader(isr);
			String line = "";
			
			while((line = br.readLine())!= null){
				response += line;
			}			
			
		} catch (Exception e) {
			System.out.println("Verbindung fehlgeschlagen!");
		}
		return response;
	}
	
	public boolean login(String username, String password){
		if(this.loggedIn){
			return true;
		}
		
		this.username = username;
		this.password = password;
		try {
			URL url = new URL("http://assemblersim.de/cloud/api/login/username="+this.username+"&password="+this.password);
			String response = httpGet(url);
			
			JSONObject json = new JSONObject(response);
			if(json.getString("status").equals("ok")){
				this.loggedIn = true;
				System.out.println("logged in");
			}
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		initialize();
		this.loggedIn = true;
		return this.loggedIn;
	}
	
	private void initialize(){
		File f = new File("res/cloud/");
		if (!f.exists()) {
			f.mkdirs();
		}
		//delete all files for a clean cloud folder
		for (File fileInFolder : f.listFiles()) {
			fileInFolder.delete();
		}
		list();
		Iterator<CloudFile> i = files.iterator();
		while (i.hasNext()) {
			CloudFile cf = (CloudFile) i.next();
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
	
	private void list(){
		
		if(!this.loggedIn){
			return;
		}
		
		try {
			URL url = new URL("http://assemblersim.de/cloud/api/list/username="+this.username+"&password="+this.password);
			String response = httpGet(url);
			
			JSONObject json = new JSONObject(response);
			JSONArray array = json.getJSONArray("files");
			Iterator<Object> i = array.iterator();
			while (i.hasNext()) {
				JSONObject obj = (JSONObject)i.next();
				CloudFile file = new CloudFile(obj.get("hash").toString(), obj.get("title").toString(), obj.get("content").toString(), obj.get("created").toString(), obj.get("modified").toString());
				files.add(file);
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
	
	public boolean isLoggedIn(){
		return loggedIn;
	}
	
	private class CloudFile{
		private String hash = "";
		private String title = "";
		private String content = "";
		private String created = "";
		private String modified = "";
		
		public CloudFile(String hash, String title, String content, String created, String modified) {
			this.hash = hash;
			this.title = title;
			this.content = content;
			this.created = created;
			this.modified = modified;
		}
		
		public String getHash() {
			return hash;
		}
		public void setHash(String hash) {
			this.hash = hash;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		public String getCreated() {
			return created;
		}
		public void setCreated(String created) {
			this.created = created;
		}
		public String getModified() {
			return modified;
		}
		public void setModified(String modified) {
			this.modified = modified;
		}
		
	}

}
