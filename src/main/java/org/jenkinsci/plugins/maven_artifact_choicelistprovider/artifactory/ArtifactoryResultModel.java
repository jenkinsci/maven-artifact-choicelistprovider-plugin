package org.jenkinsci.plugins.maven_artifact_choicelistprovider.artifactory;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * Helper Class to parse the JSON
 *
 * @author stephan.watermeyer, Diebold Nixdorf
 */
public class ArtifactoryResultModel {

	@SerializedName("results")
	public ArtifactoryResultEntryModel[] results;

	public ArtifactoryResultEntryModel[] getResults() {
		return results;
	}

	public void setResults(ArtifactoryResultEntryModel[] results) {
		this.results = results;
	}

}

/**
 * Helper Class to parse the JSON
 *
 * @author stephan.watermeyer, Diebold Nixdorf
 */
class ArtifactoryResultEntryModel {

	@SerializedName("uri")
	public String uri;

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

}
