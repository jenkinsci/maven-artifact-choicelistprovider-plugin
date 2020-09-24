package org.jenkinsci.plugins.maven_artifact_choicelistprovider.azure;

import org.jenkinsci.plugins.maven_artifact_choicelistprovider.ValidAndInvalidClassifier;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.VersionReaderException;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus3.Nexus3RestApiSearchService;
import org.junit.After;
import org.junit.Test;

import java.util.List;


public class AzureDevopsFeedApiSearchServiceTest {

    @Test
    public void testWithoutExplicitQualifier() throws VersionReaderException {
        AzureSearchService s = new AzureSearchService("https://acme.pkgs.visualstudio.com/<a-GUID>",true);
        s.setUserName("tbs");
        s.setUserPassword("tbd");
        List<String> retrieveVersions = s.retrieveVersions("anazurefeedname", "com.acme", "syrupapp", "",
                ValidAndInvalidClassifier.getDefault());
        for (String current : retrieveVersions) {
            System.out.println(current);
        }
    }
}
