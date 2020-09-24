package org.jenkinsci.plugins.maven_artifact_choicelistprovider.azure;

import org.apache.commons.io.IOUtils;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.AbstractRESTfulVersionReader;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.IVersionReader;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.ValidAndInvalidClassifier;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.VersionReaderException;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus.*;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.text.Collator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import org.sonatype.nexus.repository.maven.internal.MavenModels;
import org.apache.maven.artifact.repository.metadata.Metadata;

public class AzureSearchService  extends AbstractRESTfulVersionReader implements IVersionReader {

    public static final String RELEASE = "RELEASE";
    final String MAVEN_METADATA_FILE_NAME="maven-metadata.xml";
    private static final Logger LOGGER = Logger.getLogger(AzureSearchService.class.getName());

    private boolean showRelease;

    public AzureSearchService(String repositoryUrl, boolean showRelease)
    {
        super(repositoryUrl);
        this.showRelease = showRelease;
    }
    public AzureSearchService(String repositoryUrl)
    {
        super(repositoryUrl);
        this.showRelease = false;
    }

    @Override
    public List<String> retrieveVersions(String pRepositoryId, String pGroupId, String pArtifactId, String pPackaging,
                                         ValidAndInvalidClassifier pClassifier) throws VersionReaderException {
        List<String> strings = super.retrieveVersions(pRepositoryId, pGroupId, pArtifactId, pPackaging,
                pClassifier);
        java.util.Collections.sort(strings, Collator.getInstance());
        if(showRelease) {
            strings.add(0, RELEASE);
        }
        return strings;
    }

    @Override
    public String getRESTfulServiceEndpoint() {
        return "";
    }

    @Override
    public Set<String> callService(String pRepositoryId, String pGroupId, String pArtifactId, String pPackaging, ValidAndInvalidClassifier pClassifier) {

        final IArtifactURLBuilder artifactURL = new ArtifactMetaDataUrlBuilder();
        artifactURL.setNexusURL(getURL()).setGroupId(pGroupId).setArtifactId(pArtifactId).setRepositoryId(pRepositoryId);
        setUrl(artifactURL.build());

        Set<String> retVal = new LinkedHashSet<String>();
        LOGGER.info("call Azure service");
        final String xmlResult = getInstance().accept(MediaType.APPLICATION_XML).get(String.class);

        if (xmlResult == null) {
            LOGGER.info("response from Azure is NULL.");
        } else {
            retVal = parseResponse(xmlResult);
        }
        return retVal;    }

    private Set<String> parseResponse(String xmlResult) {
        LOGGER.info(xmlResult);
        try {
            Metadata versionInfo = MavenModels.readMetadata(IOUtils.toInputStream(xmlResult));
            List<String> versions = versionInfo.getVersioning().getVersions();

            return new HashSet<String>(versions);

        } catch (IOException e) {
            LOGGER.info("Could not read metadata String");
            LOGGER.fine("Exception: " + e.getMessage() +"When parsing " + xmlResult);
        }
        return null;
    }
}

