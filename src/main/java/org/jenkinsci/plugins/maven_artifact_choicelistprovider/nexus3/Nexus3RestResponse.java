package org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus3;

import java.util.Arrays;

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
        return Arrays.copyOf(items, items.length);
    }

    public void setItems(Item[] items) {
        this.items = Arrays.copyOf(items, items.length);
    }

    @Override
    public String toString() {
        return "Nexus3RestResponse [continuationToken = " + continuationToken + ", items = " + Arrays.toString(items) + "]";
    }

}
