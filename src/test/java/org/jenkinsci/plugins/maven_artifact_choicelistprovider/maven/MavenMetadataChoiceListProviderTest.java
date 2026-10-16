package org.jenkinsci.plugins.maven_artifact_choicelistprovider.maven;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import hudson.model.Item;
import java.util.List;
import java.util.Map;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.FakeVersionReader;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.IVersionReader;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.MavenArtifactChoiceListProviderUtils;
import org.junit.jupiter.api.Test;

class MavenMetadataChoiceListProviderTest {

    private static final List<String> TEST_INPUT = List.of(
            "https://maven.example.com/releases/com/example/example-core/1.8.1",
            "https://maven.example.com/releases/com/example/example-core/1.8.0");

    /**
     * @param pError if not null, the version reader fails with this message instead of returning {@link #TEST_INPUT}.
     */
    private static MavenMetadataChoiceListProvider createProvider(final String pError) {
        return new MavenMetadataChoiceListProvider("example-core") {
            @Override
            public IVersionReader createServiceInstance(Item item) {
                return new FakeVersionReader(TEST_INPUT, pError);
            }
        };
    }

    @Test
    void testDefaultListsFullUrls() {
        MavenMetadataChoiceListProvider provider = createProvider(null);
        assertFalse(provider.getListVersionNumbersOnly());
        assertEquals(TEST_INPUT, provider.getChoiceList());
    }

    @Test
    void testDisabledListsFullUrls() {
        MavenMetadataChoiceListProvider provider = createProvider(null);
        provider.setListVersionNumbersOnly(false);
        assertEquals(TEST_INPUT, provider.getChoiceList());
    }

    @Test
    void testEnabledListsVersionNumbersOnly() {
        MavenMetadataChoiceListProvider provider = createProvider(null);
        provider.setListVersionNumbersOnly(true);
        assertEquals(List.of("1.8.1", "1.8.0"), provider.getChoiceList());
    }

    @Test
    void testErrorMessageShownWhenDisabled() {
        MavenMetadataChoiceListProvider provider = createProvider("boom");
        assertEquals(List.of("boom"), provider.getChoiceList());
    }

    @Test
    void testErrorMessageShownWhenEnabled() {
        MavenMetadataChoiceListProvider provider = createProvider("boom");
        provider.setListVersionNumbersOnly(true);
        assertEquals(List.of("boom"), provider.getChoiceList());
    }

    // The "List Versions Now" button uses the same selection as the choice list.

    @Test
    void testButtonDisabledListsFullUrls() {
        Map<String, String> choices = MavenArtifactChoiceListProviderUtils.toMap(TEST_INPUT);
        assertEquals(TEST_INPUT, List.copyOf(MavenMetadataChoiceListProvider.selectChoiceList(choices, false)));
    }

    @Test
    void testButtonEnabledListsVersionNumbersOnly() {
        Map<String, String> choices = MavenArtifactChoiceListProviderUtils.toMap(TEST_INPUT);
        assertEquals(
                List.of("1.8.1", "1.8.0"),
                List.copyOf(MavenMetadataChoiceListProvider.selectChoiceList(choices, true)));
    }

    @Test
    void testButtonErrorMessageShownWhenEnabled() {
        Map<String, String> choices = Map.of("error", "boom");
        assertEquals(List.of("boom"), List.copyOf(MavenMetadataChoiceListProvider.selectChoiceList(choices, true)));
    }

    @Test
    void testButtonErrorMessageShownWhenDisabled() {
        Map<String, String> choices = Map.of("error", "boom");
        assertEquals(List.of("boom"), List.copyOf(MavenMetadataChoiceListProvider.selectChoiceList(choices, false)));
    }
}
