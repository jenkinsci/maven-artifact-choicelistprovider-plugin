package org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus3;

import java.util.Arrays;

/**
 * POJO for Nexus3 API.
 * 
 * @author stephan.watermeyer
 *
 */
public class Nexus3RestResponseAsset {

    private String continuationToken;

    private AssetItem[] items;

    public String getContinuationToken() {
        return continuationToken;
    }

    public void setContinuationToken(String continuationToken) {
        this.continuationToken = continuationToken;
    }

    public AssetItem[] getItems() {
        return Arrays.copyOf(items, items.length);
    }

    public void setItems(AssetItem[] items) {
        this.items = Arrays.copyOf(items, items.length);
    }

    @Override
    public String toString() {
        return "Nexus3RestResponse [continuationToken = " + continuationToken + ", items = " + Arrays.toString(items) + "]";
    }

}
