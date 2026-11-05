package org.jenkinsci.plugins.maven_artifact_choicelistprovider.artifactory;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.Extension;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.ws.rs.core.MultivaluedMap;
import org.apache.commons.lang3.StringUtils;
import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.IVersionReader2;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.ValidAndInvalidClassifier;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.VersionReaderException;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus3.MACLPChoiceParameterDefinitionBase;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

public class MACLPPipelineParameterArtifactoryMaven extends MACLPChoiceParameterDefinitionBase {

    private String artifactId;
    private String groupId;
    private String packaging;
    private String classifier;

    private static final Logger LOGGER = Logger.getLogger(MACLPPipelineParameterArtifactoryMaven.class.getName());

    @DataBoundConstructor
    public MACLPPipelineParameterArtifactoryMaven(
            String name,
            String choices,
            String description,
            String url,
            String credentialsId,
            String repository,
            String artifactId,
            String groupId,
            String classifier,
            String packaging,
            boolean reverseOrder) {
        super(name, new String[0], description, url, repository, credentialsId, reverseOrder);
        this.artifactId = artifactId;
        this.groupId = groupId;
        this.packaging = packaging;
        this.classifier = classifier;
    }

    public String getArtifactId() {
        return artifactId;
    }

    @DataBoundSetter
    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getGroupId() {
        return groupId;
    }

    @DataBoundSetter
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getPackaging() {
        return packaging;
    }

    @DataBoundSetter
    public void setPackaging(String packaging) {
        this.packaging = packaging;
    }

    public String getClassifier() {
        return classifier;
    }

    @DataBoundSetter
    public void setClassifier(String classifier) {
        this.classifier = classifier;
    }

    @Extension
    @Symbol("artifactoryMaven")
    public static final class DescriptorImpl extends ParameterDescriptor {

        @Override
        public final String getDisplayName() {
            return "Artifactory Maven Artifact";
        }
    }

    @Override
    protected IVersionReader2 createServiceInstance(String pUrl) {
        LOGGER.fine("createServiceInstance: " + pUrl);
        return new AbstractArtifactoryGAVCSearchService(pUrl);
    }

    protected List<String> retrieveVersions(IVersionReader2 pTheInstance, MultivaluedMap<String, String> pParams) throws VersionReaderException {
        final List<String> retVal;
        if(pTheInstance instanceof AbstractArtifactoryGAVCSearchService) {
            final AbstractArtifactoryGAVCSearchService artifactoryService = (AbstractArtifactoryGAVCSearchService) pTheInstance;
            final Set<String> serviceResult = artifactoryService.callService(pParams, null, packaging);
            retVal = serviceResult.stream().collect(Collectors.toList());
        } else {
            retVal = super.retrieveVersions(pTheInstance, pParams);
        }
        
        return retVal;
    }

    @Override
    protected MultivaluedMap<String, String> createParameterList() {
        LOGGER.fine("createParameterList");
        ArtifactoryGAVCRESTfulParameterBuilder mapper = new ArtifactoryGAVCRESTfulParameterBuilder();
        ValidAndInvalidClassifier classifier =
                (StringUtils.isEmpty(getClassifier()) ? null : ValidAndInvalidClassifier.fromString(getClassifier()));
        return mapper.create(getRepository(), getGroupId(), getArtifactId(), classifier);
    }

    @Override
    public int hashCode() {
        if (MACLPPipelineParameterArtifactoryMaven.class != getClass()) {
            return super.hashCode();
        }
        return Objects.hash(getName(), getDescription(), groupId, artifactId);
    }

    @Override
    @SuppressFBWarnings(
            value = "EQ_GETCLASS_AND_CLASS_CONSTANT",
            justification =
                    "ParameterDefinitionTest tests that subclasses are not equal to their parent classes, so the behavior appears to be intentional")
    public boolean equals(Object obj) {
        if (MACLPPipelineParameterArtifactoryMaven.class != getClass()) return super.equals(obj);
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        MACLPPipelineParameterArtifactoryMaven other = (MACLPPipelineParameterArtifactoryMaven) obj;
        if (!Objects.equals(getName(), other.getName())) return false;
        if (!Objects.equals(getDescription(), other.getDescription())) return false;
        if (!Objects.equals(artifactId, other.getArtifactId())) return false;
        return Objects.equals(groupId, other.getGroupId());
    }
}
