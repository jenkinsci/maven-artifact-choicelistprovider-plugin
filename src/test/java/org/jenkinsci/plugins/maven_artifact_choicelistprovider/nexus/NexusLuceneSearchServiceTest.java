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
		NexusLuceneSearchService s = new NexusLuceneSearchService(null, null, null, null, null);

		try {
			s.retrieveVersions();
			fail("shouldn work");
		} catch (VersionReaderException e) {
			// expected
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testWithoutExplicitQualifier() throws VersionReaderException {
		NexusLuceneSearchService s = new NexusLuceneSearchService("https://artifacts.alfresco.com/nexus/",
				"org.apache.tomcat", "tomcat", "");
		List<String> retrieveVersions = s.retrieveVersions();
		for (String current : retrieveVersions) {
			System.out.println(current);
		}
	}

	@Test
	public void testWithQualifier() throws VersionReaderException {
		NexusLuceneSearchService s = new NexusLuceneSearchService("https://artifacts.alfresco.com/nexus/",
				"org.apache.tomcat", "tomcat", "tgz", ValidAndInvalidClassifier.fromString("linux"));
		List<String> retrieveVersions = s.retrieveVersions();
		for (String current : retrieveVersions) {
			System.out.println(current);
			assertTrue(current.endsWith("linux.tgz"));
		}
	}

	@Test
	public void testWithNegativeQualifier() throws VersionReaderException {
		NexusLuceneSearchService s = new NexusLuceneSearchService("https://artifacts.alfresco.com/nexus/",
				"org.apache.tomcat", "tomcat", "tgz", ValidAndInvalidClassifier.fromString("!linux,!osx"));
		List<String> retrieveVersions = s.retrieveVersions();
		for (String current : retrieveVersions) {
			System.out.println(current);
			assertFalse(current.endsWith("linux.tgz"));
			assertFalse(current.endsWith("osx.tgz"));
		}
	}

	@Test
	public void testWithNotExistentQualifier() throws VersionReaderException {
		NexusLuceneSearchService s = new NexusLuceneSearchService("https://artifacts.alfresco.com/nexus/",
				"org.apache.tomcat", "tomcat", "tgz", ValidAndInvalidClassifier.fromString("foobar"));
		List<String> retrieveVersions = s.retrieveVersions();
		for (String current : retrieveVersions) {
			System.out.println(current);
		}
		assertTrue("should not return any results", retrieveVersions.isEmpty());
	}
}
