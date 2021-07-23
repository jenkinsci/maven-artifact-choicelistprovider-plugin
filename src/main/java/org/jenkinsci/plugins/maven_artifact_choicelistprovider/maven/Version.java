package org.jenkinsci.plugins.maven_artifact_choicelistprovider.maven;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "version")
@XmlAccessorType (XmlAccessType.FIELD)
public class Version {

	@XmlValue
	private String version;
	
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
}
