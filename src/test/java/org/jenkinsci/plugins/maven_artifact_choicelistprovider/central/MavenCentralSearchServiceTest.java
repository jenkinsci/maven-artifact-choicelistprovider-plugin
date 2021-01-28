package org.jenkinsci.plugins.maven_artifact_choicelistprovider.central;

import java.util.List;

import org.jenkinsci.plugins.maven_artifact_choicelistprovider.ValidAndInvalidClassifier;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.VersionReaderException;

public class MavenCentralSearchServiceTest {

	public static void main(String args[]) throws VersionReaderException {
		testSth();
	}
	
	public static void testSth() throws VersionReaderException {
		MavenCentralSearchService t = new MavenCentralSearchService();
		try {
			List<String> retrieveVersions = t.retrieveVersions("", "org.apache.tomcat", "tomcat", ".tar.gz",
					ValidAndInvalidClassifier.getDefault());
			System.out.println(retrieveVersions.size());
			for (String current : retrieveVersions) {
				System.out.println(current);
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}
}
