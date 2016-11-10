package org.jenkinsci.plugins.maven_artifact_choicelistprovider.central;

import java.util.Map;

import org.jenkinsci.plugins.maven_artifact_choicelistprovider.AbstractMavenArtifactChoiceListProvider;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.AbstractMavenArtifactDescriptorImpl;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.IVersionReader;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import hudson.Extension;
import hudson.util.FormValidation;

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

		public FormValidation doTest(@QueryParameter String url, @QueryParameter String groupId,
				@QueryParameter String artifactId, @QueryParameter String packaging, @QueryParameter String classifier,
				@QueryParameter boolean reverseOrder) {
			final IVersionReader service = new MavenCentralSearchService();
			return super.performTest(service, groupId, artifactId, packaging, classifier, reverseOrder);
		}

		@Override
		protected Map<String, String> wrapTestConnection(IVersionReader service, String pGroupId, String pArtifactId,
				String pPackaging, String pClassifier, boolean pReverseOrder) {
			return readURL(new MavenCentralSearchService(), pGroupId, pArtifactId, pPackaging, pClassifier,
					pReverseOrder);
		}

	}

	public IVersionReader createServiceInstance() {
		return new MavenCentralSearchService();
	}

}
