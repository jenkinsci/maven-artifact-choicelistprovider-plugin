package org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus3;

import javax.ws.rs.core.MultivaluedMap;

import org.jenkinsci.plugins.maven_artifact_choicelistprovider.IVersionReader;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.ValidAndInvalidClassifier;

public class Nexus3RestApiAssetService extends AbstractNexus3RestApiAssetService implements IVersionReader {

	private final Nexus3RESTfulParameterBuilderForMaven2Artifacts mMapper;

	public Nexus3RestApiAssetService(String pURL) {
		super(pURL);
		mMapper = new Nexus3RESTfulParameterBuilderForMaven2Artifacts();
	}

	@Override
	protected MultivaluedMap<String, String> createRequestParameters(String pRepositoryId, String pGroupId,
			String pArtifactId, String pPackaging, ValidAndInvalidClassifier pClassifier, String token) {
		return mMapper.create(pRepositoryId, pGroupId, pArtifactId, pPackaging, pClassifier, token);
	}

}
