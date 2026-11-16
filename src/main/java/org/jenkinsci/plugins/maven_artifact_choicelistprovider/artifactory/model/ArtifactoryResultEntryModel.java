package org.jenkinsci.plugins.maven_artifact_choicelistprovider.artifactory.model;

import com.google.gson.annotations.SerializedName;

/**
 * Helper Class to parse the JSON
 *
 * @author stephan.watermeyer, Diebold Nixdorf
 */
public class ArtifactoryResultEntryModel {

    @SerializedName("uri")
    String uri;

    @SerializedName("downloadUri")
    String downloadUri;

    @SerializedName("version")
    String version;

    @SerializedName("ext")
    String ext;

    public ArtifactoryResultEntryModel() {
        // Important to do nothing
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getDownloadUri() {
        return downloadUri;
    }

    public void setDownloadUri(String downloadUri) {
        this.downloadUri = downloadUri;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }
}