package org.jenkinsci.plugins.maven_artifact_choicelistprovider.artifactory.model;

import com.google.gson.annotations.SerializedName;

public class ArtifactoryResultModel {

    @SerializedName("results")
    ArtifactoryResultEntryModel[] results = new ArtifactoryResultEntryModel[] {};

    public ArtifactoryResultModel() {
        // Important to do nothing
    }

    public ArtifactoryResultEntryModel[] getResults() {
        return results;
    }
}
