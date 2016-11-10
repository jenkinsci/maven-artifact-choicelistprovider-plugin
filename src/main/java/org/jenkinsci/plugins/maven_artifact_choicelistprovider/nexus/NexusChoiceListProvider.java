package org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.AbstractMavenArtifactChoiceListProvider;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.AbstractMavenArtifactDescriptorImpl;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.IVersionReader;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardCredentials;
import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;

import hudson.Extension;
import hudson.security.ACL;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;

public class NexusChoiceListProvider extends AbstractMavenArtifactChoiceListProvider {

	private String url;

	@DataBoundConstructor
	public NexusChoiceListProvider(String artifactId) {
		super(artifactId);
	}

	@Extension
	public static class NexusDescriptorImpl extends AbstractMavenArtifactDescriptorImpl {

		/**
		 * the display name shown in the dropdown to select a choice provider.
		 * 
		 * @return display name
		 * @see hudson.model.Descriptor#getDisplayName()
		 */
		@Override
		public String getDisplayName() {
			return "Nexus Artifact Choice Parameter";
		}

		public ListBoxModel doFillCredentialsIdItems() {
			return new StandardListBoxModel().withEmptySelection().withMatching(
					CredentialsMatchers
							.anyOf(CredentialsMatchers.instanceOf(StandardUsernamePasswordCredentials.class)),
					CredentialsProvider.lookupCredentials(StandardCredentials.class, Jenkins.getInstance(),
							ACL.SYSTEM));
		}
		
		public FormValidation doTest(@QueryParameter String url, @QueryParameter String credentialsId,
				@QueryParameter String groupId, @QueryParameter String artifactId, @QueryParameter String packaging,
				@QueryParameter String classifier, @QueryParameter boolean reverseOrder) {
			final IVersionReader service = new NexusLuceneSearchService(url);
			return super.performTest(service, credentialsId, groupId, artifactId, packaging, classifier, reverseOrder);
		}

		@Override
		protected Map<String, String> wrapTestConnection(IVersionReader pService, String pCredentialsId, String pGroupId,
				String pArtifactId, String pPackaging, String pClassifier, boolean pReverseOrder) {
			return readURL(pService, pCredentialsId, pGroupId, pArtifactId, pPackaging, pClassifier, pReverseOrder);
		}

		public FormValidation doCheckUrl(@QueryParameter String url) {
			if (StringUtils.isBlank(url)) {
				return FormValidation.error("The nexus URL cannot be empty");
			}

			return FormValidation.ok();
		}
	}

	public String getUrl() {
		return url;
	}

	@DataBoundSetter
	public void setUrl(String url) {
		this.url = StringUtils.trim(url);
	}

	@Override
	public IVersionReader getServiceInstance() {
		return new NexusLuceneSearchService(url);
	}

}
