package org.jenkinsci.plugins.maven_artifact_choicelistprovider.central;

import java.util.List;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.ValidAndInvalidClassifier;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class MavenCentralSearchServiceTest {

    @Test
    @Disabled("FIXME: Underlying implementation seems broken")
    void testSth() throws Exception {
        MavenCentralSearchService t = new MavenCentralSearchService();
        List<String> retrieveVersions = t.retrieveVersions(
                "", "org.apache.tomcat", "tomcat", ".tar.gz", ValidAndInvalidClassifier.getDefault());
        System.out.println(retrieveVersions.size());
        for (String current : retrieveVersions) {
            System.out.println(current);
        }
    }
}
