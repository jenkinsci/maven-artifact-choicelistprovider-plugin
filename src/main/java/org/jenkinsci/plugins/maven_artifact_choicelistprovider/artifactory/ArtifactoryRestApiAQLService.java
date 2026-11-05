package org.jenkinsci.plugins.maven_artifact_choicelistprovider.artifactory;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

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

public abstract class ArtifactoryRestApiAQLService extends AbstractRESTfulVersionReader  implements IVersionReader2 {

    private static final Logger LOGGER = Logger.getLogger(ArtifactoryRestApiAQLService.class.getName());

    public static final String PARAMETER_QUERY = "aql";
    
    public ArtifactoryRestApiAQLService(String pURL) {
        super(pURL);
    }   

    @Override
    public List<String> retrieveVersions(MultivaluedMap<String, String> pParams) throws VersionReaderException {
        return this.retrieveVersions(pParams, null);
    }

    @Override
    public List<String> retrieveVersions(MultivaluedMap<String, String> pParams, ValidAndInvalidClassifier pClassifier)
            throws VersionReaderException {
        Set<String> callService = this.callService(pParams);
        return callService.stream().collect(Collectors.toList());
    }

    @Override
    public String getRESTfulServiceEndpoint() {
        return "artifactory/api/search/aql";
    }

    protected Set<String> callService(MultivaluedMap<String, String> pParams) {
        final String aql = pParams.getFirst(PARAMETER_QUERY);
        return callService(aql);
    }

    protected Set<String> callService(final String pAQL) {
        
        // init empty
        Set<String> retVal = new LinkedHashSet<>(); // retain order of insertion

        final ObjectMapper mapper = new ObjectMapper();

        WebTarget theInstance = getInstance();        

        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.info("URI: " + theInstance.getUri());
            LOGGER.info(pAQL);
        }

        try {
            final Response response = theInstance.request().post(Entity.entity(pAQL, MediaType.TEXT_PLAIN));
            LOGGER.info("responsecode from Artifactory is " + response.getStatus());
            
            String plainResult = response.readEntity(String.class);
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.info(plainResult);
            }

            if(response.getStatus() == 200 ) {               
                final JsonNode resultAsJSON = mapper.readTree(plainResult);
                if (resultAsJSON == null) {
                    LOGGER.info("response from Artifactory cannot be parse to JSON: " + plainResult);
                } else {
                    Set<String> currentResult = parseAndFilterResponse(resultAsJSON);
                    retVal.addAll(currentResult);
                }
            } else {
                LOGGER.warning("error response from Artifactory is ." + response.getStatus());
                retVal.add("invalid response from artifactory, HTTP-" + response.getStatus());
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

    protected abstract Set<String> parseAndFilterResponse(final JsonNode pJsonResult);

}

