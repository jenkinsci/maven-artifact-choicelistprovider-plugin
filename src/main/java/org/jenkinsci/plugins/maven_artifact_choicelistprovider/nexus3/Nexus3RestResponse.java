package org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus3;

/**
 * POJO for Nexus3 API.
 * 
 * @author stephan.watermeyer
 *
 */
public class Nexus3RestResponse {

    private String continuationToken;

    private Item[] items;

    public String getContinuationToken() {
        return continuationToken;
    }

    public void setContinuationToken(String continuationToken) {
        this.continuationToken = continuationToken;
    }

    public Item[] getItems() {
        return items;
    }

    public void setItems(Item[] items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "Nexus3RestResponse [continuationToken = " + continuationToken + ", items = " + items + "]";
    }

}
