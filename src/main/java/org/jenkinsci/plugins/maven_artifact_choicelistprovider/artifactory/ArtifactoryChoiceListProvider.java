package org.jenkinsci.plugins.maven_artifact_choicelistprovider.artifactory;

import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl;
import hudson.Extension;
import hudson.model.Item;
import hudson.model.Job;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import java.util.Map;
import javax.inject.Inject;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.AbstractMavenArtifactChoiceListProvider;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.AbstractMavenArtifactDescriptorImpl;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.IVersionReader;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.interceptor.RequirePOST;

public class ArtifactoryChoiceListProvider extends AbstractMavenArtifactChoiceListProvider {

    private static final long serialVersionUID = -2254479209350956383L;

    private String url;
    private String credentialsId;

    @DataBoundConstructor
    public ArtifactoryChoiceListProvider(String artifactId) {
        super(artifactId);
    }

    @Extension
    public static class ArtifactoryDescriptorImpl extends AbstractMavenArtifactDescriptorImpl {

        @Inject
        private AbstractMavenArtifactChoiceListProvider.DescriptorImpl delegate;

        /**
         * the display name shown in the dropdown to select a choice provider.
         *
         * @return display name
         * @see hudson.model.Descriptor#getDisplayName()
         */
        @Override
        public String getDisplayName() {
            return "Artifactory Artifact Choice Parameter";
        }

        public ListBoxModel doFillCredentialsIdItems(@AncestorInPath Item item, @QueryParameter String credentialsId) {
            return delegate.doFillCredentialsIdItems(item, credentialsId);
        }

        @RequirePOST
        public FormValidation doTest(
                @AncestorInPath Item pItem,
                @QueryParameter String url,
                @QueryParameter String credentialsId,
                @QueryParameter String groupId,
                @QueryParameter String artifactId,
                @QueryParameter String packaging,
                @QueryParameter String classifier,
                @QueryParameter boolean inverseFilter,
                @QueryParameter String filterExpression,
                @QueryParameter boolean reverseOrder) {

            // SECURITY-1022
            pItem.checkPermission(Job.CONFIGURE);

            final IVersionReader service = new ArtifactorySearchService(url);

            // If configured, set User Credentials
            final UsernamePasswordCredentialsImpl c = getCredentials(credentialsId, pItem);
            if (c != null) {
                service.setCredentials(c.getUsername(), c.getPassword().getPlainText());
            }
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
                IVersionReader pService,
                String pRepositoryId,
                String pGroupId,
                String pArtifactId,
                String pPackaging,
                String pClassifier,
                boolean pInverseFilter,
                String pFilterExpression,
                boolean pReverseOrder) {
            return readURL(
                    pService,
                    pRepositoryId,
                    pGroupId,
                    pArtifactId,
                    pPackaging,
                    pClassifier,
                    pInverseFilter,
                    pFilterExpression,
                    pReverseOrder);
        }

        public FormValidation doCheckUrl(@QueryParameter String url) {
            if (StringUtils.isBlank(url)) {
                return FormValidation.error("The artifactory URL cannot be empty");
            }

            return FormValidation.ok();
        }
    }

    @Override
    public IVersionReader createServiceInstance(Item item) {
        final IVersionReader retVal = new ArtifactorySearchService(url);

        final UsernamePasswordCredentialsImpl c = getCredentials(getCredentialsId(), item);
        if (c != null) {
            retVal.setCredentials(c.getUsername(), c.getPassword().getPlainText());
        }
        return retVal;
    }

    @DataBoundSetter
    public void setCredentialsId(String credentialsId) {
        this.credentialsId = credentialsId;
    }

    public String getCredentialsId() {
        return credentialsId;
    }

    @DataBoundSetter
    public void setUrl(String url) {
        this.url = StringUtils.trim(url);
    }

    public String getUrl() {
        return url;
    }
}
