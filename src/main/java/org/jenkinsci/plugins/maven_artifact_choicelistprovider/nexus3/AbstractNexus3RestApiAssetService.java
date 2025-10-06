package org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus3;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.AbstractRESTfulVersionReader;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.IVersionReader;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.ValidAndInvalidClassifier;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

abstract class AbstractNexus3RestApiAssetService extends AbstractRESTfulVersionReader implements IVersionReader {

	// https://help.sonatype.com/repomanager3/rest-and-integration-api/search-api#SearchAPI-SearchAssets
	private static final String NEXUS3_REST_API_ENDPOINT = "service/rest/v1/search/assets";

	private static final Logger LOGGER = Logger.getLogger(AbstractNexus3RestApiAssetService.class.getName());

	public AbstractNexus3RestApiAssetService(String pURL) {
		super(pURL);
	}

	@Override
	public Set<String> callService(final String pRepositoryId, final String pGroupId, final String pArtifactId,
			final String pPackaging, final ValidAndInvalidClassifier pClassifier) {

		// init empty
		Set<String> retVal = new LinkedHashSet<>(); // retain order of insertion
		String token = null;


		final ObjectMapper mapper = new ObjectMapper();

		do {
			WebTarget theInstance = getInstance();

			final MultivaluedMap<String, String> requestParams = createRequestParameters(pRepositoryId, pGroupId, pArtifactId, pPackaging, pClassifier, token);

			for (Map.Entry<String, List<String>> entries : requestParams.entrySet()) {
				theInstance = theInstance.queryParam(entries.getKey(), entries.getValue().toArray());
			}

			if (LOGGER.isLoggable(Level.INFO)) {
				LOGGER.info("URI: " + theInstance.getUri());
			}

			final String plainResult = theInstance.request(MediaType.APPLICATION_JSON).get(String.class);

			try {
				final Nexus3AssetRestResponse parsedJsonResult = mapper.readValue(plainResult, Nexus3AssetRestResponse.class);

				if (parsedJsonResult == null) {
					LOGGER.info("response from Nexus3 is NULL.");
				} else if (parsedJsonResult.getItems().length == 0) {
					LOGGER.info("response from Nexus3 does not contain any results.");

					// ISSUE20: If exactly 50 results are returned the token is still != null from
					// the previous call.
					// So we get the token from the request which should be null.
					token = parsedJsonResult.getContinuationToken();
				} else {
					Set<String> currentResult = parseAndFilterResponse(parsedJsonResult, pClassifier);
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

	protected abstract MultivaluedMap<String, String> createRequestParameters(String pRepositoryId, String pGroupId,
			String pArtifactId, String pPackaging, ValidAndInvalidClassifier pClassifier, String token);

	/**
	 * Parses the JSON response from Nexus3 and creates a list of links where the
	 * artifacts can be retrieved.
	 * 
	 * @param pJsonResult the JSON response of the Nexus3 API.
	 * @param pClassifier 
	 * @return a unique list of URLs that are matching the search criteria, sorted
	 *         by the order of the Nexus3 service.
	 */
	Set<String> parseAndFilterResponse(final Nexus3AssetRestResponse pJsonResult, final ValidAndInvalidClassifier pClassifier) {
		// Use a Map instead of a List to filter duplicated entries and also linked to
		// keep the order of response
		final Set<String> retVal = new LinkedHashSet<>();

		boolean mustApplyInvalidFilter = pClassifier != null && pClassifier.getInvalid().isEmpty() == false;
		boolean addItemToResult = true;
		
		for (final AssetItem current : pJsonResult.getItems()) {
		    if(mustApplyInvalidFilter) {
		        addItemToResult = true;
		        for(final String currentInvalidClassifier : pClassifier.getInvalid()) {
		            // Example: "https://xxx/repository/r/com/a/b/c/log-uploader/1.0.56/log-uploader-1.0.56-javadoc.jar",
		            if(current.getDownloadUrl().contains("-" + currentInvalidClassifier+ ".")) {
		                addItemToResult = false;
		            }
		        }
		    }
			
		    if(addItemToResult) {
		        retVal.add(current.getDownloadUrl());
		    }
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
