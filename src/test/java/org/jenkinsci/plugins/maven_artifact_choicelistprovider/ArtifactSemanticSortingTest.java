package org.jenkinsci.plugins.maven_artifact_choicelistprovider;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ArtifactSemanticSortingTest {

    // use sealed list to catch hidden modification attempts
    private final List<String> unsortedList = Collections.unmodifiableList(Arrays.asList(
            "https://maven.example.com/content/repositories/releases-public/com/example/studio/example-studio-core/1.0.0-beta.11",
            "https://maven.example.com/content/repositories/releases-public/com/example/studio/example-studio-core/1.0.0",
            "https://maven.example.com/content/repositories/releases-public/com/example/studio/example-studio-core/1.0.0-rc.1",
            "https://maven.example.com/content/repositories/releases-public/com/example/studio/example-studio-core/1.0.0-alpha.1",
            "https://maven.example.com/content/repositories/releases-public/com/example/studio/example-studio-core/1.0.0-beta.2",
            "https://maven.example.com/content/repositories/releases-public/com/example/studio/example-studio-core/1.0.0-alpha.beta",
            "https://maven.example.com/content/repositories/releases-public/com/example/studio/example-studio-core/1.0.0-beta",
            "https://maven.example.com/content/repositories/releases-public/com/example/studio/example-studio-core/1.0.0-alpha",
            "https://maven.example.com/content/repositories/releases-public/com/example/studio/example-studio-core/1.10.0",
            "https://maven.example.com/content/repositories/releases-public/com/example/studio/example-studio-core/1.9.21",
            "https://maven.example.com/content/repositories/releases-public/com/example/studio/example-studio-core/200.0.0",
            "https://maven.example.com/content/repositories/releases-public/com/example/studio/example-studio-core/199.20.3"
    ));

    private final List<String> expectedSortedList = Collections.unmodifiableList(Arrays.asList(
        "https://maven.example.com/content/repositories/releases-public/com/example/studio/example-studio-core/1.0.0-alpha",
        "https://maven.example.com/content/repositories/releases-public/com/example/studio/example-studio-core/1.0.0-alpha.1",
        "https://maven.example.com/content/repositories/releases-public/com/example/studio/example-studio-core/1.0.0-alpha.beta",
        "https://maven.example.com/content/repositories/releases-public/com/example/studio/example-studio-core/1.0.0-beta",
        "https://maven.example.com/content/repositories/releases-public/com/example/studio/example-studio-core/1.0.0-beta.2",
        "https://maven.example.com/content/repositories/releases-public/com/example/studio/example-studio-core/1.0.0-beta.11",
        "https://maven.example.com/content/repositories/releases-public/com/example/studio/example-studio-core/1.0.0-rc.1",
        "https://maven.example.com/content/repositories/releases-public/com/example/studio/example-studio-core/1.0.0",
        "https://maven.example.com/content/repositories/releases-public/com/example/studio/example-studio-core/1.9.21",
        "https://maven.example.com/content/repositories/releases-public/com/example/studio/example-studio-core/1.10.0",
        "https://maven.example.com/content/repositories/releases-public/com/example/studio/example-studio-core/199.20.3",
        "https://maven.example.com/content/repositories/releases-public/com/example/studio/example-studio-core/200.0.0"
        ));

    @Test
    public void testSemanticVersionSorting() {
        List<String> sortedList = AbstractMavenArtifactChoiceListProvider.semanticVersionSortArtifacts(unsortedList);
        assertEquals("Expected sorted list of URLS when provided an unsorted list", expectedSortedList, sortedList);
    }
}
