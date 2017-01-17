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

}


