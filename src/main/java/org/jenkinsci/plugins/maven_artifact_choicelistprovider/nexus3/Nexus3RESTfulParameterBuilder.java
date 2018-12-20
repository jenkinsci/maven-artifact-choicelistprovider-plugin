package org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus3;

import org.jenkinsci.plugins.maven_artifact_choicelistprovider.RESTfulParameterBuilder;

/**
 * 
 * Creates URL Parameters for a NEXUS and Artifactory Repository.
 *
 * @author stephan.watermeyer, Diebold Nixdorf
 */
public class Nexus3RESTfulParameterBuilder extends RESTfulParameterBuilder {

    public static final String PARAMETER_REPOSITORYID = "repository";

    public static final String PARAMETER_CLASSIFIER = "maven.classifier";

    public static final String PARAMETER_PACKAGING = "maven.extension";

    public static final String PARAMETER_ARTIFACTID = "name";

    public static final String PARAMETER_GROUPID = "group";
   
    public static final String PARAMETER_TOKEN = "continuationToken";

    public static final String PACKAGING_ALL = "*";

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
        return PARAMETER_TOKEN;
    }

}
