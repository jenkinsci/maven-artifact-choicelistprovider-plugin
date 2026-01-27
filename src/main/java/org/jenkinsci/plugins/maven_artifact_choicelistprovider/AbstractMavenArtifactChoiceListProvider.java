package org.jenkinsci.plugins.maven_artifact_choicelistprovider;

import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl;
import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.model.Item;
import hudson.model.Queue;
import hudson.model.queue.Tasks;
import hudson.security.ACL;
import hudson.util.ListBoxModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.annotation.Nonnull;
import jenkins.model.Jenkins;
import jp.ikedam.jenkins.plugins.extensible_choice_parameter.ChoiceListProvider;
import jp.ikedam.jenkins.plugins.extensible_choice_parameter.ExtensibleChoiceParameterDefinition;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.artifactory.ArtifactoryChoiceListProvider;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.central.MavenCentralChoiceListProvider;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus.NexusChoiceListProvider;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerRequest2;
import org.springframework.security.core.Authentication;

/**
 *
 * Base Class for different {@link ChoiceListProvider} that can display information from an artifact repository, like
 * {@link NexusChoiceListProvider}, {@link MavenCentralChoiceListProvider} and {@link ArtifactoryChoiceListProvider}
 *
 * @author stephan.watermeyer, Diebold Nixdorf
 */
public abstract class AbstractMavenArtifactChoiceListProvider extends ChoiceListProvider {

    public static final String DEFAULT_REGEX_MATCH_ALL = ".*";

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
     * Initializes the choice list with at the artifactId.
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
        StaplerRequest2 req = Stapler.getCurrentRequest2();
        final Map<String, String> mChoices;
        // Allow null Items b/c this will cause the job to be run as System,
        // since we're being run outside of an authentication context (i.e.
        // either downstream, or by the system, which means: ACL.SYSTEM)
        Item item = (req != null ? req.findAncestorObject(Item.class) : null);
        IVersionReader serviceInstance = createServiceInstance(item);

        LOGGER.log(Level.FINE, "retrieve the versions from the repository");
        mChoices = readURL(
                serviceInstance,
                getRepositoryId(),
                getGroupId(),
                getArtifactId(),
                getPackaging(),
                getClassifier(),
                getInverseFilter(),
                getFilterExpression(),
                getReverseOrder());
        LOGGER.log(Level.FINER, "found these choices: {0}", mChoices);
        // FIXME: CHANGE-1: Return only the keys, that are shorter then the values
        return new LinkedList<String>(mChoices.keySet());
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<ChoiceListProvider> {

        public ListBoxModel doFillCredentialsIdItems(@AncestorInPath Item item, @QueryParameter String credentialsId) {
            final StandardListBoxModel result = new StandardListBoxModel();
            if (item == null) {
                if (!Jenkins.get().hasPermission(Jenkins.ADMINISTER)) {
                    return result.includeCurrentValue(credentialsId);
                }
            } else {
                if (!item.hasPermission(Item.EXTENDED_READ) && !item.hasPermission(CredentialsProvider.USE_ITEM)) {
                    return result.includeCurrentValue(credentialsId);
                }
            }
            final Authentication acl =
                    item instanceof Queue.Task ? Tasks.getAuthenticationOf2((Queue.Task) item) : ACL.SYSTEM2;
            return result.includeEmptyValue()
                    .includeMatchingAs(
                            item instanceof Queue.Task ? Tasks.getAuthenticationOf2((Queue.Task) item) : acl,
                            item,
                            StandardUsernamePasswordCredentials.class,
                            Collections.emptyList(),
                            CredentialsMatchers.instanceOf(StandardUsernamePasswordCredentials.class))
                    .includeCurrentValue(credentialsId);
        }
    }

    /**
     * Different implementation will return different {@link IVersionReader} instances.
     * @param item for security checks.
     * @return the service implementation.
     */
    public abstract IVersionReader createServiceInstance(Item item);

