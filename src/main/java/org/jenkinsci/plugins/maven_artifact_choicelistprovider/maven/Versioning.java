package org.jenkinsci.plugins.maven_artifact_choicelistprovider.maven;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement(name = "versioning")
@XmlAccessorType (XmlAccessType.FIELD)
public class Versioning {

	@XmlElementWrapper(name="versions")
	@XmlElement(name="version")
	private List<Version> versions = new ArrayList<>();
	
	private String latest;
	private String release;
	
	public List<Version> getVersions() {
		return versions;
	}

	public void setVersions(List<Version> pVersions) {
		this.versions = pVersions;
	}

	public String getLatest() {
		return latest;
	}

	public void setLatest(String latest) {
		this.latest = latest;
	}

	public String getRelease() {
		return release;
	}

	public void setRelease(String release) {
		this.release = release;
	}
	
	

}
