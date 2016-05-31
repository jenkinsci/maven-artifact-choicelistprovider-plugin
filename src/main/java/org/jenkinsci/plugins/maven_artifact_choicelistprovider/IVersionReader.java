package org.jenkinsci.plugins.maven_artifact_choicelistprovider;

import java.util.List;

public interface IVersionReader {

	public List<String> retrieveVersions();
}
