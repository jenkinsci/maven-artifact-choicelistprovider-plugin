package org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus3;

/**
 * POJO for Nexus3 REST API.
 * 
 * @author stephan.watermeyer
 *
 */
public class Item {
    private String id;

    private String repository;

    private String path;

    private String downloadUrl;

    private Checksum checksum;

    private String format;

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

    @Override
    public String toString() {
        return "Item [id = " + id + ", repository = " + repository + ", path = " + path + ", downloadUrl = " + downloadUrl + ", checksum = " + checksum + ", format = " + format
                + "]";
    }
}
