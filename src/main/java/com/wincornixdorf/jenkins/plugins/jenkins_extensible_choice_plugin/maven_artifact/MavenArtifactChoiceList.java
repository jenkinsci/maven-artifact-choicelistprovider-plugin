package com.wincornixdorf.jenkins.plugins.jenkins_extensible_choice_plugin.maven_artifact;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import com.wincornixdorf.jenkins.plugins.jenkins_extensible_choice_plugin.maven_artifact.nexus.NexusLuceneSearchService;

import hudson.Extension;
import hudson.ExtensionPoint;
import hudson.model.Descriptor;
import hudson.util.FormValidation;
import jp.ikedam.jenkins.plugins.extensible_choice_parameter.ChoiceListProvider;

public class MavenArtifactChoiceList extends ChoiceListProvider implements ExtensionPoint {

	private static final Logger LOGGER = Logger.getLogger(MavenArtifactChoiceList.class.getName());

	private final String url;
	private final String groupId;
	private final String artifactId;
	private final String packaging;

	@DataBoundConstructor
	public MavenArtifactChoiceList(String url, String groupId, String artifactId, String packaging) {
		super();
		this.url = StringUtils.trim(url);
		this.groupId = StringUtils.trim(groupId);
		this.artifactId = StringUtils.trim(artifactId);
		this.packaging = StringUtils.trim(packaging);
	}

	public String getUrl() {
		return url;
	}

	public String getGroupId() {
		return groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public String getPackaging() {
		return packaging;
	}

	@Override
	public List<String> getChoiceList() {
		return readURL(getUrl(), getGroupId(), getArtifactId(), getPackaging());
	}

	static List<String> readURL(final String pURL, final String pGroupId, final String pArtifactId,
			final String pPackaging) {
		List<String> retVal = new ArrayList<String>();
		try {
			IVersionReader mService = new NexusLuceneSearchService(pURL, pGroupId, pArtifactId, pPackaging);
			retVal = mService.retrieveVersions();
		} catch (Exception e) {
			retVal.add("ERROR: " + e.getMessage());
			LOGGER.log(Level.SEVERE, "failed to retrieve versions from nexus for r:" + pURL + ", g:" + pGroupId + ", a:"
					+ pArtifactId + ", p:" + pPackaging, e);
		}
		return retVal;
	}

	@Extension
	public static class DescriptorImpl extends Descriptor<ChoiceListProvider> {
		/**
		 * the display name shown in the dropdown to select a choice provider.
		 * 
		 * @return display name
		 * @see hudson.model.Descriptor#getDisplayName()
		 */
		@Override
		public String getDisplayName() {
			return "Maven Artifact Choice Parameter";
		}

		public FormValidation doCheckUrl(@QueryParameter String url) {
			if (StringUtils.isBlank(url)) {
				return FormValidation.error("The server URL cannot be empty");
			}

			return FormValidation.ok();
		}

		public FormValidation doCheckArtifactId(@QueryParameter String artifactId) {
			if (StringUtils.isBlank(artifactId)) {
				return FormValidation.error("The artifactId cannot be empty");
			}

			return FormValidation.ok();
		}

		public FormValidation doCheckPackaging(@QueryParameter String packaging) {
			if (!StringUtils.isBlank(packaging) && packaging.startsWith(".")) {
				return FormValidation.error("packaging must not start with a .");
			}

			return FormValidation.ok();
		}

		/**
		 * Test what files will be listed.
		 * 
		 * @param baseDirPath
		 * @param includePattern
		 * @param excludePattern
		 * @param scanType
		 * @return
		 */
		public FormValidation doTest(@QueryParameter String url, @QueryParameter String groupId,
				@QueryParameter String artifactId, @QueryParameter String packaging) {
			try {
				final List<String> entriesFromURL = readURL(url, groupId, artifactId, packaging);

				if (entriesFromURL.isEmpty()) {
					return FormValidation.ok("(No Entries found)");
				}
				return FormValidation.ok(StringUtils.join(entriesFromURL, '\n'));
			} catch (Exception e) {
				return FormValidation.error("error reading versions from url:" + e.getMessage());
			}
		}
	}
}
