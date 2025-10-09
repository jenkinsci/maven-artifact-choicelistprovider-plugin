package org.jenkinsci.plugins.maven_artifact_choicelistprovider.artifactory;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Set;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.ValidAndInvalidClassifier;
import org.junit.jupiter.api.Test;

class ArtifactorySearchServiceTest {

    @Test
    void testWithoutExplicitQualifier() throws Exception {
        ArtifactorySearchService s = new ArtifactorySearchService("https://repo.jenkins-ci.org/");
        List<String> retrieveVersions = s.retrieveVersions(
                "",
                "org.jenkins-ci.plugins",
                "maven-artifact-choicelistprovider",
                "",
                ValidAndInvalidClassifier.getDefault());
        for (String current : retrieveVersions) {
            System.out.println(current);
        }
    }

    @Test
    void testWithExplicitQualifier() throws Exception {
        ArtifactorySearchService s = new ArtifactorySearchService("https://repo.jenkins-ci.org/");
        List<String> retrieveVersions = s.retrieveVersions(
                "",
                "org.jenkins-ci.plugins",
                "maven-artifact-choicelistprovider",
                "",
                ValidAndInvalidClassifier.fromString("sources"));
        for (String current : retrieveVersions) {
            System.out.println(current);
        }
    }

    @Test
    void testWithPackaging() throws Exception {
        ArtifactorySearchService s = new ArtifactorySearchService("https://repo.jenkins-ci.org/");
        List<String> retrieveVersions = s.retrieveVersions(
                "",
                "org.jenkins-ci.plugins",
                "maven-artifact-choicelistprovider",
                "hpi",
                ValidAndInvalidClassifier.getDefault());
        for (String current : retrieveVersions) {
            assertTrue(current.endsWith("hpi"), "must return an artifact URL for hpi files: " + current);
        }
    }

    @Test
    void testParseJSON() {
        String toParse =
                "{  \"results\" : [ {    \"uri\" : \"https://repo.jenkins-ci.org/jenkinsci/api/storage/releases/org/jenkins-ci/plugins/maven-artifact-choicelistprovider/1.0.1/maven-artifact-choicelistprovider-1.0.1.hpi\"  }, {    \"uri\" : \"https://repo.jenkins-ci.org/jenkinsci/api/storage/snapshots/org/jenkins-ci/plugins/maven-artifact-choicelistprovider/1.0.1-SNAPSHOT/maven-artifact-choicelistprovider-1.0.1-20161121.111338-1.hpi\"  } ]}";
        ArtifactorySearchService s = new ArtifactorySearchService("https://repo.jenkins-ci.org/");
        Set<String> parsed = s.parseResult(toParse, "");
        assertNotNull(parsed);
        assertEquals(2, parsed.size());
    }
}
