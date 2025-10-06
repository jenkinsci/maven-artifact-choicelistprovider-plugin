package org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus3;

import javax.ws.rs.core.MultivaluedMap;

import org.jenkinsci.plugins.maven_artifact_choicelistprovider.IVersionReader;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.RESTfulParameterBuilder;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.ValidAndInvalidClassifier;

public class Nexus3RestApiAssetForGenericArtifactsService extends AbstractNexus3RestApiAssetService implements IVersionReader {

	private final RESTfulParameterBuilder mMapper;

	public Nexus3RestApiAssetForGenericArtifactsService(String pURL) {
		super(pURL);
		mMapper = new Nexus3RESTfulParameterBuilderForGenericArtifacts();
	}

	@Override
	protected MultivaluedMap<String, String> createRequestParameters(String pRepositoryId, String pGroupId,
			String pArtifactId, String pPackaging, ValidAndInvalidClassifier pClassifier, String token) {
		return mMapper.create(pRepositoryId, null, pArtifactId, null, null, token);
	}

}

class Nexus3RESTfulParameterBuilderForGenericArtifacts extends RESTfulParameterBuilder {

	public static final String PARAMETER_REPOSITORYID = "repository";

	public static final String PARAMETER_ARTIFACTID = "name";

	public static final String PARAMETER_TOKEN = "continuationToken";

	public static final String PARAMETER_SORT = "sort";

	@Override
	public String getRepositoryId() {
		return PARAMETER_REPOSITORYID;
	}

	@Override
	public String getArtifactId() {
		return PARAMETER_ARTIFACTID;
	}

	@Override
	public String getGroupId() {
		return null;
	}

	@Override
	public String getPackaging() {
		return null;
	}

	@Override
	public String getClassifier() {
		return null;
	}

	@Override
	public String getContinuationToken() {
		return PARAMETER_TOKEN;
	}

	@Override
	public String getSortOrder() {
		return PARAMETER_SORT;
	}

}
