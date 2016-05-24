package com.wincornixdorf.jenkins.plugins.jenkins_extensible_choice_plugin.maven_artifact;

import static org.junit.Assert.*;

import org.junit.Test;

public class ValidAndInvalidClassifierTest {

	@Test
	public void testInit() {
		ValidAndInvalidClassifier c = ValidAndInvalidClassifier.fromString("!preinstalled");
		assertTrue("must be invalid", c.isInvalid("preinstalled"));
	}
	
	@Test
	public void testToString() {
		ValidAndInvalidClassifier c = ValidAndInvalidClassifier.fromString("!preinstalled");
		System.out.println(c.toString());
	}
}
