package org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus3;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * POJO for Nexus3 REST API - Search.
 *
 * @author stephan.watermeyer
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchItem {
    private String id;

    private String repository;

    private String format;

    private String group;

    private String name;

    private String version;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
