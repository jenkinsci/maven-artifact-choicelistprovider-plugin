package org.jenkinsci.plugins.maven_artifact_choicelistprovider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import hudson.model.Item;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class AbstractMavenArtifactChoiceListProviderTest {

    private static final List<String> TEST_INPUT = List.of(
            "https://maven.example.com/content/repositories/releases-public/com/example/example-core/1.8.1",
            "https://maven.example.com/content/repositories/releases-public/com/example/example-core/1.8.0");

    /**
     * @param pError if not null, the version reader fails with this message instead of returning {@link #TEST_INPUT}.
     */
    private static AbstractMavenArtifactChoiceListProvider createProvider(final String pError) {
        return new AbstractMavenArtifactChoiceListProvider("example-core") {
            @Override
            public IVersionReader createServiceInstance(Item item) {
                return new FakeVersionReader(TEST_INPUT, pError);
            }
        };
    }

    @Test
    void testListsFullUrls() {
        assertEquals(TEST_INPUT, createProvider(null).getChoiceList());
    }

    @Test
    void testErrorMessageShown() {
        assertEquals(List.of("boom"), createProvider("boom").getChoiceList());
    }

    @Test
    void testErrorMessageShownWhenSubclassSelectsKeys() {
        AbstractMavenArtifactChoiceListProvider provider = new AbstractMavenArtifactChoiceListProvider("example-core") {
            @Override
            public IVersionReader createServiceInstance(Item item) {
                return new FakeVersionReader(TEST_INPUT, "boom");
            }

            @Override
            protected Collection<String> selectChoiceList(Map<String, String> pChoices) {
                return pChoices.keySet();
            }
        };
        assertEquals(List.of("boom"), provider.getChoiceList());
    }

    /**
     * A provider whose {@link AbstractMavenArtifactChoiceListProvider#selectChoiceList} must never be called, because
     * the version reader always fails.
     *
     * @param pReader the failing version reader.
     */
    private static AbstractMavenArtifactChoiceListProvider createFailingProvider(final IVersionReader pReader) {
        return new AbstractMavenArtifactChoiceListProvider("example-core") {
            @Override
            public IVersionReader createServiceInstance(Item item) {
                return pReader;
            }

            @Override
            protected Collection<String> selectChoiceList(Map<String, String> pChoices) {
                return fail("selectChoiceList() must not be called for an error result, got: " + pChoices);
            }
        };
    }

    @Test
    void testErrorResultAlwaysReturnedOnVersionReaderException() {
        AbstractMavenArtifactChoiceListProvider provider =
                createFailingProvider(new FakeVersionReader(TEST_INPUT, "boom"));
        assertEquals(List.of("boom"), provider.getChoiceList());
    }

    @Test
    void testErrorResultAlwaysReturnedOnUnexpectedException() {
        AbstractMavenArtifactChoiceListProvider provider =
                createFailingProvider(new FakeVersionReader(TEST_INPUT, null) {
                    @Override
                    public List<String> retrieveVersions(
                            String pRepositoryId,
                            String pGroupId,
                            String pArtifactId,
                            String pPackaging,
                            ValidAndInvalidClassifier pAcceptedClassifier) {
                        throw new IllegalStateException("boom");
                    }
                });
        assertEquals(List.of("Unexpected Error: boom"), provider.getChoiceList());
    }
}
