package com.wincornixdorf.jenkins.plugins.jenkins_extensible_choice_plugin.maven_artifact.nexus;

import java.util.List;

import org.junit.Test;

public class NexusLuceneSearchServiceTest {

	@Test
	public void testInit() {
		NexusLuceneSearchService s = new NexusLuceneSearchService("https://davis.wincor-nixdorf.com/nexus",
				"com.wincornixdorf.pnc.releases", "pnc-brass-maven", "tar.gz");
		List<String> retrieveVersions = s.retrieveVersions();
		for (String current : retrieveVersions) {
			System.out.println(current);
		}
	}
}
