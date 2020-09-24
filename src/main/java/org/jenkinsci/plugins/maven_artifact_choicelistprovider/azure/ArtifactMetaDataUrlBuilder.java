package org.jenkinsci.plugins.maven_artifact_choicelistprovider.azure;

import org.jenkinsci.plugins.maven_artifact_choicelistprovider.AbstractMavenArtifactChoiceListProvider;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus.AbstractArtifactURLBuilder;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus.IArtifactURLBuilder;


public class ArtifactMetaDataUrlBuilder extends AbstractArtifactURLBuilder implements IArtifactURLBuilder {

    @Override
    public String build(boolean pOnlyBaseURL) {
        final StringBuilder retVal = new StringBuilder();

        retVal.append(getNexusURL()); // No Slash, is already set on setter-call
        retVal.append("_packaging").append(SLASH);
        retVal.append(getRepositoryId()).append(SLASH);
        retVal.append("maven/v1").append(SLASH);
        retVal.append(getGroupId().replace(".", SLASH)).append(SLASH);
        retVal.append(getArtifactId()).append(SLASH);
        retVal.append("maven-metadata.xml");

        return retVal.toString();

    }
}
