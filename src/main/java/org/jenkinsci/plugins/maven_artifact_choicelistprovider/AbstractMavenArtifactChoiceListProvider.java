package org.jenkinsci.plugins.maven_artifact_choicelistprovider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.artifactory.ArtifactoryChoiceListProvider;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.central.MavenCentralChoiceListProvider;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus.NexusChoiceListProvider;
import org.kohsuke.stapler.DataBoundSetter;

import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl;

import hudson.ExtensionPoint;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.security.ACL;
import jenkins.model.Jenkins;
import jp.ikedam.jenkins.plugins.extensible_choice_parameter.ChoiceListProvider;
import jp.ikedam.jenkins.plugins.extensible_choice_parameter.ExtensibleChoiceParameterDefinition;

/**
 * 
 * Base Class for different {@link ChoiceListProvider} that can display information from an artifact repository, like
 * {@link NexusChoiceListProvider}, {@link MavenCentralChoiceListProvider} and {@link ArtifactoryChoiceListProvider}
 *
 * @author stephan.watermeyer, Diebold Nixdorf
 */
public abstract class AbstractMavenArtifactChoiceListProvider extends ChoiceListProvider implements ExtensionPoint {

    private static final long serialVersionUID = -6055763342458172275L;

    private static final Logger LOGGER = Logger.getLogger(AbstractMavenArtifactChoiceListProvider.class.getName());

    private String repositoryId;
    private String groupId;
    private String artifactId;
    private String packaging;
    private String classifier;
    private boolean inverseFilter;
    private String filterExpression;
    private boolean reverseOrder;

    /**
     * Initializes the choicelist with at the artifactId.
     * 
     * @param artifactId
     *            the artifactId is the minimum required information.
     */
    public AbstractMavenArtifactChoiceListProvider(final String artifactId) {
        super();
        this.setArtifactId(artifactId);
    }

    @Override
    public List<String> getChoiceList() {

        LOGGER.log(Level.FINE, "retrieve the versions from the repository");
        final Map<String, String> mChoices = readURL(createServiceInstance(), getRepositoryId(), getGroupId(), getArtifactId(), getPackaging(), getClassifier(),
                getInverseFilter(), getFilterExpression(), getReverseOrder());
        // FIXME: CHANGE-1: Return only the keys, that are shorter then the values
        // return new ArrayList<String>(mChoices.keySet());
        return new ArrayList<String>(mChoices.values());
    }

    /**
     * Different implementation will return different {@link IVersionReader} instances.
     * 
     * @return the source of the artifacts.
     */
    public abstract IVersionReader createServiceInstance();

    /**
     * Returns the {@link UsernamePasswordCredentialsImpl} for the given CredentialId
     * 
     * @param pCredentialId
     *            the internal jenkins id for the credentials
     * @return the credentials for the ID or NULL
     */
    public static UsernamePasswordCredentialsImpl getCredentials(final String pCredentialId) {
        return CredentialsMatchers.firstOrNull(
                CredentialsProvider.lookupCredentials(UsernamePasswordCredentialsImpl.class, Jenkins.getInstance(), ACL.SYSTEM, Collections.<DomainRequirement> emptyList()),
                CredentialsMatchers.allOf(CredentialsMatchers.withId(pCredentialId)));
    }

