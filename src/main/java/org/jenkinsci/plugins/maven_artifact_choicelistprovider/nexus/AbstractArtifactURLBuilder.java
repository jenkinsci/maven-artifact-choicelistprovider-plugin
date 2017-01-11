package org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus;

/**
 * 
 * This class provides basic functionality to allow the creation of a valid download link for a given artifact. All
 * Getters never return NULL. Trying to set a NULL value will result in a {@link NullPointerException}.
 *
 * @author stephan.watermeyer, Diebold Nixdorf
 */
public abstract class AbstractArtifactURLBuilder implements IArtifactURLBuilder {

	static final String SLASH = "/";

	private String nexusURL;
	private String repositoryId;
	private String groupId;
	private String artifactId;
	private String version;
	private String classifier;
	private String packaging;

	public AbstractArtifactURLBuilder() {
		nexusURL = "";
		repositoryId = "";
		groupId = "";
		artifactId = "";
		version = "";
		classifier = "";
		packaging = "";
	}

	void checkNull(String pField, String pValue) {
		if (pValue == null) {
			throw new NullPointerException("Field " + pField + " must not be null");
		}

	}

	@Override
	public String build() {
		return build(false);
	}

	public String getNexusURL() {
		return nexusURL;
	}

	@Override
	public IArtifactURLBuilder setNexusURL(final String repoURL) {
		checkNull("NexusURL", repoURL);
		this.nexusURL = repoURL;
		if (!this.nexusURL.endsWith(SLASH)) {
			this.nexusURL = this.nexusURL + SLASH;
		}
		return this;
	}

	public String getGroupId() {
		return groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public String getVersion() {
		return version;
	}

	public String getClassifier() {
		return classifier;
	}

	public String getPackaging() {
		return packaging;
	}

	public String getRepositoryId() {
		return repositoryId;
	}

	@Override
	public AbstractArtifactURLBuilder setRepositoryId(String repositoryId) {
		checkNull("RepositoryId", repositoryId);
		this.repositoryId = repositoryId;
		return this;
	}

	@Override
	public IArtifactURLBuilder setPackaging(String packaging) {
		checkNull("Packaging", packaging);
		this.packaging = packaging;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus.IArtifactURLBuilder#setClassifier(java.lang.String)
	 */
	@Override
	public IArtifactURLBuilder setClassifier(String classifier) {
		checkNull("Classifier", classifier);
		this.classifier = classifier;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus.IArtifactURLBuilder#setArtifactId(java.lang.String)
	 */
	@Override
	public IArtifactURLBuilder setArtifactId(String artifactId) {
		checkNull("ArtifactId", artifactId);
		this.artifactId = artifactId;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus.IArtifactURLBuilder#setGroupId(java.lang.String)
	 */
	@Override
	public IArtifactURLBuilder setGroupId(String groupId) {
		checkNull("GroupId", groupId);
		this.groupId = groupId;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus.IArtifactURLBuilder#setVersion(java.lang.String)
	 */
	@Override
	public IArtifactURLBuilder setVersion(String version) {
		checkNull("Version", version);
		this.version = version;
		return this;
	}

}
