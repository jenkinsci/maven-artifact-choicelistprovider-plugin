package org.jenkinsci.plugins.maven_artifact_choicelistprovider;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import org.junit.Test;

import static org.junit.Assert.*;

public class ArtifactFilterTest {

    // use sealed list to catch hidden modification attempts
    private final List<String> testInput = Collections.unmodifiableList(Arrays.asList(
            "https://maven.example.com/content/repositories/releases-public/com/example/studio/example-studio-core/200.0.0-NEW",
            "https://maven.example.com/content/repositories/snapshots-public/com/example/studio/example-studio-core/200.0.0-NEW-SNAPSHOT",
            "https://maven.example.com/content/repositories/snapshots-public/com/example/studio/example-studio-core/1.9.0-SNAPSHOT",
            "https://maven.example.com/content/repositories/snapshots-public/com/example/studio/example-studio-core/1.8.2-SNAPSHOT",
            "https://maven.example.com/content/repositories/releases-public/com/example/studio/example-studio-core/1.8.1",
            "https://maven.example.com/content/repositories/snapshots-public/com/example/studio/example-studio-core/1.8.1-SNAPSHOT",
            "https://maven.example.com/content/repositories/releases-public/com/example/studio/example-studio-core/1.8.0",
            "https://maven.example.com/content/repositories/snapshots-public/com/example/studio/example-studio-core/1.8.0-SNAPSHOT",
            "https://maven.example.com/content/repositories/releases-public/com/example/studio/example-studio-core/1.8.0-BETA2",
            "https://maven.example.com/content/repositories/releases-public/com/example/studio/example-studio-core/1.8.0-BETA",
            "https://maven.example.com/content/repositories/releases-public/com/example/studio/example-studio-core/1.7.2",
            "https://maven.example.com/content/repositories/releases-public/com/example/studio/example-studio-core/1.7.1",
            "https://maven.example.com/content/repositories/releases-public/com/example/studio/example-studio-core/1.7.0"
    ));

    @Test
    public void testFilterOutByInverse() {
        boolean inverse = true;
        String pattern = ".*NEW.*";
        List<String> filteredList = AbstractMavenArtifactChoiceListProvider.filterArtifacts(testInput, inverse, pattern);

        assertEquals("Expected only the 200.0.0-NEW and 200.0.0-NEW-SNAPSHOT removed from original list", testInput.size()-2, filteredList.size());
    }

    @Test
    public void testFilterOutByAdvancedRegexp() {
        boolean inverse = false;
        String pattern = "(?!.*NEW).*";
        List<String> filteredList = AbstractMavenArtifactChoiceListProvider.filterArtifacts(testInput, inverse, pattern);

        assertEquals("Expected only the 200.0.0-NEW and 200.0.0-NEW-SNAPSHOT removed from original list", testInput.size()-2, filteredList.size());
    }

    @Test
    public void testFilterIn() {
        boolean inverse = false;
        String pattern = ".*NEW.*";
        List<String> filteredList = AbstractMavenArtifactChoiceListProvider.filterArtifacts(testInput, inverse, pattern);

        assertEquals("Expected only the 200.0.0-NEW and 200.0.0-NEW-SNAPSHOT remained", 2, filteredList.size());
    }

    @Test
    public void testFilterInWholeURL() {
        boolean inverse = false;
        String pattern = ".*/releases-public/.*NEW.*";
        List<String> filteredList = AbstractMavenArtifactChoiceListProvider.filterArtifacts(testInput, inverse, pattern);

        assertEquals("Expected only the 200.0.0-NEW remained", 1, filteredList.size());
    }

    @Test
    public void testDefaultFilter() {
        boolean inverse = false;
        String pattern = ".*";
        List<String> filteredList = AbstractMavenArtifactChoiceListProvider.filterArtifacts(testInput, inverse, pattern);

        assertEquals("Expected all items from original list", testInput.size(), filteredList.size());
    }

    @Test
    public void testInputsFromJobsBeforeTheUpgrade() {
        boolean inverse = false;
        String pattern = null;
        List<String> filteredList = AbstractMavenArtifactChoiceListProvider.filterArtifacts(testInput, inverse, pattern);

        assertEquals("Expected all items from original list to provide nice upgrade experience", testInput.size(), filteredList.size());
    }

    @Test(expected = PatternSyntaxException.class)
    public void testInvalidRegexp() {
        boolean inverse = true;
        String pattern = "(";
        AbstractMavenArtifactChoiceListProvider.filterArtifacts(testInput, inverse, pattern);
    }
}
