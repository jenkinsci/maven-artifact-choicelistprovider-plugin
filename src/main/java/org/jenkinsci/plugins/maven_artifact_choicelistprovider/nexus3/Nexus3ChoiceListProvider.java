package org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus3;

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
import org.kohsuke.stapler.StaplerRequest2;
import org.kohsuke.stapler.interceptor.RequirePOST;

import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl;

import hudson.Extension;
import hudson.model.Item;
import hudson.model.Job;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import net.sf.json.JSONObject;

public class Nexus3ChoiceListProvider extends AbstractMavenArtifactChoiceListProvider {

    private static final long serialVersionUID = -5192115026547049358L;

    private String url;
    private String credentialsId;

    @DataBoundConstructor
    public Nexus3ChoiceListProvider(String artifactId) {
        super(artifactId);
    }

    @Extension
    public static class Nexus3DescriptorImpl extends AbstractMavenArtifactDescriptorImpl {

    	@Inject
    	private AbstractMavenArtifactChoiceListProvider.DescriptorImpl delegate;
    	
        public Nexus3DescriptorImpl() {
            // When Jenkins is restarted, load any saved configuration from disk.
            load();
        }

        /**
         * the display name shown in the drop down to select a choice provider.
         * 
         * @return display name
         * @see hudson.model.Descriptor#getDisplayName()
         */
        @Override
        public String getDisplayName() {
            return "Nexus3 Artifact Choice Parameter";
        }

        public ListBoxModel doFillCredentialsIdItems(@AncestorInPath Item pItem, @QueryParameter String credentialsId) {
        	return delegate.doFillCredentialsIdItems(pItem, credentialsId);
        }

        @RequirePOST
        public FormValidation doTest(@AncestorInPath Item pItem, @QueryParameter String url, @QueryParameter String credentialsId, @QueryParameter String repositoryId,
                @QueryParameter String groupId, @QueryParameter String artifactId, @QueryParameter String packaging, @QueryParameter String classifier,
                @QueryParameter boolean inverseFilter, @QueryParameter String filterExpression, @QueryParameter boolean reverseOrder) {

            // SECURITY-1022
            pItem.checkPermission(Job.CONFIGURE);

            final IVersionReader service = new Nexus3RestApiAssetMavenService(url);

			// If configured, set User Credentials
            final UsernamePasswordCredentialsImpl c = getCredentials(credentialsId, pItem);
            if (c != null) {
                service.setCredentials(c.getUsername(), c.getPassword().getPlainText());
            }
            return super.performTest(service, repositoryId, groupId, artifactId, packaging, classifier, inverseFilter, filterExpression, reverseOrder);
        }

        @Override
        protected Map<String, String> wrapTestConnection(IVersionReader pService, String pRepositoryId, String pGroupId, String pArtifactId, String pPackaging, String pClassifier,
                boolean pInverseFilter, String pFilterExpression, boolean pReverseOrder) {
            return readURL(pService, pRepositoryId, pGroupId, pArtifactId, pPackaging, pClassifier,
                pInverseFilter, pFilterExpression, pReverseOrder);
        }

        public FormValidation doCheckUrl(@QueryParameter String url) {
            if (StringUtils.isBlank(url)) {
                return FormValidation.error("The nexus URL cannot be empty");
            }

            return FormValidation.ok();
        }

        /**
         * Saves the Global-Option Settings
         */
        @Override
        public boolean configure(StaplerRequest2 staplerRequest, JSONObject json) throws FormException {
            save();
            return true;
        }

    }

    @Override
    public IVersionReader createServiceInstance(Item item) {
        // init the service
        final IVersionReader retVal = new Nexus3RestApiAssetMavenService(url);
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
