package org.jenkinsci.plugins.maven_artifact_choicelistprovider;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * Creates URL Parameters for a Nexus, Nexus3 and Artifactory Repository.
 *
 * @author stephan.watermeyer, Diebold Nixdorf
 */
public abstract class RESTfulParameterBuilder {

	public static final String PACKAGING_ALL = "*";
	
	public static final String CLASSIFIER_ALL = "*";

	// Always order the results by semantic version
    public static final String DEFAULT_SORTORDER = "version";
    
    private static final Logger LOGGER = Logger.getLogger(RESTfulParameterBuilder.class.getName());

    public MultivaluedMap<String, String> create(final String pRepositoryId, final String pName) {
        return create(pRepositoryId, null, pName, null, null, "");
    }

    public MultivaluedMap<String, String> create(final String pRepositoryId, final String pGroup, final String pName, String pToken) {
        return create(pRepositoryId, pGroup, pName, null, null, pToken);
            }
    
    public MultivaluedMap<String, String> create(final String pRepositoryId, final String pGroupId, final String pArtifactId, final String pPackaging,
            final ValidAndInvalidClassifier pClassifier) {
        return create(pRepositoryId, pGroupId, pArtifactId, pPackaging, pClassifier, "");
        
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
     *            the Nexus 3 Token.
     * @return the parameters to be used for the request.
     */
    public MultivaluedMap<String, String> create(final String pRepositoryId, final String pGroupId, final String pArtifactId, final String pPackaging,
            final ValidAndInvalidClassifier pClassifier, String pToken) {
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("create parameters for: repositoryId: " + pRepositoryId + " g:" + pGroupId + ", a:" + pArtifactId + ", p:" + pPackaging + ", c: " + (pClassifier == null ? "null" : pClassifier.toString())
                    + " t:" + pToken);
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
        if (!StringUtils.isEmpty(pPackaging) && !PACKAGING_ALL.equals(pPackaging)) {
            requestParams.putSingle(getPackaging(), pPackaging);
        }
        if (pClassifier != null && !StringUtils.isEmpty(getClassifier())) {
            boolean retrieveAllClassifiers = false;
            
            // FIXME: There is of course a better way how to do it...
            final List<String> query = new ArrayList<String>();
            for (String current : pClassifier.getValid()) {
                query.add(current);
                
                
                if(RESTfulParameterBuilder.CLASSIFIER_ALL.equals(current)) {
                    retrieveAllClassifiers = true;
                } 
            }

            if (retrieveAllClassifiers || pClassifier.getInvalid().isEmpty() == false) {
                // in this case the parameter "maven.classifier" must not be present in the REST call to retrieve all classifiers.
                // if there is a "invalid" selection, we need to retrieve all elements to filter the "invalid" entries after retrieval.
            } else if (!query.isEmpty()) {
                requestParams.put(getClassifier(), query);
            } else {
                // In case the classifier has not been given, we send it to Nexus3 API to retrieve only the default artifacts like JARs.
                requestParams.putSingle(getClassifier(), "");
            }
        }
        
        if (!StringUtils.isEmpty(pToken)) {
            requestParams.putSingle(getContinuationToken(), pToken);
        }
        
        requestParams.putSingle(getSortOrder(), DEFAULT_SORTORDER);
        
        return requestParams;
    }

    public abstract String getRepositoryId();

    public abstract String getGroupId();

    public abstract String getArtifactId();

    public abstract String getPackaging();

    public abstract String getClassifier();
    
    public abstract String getContinuationToken();
    
    public abstract String getSortOrder();

}
