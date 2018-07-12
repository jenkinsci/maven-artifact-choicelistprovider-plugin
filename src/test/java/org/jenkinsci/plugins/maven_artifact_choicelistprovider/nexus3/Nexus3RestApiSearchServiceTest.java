package org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus3;

import java.util.List;

import org.jenkinsci.plugins.maven_artifact_choicelistprovider.ValidAndInvalidClassifier;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.VersionReaderException;
import org.junit.After;
import org.junit.Test;

import junit.framework.TestCase;

public class Nexus3RestApiSearchServiceTest extends TestCase {

    @After
    public void before() {
        System.out.println("---");
    }
    
    @Test
    public void testWithoutExplicitQualifier() throws VersionReaderException {
        Nexus3RestApiSearchService s = new Nexus3RestApiSearchService("https://davis.wincor-nixdorf.com/n3/");
        s.setUserName("TBD");
        s.setUserPassword("TBD");
        List<String> retrieveVersions = s.retrieveVersions("maven-central", "org.apache.ant", "ant", "",
                ValidAndInvalidClassifier.getDefault());
        for (String current : retrieveVersions) {
            System.out.println(current);
        }
    }
}
