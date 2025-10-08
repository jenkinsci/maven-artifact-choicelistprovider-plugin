package org.jenkinsci.plugins.maven_artifact_choicelistprovider.artifactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.ValidAndInvalidClassifier;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.VersionReaderException;

public class ArtifactorySearchServiceTest {

    public static void main() throws Exception {
        testWithoutExplicitQualifier();
        testWithExplicitQualifier();
        testWithPackaging();
        testParseJSON();
    }

    public static void testWithoutExplicitQualifier() throws VersionReaderException {
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

    public static void testWithExplicitQualifier() throws VersionReaderException {
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

    public static void testWithPackaging() throws VersionReaderException {
        ArtifactorySearchService s = new ArtifactorySearchService("https://repo.jenkins-ci.org/");
        List<String> retrieveVersions = s.retrieveVersions(
                "",
                "org.jenkins-ci.plugins",
                "maven-artifact-choicelistprovider",
                "hpi",
                ValidAndInvalidClassifier.getDefault());
        for (String current : retrieveVersions) {
            assertTrue("must return an artifact URL for hpi files: " + current, current.endsWith("hpi"));
        }
    }

    public static void testParseJSON() {
        String toParse =
                "{  \"results\" : [ {    \"uri\" : \"https://repo.jenkins-ci.org/jenkinsci/api/storage/releases/org/jenkins-ci/plugins/maven-artifact-choicelistprovider/1.0.1/maven-artifact-choicelistprovider-1.0.1.hpi\"  }, {    \"uri\" : \"https://repo.jenkins-ci.org/jenkinsci/api/storage/snapshots/org/jenkins-ci/plugins/maven-artifact-choicelistprovider/1.0.1-SNAPSHOT/maven-artifact-choicelistprovider-1.0.1-20161121.111338-1.hpi\"  } ]}";
        ArtifactorySearchService s = new ArtifactorySearchService("https://repo.jenkins-ci.org/");
        Set<String> parsed = s.parseResult(toParse, "");
        assertNotNull(parsed);
        assertEquals(2, parsed.size());
    }
}
