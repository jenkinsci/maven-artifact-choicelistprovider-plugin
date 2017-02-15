package org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus;

/**
 * 
 * This interface is to allow different implementation on how to retrieve an artifact from a Nexus repository. Therefore
 * this interface provides functionality to define the artifact details and a {@link #build()} method to create the URL
 * for the given artifact.
 *
 * @author stephan.watermeyer, Diebold Nixdorf
 */
public interface IArtifactURLBuilder {

	/**
	 * Based on the given details the URL for the artifact is created.
	 * 
	 * @return an URL where the artifact can be downloaded.
	 */
	String build();

	/**
	 * Based on the given details the URL for the artifact is created.
	 * 
	 * @param pOnlyBaseURL
	 *            <code>true</code> if only the URI should be returned.
	 * @return the URL or the folder where the artifact can be found or downloaded.
	 */
	String build(boolean pOnlyBaseURL);

	IArtifactURLBuilder setNexusURL(String repoURL);

	IArtifactURLBuilder setPackaging(String extension);

	IArtifactURLBuilder setClassifier(String classifier);

	IArtifactURLBuilder setArtifactId(String artifactId);

	IArtifactURLBuilder setGroupId(String groupId);

	IArtifactURLBuilder setVersion(String version);

	AbstractArtifactURLBuilder setRepositoryId(String repositoryId);

}
