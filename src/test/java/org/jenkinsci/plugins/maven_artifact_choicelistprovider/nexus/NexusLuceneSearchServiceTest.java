package org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.ValidAndInvalidClassifier;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.VersionReaderException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 *
 * Integration Test that verifies the retrieval works as expected. Don't forget
 * to set a proxy, if you are using one.
 *
 * The alfreso nexus is used, as it is available in public domain.
 *
 * @author stephan.watermeyer, Diebold Nixdorf
 */
class NexusLuceneSearchServiceTest {

    @Test
    void testWithNull() {
        NexusLuceneSearchService s = new NexusLuceneSearchService(null);

        assertThrows(VersionReaderException.class, () -> s.retrieveVersions(null, null, null, null, null));
    }

    @Test
    @Disabled("FIXME: Underlying implementation seems broken")
    void testWithoutExplicitQualifier() throws Exception {
        NexusLuceneSearchService s = new NexusLuceneSearchService("https://artifacts.alfresco.com/nexus/");
        List<String> retrieveVersions =
                s.retrieveVersions("", "org.apache.tomcat", "tomcat", "", ValidAndInvalidClassifier.getDefault());
        for (String current : retrieveVersions) {
            System.out.println(current);
        }
    }

    @Test
    @Disabled("FIXME: Underlying implementation seems broken")
    void testWithQualifier() throws Exception {
        NexusLuceneSearchService s = new NexusLuceneSearchService("https://artifacts.alfresco.com/nexus/");
        List<String> retrieveVersions = s.retrieveVersions(
                "", "org.apache.tomcat", "tomcat", "tgz", ValidAndInvalidClassifier.fromString("linux"));
        for (String current : retrieveVersions) {
            System.out.println(current);
            assertTrue(current.contains("tgz"));
            assertTrue(current.contains("linux"));
        }
    }

    @Test
    @Disabled("FIXME: Underlying implementation seems broken")
    void testWithNegativeQualifier() throws Exception {
        NexusLuceneSearchService s = new NexusLuceneSearchService("https://artifacts.alfresco.com/nexus/");
        List<String> retrieveVersions = s.retrieveVersions(
                "", "org.apache.tomcat", "tomcat", "tgz", ValidAndInvalidClassifier.fromString("!linux,!osx"));
        for (String current : retrieveVersions) {
            System.out.println(current);
            assertFalse(current.endsWith("linux.tgz"));
            assertFalse(current.endsWith("osx.tgz"));
        }
    }

    @Test
    @Disabled("FIXME: Underlying implementation seems broken")
    void testWithNotExistentQualifier() throws Exception {
        NexusLuceneSearchService s = new NexusLuceneSearchService("https://artifacts.alfresco.com/nexus/");
        List<String> retrieveVersions = s.retrieveVersions(
                "", "org.apache.tomcat", "tomcat", "tgz", ValidAndInvalidClassifier.fromString("foobar"));
        for (String current : retrieveVersions) {
            System.out.println(current);
        }
        assertTrue(retrieveVersions.isEmpty(), "should not return any results");
    }
}
