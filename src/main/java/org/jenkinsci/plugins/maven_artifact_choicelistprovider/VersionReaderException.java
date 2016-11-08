package org.jenkinsci.plugins.maven_artifact_choicelistprovider;

public class VersionReaderException extends Exception {

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 6016339275957214183L;

	public VersionReaderException() {
		super();
	}

	public VersionReaderException(String message, Throwable cause) {
		super(message, cause);
	}

	public VersionReaderException(String message) {
		super(message);
	}

	public VersionReaderException(Throwable cause) {
		super(cause);
	}

}
