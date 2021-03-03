package de.assemblersim.application.cloud;

import java.util.ArrayList;
import java.util.List;

public class CloudFileModel {

	private List<CloudFile> files = new ArrayList<>();

	public CloudFileModel() {

	}

	public List<CloudFile> getFiles() {
		return files;
	}

	public void addFile(CloudFile file) {
		this.files.add(file);
	}
}
