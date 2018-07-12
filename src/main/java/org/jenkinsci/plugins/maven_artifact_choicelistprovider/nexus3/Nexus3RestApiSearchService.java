package org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus3;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.jenkinsci.plugins.maven_artifact_choicelistprovider.AbstractRESTfulVersionReader;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.IVersionReader;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.ValidAndInvalidClassifier;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.WebResource;

public class Nexus3RestApiSearchService extends AbstractRESTfulVersionReader implements IVersionReader {

    private static final String NEXUS3_REST_API_ENDPOINT = "service/rest/beta/search/assets";

    private static final Logger LOGGER = Logger.getLogger(Nexus3RestApiSearchService.class.getName());

    public Nexus3RestApiSearchService(String pURL) {
        super(pURL);
    }

    @Override
    public Set<String> callService(final String pRepositoryId, final String pGroupId, final String pArtifactId, final String pPackaging,
            final ValidAndInvalidClassifier pClassifier) {

        final MultivaluedMap<String, String> requestParams = new Nexus3RESTfulParameterBuilder().create(pRepositoryId, pGroupId, pArtifactId, pPackaging, pClassifier);

        // init empty
        Set<String> retVal = Collections.emptySet();

        LOGGER.info("call nexus service");
        final WebResource rs = getInstance();

        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.info("URI: " + rs.queryParams(requestParams).getURI());
        }

        final String plainResult = rs.queryParams(requestParams).accept(MediaType.APPLICATION_JSON).get(String.class);
        final ObjectMapper mapper = new ObjectMapper();

        try {
            final Nexus3RestResponse parsedJsonResult = mapper.readValue(plainResult, Nexus3RestResponse.class);
            if (parsedJsonResult == null) {
                LOGGER.info("response from Nexus3 is NULL.");
            } else if (parsedJsonResult.getItems().length == 0) {
                LOGGER.info("response from Nexus3 does not contain any results.");
            } else {
                retVal = parseResponse(parsedJsonResult);
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
     * Parses the JSON response from Nexus3 and creates a list of links where the artifacts can be retrieved.
     * 
     * @param pJsonResult
     *            the JSON response of the Nexus3 API.
     * @return a unique list of URLs that are matching the search criteria, sorted by the order of the Nexus3 service.
     */
    Set<String> parseResponse(final Nexus3RestResponse pJsonResult) {
        // Use a Map instead of a List to filter duplicated entries and also linked to keep the order of XML response
        final Set<String> retVal = new LinkedHashSet<String>();

        for (Item current : pJsonResult.getItems()) {
            retVal.add(current.getDownloadUrl());
        }
        return retVal;
    }

    /**
     * Return the configured service endpoint in this repository.
     * 
     * @return the configured service endpoint in this repository.
     */
    @Override
    public String getRESTfulServiceEndpoint() {
        return NEXUS3_REST_API_ENDPOINT;
    }

}
