# MavenArtifact ChoiceListProvider

## Official Documentation
The user documentation is available here
* https://wiki.jenkins-ci.org/display/JENKINS/Maven+Artifact+ChoiceListProvider+Plugin

In this README you will find more technical information for Developers that are interested in supporting or enhancing this plugin.

## What does this  this?
This Plugin adds an additional ChoiceListProvider to famous <a href="https://wiki.jenkins-ci.org/display/JENKINS/Extensible+Choice+Parameter+plugin">Extensible Choice Parameter</a> Plugin.

With this extension its possible to use the Lucene Service from various Artifact Repositories (like Nexus, MavenCentral or Artifactory) to search for artifacts using groupId, artifactId and the packaging.

This plugin provides a build parameter and will let the user choose a version from the available artifact versions in the choosen repository. The Plugin will return the full URL of the choosen artifact, so that it will be available during the build, i.E. you can retrieve the artifact by using "wget"

### Example
We are using this plugin to let our QA department choose between the various available versions of our software. In combination with the "Publish via SSH" plugin the choosen artifact URL is passed to the testserver which is then able to retrieve the artifact and install it.

## Configuration Example
![Alt text](/src/site/resources/project-config-1.jpg?raw=true "Example Project Configuration")

## Documentation
The user documentation is available here
* https://wiki.jenkins-ci.org/display/JENKINS/Maven+Artifact+ChoiceListProvider+Plugin

# Continouos Delivery
https://jenkins.ci.cloudbees.com/job/plugins/job/maven-artifact-choicelistprovider-plugin/

## Authors
Stephan Watermeyer (Profile: https://github.com/phreakadelle)

## License
Licensed under the [MIT License (MIT)](https://github.com/heremaps/buildrotator-plugin/blob/master/LICENSE).
