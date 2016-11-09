package org.jenkinsci.plugins.maven_artifact_choicelistprovider.central;

import java.util.List;

import org.jenkinsci.plugins.maven_artifact_choicelistprovider.VersionReaderException;
import org.junit.After;
import org.junit.Test;

public class MavenCentralSearchServiceTest {

	@After
	public void before() {
		System.out.println("---");
	}

	@Test
	public void testSth() throws VersionReaderException {
		MavenCentralSearchService t = new MavenCentralSearchService("org.apache.tomcat", "tomcat", ".tar.gz");
		try {
			List<String> retrieveVersions = t.retrieveVersions();
			System.out.println(retrieveVersions.size());
			for(String current : retrieveVersions) {
				System.out.println(current);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
