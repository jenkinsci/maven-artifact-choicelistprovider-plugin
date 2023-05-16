package org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus3;

import org.jenkinsci.plugins.maven_artifact_choicelistprovider.RESTfulParameterBuilder;

/**
 * 
 * Creates URL Parameters for a NEXUS and Artifactory Repository.
 *
 * @author stephan.watermeyer, Diebold Nixdorf
 */
public class Nexus3RESTfulParameterBuilderForGenericArtifacts extends RESTfulParameterBuilder {

	public static final String PARAMETER_REPOSITORYID = "repository";

	public static final String PARAMETER_ARTIFACTID = "name";

	public static final String PARAMETER_TOKEN = "continuationToken";

	public static final String PARAMETER_SORT = "sort";

	@Override
	public String getRepositoryId() {
		return PARAMETER_REPOSITORYID;
	}

	@Override
	public String getArtifactId() {
		return PARAMETER_ARTIFACTID;
	}

	@Override
	public String getGroupId() {
		return null;
	}

	@Override
	public String getPackaging() {
		return null;
	}

	@Override
	public String getClassifier() {
		return null;
	}

	@Override
	public String getContinuationToken() {
		return PARAMETER_TOKEN;
	}

	@Override
	public String getSortOrder() {
		return PARAMETER_SORT;
	}

}
