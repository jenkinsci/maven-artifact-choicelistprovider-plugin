package org.jenkinsci.plugins.maven_artifact_choicelistprovider;

import hudson.model.Descriptor;
import hudson.util.FormValidation;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import jp.ikedam.jenkins.plugins.extensible_choice_parameter.ChoiceListProvider;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.QueryParameter;

/**
 *
 * Base Class for a Descriptor.
 *
 * @author stephan.watermeyer, Diebold Nixdorf
 */
public abstract class AbstractMavenArtifactDescriptorImpl extends Descriptor<ChoiceListProvider> {

    public FormValidation doCheckGroupId(@QueryParameter String groupId, @QueryParameter String artifactId) {
        if (StringUtils.isBlank(groupId) && StringUtils.isBlank(artifactId)) {
            return FormValidation.error("The groudId cannot be empty. Please fill at least GroupId or ArtifactId.");
        }

        return FormValidation.ok();
    }

    public FormValidation doCheckArtifactId(@QueryParameter String artifactId, @QueryParameter String groupId) {
        if (StringUtils.isBlank(artifactId) && StringUtils.isBlank(groupId)) {
            return FormValidation.error("The artifactId cannot be empty. Please fill at least ArtifactId or GroupId.");
        }

        return FormValidation.ok();
    }

    public FormValidation doCheckPackaging(@QueryParameter String packaging) {
        if (!StringUtils.isBlank(packaging) && packaging.startsWith(".")) {
            return FormValidation.error("packaging must not start with a .");
        }

        return FormValidation.ok();
    }

    public FormValidation doCheckClassifier(@QueryParameter String classifier) {
        if (StringUtils.isBlank(classifier)) {
            return FormValidation.ok("OK, will not filter for any classifier");
        }
        return FormValidation.ok();
    }

    public FormValidation doCheckFilterExpression(@QueryParameter String filterExpression) {
        if (StringUtils.isEmpty(filterExpression)) {
            return FormValidation.warning(
                    "Although blank string is a syntactically valid regular expression, it would result no match. "
                            + "To maintain backward compatibility of the plugin, blank string is considered as match all. "
                            + "Please explicitly type '.*' instead (without quotes) to remove this warning.");
        }

        try {
            Pattern.compile(filterExpression);
        } catch (PatternSyntaxException pse) {
            return FormValidation.error(
                    "Filter Expression is not a valid regular expression. Try '.*' instead (without quotes). "
                            + "Please check https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html "
                            + "for the accepted syntax");
        }

        return FormValidation.ok();
    }

    public FormValidation performTest(
            final IVersionReader pService,
            @QueryParameter String repositoryId,
            @QueryParameter String groupId,
            @QueryParameter String artifactId,
            @QueryParameter String packaging,
            @QueryParameter String classifier,
            @QueryParameter boolean inverseFilter,
            @QueryParameter String filterExpression,
            @QueryParameter boolean reverseOrder) {
        if (StringUtils.isEmpty(packaging) && !StringUtils.isEmpty(classifier)) {
            return FormValidation.error(
                    "You have choosen an empty Packaging configuration but have configured a Classifier. Please either define a Packaging value or remove the Classifier");
        }

        try {
            final Map<String, String> entriesFromURL = wrapTestConnection(
                    pService,
                    repositoryId,
                    groupId,
                    artifactId,
                    packaging,
                    classifier,
                    inverseFilter,
                    filterExpression,
                    reverseOrder);

            if (entriesFromURL.isEmpty()) {
                return FormValidation.ok("(Working, but no Entries found)");
            }
            return FormValidation.ok(StringUtils.join(entriesFromURL.values(), '\n'));
        } catch (Exception e) {
            return FormValidation.error("error reading versions from url:" + e.getMessage());
        }
    }

    /**
     * Own implementations of this DescriptorImpl might do this normally as a static inner class. The
     * surrounding class then has to extend {@link AbstractMavenArtifactChoiceListProvider} and thus this wrapper method
     * can forward to the implementation of readURL.
     *
     * @param service
     *            TBD
     * @param repositoryId
     *            TBD
     * @param groupId
     *            TBD
     * @param artifactId
     *            TBD
     * @param packaging
     *            TBD
     * @param classifier
     *            TBD
     * @param inverseFilter
     *            TBD
     * @param filterExpression
     *            TBD
     * @param reverseOrder
     *            TBD
     * @return the list of found items.
     */
    protected abstract Map<String, String> wrapTestConnection(
            IVersionReader service,
            String repositoryId,
            String groupId,
            String artifactId,
            String packaging,
            String classifier,
            boolean inverseFilter,
            String filterExpression,
            boolean reverseOrder);
}
