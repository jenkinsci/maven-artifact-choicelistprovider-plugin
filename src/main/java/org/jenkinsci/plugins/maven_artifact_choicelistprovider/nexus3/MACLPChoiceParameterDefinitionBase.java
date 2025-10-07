package org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.IVersionReader;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.IVersionReader2;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerRequest2;
import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl;

import hudson.model.ChoiceParameterDefinition;
import hudson.model.Item;
import hudson.model.ParameterValue;
import hudson.model.StringParameterValue;
import hudson.security.ACL;
import net.sf.json.JSONObject;

public abstract class MACLPChoiceParameterDefinitionBase extends ChoiceParameterDefinition {

    private String url;
    private String repository;
    private String credentialsId;

    private static final Logger LOGGER = Logger.getLogger(MACLPChoiceParameterDefinitionBase.class.getName());

    public MACLPChoiceParameterDefinitionBase(String name, String[] choices, String description, String url,
            String repository, String credentialsId) {
        super(name, choices, description);
        this.url = url;
        this.repository = repository;
        this.credentialsId = credentialsId;

    }

    public String getUrl() {
        return url;
    }

    @DataBoundSetter
    public void setUrl(String url) {
        this.url = url;
    }

    public String getCredentialsId() {
        return credentialsId;
    }

    @DataBoundSetter
    public void setCredentialsId(String credentialsId) {
        this.credentialsId = credentialsId;
    }

    public String getRepository() {
        return repository;
    }

    @DataBoundSetter
    public void setRepository(String repository) {
        this.repository = repository;
    }

    public List<String> getChoices() {
        LOGGER.log(Level.FINE, "getChoices");

        Item item = null;
        StaplerRequest2 req = Stapler.getCurrentRequest2();

        
        if (req != null) {
            item = req.findAncestorObject(Item.class);
        }
        List<String> retVal = Collections.emptyList();

        if (StringUtils.isEmpty(getUrl())) {
            LOGGER.log(Level.FINE, "not properly initialized. URL is empty.");
            retVal = new ArrayList<String>();
            retVal.add("Job configuration has been changed manually. Please re-run the job to re-initiated this field.");
            return retVal;
        }

        final IVersionReader2 serviceInstances = createServiceInstance(getUrl());

        if (StringUtils.isNotEmpty(getCredentialsId())) {
            LOGGER.log(Level.FINE, "try to get credentials: " + getCredentialsId());
            final UsernamePasswordCredentialsImpl c = CredentialsMatchers.firstOrNull(
                    CredentialsProvider.lookupCredentialsInItem(UsernamePasswordCredentialsImpl.class, item,
                            ACL.SYSTEM2, Collections.<DomainRequirement>emptyList()),
                    CredentialsMatchers.allOf(CredentialsMatchers.withId(getCredentialsId())));

            if (c != null) {
                serviceInstances.setCredentials(c.getUsername(), c.getPassword().getPlainText());
            } else {
                LOGGER.log(Level.WARNING, "unable to find usernamepassword credentials with id: " + getCredentialsId()
                        + ". continue without");
            }
        }

        try {
            final MultivaluedMap<String, String> params = createBaseParameterList();
            params.putAll(createParameterList());

            LOGGER.log(Level.FINE, "call the service");
            retVal = serviceInstances.retrieveVersions(params);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
        }

        return retVal;
    }

    @Override
    public boolean isValid(ParameterValue value) {
        // Always true
        return true;
    }

    protected MultivaluedMap<String, String> createBaseParameterList() {
        MultivaluedHashMap<String, String> retVal = new MultivaluedHashMap<>();
        return retVal;
    }

     @Override
    public ParameterValue createValue(StaplerRequest2 req, JSONObject jo) {
        LOGGER.log(Level.FINE, "createValue " + jo.toString());
        return super.createValue(req, jo);
    }

    @Override
    public StringParameterValue createValue(String value) {
        LOGGER.log(Level.FINE, "createValue " + value);
        return super.createValue(value);
    }

    protected abstract IVersionReader2 createServiceInstance(String pUrl);

    protected abstract MultivaluedMap<String, String> createParameterList();

}
