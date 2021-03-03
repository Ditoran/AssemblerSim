package de.assemblersim.application.cloud;

public class CloudFile {
	/**
	 * 
	 */
	private String hash = "";
	String title = "";
	String content = "";
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

	public String getModificationDate() {
		return modified;
	}

	public void setModified(String modified) {
		this.modified = modified;
	}

}