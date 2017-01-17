package org.jenkinsci.plugins.maven_artifact_choicelistprovider.artifactory;

import com.google.gson.annotations.SerializedName;

/**
 * Helper Class to parse the JSON
 *
 * @author stephan.watermeyer, Diebold Nixdorf
 */
public class ArtifactoryResultEntryModel {

	@SerializedName("uri")
	public String uri;

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

}