package org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus3;

import java.util.List;

import org.jenkinsci.plugins.maven_artifact_choicelistprovider.ValidAndInvalidClassifier;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.VersionReaderException;

public class Nexus3RestApiSearchServiceTest  {

	public static void main(String args[]) throws VersionReaderException {
		testWithoutExplicitQualifier();
	}
    
    public static void testWithoutExplicitQualifier() throws VersionReaderException {
        Nexus3RestApiSearchService s = new Nexus3RestApiSearchService("https://davis.dieboldnixdorf.com/n3/");
        s.setUserName("TBD");
        s.setUserPassword("TBD");
        List<String> retrieveVersions = s.retrieveVersions("maven-central", "org.apache.ant", "ant", "",
                ValidAndInvalidClassifier.getDefault());
        for (String current : retrieveVersions) {
            System.out.println(current);
        }
    }
}