    /**
     * Returns the {@link UsernamePasswordCredentialsImpl} for the given CredentialId
     *
     * @param pCredentialId
     *            the internal jenkins id for the credentials
     * @return the credentials for the ID or NULL
     */
    // NOTE: we remove the @Nonnull annotation from pItem because we WANT to support the ability to query
    // as system, outside of an authentication context, because we may not have one at the time we need
    // to run the query ...
    public static UsernamePasswordCredentialsImpl getCredentials(@Nonnull String pCredentialId, Item pItem) {
        final Authentication acl =
                pItem instanceof Queue.Task ? Tasks.getAuthenticationOf2((Queue.Task) pItem) : ACL.SYSTEM2;
        return CredentialsMatchers.firstOrNull(
                CredentialsProvider.lookupCredentialsInItem(
                        UsernamePasswordCredentialsImpl.class, pItem, acl, Collections.<DomainRequirement>emptyList()),
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
    public static Map<String, String> readURL(
            final IVersionReader pInstance,
            final String pRepositoryId,
            final String pGroupId,
            final String pArtifactId,
            final String pPackaging,
            String pClassifier,
            final boolean pInverseFilter,
            final String pFilterExpression,
            final boolean pReverseOrder) {
        Map<String, String> retVal = new LinkedHashMap<String, String>();
        try {
            ValidAndInvalidClassifier classifierBox = ValidAndInvalidClassifier.fromString(pClassifier);

            List<String> choices =
                    pInstance.retrieveVersions(pRepositoryId, pGroupId, pArtifactId, pPackaging, classifierBox);
            if (LOGGER.isLoggable(Level.FINER)) {
                LOGGER.log(
                        Level.FINER,
                        "loaded the following choices from repository {0} for {1}:{2}:{3}:{4} -> {5}",
                        new Object[] {pRepositoryId, pGroupId, pArtifactId, pPackaging, classifierBox, choices});
            }

            List<String> filteredChoices = filterArtifacts(choices, pInverseFilter, pFilterExpression);
            if (LOGGER.isLoggable(Level.FINER)) {
                LOGGER.log(
                        Level.FINER,
                        "filtered down using /{0}/ (inverted={1}) to the following choices: {2}",
                        new Object[] {pFilterExpression, pInverseFilter, filteredChoices});
            }

            if (pReverseOrder) Collections.reverse(filteredChoices);

            retVal = MavenArtifactChoiceListProviderUtils.toMap(filteredChoices);
            if (LOGGER.isLoggable(Level.FINER)) {
                LOGGER.log(Level.FINER, "returning the final choices: {0}", new Object[] {retVal});
            }
        } catch (VersionReaderException e) {
            LOGGER.log(
                    Level.INFO,
                    "failed to retrieve versions from repository for g:" + pGroupId + ", a:" + pArtifactId + ", p:"
                            + pPackaging + ", c:" + pClassifier,
                    e);
            retVal.put("error", e.getMessage());
        } catch (Exception e) {
            LOGGER.log(
                    Level.WARNING,
                    "failed to retrieve versions from nexus for g:" + pGroupId + ", a:" + pArtifactId + ", p:"
                            + pPackaging + ", c:" + pClassifier,
                    e);
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
    public static List<String> filterArtifacts(
            final List<String> pChoices, final boolean pInverseFilter, final String pFilterExpression) {
        final List<String> retVal;

        // We only apply and compile regex if someone has configured something none-default.
        if (StringUtils.isEmpty(pFilterExpression) || DEFAULT_REGEX_MATCH_ALL.equals(pFilterExpression)) {
            LOGGER.log(Level.FINE, "do not filter artifacts.");
            retVal = pChoices;
        } else {
            LOGGER.log(Level.FINE, "filter artifacts based on " + pFilterExpression + ", inverse: " + pInverseFilter);
            retVal = new ArrayList<>();
            final Pattern compiledFilter = Pattern.compile(
                    StringUtils.isEmpty(pFilterExpression) ? DEFAULT_REGEX_MATCH_ALL : pFilterExpression);

            for (String choice : pChoices) {
                final boolean match = compiledFilter.matcher(choice).matches();
                // Using XOR operator: EITHER inverse was requested and filter doesn't match OR filter simply matches
                if (pInverseFilter ^ match) {
                    retVal.add(choice);
                }
            }
        }
        return retVal;
    }

    @Override
    public void onBuildCompletedWithValue(
            AbstractBuild<?, ?> build, ExtensibleChoiceParameterDefinition def, String value) {
        super.onBuildCompletedWithValue(build, def, value);
        LOGGER.log(Level.INFO, "onBuildCompletedWithValue" + value);
    }

    @Override
    public void onBuildTriggeredWithValue(
            AbstractProject<?, ?> job, ExtensibleChoiceParameterDefinition def, String value) {
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
