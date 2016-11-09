package org.jenkinsci.plugins.maven_artifact_choicelistprovider.central;

import java.util.Map;

import org.jenkinsci.plugins.maven_artifact_choicelistprovider.AbstractMavenArtifactChoiceListProvider;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.AbstractMavenArtifactDescriptorImpl;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.IVersionReader;
import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;

public class MavenCentralChoiceListProvider extends AbstractMavenArtifactChoiceListProvider {

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

		@Override
		protected Map<String, String> wrapTestConnection(IVersionReader service, String pCredentialsId, String pGroupId,
				String pArtifactId, String pPackaging, String pClassifier, boolean pReverseOrder) {
			return readURL(new MavenCentralSearchService(), pCredentialsId, pGroupId, pArtifactId, pPackaging,
					pClassifier, pReverseOrder);
		}

	}

	public static IVersionReader getStaticServiceInstance() {
		return new MavenCentralSearchService();
	}

	public IVersionReader getServiceInstance() {
		return getStaticServiceInstance();
	}

}
