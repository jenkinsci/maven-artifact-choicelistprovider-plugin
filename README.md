# MavenArtifact ChoiceListProvider

## What does this  this?
This Plugin adds an additional ChoiceListProvider to famous <a href="https://plugins.jenkins.io/extensible-choice-parameter">Extensible Choice Parameter</a> Plugin.

With this extension its possible to use the Service API from a Maven Repositories like Nexus 2, Nexus 3, Maven-Central or Artifactory to search for artifacts using groupId, artifactId and packaging.

This plugin provides a build parameter and will let the user choose a version from the available artifacts in the choosen repository. The Plugin will return the full URL of the choosen artifact, so that it will be available during the build as environemt paramter. This can be further used to retrieve the artifact with `wget` or `curl`

### Example
We are using this plugin to let our QA department choose between the various available versions of our software. In combination with the "Publish via SSH" plugin the choosen artifact URL is passed to the testserver which is then able to retrieve the artifact and install it.

## Configuration Example
![Alt text](/src/site/resources/project-config-1.jpg?raw=true "Example Project Configuration")

# Known Issues
## Nexus Snapshots
If you would like to use Snapshot Versions of your artifacts you have to enable the tick-box in the Jenkins Settings. Only with the RESTful interface of Nexus 2 you will be able to retrieve Snapshot versions.
![Alt text](/src/site/resources/project-config-2.png?raw=true "Nexus Snapshots")

## Artifactory
Artifactory API is not returning the correct ArtifactIds but only a URL to a JSON file that contains the DownloadURI of the Artifact. As currently there is no way in the Extensible Choice Plugin to intercept the selected value and because its not performant to query Artifactory for all items in the list for the correct downloadURI, the workaround is like this:

```
wget `wget -qO - https://repo.jenkins-ci.org/api/storage/releases/org/jenkins-ci/plugins/ant-in-workspace/1.1.0/ant-in-workspace-1.1.0-javadoc.jar | json downloadUri\`
```
The json command like tool is required:
```
json --version
json 9.0.6
written by Trent Mick
https://github.com/trentm/json
```

# Continouos Delivery
https://ci.jenkins.io/job/Plugins/job/maven-artifact-choicelistprovider-plugin/

## Authors
Stephan Watermeyer (Profile: https://github.com/phreakadelle)

## License
Licensed under the [MIT License (MIT)](https://github.com/heremaps/buildrotator-plugin/blob/master/LICENSE).
