package org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 
 * This implementation creates a download link for the given artifact by using an inbuild Nexus service. So this
 * implementation uses the RESTful interface of Nexus to submit the artifact details to the service, and the Nexus
 * service will return the artifact.
 * <br>
 * Example:
 * <a href=
 * "https://server/service/local/artifact/maven/content?r=repositoryId&g=groupId&a=artifactId&p=packaging&v=versionId">
 * Example</a>
 * <br>
 * Further documentation:
 * <a href=
 * "https://support.sonatype.com/hc/en-us/articles/213465488-How-can-I-retrieve-a-snapshot-if-I-don-t-know-the-exact-filename-">
 * Sonatype Support</a>
 * 
 * @author stephan.watermeyer, Diebold Nixdorf
 */
public class NexusContentServiceArtifactURLBuilder extends AbstractArtifactURLBuilder implements IArtifactURLBuilder {

	private static final String ENCODING = "UTF-8";

	/**
	 * The RESTful service URI.
	 */
	static final String SERVICE_URI = "service/local/artifact/maven/content?";

	/**
	 * Separator for URL parameters.
	 */
	static final String AMPERSAND = "&";

	@Override
	public String build(boolean pOnlyBaseURL) {
		final StringBuilder retVal = new StringBuilder();
		retVal.append(getNexusURL()); // No-Slash. Is already defined on getter call
		retVal.append(SERVICE_URI);
		retVal.append("r=").append(getRepositoryId()).append(AMPERSAND);
		retVal.append("g=").append(getGroupId()).append(AMPERSAND);
		retVal.append("a=").append(getArtifactId()).append(AMPERSAND);
		if (getPackaging() != null) {
			retVal.append("p=").append(getPackaging()).append(AMPERSAND);
		}
		if (getClassifier() != null) {
			retVal.append("c=").append(getClassifier()).append(AMPERSAND);
		}
		if (getVersion() != null) {
			retVal.append("v=").append(encode(getVersion()));
		}
		return retVal.toString();
	}
	
	String encode(final String pValue) {
		try {
			return URLEncoder.encode(pValue, ENCODING);
		} catch (UnsupportedEncodingException e) {
			return pValue;
		}
	}

}
