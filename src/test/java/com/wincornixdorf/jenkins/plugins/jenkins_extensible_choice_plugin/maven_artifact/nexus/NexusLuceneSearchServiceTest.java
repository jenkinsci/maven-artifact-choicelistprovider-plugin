package com.wincornixdorf.jenkins.plugins.jenkins_extensible_choice_plugin.maven_artifact.nexus;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Test;

import com.wincornixdorf.jenkins.plugins.jenkins_extensible_choice_plugin.maven_artifact.ValidAndInvalidClassifier;

public class NexusLuceneSearchServiceTest {

	@After
	public void before() {
		System.out.println("---");
	}

	@Test
	public void testWithoutExplicitQualifier() {
		NexusLuceneSearchService s = new NexusLuceneSearchService("https://davis.wincor-nixdorf.com/nexus",
				"com.wincornixdorf.pnc.releases", "pnc-brass-maven", "tar.gz");
		List<String> retrieveVersions = s.retrieveVersions();
		for (String current : retrieveVersions) {
			System.out.println(current);
		}
	}

	@Test
	public void testWithQualifier() {
		NexusLuceneSearchService s = new NexusLuceneSearchService("https://davis.wincor-nixdorf.com/nexus",
				"com.wincornixdorf.pnc.releases", "pnc-brass-maven", "tar.gz",
				ValidAndInvalidClassifier.fromString("preinstalled"));
		List<String> retrieveVersions = s.retrieveVersions();
		for (String current : retrieveVersions) {
			System.out.println(current);
		}
	}

	@Test
	public void testWithNegativeQualifier() {
		NexusLuceneSearchService s = new NexusLuceneSearchService("https://davis.wincor-nixdorf.com/nexus",
				"com.wincornixdorf.pnc.releases", "pnc-brass-maven", "tar.gz",
				ValidAndInvalidClassifier.fromString("!preinstalled"));
		List<String> retrieveVersions = s.retrieveVersions();
		for (String current : retrieveVersions) {
			System.out.println(current);
		}
	}

	@Test
	public void testWithNotExistentQualifier() {
		NexusLuceneSearchService s = new NexusLuceneSearchService("https://davis.wincor-nixdorf.com/nexus",
				"com.wincornixdorf.pnc.releases", "pnc-brass-maven", "tar.gz",
				ValidAndInvalidClassifier.fromString("dontexists"));
		List<String> retrieveVersions = s.retrieveVersions();
		for (String current : retrieveVersions) {
			System.out.println(current);
		}
		assertTrue("should not return any results", retrieveVersions.isEmpty());
	}
}
