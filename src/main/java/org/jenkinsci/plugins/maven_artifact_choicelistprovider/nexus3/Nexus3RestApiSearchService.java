package org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus3;

import javax.ws.rs.core.MultivaluedMap;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.RESTfulParameterBuilder;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.ValidAndInvalidClassifier;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.VersionReaderException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Nexus3RestApiSearchService extends Nexus3RestApiSearchServiceBase  {

    private static final Logger LOGGER = Logger.getLogger(Nexus3RestApiSearchService.class.getName());

    private String imagePrefix; 

    public Nexus3RestApiSearchService(String pURL) {
        this(pURL, null);
    }
	
    public Nexus3RestApiSearchService(String pURL, String pImagePrefix) {
		super(pURL);
        this.imagePrefix = pImagePrefix;
	}

	@Override
	protected MultivaluedMap<String, String> createRequestParameters(String pRepository, String pGroup,
			String pName, String token) {
		return Nexus3RESTfulParameterBuilderForSearch.create(pRepository, pGroup, pName, token);
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

    protected Set<String> callService(MultivaluedMap<String, String> pParams, final ValidAndInvalidClassifier pClassifier) {

		// init empty
		Set<String> retVal = new LinkedHashSet<>(); // retain order of insertion
		String token = null;

		final ObjectMapper mapper = new ObjectMapper();

		do {
			WebTarget theInstance = getInstance();

			// Update the token in every iteration
			pParams.putSingle(PARAMETER_TOKEN, token);

			for (Map.Entry<String, List<String>> entries : pParams.entrySet()) {
				theInstance = theInstance.queryParam(entries.getKey(), entries.getValue().toArray());
			}

			if (LOGGER.isLoggable(Level.INFO)) {
				LOGGER.info("URI: " + theInstance.getUri());
			}

			final String plainResult = theInstance.request(MediaType.APPLICATION_JSON).get(String.class);

			if (LOGGER.isLoggable(Level.FINEST)) {
				LOGGER.info(plainResult);
			}

			try {
				final Nexus3RestResponseSearch parsedJsonResult = mapper.readValue(plainResult, Nexus3RestResponseSearch.class);

				if (parsedJsonResult == null) {
					LOGGER.info("response from Nexus3 is NULL.");
				} else if (parsedJsonResult.getItems().length == 0) {
					LOGGER.info("response from Nexus3 does not contain any results.");

					// ISSUE20: If exactly 50 results are returned the token is still != null from
					// the previous call.
					// So we get the token from the request which should be null.
					token = parsedJsonResult.getContinuationToken();
				} else {
					Set<String> currentResult = parseAndFilterResponse(parsedJsonResult);
					retVal.addAll(currentResult);

					// control the loop and maybe query again
					token = parsedJsonResult.getContinuationToken();
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
		} while (!StringUtils.isEmpty(token));

		return retVal;
	}

    /**
	 * Parses the JSON response from Nexus3 and creates a list of links where the
	 * artifacts can be retrieved.
	 * 
	 * @param pJsonResult the JSON response of the Nexus3 API.
	 * @param pClassifier 
	 * @return a unique list of URLs that are matching the search criteria, sorted
	 *         by the order of the Nexus3 service.
	 */
	Set<String> parseAndFilterResponse(final Nexus3RestResponseSearch pJsonResult) {
		// Use a Map instead of a List to filter duplicated entries and also linked to
		// keep the order of response
		final Set<String> retVal = new LinkedHashSet<>();

		boolean addItemToResult = true;
		
		for (final SearchItem current : pJsonResult.getItems()) {
			
		    if(addItemToResult) {
                if(StringUtils.isEmpty(imagePrefix)) {
		            retVal.add(current.getName() + ":" + current.getVersion());
                } else {
                    retVal.add(imagePrefix + current.getName() + ":" + current.getVersion());
                }
		    }
		}
		return retVal;
	}

}

class Nexus3RESTfulParameterBuilderForSearch {

    private static final Logger LOGGER = Logger.getLogger(Nexus3RESTfulParameterBuilderForSearch.class.getName());

    public static final String PARAMETER_REPOSITORY = "repository";

    public static final String PARAMETER_NAME = "name";

    public static final String PARAMETER_GROUP = "group";
    
    public static final String PACKAGING_ALL = "*";
    
    public static final String PARAMETER_SORT = "sort";


    public String getContinuationToken() {
        return Nexus3RestApiSearchServiceBase.PARAMETER_TOKEN;
    }

	public String getSortOrder() {
		return PARAMETER_SORT;
	}

    public String getName() {
        return PARAMETER_NAME;
    }

    public String getRepository() {
        return PARAMETER_REPOSITORY;
    }

    public static MultivaluedMap<String, String> create(String pRepository, String pGroup, String pName) {
        return create(pRepository,pGroup,pName, null);
    }
    public static MultivaluedMap<String, String> create(String pRepository, String pGroup, String pName, String pToken) {
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("create parameters for: repository: " + pRepository + " g:" + pGroup + ", n:" + pName + ", t:" + pToken);
        }

        MultivaluedMap<String, String> requestParams = new MultivaluedHashMap<String, String>();
        if (!StringUtils.isEmpty(pRepository)) {
            requestParams.putSingle(PARAMETER_REPOSITORY, pRepository);
        }
        if (!StringUtils.isEmpty(pGroup)) {
            requestParams.putSingle(PARAMETER_GROUP, pGroup);
        }
        if (!StringUtils.isEmpty(pName)) {
            requestParams.putSingle(PARAMETER_NAME, pName);
        }
      
        if (!StringUtils.isEmpty(pToken)) {
            requestParams.putSingle(Nexus3RestApiSearchServiceBase.PARAMETER_TOKEN, pToken);
        }
        
        requestParams.putSingle(PARAMETER_SORT, RESTfulParameterBuilder.DEFAULT_SORTORDER);
        
        return requestParams;
    }

}
