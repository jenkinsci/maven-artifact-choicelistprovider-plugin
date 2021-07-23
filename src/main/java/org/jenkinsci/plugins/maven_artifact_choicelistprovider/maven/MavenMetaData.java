package org.jenkinsci.plugins.maven_artifact_choicelistprovider.maven;

@javax.xml.bind.annotation.XmlRootElement(name = "metadata")
public class MavenMetaData {

	private String groupId;

	private String artifactId;

	private Versioning versioning;

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

	public Versioning getVersioning() {
		return versioning;
	}

	public void setVersioning(Versioning versioning) {
		this.versioning = versioning;
	}

}
