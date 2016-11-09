package org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.AbstractMavenArtifactChoiceListProvider;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.AbstractMavenArtifactDescriptorImpl;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.IVersionReader;
import org.kohsuke.stapler.DataBoundSetter;

import hudson.Extension;

public class NexusChoiceListProvider extends AbstractMavenArtifactChoiceListProvider {

	private String url;

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

		@Override
		protected Map<String, String> wrapReadURL(String pCredentialsId, String pGroupId, String pArtifactId,
				String pPackaging, String pClassifier, boolean pReverseOrder) {
			return readURL(getStaticServiceInstance(), pCredentialsId, pGroupId, pArtifactId, pPackaging, pClassifier,
					pReverseOrder);
		}
	}

	public String getUrl() {
		return url;
	}

	@DataBoundSetter
	public void setUrl(String url) {
		this.url = StringUtils.trim(url);
	}

	public static IVersionReader getStaticServiceInstance() {
		return new NexusLuceneSearchService("");
	}

	public IVersionReader getServiceInstance() {
		return getStaticServiceInstance();
	}

}
