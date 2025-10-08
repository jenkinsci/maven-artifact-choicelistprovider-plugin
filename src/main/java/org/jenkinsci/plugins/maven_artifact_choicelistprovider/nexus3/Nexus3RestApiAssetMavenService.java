package org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus3;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ws.rs.core.MultivaluedMap;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.RESTfulParameterBuilder;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.ValidAndInvalidClassifier;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.VersionReaderException;

public class Nexus3RestApiAssetMavenService extends Nexus3RestApiAssetBase {

    private final RESTfulParameterBuilder mMapper;

    public Nexus3RestApiAssetMavenService(String pURL) {
        super(pURL);
        mMapper = new Nexus3RESTfulParameterBuilderForAssets();
    }

    @Override
    protected MultivaluedMap<String, String> createRequestParameters(
            String pRepositoryId,
            String pGroupId,
            String pArtifactId,
            String pPackaging,
            ValidAndInvalidClassifier pClassifier,
            String token) {
        return mMapper.create(pRepositoryId, pGroupId, pArtifactId, pPackaging, pClassifier, token);
    }

    @Override
    public List<String> retrieveVersions(MultivaluedMap<String, String> pParams, ValidAndInvalidClassifier pClassifier)
            throws VersionReaderException {
        Set<String> callService = super.callService(pParams, pClassifier);
        return callService.stream().collect(Collectors.toList());
    }

    @Override
    public List<String> retrieveVersions(MultivaluedMap<String, String> pParams) throws VersionReaderException {
        return this.retrieveVersions(pParams, null);
    }
}

class Nexus3RESTfulParameterBuilderForAssets extends RESTfulParameterBuilder {

    public static final String PARAMETER_REPOSITORYID = "repository";

    public static final String PARAMETER_CLASSIFIER = "maven.classifier";

    public static final String PARAMETER_PACKAGING = "maven.extension";

    public static final String PARAMETER_ARTIFACTID = "maven.artifactId";

    public static final String PARAMETER_GROUPID = "maven.groupId";

    public static final String PACKAGING_ALL = "*";

    public static final String PARAMETER_SORT = "sort";

    @Override
    public String getRepositoryId() {
        return PARAMETER_REPOSITORYID;
    }

    @Override
    public String getGroupId() {
        return PARAMETER_GROUPID;
    }

    @Override
    public String getArtifactId() {
        return PARAMETER_ARTIFACTID;
    }

    @Override
    public String getPackaging() {
        return PARAMETER_PACKAGING;
    }

    @Override
    public String getClassifier() {
        return PARAMETER_CLASSIFIER;
    }

    @Override
    public String getContinuationToken() {
        return Nexus3RestApiSearchService.PARAMETER_TOKEN;
    }

    @Override
    public String getSortOrder() {
        return PARAMETER_SORT;
    }
}
