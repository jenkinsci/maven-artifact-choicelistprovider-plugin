package org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus3;

import java.util.Objects;
import java.util.logging.Logger;

import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.IVersionReader2;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.ws.rs.core.MultivaluedMap;

import hudson.Extension;

public class MACLPPipelineParameterNexus3Generic extends MACLPChoiceParameterDefinitionBase {

    private String assetName;
    private String group;

    private static final Logger LOGGER = Logger.getLogger(MACLPPipelineParameterNexus3DockerImages.class.getName());

    @DataBoundConstructor
    public MACLPPipelineParameterNexus3Generic(String name, String choices, String description, String url, String credentialsId, String repository,
            String assetName, String group, boolean reverseOrder) {
        super(name, new String[0], description, url, repository, credentialsId, reverseOrder);
       this.assetName = assetName;
       this.group = group;
    }

    public String getAssetName() {
        return assetName;
    }

    @DataBoundSetter
    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getGroup() {
        return group;
    }

    @DataBoundSetter
    public void setGroup(String group) {
        this.group = group;
    }

    @Extension
    @Symbol("nexus3Generic")
    public static final class DescriptorImpl extends ParameterDescriptor {

        @Override
        public final String getDisplayName() {
            return "Nexus3 Generic artifact";
        }
    }
    
    @Override
    protected IVersionReader2 createServiceInstance(String pUrl) {
        LOGGER.fine("createServiceInstance: "  + pUrl);
        return new Nexus3RestApiAssetGenericService(pUrl);
    }

    @Override
    protected MultivaluedMap<String, String> createParameterList() {
        LOGGER.fine("createParameterList");
        Nexus3RESTfulParameterBuilderForGenericArtifacts mapper = new Nexus3RESTfulParameterBuilderForGenericArtifacts();
        return mapper.create(getRepository(), getGroup(), getAssetName(), null, null);
    }

     @Override
    public int hashCode() {
        if (MACLPPipelineParameterNexus3Generic.class != getClass()) {
            return super.hashCode();
        }
        return Objects.hash(getName(), getDescription(), group, assetName);
    }

    @Override
    @SuppressFBWarnings(value = "EQ_GETCLASS_AND_CLASS_CONSTANT", justification = "ParameterDefinitionTest tests that subclasses are not equal to their parent classes, so the behavior appears to be intentional")
    public boolean equals(Object obj) {
        if (MACLPPipelineParameterNexus3Generic.class != getClass())
            return super.equals(obj);
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MACLPPipelineParameterNexus3Generic other = (MACLPPipelineParameterNexus3Generic) obj;
        if (!Objects.equals(getName(), other.getName()))
            return false;
        if (!Objects.equals(getDescription(), other.getDescription()))
            return false;
        if (!Objects.equals(assetName, other.getAssetName()))
                return false;
        return Objects.equals(group, other.getGroup());
    }

}
