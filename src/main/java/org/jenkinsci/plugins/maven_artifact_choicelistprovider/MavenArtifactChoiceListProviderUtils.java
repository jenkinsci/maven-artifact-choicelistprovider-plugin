package org.jenkinsci.plugins.maven_artifact_choicelistprovider;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MavenArtifactChoiceListProviderUtils {

    /**
     * Cuts of the first parts of the URL and only returns a smaller set of items.
     *
     * @param pChoices
     *            the list which is transformed to a map
     * @return the map containing the short url as Key and the long url as value.
     */
    public static Map<String, String> toMap(List<String> pChoices) {
        Map<String, String> retVal = new LinkedHashMap<String, String>();
        for (String current : pChoices) {
            retVal.put(current.substring(current.lastIndexOf("/") + 1), current);
        }
        return retVal;
    }
}
