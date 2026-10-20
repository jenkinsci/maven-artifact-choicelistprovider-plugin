package org.jenkinsci.plugins.maven_artifact_choicelistprovider;

import java.util.List;

/**
 * An {@link IVersionReader} that returns fixed versions, or fails with a fixed message.
 */
public class FakeVersionReader implements IVersionReader {

    private final List<String> versions;
    private final String error;

    /**
     * @param pVersions the versions to return.
     * @param pError if not null, {@link #retrieveVersions} fails with this message instead.
     */
    public FakeVersionReader(final List<String> pVersions, final String pError) {
        this.versions = pVersions;
        this.error = pError;
    }

    @Override
    public List<String> retrieveVersions(
            String pRepositoryId,
            String pGroupId,
            String pArtifactId,
            String pPackaging,
            ValidAndInvalidClassifier pAcceptedClassifier)
            throws VersionReaderException {
        if (error != null) {
            throw new VersionReaderException(error);
        }
        return versions;
    }

    @Override
    public void setUserName(String pUserName) {}

    @Override
    public void setUserPassword(String pUserPassword) {}

    @Override
    public void setCredentials(String pUserName, String pUserPassword) {}
}
