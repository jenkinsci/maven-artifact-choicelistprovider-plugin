package org.jenkinsci.plugins.maven_artifact_choicelistprovider.maven;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.VersionReaderException;
import org.junit.Test;

public class MavenMetadataResponseParserTest {

	@Test
	public void test() throws IOException, VersionReaderException {
		InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("maven-metadata.xml");
		assertNotNull(resourceAsStream);
		String mockedResponse = IOUtils.toString(resourceAsStream, Charset.forName("UTF-8"));

		MavenMetadataSearchService service = new MavenMetadataSearchService("");
		try {
			List<String> versions = service.parseVersions(mockedResponse);
			assertEquals("Parsing couldn't find all versions", 144, versions.size());
			assertEquals("Parsing didn't preserve order", "200.0.0-ASSA-SNAPSHOT", versions.get(versions.size() - 1));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testEmpty() throws IOException, VersionReaderException {
		InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("maven-metadata-empty.xml");
		assertNotNull(resourceAsStream);
		String mockedResponse = IOUtils.toString(resourceAsStream, Charset.forName("UTF-8"));

		MavenMetadataSearchService service = new MavenMetadataSearchService("");
		List<String> versions = service.parseVersions(mockedResponse);

		assertEquals("Parsing should find 0 version", 0, versions.size());
	}

	@Test
	public void testInvalidXML() throws IOException, VersionReaderException {
		InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("maven-metadata.notxml");
		assertNotNull(resourceAsStream);
		String mockedResponse = IOUtils.toString(resourceAsStream, Charset.forName("UTF-8"));
		try {
			MavenMetadataSearchService service = new MavenMetadataSearchService("");
			service.parseVersions(mockedResponse);
			fail("Expected a stack of RuntimeException(SAXException) exception");
		} catch (VersionReaderException e) {
			// happy path
		}
	}
}
