package org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus3;

import java.util.logging.Logger;

import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.IVersionReader2;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import javax.ws.rs.core.MultivaluedMap;

import hudson.Extension;

public class MACLPPipelineParameterNexus3DockerImages extends MACLPChoiceParameterDefinitionBase {

    private String artifact;
    private String group;

    private static final Logger LOGGER = Logger.getLogger(MACLPPipelineParameterNexus3DockerImages.class.getName());

    @DataBoundConstructor
    public MACLPPipelineParameterNexus3DockerImages(String name, String choices, String description, String url, String credentialsId, String repository,
            String artifact, String group) {
        super(name, new String[0], description, url, repository, credentialsId);
        this.group = group;
        this.artifact = artifact;
    }

    public String getArtifact() {
        return artifact;
    }

    @DataBoundSetter
    public void setArtifact(String artifact) {
        this.artifact = artifact;
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
        LOGGER.fine("createServiceInstance: "  + pUrl);
        return new Nexus3RestApiSearchService(pUrl);
    }

    @Override
    protected MultivaluedMap<String, String> createParameterList() {
        LOGGER.fine("createParameterList");
        Nexus3RESTfulParameterBuilderForSearch x = new Nexus3RESTfulParameterBuilderForSearch();
        return x.create(getRepository(), getGroup(), getArtifact(), "");
    }

}
