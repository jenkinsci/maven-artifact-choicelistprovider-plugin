package org.jenkinsci.plugins.maven_artifact_choicelistprovider.artifactory;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.lang3.StringUtils;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.ValidAndInvalidClassifier;

import com.fasterxml.jackson.databind.JsonNode;

public class ArtifactoryAQLDockerImageService extends ArtifactoryRestApiAQLService{

    private String imagePrefix;

    public ArtifactoryAQLDockerImageService(String pURL) {
        super(pURL);
    }
    
    public ArtifactoryAQLDockerImageService(String pURL, String pImagePrefix) {
        super(pURL);
        this.imagePrefix = pImagePrefix;
    }

    @Override
    public Set<String> callService(
            final String pRepositoryId,
            final String pGroup,
            final String pName,
            final String pPackaging,
            final ValidAndInvalidClassifier pClassifier) {
        final String queryString = new ArtifactoryAQLDockerImageAndTagBuilder()
                    .setRepositoryId(pRepositoryId)
                    .setImageName(pName)
                    .build();        
        final MultivaluedMap<String, String> requestParams = ArtifactoryRESTfulParameterBuilderForAQL.create(queryString);
        return this.callService(requestParams);
    }

     /**
     * Parses the JSON response from Artifactory and creates a list of links where the
     * artifacts can be retrieved.
     *
     * @param pJsonResult the JSON response of the Artifactory API.
     * @return a unique list of URLs that are matching the search criteria, sorted
     *         by the order of the Artifactory service.
     */
    protected Set<String> parseAndFilterResponse(final JsonNode pJsonResult) {
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

