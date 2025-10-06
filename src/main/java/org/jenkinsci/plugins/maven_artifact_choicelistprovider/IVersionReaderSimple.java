package org.jenkinsci.plugins.maven_artifact_choicelistprovider;

import java.util.List;

public interface IVersionReaderSimple {

	public List<String> retrieveVersions(String pRepositoryId, String pGroup, String pName) throws VersionReaderException;

	public void setUserName(final String pUserName);

	public void setUserPassword(final String pUserPassword);

	public void setCredentials(final String pUserName, final String pUserPassword);
}
