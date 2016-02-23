package de.doubleslash.jedm.generate;

/**
 * Contains all paths to the needed files.
 * 
 * @author swild
 */
public enum FilePaths {

	CENTRAL_XML_NAME("/central-rules.xml");

	private String filePath;

	FilePaths(final String filePath) {
		this.filePath = filePath;
	}

	public String path() {
		return filePath;
	}
}
