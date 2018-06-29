package org.jenkinsci.plugins.maven_artifact_choicelistprovider;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

/**
 * 
 * Creates URL Parameters for a NEXUS and Artifactory Repository.
 *
 * @author stephan.watermeyer, Diebold Nixdorf
 */
public abstract class RESTfulParameterBuilder {

    public static final String PACKAGING_ALL = "*";

    private static final Logger LOGGER = Logger.getLogger(RESTfulParameterBuilder.class.getName());

    /**
     * Creates the parameter list for the RESTful service.
     * 
     * @param pRepositoryId
     *            the repositoryId.
     * @param pGroupId
     *            the GroupId
     * @param pArtifactId
     *            the ArtifactId
     * @param pPackaging
     *            the Packaging
     * @param pClassifier
     *            the Classifier
     * @return the parameters to be used for the request.
     */
    public MultivaluedMap<String, String> create(final String pRepositoryId, final String pGroupId, final String pArtifactId, final String pPackaging,
            final ValidAndInvalidClassifier pClassifier) {
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("create parameters for: repositoryId: " + pRepositoryId + " g:" + pGroupId + ", a:" + pArtifactId + ", p:" + pPackaging + ", c: " + pClassifier.toString());
        }

        MultivaluedMap<String, String> requestParams = new MultivaluedHashMap<String, String>();
        if (pRepositoryId != "")
            requestParams.putSingle(getRepositoryId(), pRepositoryId);
        if (pGroupId != "")
            requestParams.putSingle(getGroupId(), pGroupId);
        if (pArtifactId != "")
            requestParams.putSingle(getArtifactId(), pArtifactId);
        if (pPackaging != "" && !PACKAGING_ALL.equals(pPackaging))
            requestParams.putSingle(getPackaging(), pPackaging);
        if (pClassifier != null) {
            // FIXME: There is of course a better way how to do it...
            final List<String> query = new ArrayList<String>();
            for (String current : pClassifier.getValid())
                query.add(current);

            if (!query.isEmpty())
                requestParams.put(getClassifier(), query);
        }
        return requestParams;
    }

    public abstract String getRepositoryId();

    public abstract String getGroupId();

    public abstract String getArtifactId();

    public abstract String getPackaging();

    public abstract String getClassifier();

}
