package org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus3;

import java.io.IOException;
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

    // https://nexus-dev.thyssenkrupp.com/service/rest/beta/search/assets?repository=maven-public&group=com.thyssenkrupp.gssit&name=hrforms&maven.extension=war
    private static final String LUCENE_SEARCH_SERVICE_URI = "service/rest/beta/search/assets";

    private static final Logger LOGGER = Logger.getLogger(Nexus3RestApiSearchService.class.getName());

    public Nexus3RestApiSearchService(String pURL) {
        super(pURL);
    }

    @Override
    public Set<String> callService(final String pRepositoryId, final String pGroupId, final String pArtifactId, final String pPackaging,
            final ValidAndInvalidClassifier pClassifier) {

        final MultivaluedMap<String, String> requestParams = new Nexus3RESTfulParameterBuilder().create(pRepositoryId, pGroupId, pArtifactId, pPackaging, pClassifier);

        Set<String> retVal = new LinkedHashSet<String>();
        LOGGER.info("call nexus service");
        WebResource rs = getInstance();

        if(LOGGER.isLoggable(Level.INFO)) {
            LOGGER.info("URI: " + rs.queryParams(requestParams).getURI());
        }
        
        final String result = rs.queryParams(requestParams).accept(MediaType.APPLICATION_JSON).get(String.class);
        ObjectMapper mapper = new ObjectMapper();
        Nexus3RestResponse jsonResult;
        try {
            jsonResult = mapper.readValue(result, Nexus3RestResponse.class);
            if (jsonResult == null) {
                LOGGER.info("response from Nexus is NULL.");
            } else if (jsonResult.getItems().length == 0) {
                LOGGER.info("response from Nexus does not contain any results.");
            } else {
                retVal = parseResponse(jsonResult, pPackaging, pClassifier);
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
     * Parses the XML response from Nexus and creates a list of links where the artifacts can be retrieved.
     * 
     * @param jsonResult
     * @param pPackaging
     * @param pClassifier
     * @return a unique list of URLs that are matching the search criteria, sorted by the order of the Nexus service.
     */
    Set<String> parseResponse(final Nexus3RestResponse jsonResult, final String pPackaging, final ValidAndInvalidClassifier pClassifier) {
        // Use a Map instead of a List to filter duplicated entries and also linked to keep the order of XML response
        final Set<String> retVal = new LinkedHashSet<String>();

        for (Items current : jsonResult.getItems()) {
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
        return LUCENE_SEARCH_SERVICE_URI;
    }

}