    /**
     * Retrieves the versions from the given source.
     * 
     * @param pInstance
     *            the artifact repository service.
     * @param pRepositoryId
     *            the repositoryId
     * @param pGroupId
     *            the groupId of the artifact
     * @param pArtifactId
     *            the artifactId
     * @param pPackaging
     *            the packaging
     * @param pClassifier
     *            the classifier
     * @param pInverseFilter
     *            <code>true</code> if the result should contain artifacts which don't match the pFilterExpression regexp
     *            <code>false</code> if the result should contain artifacts which match the pFilterExpression regexp
     * @param pFilterExpression
     *            Regexp applied on the artifacts for further selection of what should be returned. Empty string acts like <code>.*</code>.
     * @param pReverseOrder
     *            <code>true</code> if the result should be reversed.
     * @return never null
     */
    public static Map<String, String> readURL(final IVersionReader pInstance, final String pRepositoryId, final String pGroupId, final String pArtifactId, final String pPackaging,
            String pClassifier, final boolean pInverseFilter, final String pFilterExpression, final boolean pReverseOrder) {
        Map<String, String> retVal = new LinkedHashMap<String, String>();
        try {
            ValidAndInvalidClassifier classifierBox = ValidAndInvalidClassifier.fromString(pClassifier);

            List<String> choices = pInstance.retrieveVersions(pRepositoryId, pGroupId, pArtifactId, pPackaging, classifierBox);

            List<String> filteredChoices = filterArtifacts(choices, pInverseFilter, pFilterExpression);

            if (pReverseOrder)
                Collections.reverse(filteredChoices);

            retVal = toMap(filteredChoices);
        } catch (VersionReaderException e) {
            LOGGER.log(Level.INFO, "failed to retrieve versions from repository for g:" + pGroupId + ", a:" + pArtifactId + ", p:" + pPackaging + ", c:" + pClassifier, e);
            retVal.put("error", e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "failed to retrieve versions from nexus for g:" + pGroupId + ", a:" + pArtifactId + ", p:" + pPackaging + ", c:" + pClassifier, e);
            retVal.put("error", "Unexpected Error: " + e.getMessage());
        }
        return retVal;
    }

    /**
     * Returns a new list containing/excluding the matching artifacts based on whether inversion was required.
     *
     * @param pChoices List of artifacts to filter. This function won't change its elements.
     * @param pInverseFilter Decides whether the pFilterExpression should be negated.
     *                       If <code>true</code> then only non matching artifacts will be returned.
     * @param pFilterExpression A regular expression which will be applied to the whole artifact string.
     *                          Empty string and null are treated as perfect match (value of inverse is still considered).
     * @return A new list of matching artifacts only
     * @throws PatternSyntaxException If pFilterExpression parameter is not a valid regular expression
     */
    public static List<String> filterArtifacts(final List<String> pChoices, final boolean pInverseFilter, final String pFilterExpression) {
        final List<String> filteredList = new ArrayList<>();
        final Pattern compiledFilter = Pattern.compile(StringUtils.isEmpty(pFilterExpression) ? ".*" : pFilterExpression);

        for(String choice : pChoices) {
            final boolean match = compiledFilter.matcher(choice).matches();
            // Using XOR operator: EITHER inverse was requested and filter doesn't match OR filter simply matches
            if (pInverseFilter ^ match) {
                filteredList.add(choice);
            }
        }
        return filteredList;
    }

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

    @Override
    public void onBuildCompletedWithValue(AbstractBuild<?, ?> build, ExtensibleChoiceParameterDefinition def, String value) {
        super.onBuildCompletedWithValue(build, def, value);
        LOGGER.log(Level.INFO, "onBuildCompletedWithValue" + value);
    }

    @Override
    public void onBuildTriggeredWithValue(AbstractProject<?, ?> job, ExtensibleChoiceParameterDefinition def, String value) {
        super.onBuildTriggeredWithValue(job, def, value);
        LOGGER.log(Level.INFO, "onBuildTriggeredWithValue: " + value);
    }

    @DataBoundSetter
    public void setGroupId(String groupId) {
        this.groupId = StringUtils.trim(groupId);
    }

    @DataBoundSetter
    public void setArtifactId(String artifactId) {
        this.artifactId = StringUtils.trim(artifactId);
    }

    @DataBoundSetter
    public void setPackaging(String packaging) {
        this.packaging = StringUtils.trim(packaging);
    }

    @DataBoundSetter
    public void setClassifier(String classifier) {
        this.classifier = StringUtils.trim(classifier);
    }

    @DataBoundSetter
    public void setInverseFilter(boolean inverseFilter) {
        this.inverseFilter = inverseFilter;
    }

    @DataBoundSetter
    public void setFilterExpression(String filterExpression) {
        this.filterExpression = filterExpression;
    }

    @DataBoundSetter
    public void setReverseOrder(boolean reverseOrder) {
        this.reverseOrder = reverseOrder;
    }

    @DataBoundSetter
    public void setRepositoryId(String repositoryId) {
        this.repositoryId = repositoryId;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getPackaging() {
        return packaging;
    }

    public String getClassifier() {
        return classifier;
    }

    public boolean getInverseFilter() {
        return inverseFilter;
    }

    public String getFilterExpression() {
        return filterExpression;
    }

    public boolean getReverseOrder() {
        return reverseOrder;
    }

    public String getRepositoryId() {
        return repositoryId;
    }

}
