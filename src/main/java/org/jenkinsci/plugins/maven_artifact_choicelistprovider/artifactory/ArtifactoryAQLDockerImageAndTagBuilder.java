package org.jenkinsci.plugins.maven_artifact_choicelistprovider.artifactory;

public class ArtifactoryAQLDockerImageAndTagBuilder {

    private String repositoryId;
    private String imageName;

    public ArtifactoryAQLDockerImageAndTagBuilder setRepositoryId(String repositoryId) {
        this.repositoryId = repositoryId;
        return this;
    }

    public ArtifactoryAQLDockerImageAndTagBuilder setImageName(String imageName) {
        this.imageName = imageName;
        return this;
    }
    public String build() {
        final StringBuilder aql = new StringBuilder();
        aql.append("items.find({");
        aql.append("\"repo\": \"").append(repositoryId).append("\",");
        aql.append("\"@docker.repoName\": {\"$eq\":\"").append(imageName).append("\"}");
        aql.append("}).include(\"repo\",\"path\",\"name\",\"@docker.repoName\",\"@docker.manifest\")");
        return aql.toString();
    }
    
}
