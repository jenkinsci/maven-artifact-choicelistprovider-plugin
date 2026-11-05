package org.jenkinsci.plugins.maven_artifact_choicelistprovider.artifactory;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.commons.lang3.StringUtils;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.AbstractRESTfulVersionReader;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.IVersionReader2;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.ValidAndInvalidClassifier;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.VersionReaderException;


/**
 *
 * Class utilizes the RESTful Search API from jFrog Artifactory to search for
 * items. <br>
 * <a href="https://www.jfrog.com/confluence/display/RTF/Artifactory+REST+API">Documentation</a>
 *
 * @author stephan.watermeyer, Diebold Nixdorf
 */
public class AbstractArtifactoryGAVCSearchService extends AbstractRESTfulVersionReader implements IVersionReader2 {

    private static final Logger LOGGER = Logger.getLogger(AbstractArtifactoryGAVCSearchService.class.getName());

    /**
     * Default Read Timeout when connecting to the Repository Service
     */
    private static final int ARTIFACTORY_SERVICE_READ_TIMEOUT = 15000;

    public AbstractArtifactoryGAVCSearchService(String pURL) {
        super(pURL);
    }

    private static final String SEARCH_SERVICE = "artifactory/api/search/gavc";

    @Override
    public String getRESTfulServiceEndpoint() {
        return SEARCH_SERVICE;
    }
   
    @Override 
    public Set<String> callService(
            String pRepositoryIdList,
            String pGroupId,
            String pArtifactId,
            String pPackaging,
            ValidAndInvalidClassifier pClassifier) {
        
        // repositoryIds can be a comma separated list
        final MultivaluedMap<String, String> requestParams =
                new ArtifactoryGAVCRESTfulParameterBuilder().create(pRepositoryIdList, pGroupId, pArtifactId, pClassifier);

        return this.callService(requestParams, pClassifier, pPackaging);
    }

    protected Set<String> callService(MultivaluedMap<String, String> requestParams,
            ValidAndInvalidClassifier pClassifier,
            String pPackaging) {

        Set<String> retVal = new LinkedHashSet<>();
        LOGGER.info("call artifactory service");

        WebTarget theInstance = getInstance();
        for (Map.Entry<String, List<String>> entries : requestParams.entrySet()) {
            theInstance = theInstance.queryParam(entries.getKey(), entries.getValue().toArray());
        }
        
        final String plainResult = theInstance.request(MediaType.APPLICATION_JSON).get(String.class);

        if (plainResult == null) {
            LOGGER.info("response from Artifactory service is NULL.");
        } else {
            LOGGER.info("parse result from artifactory service to JSON");
            retVal = parseResult(plainResult, pPackaging);
        }

        return retVal;
    }

    Set<String> parseResult(final String pContent, final String pPackaging) {
        final Set<String> retVal = new LinkedHashSet<>();
        try {
            final ArtifactoryResultModel fromJson = new Gson().fromJson(pContent, ArtifactoryResultModel.class);
            for (ArtifactoryResultEntryModel current : fromJson.getResults()) {

                // XXX: As the Artifactory Service is not able to filter on packaging level, we do it in the code.
                if (validatePackaging(current, pPackaging)) {
                    retVal.add(current.getDownloadUri());
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "failed to parse JSON returned from ArtifactoryService: '" + pContent + "'", e);
        }
        return retVal;
    }

    private boolean validatePackaging(final ArtifactoryResultEntryModel current, String pRequestedPackaging) {
        if (StringUtils.isEmpty(pRequestedPackaging.trim())) {
            return true;
        }

        // in case the packaging is not empty, the equals has to check the given package
        return pRequestedPackaging.equalsIgnoreCase(current.getExt());
    }

    /**
     * Our Artifactory is slow. We extend the default timeout.
     */
    protected int getTimeout() {
        return ARTIFACTORY_SERVICE_READ_TIMEOUT;
    }

    @Override
    public List<String> retrieveVersions(MultivaluedMap<String, String> pParams) throws VersionReaderException {
        Set<String> callService = this.callService(pParams, null, null);
        return callService.stream().collect(Collectors.toList());
    }

    @Override
    public List<String> retrieveVersions(MultivaluedMap<String, String> pParams, ValidAndInvalidClassifier pClassifier)
            throws VersionReaderException {
        Set<String> callService = this.callService(pParams, pClassifier, null);
        return callService.stream().collect(Collectors.toList());
    }

}

/**
 * Helper Class to parse the JSON
 *
 * @author stephan.watermeyer, Diebold Nixdorf
 */
class ArtifactoryResultEntryModel {

    @SerializedName("uri")
    String uri;

    @SerializedName("downloadUri")
    String downloadUri;

    @SerializedName("ext")
    String ext;

    public ArtifactoryResultEntryModel() {
        // Important to do nothing
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getDownloadUri() {
        return downloadUri;
    }

    public void setDownloadUri(String downloadUri) {
        this.downloadUri = downloadUri;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }
}

class ArtifactoryResultModel {

    @SerializedName("results")
    ArtifactoryResultEntryModel[] results = new ArtifactoryResultEntryModel[] {};

    public ArtifactoryResultModel() {
        // Important to do nothing
    }

    public ArtifactoryResultEntryModel[] getResults() {
        return results;
    }
}
