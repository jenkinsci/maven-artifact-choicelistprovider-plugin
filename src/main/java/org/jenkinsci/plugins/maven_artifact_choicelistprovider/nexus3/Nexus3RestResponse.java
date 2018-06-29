package org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus3;

public class Nexus3RestResponse {

    private String continuationToken;

    private Items[] items;

    public String getContinuationToken() {
        return continuationToken;
    }

    public void setContinuationToken(String continuationToken) {
        this.continuationToken = continuationToken;
    }

    public Items[] getItems() {
        return items;
    }

    public void setItems(Items[] items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "ClassPojo [continuationToken = " + continuationToken + ", items = " + items + "]";
    }

}
