package org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus;

import org.junit.Test;

import junit.framework.TestCase;

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
}
