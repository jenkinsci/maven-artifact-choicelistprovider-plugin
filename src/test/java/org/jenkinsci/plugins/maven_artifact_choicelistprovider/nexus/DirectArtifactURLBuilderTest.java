package org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus;

import junit.framework.TestCase;
import org.junit.Test;

public class DirectArtifactURLBuilderTest extends TestCase {

    @Test
    public void testWithEndingSlash() {
        DirectArtifactURLBuilder builder = new DirectArtifactURLBuilder();
        builder.setNexusURL("http://foo.com/bar");

        final String urlA = builder.build();

        builder.setNexusURL("http://foo.com/bar/");
        final String urlB = builder.build();

        assertEquals(urlA, urlB);
    }

    @Test
    public void testWithClassifierDash() {
        DirectArtifactURLBuilder builder = new DirectArtifactURLBuilder();
        builder.setNexusURL("http://repo1.maven.org/maven2/");
        builder.setArtifactId("commons-lang3");
        builder.setClassifier("sources");
        builder.setGroupId("org.apache.commons");
        builder.setVersion("3.7");
        builder.setPackaging("jar");
        final String urlA = builder.build();
        assertTrue(urlA.endsWith("commons-lang3-3.7-sources.jar"));

        builder.setClassifier(null);
        final String urlB = builder.build();
        assertTrue(urlB.endsWith("commons-lang3-3.7.jar"));

        builder.setClassifier("");
        final String urlC = builder.build();
        assertTrue(urlC.endsWith("commons-lang3-3.7.jar"));
    }
}
