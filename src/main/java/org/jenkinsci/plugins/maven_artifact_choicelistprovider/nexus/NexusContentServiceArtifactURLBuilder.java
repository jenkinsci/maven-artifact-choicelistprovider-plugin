package org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * This implementation creates a download link for the given artifact by using
 * an inbuild Nexus service. So this implementation uses the RESTful interface
 * of Nexus to submit the artifact details to the service, and the Nexus service
 * will return the artifact. <br>
 * Example: <a href=
 * "https://server/service/local/artifact/maven/content?r=repositoryId&g=groupId&a=artifactId&p=packaging&v=versionId">
 * Example</a> <br>
 * Further documentation: <a href=
 * "https://support.sonatype.com/hc/en-us/articles/213465488-How-can-I-retrieve-a-snapshot-if-I-don-t-know-the-exact-filename-">
 * Sonatype Support</a>
 *
 * @author stephan.watermeyer, Diebold Nixdorf
 */
public class NexusContentServiceArtifactURLBuilder extends AbstractArtifactURLBuilder {

    private static final String ENCODING = "UTF-8";

    private static final Logger LOGGER = Logger.getLogger(NexusContentServiceArtifactURLBuilder.class.getName());

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
            final String retVal = URLEncoder.encode(pValue, ENCODING);
            // LOGGER.log(Level.FINER, "convert " + pValue + " to " + retVal);
            return retVal;
        } catch (UnsupportedEncodingException e) {
            LOGGER.log(Level.FINE, "failed to convert version to  " + ENCODING + ":" + pValue);
            return pValue;
        }
    }
}
