package org.jenkinsci.plugins.maven_artifact_choicelistprovider.central;

import com.google.gson.Gson;

/**
 * Wrapper for parsing a response from Maven Central Search Service
 *
 * @author stephan.watermeyer, Diebold Nixdorf
 */
public class MavenCentralResponseParser {

	public static MavenCentralResponseModel parse(String pContent) {
		return new Gson().fromJson(pContent, MavenCentralResponseModel.class);
	}
}
