package org.jenkinsci.plugins.maven_artifact_choicelistprovider.azure;

import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl;
import hudson.Extension;
import hudson.model.Item;
import hudson.model.Job;
import hudson.security.ACL;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.AbstractMavenArtifactChoiceListProvider;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.AbstractMavenArtifactDescriptorImpl;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.IVersionReader;
import org.kohsuke.stapler.*;

import javax.ws.rs.POST;
import javax.ws.rs.QueryParam;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Logger;


public class AzureChoiceListProvider  extends AbstractMavenArtifactChoiceListProvider{

    private static final Logger LOGGER = Logger.getLogger(AzureChoiceListProvider.class.getName());

    private String url;
    private String credentialsId;
    private boolean showRelease;

    @DataBoundConstructor
    public AzureChoiceListProvider(String artifactId) {
        super(artifactId);
    }

    @Extension
    public static class AzureDescriptorImpl extends AbstractMavenArtifactDescriptorImpl {

        private boolean showRelease;

        public AzureDescriptorImpl() {
            // When Jenkins is restarted, load any saved configuration from disk.
            load();
        }

        /**
         * the display name shown in the dropdown to select a choice provider.
         *
         * @return display name
         * @see hudson.model.Descriptor#getDisplayName()
         */
        @Override
        public String getDisplayName() {
            return "Azure Artifact Choice Parameter";
        }

        public ListBoxModel doFillCredentialsIdItems(@AncestorInPath Item pItem) {
            final ListBoxModel retVal;

            // SECURITY-1022
            if (pItem.hasPermission(Job.CONFIGURE)) {
                retVal= new StandardListBoxModel().includeEmptyValue().includeMatchingAs(ACL.SYSTEM, pItem, StandardUsernamePasswordCredentials.class,
                        Collections.<DomainRequirement> emptyList(), CredentialsMatchers.instanceOf(StandardUsernamePasswordCredentials.class));
            } else {
                retVal= new StandardListBoxModel().includeEmptyValue();
            }

            return retVal;
        }

        @POST
        public FormValidation doTest(@AncestorInPath Item pItem, @QueryParameter String url, @QueryParameter String credentialsId, @QueryParameter String repositoryId,
                                     @QueryParameter String groupId, @QueryParameter String artifactId, @QueryParameter String packaging, @QueryParameter String classifier,
                                     @QueryParameter boolean reverseOrder, @QueryParameter boolean showRelease) {

            // SECURITY-1022
            pItem.checkPermission(Job.CONFIGURE);

            final IVersionReader service = new AzureSearchService(url,showRelease);

            // If configured, set User Credentials
            final UsernamePasswordCredentialsImpl c = getCredentials(credentialsId);
            if (c != null) {
                service.setCredentials(c.getUsername(), c.getPassword().getPlainText());
            }

            return super.performTest(service, repositoryId, groupId, artifactId, packaging, classifier, reverseOrder);
        }

        @Override
        protected Map<String, String> wrapTestConnection(IVersionReader pService, String pRepositoryId, String pGroupId, String pArtifactId, String pPackaging, String pClassifier,
                                                         boolean pReverseOrder) {
            return readURL(pService, pRepositoryId, pGroupId, pArtifactId, pPackaging, pClassifier, pReverseOrder);
        }

        public FormValidation doCheckUrl(@QueryParameter String url) {
            if (StringUtils.isBlank(url)) {
                return FormValidation.error("The repository URL cannot be empty");
            }

            return FormValidation.ok();
        }
    }

    @Override
    public IVersionReader createServiceInstance() {

        // init the service
        final IVersionReader retVal = new AzureSearchService(url,showRelease);
        final UsernamePasswordCredentialsImpl c = getCredentials(getCredentialsId());
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

    @DataBoundSetter
    public void setShowRelease(boolean showRelease) {
        this.showRelease = showRelease;
    }

    public boolean getShowRelease() {
        return showRelease;
    }

}
