package org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus;

import org.jenkinsci.plugins.maven_artifact_choicelistprovider.RESTfulParameterBuilder;

/**
 * Valid for Nexus2 and Artifactory.
 * 
 * @author stephan.watermeyer
 *
 */
public class StandardRESTfulParameterBuilder extends RESTfulParameterBuilder {

    public static final String PARAMETER_REPOSITORYID = "repositoryId";

    public static final String PARAMETER_CLASSIFIER = "c";

    public static final String PARAMETER_PACKAGING = "p";

    public static final String PARAMETER_ARTIFACTID = "a";

    public static final String PARAMETER_GROUPID = "g";

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

}
