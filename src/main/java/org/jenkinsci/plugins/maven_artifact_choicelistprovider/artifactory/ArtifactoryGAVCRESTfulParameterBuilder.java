package org.jenkinsci.plugins.maven_artifact_choicelistprovider.artifactory;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.commons.lang3.StringUtils;

import org.jenkinsci.plugins.maven_artifact_choicelistprovider.RESTfulParameterBuilder;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.ValidAndInvalidClassifier;

/**
 * Valid for Artifactory.
 * https://docs.jfrog.com/artifactory/reference/searchgavc
 *
 * @author stephan.watermeyer
 *
 */
public class ArtifactoryGAVCRESTfulParameterBuilder {

    public static final String PARAMETER_REPOSITORYID = "repos";

    public static final String PARAMETER_CLASSIFIER = "c";

    //public static final String PARAMETER_PACKAGING = "p";

    public static final String PARAMETER_ARTIFACTID = "a";

    public static final String PARAMETER_GROUPID = "g";

    public static final String PARAMETER_SPECIFIC = "specific";

    public static Boolean SPECIFIC_MODE = true;

    private static final Logger LOGGER = Logger.getLogger(ArtifactoryGAVCRESTfulParameterBuilder.class.getName());
    
    public MultivaluedMap<String, String> create(final String pRepositoryId, final String pName) {
        return create(pRepositoryId, null, pName, null, "");
    }

    public MultivaluedMap<String, String> create(
            final String pRepositoryId, final String pGroup, final String pName, String pToken) {
        return create(pRepositoryId, pGroup, pName, null, pToken);
    }

    public MultivaluedMap<String, String> create(
            final String pRepositoryId,
            final String pGroupId,
            final String pArtifactId,
            final ValidAndInvalidClassifier pClassifier) {
        return create(pRepositoryId, pGroupId, pArtifactId, pClassifier, "");
    }
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
     * @param pToken
     *            the Continuation Token.
     * @return the parameters to be used for the request.
     */
    public MultivaluedMap<String, String> create(
            final String pRepositoryId,
            final String pGroupId,
            final String pArtifactId,
            final ValidAndInvalidClassifier pClassifier,
            String pToken) {
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("create parameters for: repositoryId: " + pRepositoryId + " g:" + pGroupId + ", a:"
                    + pArtifactId + ", p: artifactory-does-not-support-packaging-as-parameter, c: "
                    + (pClassifier == null ? "null" : pClassifier.toString()) + " t:" + pToken);
        }

        MultivaluedMap<String, String> requestParams = new MultivaluedHashMap<String, String>();
        if (!StringUtils.isEmpty(pRepositoryId)) {
            requestParams.putSingle(getRepositoryId(), pRepositoryId);
        }
        if (!StringUtils.isEmpty(pGroupId)) {
            requestParams.putSingle(getGroupId(), pGroupId);
        }
        if (!StringUtils.isEmpty(pArtifactId)) {
            requestParams.putSingle(getArtifactId(), pArtifactId);
        }

        if (pClassifier != null && !StringUtils.isEmpty(getClassifier())) {
            boolean retrieveAllClassifiers = false;

            // FIXME: There is of course a better way how to do it...
            final List<String> query = new ArrayList<String>();
            for (String current : pClassifier.getValid()) {
                query.add(current);

                if (RESTfulParameterBuilder.CLASSIFIER_ALL.equals(current)) {
                    retrieveAllClassifiers = true;
                }
            }

            if (retrieveAllClassifiers || pClassifier.getInvalid().isEmpty() == false) {
                // in this case the parameter "maven.classifier" must not be present in the REST call to retrieve all
                // classifiers.
                // if there is a "invalid" selection, we need to retrieve all elements to filter the "invalid" entries
                // after retrieval.
            } else if (!query.isEmpty()) {
                requestParams.put(getClassifier(), query);
            } else {
                // In case the classifier has not been given, we send it to Nexus3 API to retrieve only the default
                // artifacts like JARs.
                requestParams.putSingle(getClassifier(), "");
            }
        }

        // Specific Mode is activated, as we want to get the downloadUri directly.
        requestParams.putSingle(PARAMETER_SPECIFIC, SPECIFIC_MODE.toString());

        if (!StringUtils.isEmpty(pToken)) {
            requestParams.putSingle(getContinuationToken(), pToken);
        }

        return requestParams;
    }
    
    public String getRepositoryId() {
        return PARAMETER_REPOSITORYID;
    }
    
    public String getGroupId() {
        return PARAMETER_GROUPID;
    }

    
    public String getArtifactId() {
        return PARAMETER_ARTIFACTID;
    }

    public String getClassifier() {
        return PARAMETER_CLASSIFIER;
    }

    public String getContinuationToken() {
        throw new UnsupportedOperationException("this method is not supported getContinuationToken()");
    }

}
