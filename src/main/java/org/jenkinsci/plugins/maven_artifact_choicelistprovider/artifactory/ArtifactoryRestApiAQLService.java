package org.jenkinsci.plugins.maven_artifact_choicelistprovider.artifactory;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.AbstractRESTfulVersionReader;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.IVersionReader2;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.ValidAndInvalidClassifier;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.VersionReaderException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ws.rs.core.MediaType;

public class ArtifactoryRestApiAQLService extends AbstractRESTfulVersionReader  implements IVersionReader2 {

    private static final Logger LOGGER = Logger.getLogger(ArtifactoryRestApiAQLService.class.getName());

    public static final String PARAMETER_QUERY = "aql";
    
    private String imagePrefix;

    public ArtifactoryRestApiAQLService(String pURL) {
        super(pURL);
    }

     public ArtifactoryRestApiAQLService(String pURL, String pImagePrefix) {
        super(pURL);
        this.imagePrefix = pImagePrefix;
    }

    @Override
    public List<String> retrieveVersions(MultivaluedMap<String, String> pParams) throws VersionReaderException {
        return this.retrieveVersions(pParams, null);
    }

    @Override
    public List<String> retrieveVersions(MultivaluedMap<String, String> pParams, ValidAndInvalidClassifier pClassifier)
            throws VersionReaderException {
        Set<String> callService = this.callService(pParams, pClassifier);
        return callService.stream().collect(Collectors.toList());
    }

    
    @Override
    @Deprecated
    public Set<String> callService(
            final String pRepositoryId,
            final String pGroup,
            final String pName,
            final String pPackaging,
            final ValidAndInvalidClassifier pClassifier) {
        final MultivaluedMap<String, String> requestParams = ArtifactoryRESTfulParameterBuilderForAQL.create(pRepositoryId, pName);;
        return this.callService(requestParams, pClassifier);
    }

    @Override
    public String getRESTfulServiceEndpoint() {
        return "artifactory/api/search/aql";
    }

    protected Set<String> callService(
            MultivaluedMap<String, String> pParams, final ValidAndInvalidClassifier pClassifier) {
        
        // init empty
        Set<String> retVal = new LinkedHashSet<>(); // retain order of insertion

        final ObjectMapper mapper = new ObjectMapper();

        WebTarget theInstance = getInstance();

        String aql = pParams.getFirst(PARAMETER_QUERY);

        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.info("URI: " + theInstance.getUri());
        }

        try {
            final Response response = theInstance.request().post(Entity.entity(aql, MediaType.TEXT_PLAIN));
            LOGGER.info("responsecode from Artifactory is " + response.getStatus());
            
            String plainResult = response.readEntity(String.class);

            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.info(plainResult);
            }

            final JsonNode resultAsJSON = mapper.readTree(plainResult);
            if(response.getStatus() != 200 ) {
                LOGGER.warning("error response from Artifactory is ." + response.getStatus());
            }

            if (resultAsJSON == null) {
                LOGGER.info("response from Artifactory is NULL.");
            } else {
                Set<String> currentResult = parseAndFilterResponse(resultAsJSON);
                retVal.addAll(currentResult);
            }
        } catch (JsonParseException e) {
            LOGGER.log(Level.WARNING, "failed to parse", e);
        } catch (JsonMappingException e) {
            LOGGER.log(Level.WARNING, "failed to map", e);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "failed to ioexception", e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "unexpected error", e);
        }

        return retVal;
    }

    /**
     * Parses the JSON response from Artifactory and creates a list of links where the
     * artifacts can be retrieved.
     *
     * @param pJsonResult the JSON response of the Artifactory API.
     * @return a unique list of URLs that are matching the search criteria, sorted
     *         by the order of the Artifactory service.
     */
    Set<String> parseAndFilterResponse(final JsonNode pJsonResult) {
        // Use a Map instead of a List to filter duplicated entries and also linked to
        // keep the order of response
        final Set<String> retVal = new LinkedHashSet<>();

        for (JsonNode result : pJsonResult.path("results")) {

            String name = null;
            String tag = null;

            for (JsonNode property : result.path("properties")) {

                switch (property.path("key").asText()) {
                    case "docker.repoName":
                        name = property.path("value").asText();
                        break;
                    case "docker.manifest":
                        tag = property.path("value").asText();
                        break;
                }
            }

            if (name != null && tag != null) {
                if (StringUtils.isEmpty(imagePrefix)) {
                    retVal.add(name + ":" + tag);
                } else {
                    retVal.add(imagePrefix + name + ":" + tag);
                }
            }
        }

        return retVal;
    }
   
}

