package org.jenkinsci.plugins.maven_artifact_choicelistprovider.maven;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.xml.sax.SAXException;

public class MavenMetadataResponseParserTest {

    @Test
    public void test() throws IOException {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("maven-metadata.xml");
        assertNotNull(resourceAsStream);
        String mockedResponse = IOUtils.toString(resourceAsStream);

        List<String> versions = MavenMetadataSearchService.parseVersions(mockedResponse);

        assertEquals("Parsing couldn't find all versions", 144, versions.size());
        assertEquals("Parsing didn't preserve order", "200.0.0-ASSA-SNAPSHOT", versions.get(versions.size() - 1));
    }

    @Test
    public void testEmpty() throws IOException {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("maven-metadata-empty.xml");
        assertNotNull(resourceAsStream);
        String mockedResponse = IOUtils.toString(resourceAsStream);

        List<String> versions = MavenMetadataSearchService.parseVersions(mockedResponse);

        assertEquals("Parsing should find 0 version", 0, versions.size());
    }

    @Test
    public void testInvalidXML() throws IOException {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("maven-metadata.notxml");
        assertNotNull(resourceAsStream);
        String mockedResponse = IOUtils.toString(resourceAsStream);
        try {
            MavenMetadataSearchService.parseVersions(mockedResponse);
        } catch (RuntimeException e) {
            if (e.getCause() instanceof SAXException) {
                return; //happy path
            }
        }
        fail("Expected a stack of RuntimeException(SAXException) exception");
    }
}
