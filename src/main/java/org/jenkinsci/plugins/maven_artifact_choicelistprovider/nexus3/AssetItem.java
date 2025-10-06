package org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus3;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * POJO for Nexus3 REST API - Asset.
 * 
 * @author stephan.watermeyer
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AssetItem {
	private String id;

	private String repository;

	private String path;

	private String downloadUrl;

	private Checksum checksum;

	private String format;

	private String contentType;

	private String lastModified;

	private Maven2Asset maven2;

	private NPMAsset npm;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRepository() {
		return repository;
	}

	public void setRepository(String repository) {
		this.repository = repository;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public Checksum getChecksum() {
		return checksum;
	}

	public void setChecksum(Checksum checksum) {
		this.checksum = checksum;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getLastModified() {
		return lastModified;
	}

	public void setLastModified(String lastModified) {
		this.lastModified = lastModified;
	}

	public Maven2Asset getMaven2() {
		return maven2;
	}

	public NPMAsset getNpm() {
		return npm;
	}

	public void setNpm(NPMAsset npm) {
		this.npm = npm;
	}

	public void setMaven2(Maven2Asset maven2) {
		this.maven2 = maven2;
	}

	@Override
	public String toString() {
		return "Item [id = " + id + ", repository = " + repository + ", path = " + path + ", downloadUrl = "
				+ downloadUrl + ", checksum = " + checksum + ", format = " + format + "]";
	}
}
