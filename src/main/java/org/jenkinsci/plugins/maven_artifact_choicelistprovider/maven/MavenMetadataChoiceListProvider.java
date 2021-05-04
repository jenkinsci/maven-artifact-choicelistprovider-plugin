package org.jenkinsci.plugins.maven_artifact_choicelistprovider.maven;

import java.util.Collections;
import java.util.Map;

import javax.ws.rs.POST;

import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.AbstractMavenArtifactChoiceListProvider;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.IVersionReader;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.Item;
import hudson.model.Job;
import hudson.security.ACL;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import jp.ikedam.jenkins.plugins.extensible_choice_parameter.ChoiceListProvider;


/**
 * @author Attila Simon
 */
public class MavenMetadataChoiceListProvider extends AbstractMavenArtifactChoiceListProvider {
    private static final long serialVersionUID = -3154940299983798059L;

    private String url;
    private String credentialsId;

    @DataBoundConstructor
    public MavenMetadataChoiceListProvider(String artifactId) {
        super(artifactId);
    }

    @Extension
    public static class MavenMetadataDescriptorImpl extends Descriptor<ChoiceListProvider> {

        public MavenMetadataDescriptorImpl() {
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
            return "Maven Version Only Choice Parameter";
        }

        public ListBoxModel doFillCredentialsIdItems(@AncestorInPath Item pItem) {
            final ListBoxModel retVal;

            // SECURITY-1022
            if (pItem.hasPermission(Job.CONFIGURE)) {
                retVal = new StandardListBoxModel().includeEmptyValue().includeMatchingAs(ACL.SYSTEM, pItem, StandardUsernamePasswordCredentials.class,
                        Collections.emptyList(), CredentialsMatchers.instanceOf(StandardUsernamePasswordCredentials.class));
            } else {
                retVal = new StandardListBoxModel().includeEmptyValue();
            }

            return retVal;
        }

        public FormValidation doCheckUrl(@QueryParameter String url) {
            if (StringUtils.isBlank(url)) {
                return FormValidation.error("The maven URL cannot be empty");
            }

            return FormValidation.ok();
        }

        public FormValidation doCheckRepositoryId(@QueryParameter String repositoryId) {
            if (StringUtils.isBlank(repositoryId)) {
                return FormValidation.error("The repository cannot be empty.");
            }

            return FormValidation.ok();
        }

        public FormValidation doCheckGroupId(@QueryParameter String groupId) {
            if (StringUtils.isBlank(groupId)) {
                return FormValidation.error("The groudId cannot be empty.");
            }

            return FormValidation.ok();
        }

        public FormValidation doCheckArtifactId(@QueryParameter String artifactId) {
            if (StringUtils.isBlank(artifactId)) {
                return FormValidation.error("The artifactId cannot be empty.");
            }

            return FormValidation.ok();
        }

        @POST
        public FormValidation doTest(@AncestorInPath Item pItem, @QueryParameter String url, @QueryParameter String credentialsId, @QueryParameter String repositoryId,
                                     @QueryParameter String groupId, @QueryParameter String artifactId,
                                     @QueryParameter boolean inverseFilter, @QueryParameter String filterExpression, @QueryParameter boolean reverseOrder) {

            // SECURITY-1022
            pItem.checkPermission(Job.CONFIGURE);

            final IVersionReader service = new MavenMetadataSearchService(url);

            // If configured, set User Credentials
            final UsernamePasswordCredentialsImpl c = getCredentials(credentialsId);
            if (c != null) {
                service.setCredentials(c.getUsername(), c.getPassword().getPlainText());
            }
            return performTest(service, repositoryId, groupId, artifactId, inverseFilter, filterExpression, reverseOrder);
        }


        private FormValidation performTest(final IVersionReader pService, @QueryParameter String repositoryId, @QueryParameter String groupId, @QueryParameter String artifactId,
                                           @QueryParameter boolean inverseFilter, @QueryParameter String filterExpression, @QueryParameter boolean reverseOrder) {

            try {
                final Map<String, String> entriesFromURL = wrapTestConnection(pService, repositoryId, groupId, artifactId, inverseFilter, filterExpression, reverseOrder);

                if (entriesFromURL.isEmpty()) {
                    return FormValidation.ok("(Working, but no Entries found)");
                }
                return FormValidation.ok(StringUtils.join(entriesFromURL.values(), '\n'));
            } catch (Exception e) {
                return FormValidation.error("error reading versions from url:" + e.getMessage());
            }
        }

        private Map<String, String> wrapTestConnection(IVersionReader pService, String pRepositoryId, String pGroupId, String pArtifactId,
                                                       boolean pInverseFilter, String pFilterExpression, boolean pReverseOrder) {
            return readURL(pService, pRepositoryId, pGroupId, pArtifactId, null, null,
                    pInverseFilter, pFilterExpression, pReverseOrder);
        }
    }

    @Override
    public IVersionReader createServiceInstance() {
        // init the service
        final IVersionReader retVal = new MavenMetadataSearchService(url);
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
}
