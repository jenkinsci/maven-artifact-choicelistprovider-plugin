package org.jenkinsci.plugins.maven_artifact_choicelistprovider.artifactory;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Set;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.ValidAndInvalidClassifier;
import org.junit.jupiter.api.Test;

class AbstractArtifactoryGAVCSearchServiceTest {

    @Test
    void testWithoutExplicitQualifier() throws Exception {
        AbstractArtifactoryGAVCSearchService s = new AbstractArtifactoryGAVCSearchService("https://repo.jenkins-ci.org/");
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
        AbstractArtifactoryGAVCSearchService s = new AbstractArtifactoryGAVCSearchService("https://repo.jenkins-ci.org/");
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
        AbstractArtifactoryGAVCSearchService s = new AbstractArtifactoryGAVCSearchService("https://repo.jenkins-ci.org/");
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
}
