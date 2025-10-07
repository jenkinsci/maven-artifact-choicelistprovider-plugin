package org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus3;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.AbstractMavenArtifactChoiceListProvider;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.IVersionReader;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.MavenArtifactChoiceListProviderUtils;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.ValidAndInvalidClassifier;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.VersionReaderException;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest2;
import org.kohsuke.stapler.interceptor.RequirePOST;

import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.Item;
import hudson.model.Job;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import jp.ikedam.jenkins.plugins.extensible_choice_parameter.ChoiceListProvider;
import net.sf.json.JSONObject;

public class Nexus3GenericChoiceListProvider extends AbstractMavenArtifactChoiceListProvider {

	private static final long serialVersionUID = -5192115026547049358L;

	private static final Logger LOGGER = Logger.getLogger(Nexus3GenericChoiceListProvider.class.getName());

	private String url;
	private String credentialsId;

	@DataBoundConstructor
	public Nexus3GenericChoiceListProvider(String name) {
		super(name);
	}

	@Extension
	public static class Nexus3GenericDescriptorImpl extends Descriptor<ChoiceListProvider> {

		@Inject
    	private AbstractMavenArtifactChoiceListProvider.DescriptorImpl delegate;
		
		public Nexus3GenericDescriptorImpl() {
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
			return "Nexus3 Generic Choice Parameter";
		}

		public ListBoxModel doFillCredentialsIdItems(@AncestorInPath Item pItem, @QueryParameter String credentialsId) {
			return delegate.doFillCredentialsIdItems(pItem, credentialsId);
		}

		@RequirePOST
		public FormValidation doTest(@AncestorInPath Item pItem, @QueryParameter String url,
				@QueryParameter String credentialsId, @QueryParameter String repositoryId, @QueryParameter String name,
				@QueryParameter boolean inverseFilter, @QueryParameter String filterExpression,
				@QueryParameter boolean reverseOrder) {

			// SECURITY-1022
			pItem.checkPermission(Job.CONFIGURE);

			final IVersionReader service = new Nexus3RestApiAssetForGenericArtifactsService(url);
			
			// If configured, set User Credentials
			final UsernamePasswordCredentialsImpl c = getCredentials(credentialsId, pItem);
			if (c != null) {
				service.setCredentials(c.getUsername(), c.getPassword().getPlainText());
			}

			if (StringUtils.isEmpty(name)) {
				return FormValidation.error("The Name attribute cannot be empty.");
			}
	            
			try {
				final Map<String, String> entriesFromURL = readURL(service, repositoryId, name, inverseFilter, filterExpression, reverseOrder);

				if (entriesFromURL.isEmpty()) {
					return FormValidation.ok("(Working, but no Entries found)");
				}
				return FormValidation.ok(StringUtils.join(entriesFromURL.values(), '\n'));
			} catch (Exception e) {
				return FormValidation.error("error reading versions from url:" + e.getMessage());
			}

		}		

		private Map<String, String> readURL(final IVersionReader pInstance, final String pRepositoryId,
				final String pName, final boolean pInverseFilter, final String pFilterExpression,
				final boolean pReverseOrder) {
			Map<String, String> retVal = new LinkedHashMap<String, String>();
			try {

				List<String> choices = pInstance.retrieveVersions(pRepositoryId, "", pName, "",  ValidAndInvalidClassifier.getDefault());

				List<String> filteredChoices = filterArtifacts(choices, pInverseFilter, pFilterExpression);

				if (pReverseOrder) {
					Collections.reverse(filteredChoices);
				}

				retVal = MavenArtifactChoiceListProviderUtils.toMap(filteredChoices);
			} catch (VersionReaderException e) {
				LOGGER.log(Level.INFO,
						"failed to retrieve versions from repository for r:" + pRepositoryId + ", n:" + pName, e);
				retVal.put("error", e.getMessage());
			} catch (Exception e) {
				LOGGER.log(Level.WARNING,
						"failed to retrieve versions from nexus for r:" + pRepositoryId + ", n:" + pName, e);
				retVal.put("error", "Unexpected Error: " + e.getMessage());
			}
			return retVal;
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
		final IVersionReader retVal = new Nexus3RestApiAssetForGenericArtifactsService(url);
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
	
	@DataBoundSetter
	public void setName(String name) {
		this.setArtifactId(name);
	}

	public String getName() {
		return this.getArtifactId();
	}

}
