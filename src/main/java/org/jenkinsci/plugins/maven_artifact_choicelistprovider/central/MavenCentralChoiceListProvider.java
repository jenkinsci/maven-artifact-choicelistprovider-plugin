package org.jenkinsci.plugins.maven_artifact_choicelistprovider.central;

import hudson.Extension;
import hudson.model.Item;
import hudson.util.FormValidation;
import java.util.Map;
import jp.ikedam.jenkins.plugins.extensible_choice_parameter.ChoiceListProvider;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.AbstractMavenArtifactChoiceListProvider;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.AbstractMavenArtifactDescriptorImpl;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.IVersionReader;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

/**
 *
 * The implementation of the {@link ChoiceListProvider} for MavenCentral repository.
 *
 * @author stephan.watermeyer, Diebold Nixdorf
 */
public class MavenCentralChoiceListProvider extends AbstractMavenArtifactChoiceListProvider {

    private static final long serialVersionUID = -4215624253720954168L;

    @DataBoundConstructor
    public MavenCentralChoiceListProvider(String artifactId) {
        super(artifactId);
    }

    @Extension
    public static class MavenDescriptorImpl extends AbstractMavenArtifactDescriptorImpl {

        /**
         * the display name shown in the dropdown to select a choice provider.
         *
         * @return display name
         * @see hudson.model.Descriptor#getDisplayName()
         */
        @Override
        public String getDisplayName() {
            return "MavenCentral Artifact Choice Parameter";
        }

        public FormValidation doTest(
                @QueryParameter String url,
                @QueryParameter String groupId,
                @QueryParameter String artifactId,
                @QueryParameter String packaging,
                @QueryParameter String classifier,
                @QueryParameter boolean inverseFilter,
                @QueryParameter String filterExpression,
                @QueryParameter boolean reverseOrder) {
            final IVersionReader service = new MavenCentralSearchService();
            return super.performTest(
                    service,
                    "",
                    groupId,
                    artifactId,
                    packaging,
                    classifier,
                    inverseFilter,
                    filterExpression,
                    reverseOrder);
        }

        @Override
        protected Map<String, String> wrapTestConnection(
                IVersionReader service,
                String pRepositoryId,
                String pGroupId,
                String pArtifactId,
                String pPackaging,
                String pClassifier,
                boolean pInverseFilter,
                String pFilterExpression,
                boolean pReverseOrder) {
            return readURL(
                    new MavenCentralSearchService(),
                    pRepositoryId,
                    pGroupId,
                    pArtifactId,
                    pPackaging,
                    pClassifier,
                    pInverseFilter,
                    pFilterExpression,
                    pReverseOrder);
        }
    }

    public IVersionReader createServiceInstance(Item item) {
        return new MavenCentralSearchService();
    }
}
