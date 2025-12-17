package org.jenkinsci.plugins.maven_artifact_choicelistprovider;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ValidAndInvalidClassifierTest {

    @Test
    void testOnlyOneInvalid() {
        ValidAndInvalidClassifier c = ValidAndInvalidClassifier.fromString("!preinstalled");
        assertFalse(c.isValid("preinstalled"), "must be invalid");
        assertTrue(c.isValid("foo"), "must be valid");
    }

    @Test
    void testOnlyOneValid() {
        ValidAndInvalidClassifier c = ValidAndInvalidClassifier.fromString("preinstalled");
        assertTrue(c.isValid("preinstalled"), "must be valid");
        assertFalse(c.isValid("foo"), "must be invalid");
    }

    @Test
    void testMultipleInvalid() {
        ValidAndInvalidClassifier c = ValidAndInvalidClassifier.fromString("!preinstalled,!source");
        assertFalse(c.isValid("preinstalled"), "must be invalid");
        assertFalse(c.isValid("source"), "must be invalid");
        assertTrue(c.isValid("foo"), "must be valid");
    }

    @Test
    void testMultipleValid() {
        ValidAndInvalidClassifier c = ValidAndInvalidClassifier.fromString("preinstalled,source");
        assertTrue(c.isValid("preinstalled"), "must be invalid");
        assertTrue(c.isValid("source"), "must be invalid");
        assertFalse(c.isValid("foo"), "must be valid");
    }

    @Test
    void testWithNull() {
        ValidAndInvalidClassifier c = ValidAndInvalidClassifier.fromString("");
        assertTrue(c.isValid(null), "must be valid by default");
        assertTrue(c.isValid(""), "must be valid by default");
        assertTrue(c.isValid("foo"), "must be valid by default");
    }

    @Test
    void testInit() {
        ValidAndInvalidClassifier c = ValidAndInvalidClassifier.fromString(",,,");
        assertTrue(c.isValid("foo"), "must be valid by default");
    }

    @Test
    void testToString() {
        ValidAndInvalidClassifier c = ValidAndInvalidClassifier.fromString("!preinstalled");
        System.out.println(c);
    }
}
