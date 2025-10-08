package org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus3;

import java.util.Set;
import java.util.logging.Logger;

import javax.ws.rs.core.MultivaluedMap;

import org.jenkinsci.plugins.maven_artifact_choicelistprovider.AbstractRESTfulVersionReader;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.IVersionReader2;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.ValidAndInvalidClassifier;


abstract class Nexus3RestApiSearchServiceBase extends AbstractRESTfulVersionReader implements IVersionReader2 {

	public static final String PARAMETER_TOKEN = "continuationToken";

	// https://help.sonatype.com/repomanager3/rest-and-integration-api/search-api#SearchAPI-SearchAssets
	private static final String NEXUS3_ASSET_REST_API_ENDPOINT = "service/rest/v1/search";

	private static final Logger LOGGER = Logger.getLogger(Nexus3RestApiAssetBase.class.getName());

	public Nexus3RestApiSearchServiceBase(String pURL) {
		super(pURL);
	}

	@Override
	@Deprecated
	public Set<String> callService(final String pRepositoryId, final String pGroup, final String pName,
		final String pPackaging, final ValidAndInvalidClassifier pClassifier) {
		final MultivaluedMap<String, String> requestParams = createRequestParameters(pRepositoryId, pGroup, pName, null);
		return this.callService(requestParams, pClassifier);
	}

	abstract Set<String> callService(MultivaluedMap<String, String> pParams, final ValidAndInvalidClassifier pClassifier);

	protected abstract MultivaluedMap<String, String> createRequestParameters(String pRepositoryId, String pGroup, String pName, String token);

	/**
	 * Return the configured service endpoint in this repository.
	 * 
	 * @return the configured service endpoint in this repository.
	 */
	@Override
	public String getRESTfulServiceEndpoint() {
		return NEXUS3_ASSET_REST_API_ENDPOINT;
	}

}
