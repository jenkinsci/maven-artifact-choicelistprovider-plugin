package org.jenkinsci.plugins.maven_artifact_choicelistprovider;

import java.util.List;

import javax.ws.rs.core.MultivaluedMap;

public interface IVersionReader2 {

	public List<String> retrieveVersions(MultivaluedMap<String, String> pParams) throws VersionReaderException;

	public void setUserName(final String pUserName);

	public void setUserPassword(final String pUserPassword);

	public void setCredentials(final String pUserName, final String pUserPassword);
}
