package org.jenkinsci.plugins.maven_artifact_choicelistprovider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import hudson.model.Item;
import java.util.List;
import org.junit.jupiter.api.Test;

class ListVersionNumbersOnlyTest {

    private static final List<String> TEST_INPUT = List.of(
            "https://maven.example.com/content/repositories/releases-public/com/example/example-core/1.8.1",
            "https://maven.example.com/content/repositories/releases-public/com/example/example-core/1.8.0");

    private static AbstractMavenArtifactChoiceListProvider createProvider() {
        return createProvider(null);
    }

    /**
     * @param pError if not null, the version reader fails with this message instead of returning {@link #TEST_INPUT}.
     */
    private static AbstractMavenArtifactChoiceListProvider createProvider(final String pError) {
        return new AbstractMavenArtifactChoiceListProvider("example-core") {
            @Override
            public IVersionReader createServiceInstance(Item item) {
                return new IVersionReader() {
                    @Override
                    public List<String> retrieveVersions(
                            String pRepositoryId,
                            String pGroupId,
                            String pArtifactId,
                            String pPackaging,
                            ValidAndInvalidClassifier pAcceptedClassifier)
                            throws VersionReaderException {
                        if (pError != null) {
                            throw new VersionReaderException(pError);
                        }
                        return TEST_INPUT;
                    }

                    @Override
                    public void setUserName(String pUserName) {}

                    @Override
                    public void setUserPassword(String pUserPassword) {}

                    @Override
                    public void setCredentials(String pUserName, String pUserPassword) {}
                };
            }
        };
    }

    @Test
    void testDefaultListsFullUrls() {
        AbstractMavenArtifactChoiceListProvider provider = createProvider();
        assertFalse(provider.getListVersionNumbersOnly());
        assertEquals(TEST_INPUT, provider.getChoiceList());
    }

    @Test
    void testDisabledListsFullUrls() {
        AbstractMavenArtifactChoiceListProvider provider = createProvider();
        provider.setListVersionNumbersOnly(false);
        assertEquals(TEST_INPUT, provider.getChoiceList());
    }

    @Test
    void testEnabledListsVersionNumbersOnly() {
        AbstractMavenArtifactChoiceListProvider provider = createProvider();
        provider.setListVersionNumbersOnly(true);
        assertEquals(List.of("1.8.1", "1.8.0"), provider.getChoiceList());
    }

    @Test
    void testErrorMessageShownWhenDisabled() {
        AbstractMavenArtifactChoiceListProvider provider = createProvider("boom");
        assertEquals(List.of("boom"), provider.getChoiceList());
    }

    @Test
    void testErrorMessageShownWhenEnabled() {
        AbstractMavenArtifactChoiceListProvider provider = createProvider("boom");
        provider.setListVersionNumbersOnly(true);
        assertEquals(List.of("boom"), provider.getChoiceList());
    }
}
