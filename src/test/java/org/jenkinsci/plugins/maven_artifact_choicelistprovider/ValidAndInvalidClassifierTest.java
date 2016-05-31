package org.jenkinsci.plugins.maven_artifact_choicelistprovider;

import static org.junit.Assert.*;

import org.jenkinsci.plugins.maven_artifact_choicelistprovider.ValidAndInvalidClassifier;
import org.junit.Test;

public class ValidAndInvalidClassifierTest {

	@Test
	public void testOnlyOneInvalid() {
		ValidAndInvalidClassifier c = ValidAndInvalidClassifier.fromString("!preinstalled");
		assertFalse("must be invalid", c.isValid("preinstalled"));
		assertTrue("must be valid", c.isValid("foo"));
	}

	@Test
	public void testOnlyOneValid() {
		ValidAndInvalidClassifier c = ValidAndInvalidClassifier.fromString("preinstalled");
		assertTrue("must be valid", c.isValid("preinstalled"));
		assertFalse("must be invalid", c.isValid("foo"));
	}

	@Test
	public void testMultipleInvalid() {
		ValidAndInvalidClassifier c = ValidAndInvalidClassifier.fromString("!preinstalled,!source");
		assertFalse("must be invalid", c.isValid("preinstalled"));
		assertFalse("must be invalid", c.isValid("source"));
		assertTrue("must be valid", c.isValid("foo"));
	}
	
	@Test
	public void testMultipleValid() {
		ValidAndInvalidClassifier c = ValidAndInvalidClassifier.fromString("preinstalled,source");
		assertTrue("must be invalid", c.isValid("preinstalled"));
		assertTrue("must be invalid", c.isValid("source"));
		assertFalse("must be valid", c.isValid("foo"));
	}
	
	@Test
	public void testWithNull() {
		ValidAndInvalidClassifier c = ValidAndInvalidClassifier.fromString("");
		assertTrue("must be valid by default", c.isValid(null));
		assertTrue("must be valid by default", c.isValid(""));
		assertTrue("must be valid by default", c.isValid("foo"));
	}

	@Test
	public void testInit() {
		ValidAndInvalidClassifier c = ValidAndInvalidClassifier.fromString(",,,");
		assertTrue("must be valid by default", c.isValid("foo"));
	}

	
	@Test
	public void testToString() {
		ValidAndInvalidClassifier c = ValidAndInvalidClassifier.fromString("!preinstalled");
		System.out.println(c.toString());
	}
}
