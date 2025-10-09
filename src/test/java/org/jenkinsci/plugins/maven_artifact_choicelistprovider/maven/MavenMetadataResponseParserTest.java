package org.jenkinsci.plugins.maven_artifact_choicelistprovider.maven;

import static org.junit.jupiter.api.Assertions.*;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.VersionReaderException;
import org.junit.jupiter.api.Test;

class MavenMetadataResponseParserTest {

    @Test
    void test() throws Exception {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("maven-metadata.xml");
        assertNotNull(resourceAsStream);
        String mockedResponse = IOUtils.toString(resourceAsStream, Charset.forName("UTF-8"));

        MavenMetadataSearchService service = new MavenMetadataSearchService("");
        List<String> versions = service.parseVersions(mockedResponse);

        assertEquals(144, versions.size(), "Parsing couldn't find all versions");
        assertEquals("200.0.0-ASSA-SNAPSHOT", versions.get(versions.size() - 1), "Parsing didn't preserve order");
    }

    @Test
    void testEmpty() throws Exception {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("maven-metadata-empty.xml");
        assertNotNull(resourceAsStream);
        String mockedResponse = IOUtils.toString(resourceAsStream, Charset.forName("UTF-8"));

        MavenMetadataSearchService service = new MavenMetadataSearchService("");
        List<String> versions = service.parseVersions(mockedResponse);

        assertEquals(0, versions.size(), "Parsing should find 0 version");
    }

    @Test
    void testInvalidXML() throws Exception {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("maven-metadata.notxml");
        assertNotNull(resourceAsStream);
        String mockedResponse = IOUtils.toString(resourceAsStream, Charset.forName("UTF-8"));

        MavenMetadataSearchService service = new MavenMetadataSearchService("");

        assertThrows(VersionReaderException.class, () -> service.parseVersions(mockedResponse));
    }
}
