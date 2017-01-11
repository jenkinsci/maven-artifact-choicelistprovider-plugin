package org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus;

/**
 * 
 * Provides functionality to retrieve an artifact as a direct link from the repository, such as:
 * https://server/content/repositories/repositoryId/com/thegroup/id/artifactId/version/artifactId-42.tar.gz
 * 
 * Limitations: This implementation does not work for snapshots.
 *
 * @author stephan.watermeyer, Diebold Nixdorf
 */
public class DirectArtifactURLBuilder extends AbstractArtifactURLBuilder implements IArtifactURLBuilder {

	@Override
	public String build(boolean pOnlyBaseURL) {
		final StringBuilder retVal = new StringBuilder();

		retVal.append(getNexusURL()); // No Slash, is already set on setter-call
		retVal.append("content/repositories").append(SLASH);
		retVal.append(getRepositoryId()).append(SLASH);
		retVal.append(getGroupId().replace(".", SLASH)).append(SLASH);
		retVal.append(getArtifactId()).append(SLASH);
		retVal.append(getVersion());

		if (!pOnlyBaseURL) {
			retVal.append(SLASH);
			retVal.append(getArtifactId());
			retVal.append("-");
			retVal.append(getVersion());
			if (getClassifier() == null) {
				retVal.append("");
			} else {
				retVal.append(getClassifier());
			}
			retVal.append(".");
			retVal.append(getPackaging());

		}
		return retVal.toString();
	}
}
