package org.jenkinsci.plugins.maven_artifact_choicelistprovider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.regex.PatternSyntaxException;
import org.junit.jupiter.api.Test;

class ArtifactFilterTest {

    // use sealed list to catch hidden modification attempts
    private static final List<String> TEST_INPUT = List.of(
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
            "https://maven.example.com/content/repositories/releases-public/com/example/studio/example-studio-core/1.7.0");

    @Test
    void testFilterOutByInverse() {
        boolean inverse = true;
        String pattern = ".*NEW.*";
        List<String> filteredList =
                AbstractMavenArtifactChoiceListProvider.filterArtifacts(TEST_INPUT, inverse, pattern);

        assertEquals(
                TEST_INPUT.size() - 2,
                filteredList.size(),
                "Expected only the 200.0.0-NEW and 200.0.0-NEW-SNAPSHOT removed from original list");
    }

    @Test
    void testFilterOutByAdvancedRegexp() {
        boolean inverse = false;
        String pattern = "(?!.*NEW).*";
        List<String> filteredList =
                AbstractMavenArtifactChoiceListProvider.filterArtifacts(TEST_INPUT, inverse, pattern);

        assertEquals(
                TEST_INPUT.size() - 2,
                filteredList.size(),
                "Expected only the 200.0.0-NEW and 200.0.0-NEW-SNAPSHOT removed from original list");
    }

    @Test
    void testFilterIn() {
        boolean inverse = false;
        String pattern = ".*NEW.*";
        List<String> filteredList =
                AbstractMavenArtifactChoiceListProvider.filterArtifacts(TEST_INPUT, inverse, pattern);

        assertEquals(2, filteredList.size(), "Expected only the 200.0.0-NEW and 200.0.0-NEW-SNAPSHOT remained");
    }

    @Test
    void testFilterInWholeURL() {
        boolean inverse = false;
        String pattern = ".*/releases-public/.*NEW.*";
        List<String> filteredList =
                AbstractMavenArtifactChoiceListProvider.filterArtifacts(TEST_INPUT, inverse, pattern);

        assertEquals(1, filteredList.size(), "Expected only the 200.0.0-NEW remained");
    }

    @Test
    void testDefaultFilter() {
        boolean inverse = false;
        String pattern = AbstractMavenArtifactChoiceListProvider.DEFAULT_REGEX_MATCH_ALL;
        List<String> filteredList =
                AbstractMavenArtifactChoiceListProvider.filterArtifacts(TEST_INPUT, inverse, pattern);

        assertEquals(TEST_INPUT.size(), filteredList.size(), "Expected all items from original list");
    }

    @Test
    void testInputsFromJobsBeforeTheUpgrade() {
        boolean inverse = false;
        String pattern = null;
        List<String> filteredList =
                AbstractMavenArtifactChoiceListProvider.filterArtifacts(TEST_INPUT, inverse, pattern);

        assertEquals(
                TEST_INPUT.size(),
                filteredList.size(),
                "Expected all items from original list to provide nice upgrade experience");
    }

    @Test
    void testInvalidRegexp() {
        boolean inverse = true;
        String pattern = "(";

        assertThrows(
                PatternSyntaxException.class,
                () -> AbstractMavenArtifactChoiceListProvider.filterArtifacts(TEST_INPUT, inverse, pattern));
    }
}
