package org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus;

import static org.junit.Assert.*;

import java.util.List;

import org.jenkinsci.plugins.maven_artifact_choicelistprovider.ValidAndInvalidClassifier;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.VersionReaderException;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus.NexusLuceneSearchService;
import org.junit.After;
import org.junit.Test;

/**
 * 
 * Integration Test that verifies the retrieval works as expected. Don't forget to set a proxy, if you are using one.
 * 
 * The alfreso nexus is used, as it is available in public domain.
 *
 * @author stephan.watermeyer, Diebold Nixdorf
 */
public class NexusLuceneSearchServiceTest {

	@After
	public void before() {
		System.out.println("---");
	}

	@Test
	public void testWithNull() throws VersionReaderException {
		NexusLuceneSearchService s = new NexusLuceneSearchService(null);

		try {
			s.retrieveVersions(null, null, null, null, null);
			fail("shouldn work");
		} catch (VersionReaderException e) {
			// expected
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testWithoutExplicitQualifier() throws VersionReaderException {
		NexusLuceneSearchService s = new NexusLuceneSearchService("https://artifacts.alfresco.com/nexus/");
		List<String> retrieveVersions = s.retrieveVersions("", "org.apache.tomcat", "tomcat", "",
				ValidAndInvalidClassifier.getDefault());
		for (String current : retrieveVersions) {
			System.out.println(current);
		}
	}

	@Test
	public void testWithQualifier() throws VersionReaderException {
		NexusLuceneSearchService s = new NexusLuceneSearchService("https://artifacts.alfresco.com/nexus/");
		List<String> retrieveVersions = s.retrieveVersions("", "org.apache.tomcat", "tomcat", "tgz",
				ValidAndInvalidClassifier.fromString("linux"));
		for (String current : retrieveVersions) {
			System.out.println(current);
			assertTrue(current.contains("tgz"));
			assertTrue(current.contains("linux"));
		}
	}

	@Test
	public void testWithNegativeQualifier() throws VersionReaderException {
		NexusLuceneSearchService s = new NexusLuceneSearchService("https://artifacts.alfresco.com/nexus/");
		List<String> retrieveVersions = s.retrieveVersions("", "org.apache.tomcat", "tomcat", "tgz",
				ValidAndInvalidClassifier.fromString("!linux,!osx"));
		for (String current : retrieveVersions) {
			System.out.println(current);
			assertFalse(current.endsWith("linux.tgz"));
			assertFalse(current.endsWith("osx.tgz"));
		}
	}

	@Test
	public void testWithNotExistentQualifier() throws VersionReaderException {
		NexusLuceneSearchService s = new NexusLuceneSearchService("https://artifacts.alfresco.com/nexus/");
		List<String> retrieveVersions = s.retrieveVersions("", "org.apache.tomcat", "tomcat", "tgz",
				ValidAndInvalidClassifier.fromString("foobar"));
		for (String current : retrieveVersions) {
			System.out.println(current);
		}
		assertTrue("should not return any results", retrieveVersions.isEmpty());
	}
}
