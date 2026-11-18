package org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus3;

import java.util.Arrays;

/**
 * POJO for Nexus3 API - Search
 *
 * @author stephan.watermeyer
 *
 */
public class Nexus3RestResponseSearch {

    private String continuationToken;

    private String total;

    private SearchItem[] items;

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getContinuationToken() {
        return continuationToken;
    }

    public void setContinuationToken(String continuationToken) {
        this.continuationToken = continuationToken;
    }

    public SearchItem[] getItems() {
        return Arrays.copyOf(items, items.length);
    }

    public void setItems(SearchItem[] items) {
        this.items = Arrays.copyOf(items, items.length);
    }

    @Override
    public String toString() {
        return "Nexus3RestResponse [continuationToken = " + continuationToken + ", items = " + Arrays.toString(items)
                + "]";
    }
}
