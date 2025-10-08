package org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus3;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.Extension;
import java.util.Objects;
import java.util.logging.Logger;
import javax.ws.rs.core.MultivaluedMap;
import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.IVersionReader2;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

public class MACLPPipelineParameterNexus3DockerImages extends MACLPChoiceParameterDefinitionBase {

    private String imageName;
    private String group;

    private String imagePrefix;

    private static final Logger LOGGER = Logger.getLogger(MACLPPipelineParameterNexus3DockerImages.class.getName());

    @DataBoundConstructor
    public MACLPPipelineParameterNexus3DockerImages(
            String name,
            String choices,
            String description,
            String url,
            String credentialsId,
            String repository,
            String imageName,
            String imagePrefix,
            String group,
            boolean reverseOrder) {
        super(name, new String[0], description, url, repository, credentialsId, reverseOrder);
        this.group = group;
        this.imageName = imageName;
        this.imagePrefix = imagePrefix;
    }

    public String getImagePrefix() {
        return imagePrefix;
    }

    @DataBoundSetter
    public void setImagePrefix(String imagePrefix) {
        this.imagePrefix = imagePrefix;
    }

    public String getImageName() {
        return imageName;
    }

    @DataBoundSetter
    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getGroup() {
        return group;
    }

    @DataBoundSetter
    public void setGroup(String group) {
        this.group = group;
    }

    @Extension
    @Symbol("nexus3DockerImage")
    public static final class DescriptorImpl extends ParameterDescriptor {

        @Override
        public final String getDisplayName() {
            return "Nexus3 Docker Image";
        }
    }

    @Override
    protected IVersionReader2 createServiceInstance(String pUrl) {
        LOGGER.fine("createServiceInstance: " + pUrl);
        return new Nexus3RestApiSearchService(pUrl, getImagePrefix());
    }

    @Override
    protected MultivaluedMap<String, String> createParameterList() {
        LOGGER.fine("createParameterList");
        return Nexus3RESTfulParameterBuilderForSearch.create(getRepository(), getGroup(), getImageName());
    }

    @Override
    public int hashCode() {
        if (MACLPPipelineParameterNexus3DockerImages.class != getClass()) {
            return super.hashCode();
        }
        return Objects.hash(getName(), getDescription(), group, imageName);
    }

    @Override
    @SuppressFBWarnings(
            value = "EQ_GETCLASS_AND_CLASS_CONSTANT",
            justification =
                    "ParameterDefinitionTest tests that subclasses are not equal to their parent classes, so the behavior appears to be intentional")
    public boolean equals(Object obj) {
        if (MACLPPipelineParameterNexus3DockerImages.class != getClass()) return super.equals(obj);
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        MACLPPipelineParameterNexus3DockerImages other = (MACLPPipelineParameterNexus3DockerImages) obj;
        if (!Objects.equals(getName(), other.getName())) return false;
        if (!Objects.equals(getDescription(), other.getDescription())) return false;
        if (!Objects.equals(imageName, other.getImageName())) return false;
        return Objects.equals(group, other.getGroup());
    }
}
