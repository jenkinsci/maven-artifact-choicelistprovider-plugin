package org.jenkinsci.plugins.maven_artifact_choicelistprovider;

import java.util.List;

public interface IVersionReader {

	public List<String> retrieveVersions(String pGroupId, String pArtifactId, String pPackaging)
			throws VersionReaderException;

	public List<String> retrieveVersions(String pGroupId, String pArtifactId, String pPackaging,
			ValidAndInvalidClassifier pAcceptedClassifier) throws VersionReaderException;

	public void setUserName(final String pUserName);

	public void setUserPassword(final String pUserPassword);

	public void setCredentials(final String pUserName, final String pUserPassword);
}
