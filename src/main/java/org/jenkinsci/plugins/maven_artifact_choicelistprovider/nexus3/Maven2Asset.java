package org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus3;

public class Maven2Asset extends BaseAsset {

	private String extension;

	private String groupId;

	private String artifactId;

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

}
