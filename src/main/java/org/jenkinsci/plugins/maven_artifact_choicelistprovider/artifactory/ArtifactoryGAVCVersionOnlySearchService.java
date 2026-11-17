package org.jenkinsci.plugins.maven_artifact_choicelistprovider.artifactory;

import com.google.gson.Gson;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.IVersionReader2;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.artifactory.model.ArtifactoryResultEntryModel;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.artifactory.model.ArtifactoryResultModel;

/**
 *
 * Class utilizes the RESTful Search API from jFrog Artifactory to search for
 * items. <br>
 * <a href="https://www.jfrog.com/confluence/display/RTF/Artifactory+REST+API">Documentation</a>
 *
 * @author stephan.watermeyer, Diebold Nixdorf
 */
public class ArtifactoryGAVCVersionOnlySearchService extends ArtifactoryGAVCSearchService implements IVersionReader2 {

    private static final Logger LOGGER = Logger.getLogger(ArtifactoryGAVCVersionOnlySearchService.class.getName());

    public ArtifactoryGAVCVersionOnlySearchService(String pURL) {
        super(pURL);
    }

    @Override
    Set<String> parseResult(final String pContent, final String pPackaging) {
        final Set<String> retVal = new LinkedHashSet<>();
        try {
            final ArtifactoryResultModel fromJson = new Gson().fromJson(pContent, ArtifactoryResultModel.class);
            for (ArtifactoryResultEntryModel current : fromJson.getResults()) {

                // XXX: As the Artifactory Service is not able to filter on packaging level, we do it in the code.
                if (validatePackaging(current, pPackaging)) {
                    retVal.add(current.getVersion());
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "failed to parse JSON returned from ArtifactoryService: '" + pContent + "'", e);
        }
        return retVal;
    }
}
