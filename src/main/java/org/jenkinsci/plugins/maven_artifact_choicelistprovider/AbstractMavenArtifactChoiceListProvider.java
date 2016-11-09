package org.jenkinsci.plugins.maven_artifact_choicelistprovider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl;

import hudson.ExtensionPoint;
import hudson.model.AbstractProject;
import hudson.security.ACL;
import jenkins.model.Jenkins;
import jp.ikedam.jenkins.plugins.extensible_choice_parameter.ChoiceListProvider;
import jp.ikedam.jenkins.plugins.extensible_choice_parameter.ExtensibleChoiceParameterDefinition;

public abstract class AbstractMavenArtifactChoiceListProvider extends ChoiceListProvider implements ExtensionPoint {

	private static final Logger LOGGER = Logger.getLogger(AbstractMavenArtifactChoiceListProvider.class.getName());

	private String groupId;
	private String artifactId;
	private String packaging;
	private String classifier;
	private boolean reverseOrder;
	private String credentialsId;

	protected Map<String, String> mChoices;

	@DataBoundConstructor
	public AbstractMavenArtifactChoiceListProvider(String artifactId) {
		super();
		this.setArtifactId(artifactId);
	}

	@Override
	public List<String> getChoiceList() {
		if (mChoices == null) {
			mChoices = readURL(getServiceInstance(), getCredentialsId(), getGroupId(), getArtifactId(), getPackaging(),
					getClassifier(), getReverseOrder());
		}
		// FIXME: CHANGE-1: Return only the keys, that are shorter then the values
		// return new ArrayList<String>(mChoices.keySet());
		return new ArrayList<String>(mChoices.values());
	}
	
	public abstract IVersionReader getServiceInstance();

	/**
	 * FIXME: CHANGE-1: Needs to be implemented. But currently i dont know how to update the environment variable to use
	 * the new value.
	 */
	@Override
	public void onBuildTriggeredWithValue(AbstractProject<?, ?> pJob, ExtensibleChoiceParameterDefinition pDef,
			String pOldValue) {
		String newValue = pOldValue;
		if (mChoices != null) {
			LOGGER.log(Level.INFO, "get full url for item:" + pOldValue);
			if (mChoices.containsKey(pOldValue)) {
				newValue = mChoices.get(pOldValue);
			}
		}
		LOGGER.log(Level.INFO, "target value is: " + newValue);
		// FIXME: CHANGE-1: How to update the build env variables to replace the current parameter? I dont know...
	}

	public static Map<String, String> readURL(final IVersionReader pInstance, final String pCredentialsId,
			final String pGroupId, final String pArtifactId, final String pPackaging, String pClassifier,
			final boolean pReverseOrder) {
		Map<String, String> retVal = new LinkedHashMap<String, String>();
		try {
			ValidAndInvalidClassifier classifierBox = ValidAndInvalidClassifier.fromString(pClassifier);

			// If configured, set User Credentials
			final UsernamePasswordCredentialsImpl c = getCredentials(pCredentialsId);
			if (c != null) {
				pInstance.setCredentials(c.getUsername(), c.getPassword().getPlainText());
			}

			List<String> choices = pInstance.retrieveVersions(pGroupId, pArtifactId, pPackaging, classifierBox);

			if (pReverseOrder)
				Collections.reverse(choices);

			retVal = toMap(choices);
		} catch (VersionReaderException e) {
			LOGGER.log(Level.INFO, "failed to retrieve versions from repository for g:" + pGroupId + ", a:"
					+ pArtifactId + ", p:" + pPackaging + ", c:" + pClassifier, e);
			retVal.put("error", e.getMessage());
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "failed to retrieve versions from nexus for g:" + pGroupId + ", a:" + pArtifactId
					+ ", p:" + pPackaging + ", c:" + pClassifier, e);
			retVal.put("error", "Unexpected Error: " + e.getMessage());
		}
		return retVal;
	}

	/**
	 * 
	 * @param pCredentialId
	 * @return the credentials for the ID or NULL
	 */
	static UsernamePasswordCredentialsImpl getCredentials(final String pCredentialId) {
		return CredentialsMatchers
				.firstOrNull(
						CredentialsProvider.lookupCredentials(UsernamePasswordCredentialsImpl.class,
								Jenkins.getInstance(), ACL.SYSTEM),
						CredentialsMatchers.allOf(CredentialsMatchers.withId(pCredentialId)));
	}

	/**
	 * Cuts of the first parts of the URL and only returns a smaller set of items.
	 * 
	 * @param pChoices
	 *            the list which is transformed to a map
	 * @return the map containing the short url as Key and the long url as value.
	 */
	public static Map<String, String> toMap(List<String> pChoices) {
		Map<String, String> retVal = new LinkedHashMap<String, String>();
		for (String current : pChoices) {
			retVal.put(current.substring(current.lastIndexOf("/") + 1), current);
		}
		return retVal;
	}

	@DataBoundSetter
	public void setGroupId(String groupId) {
		this.groupId = StringUtils.trim(groupId);
	}

	@DataBoundSetter
	public void setArtifactId(String artifactId) {
		this.artifactId = StringUtils.trim(artifactId);
	}

	@DataBoundSetter
	public void setPackaging(String packaging) {
		this.packaging = StringUtils.trim(packaging);
	}

	@DataBoundSetter
	public void setClassifier(String classifier) {
		this.classifier = StringUtils.trim(classifier);
	}

	@DataBoundSetter
	public void setReverseOrder(boolean reverseOrder) {
		this.reverseOrder = reverseOrder;
	}

	@DataBoundSetter
	public void setCredentialsId(String credentialsId) {
		this.credentialsId = credentialsId;
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

	public String getClassifier() {
		return classifier;
	}

	public boolean getReverseOrder() {
		return reverseOrder;
	}

	public String getCredentialsId() {
		return credentialsId;
	}

}
