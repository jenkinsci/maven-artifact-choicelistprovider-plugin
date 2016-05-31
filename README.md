# MavenArtifact ChoiceListProvider
## What is this?
This Plugin adds an additional ChoiceListProvider to famous <a href="https://wiki.jenkins-ci.org/display/JENKINS/Extensible+Choice+Parameter+plugin">Extensible Choice Parameter</a> Plugin.

With this extension its possible to use the Lucene Service from a Nexus Repository to search for artifacts using groupId, artifactId and the packaging.
## Configuration Example
![Alt text](/src/site/resources/project-config-1.jpg?raw=true "Example Project Configuration")

## Changelog
### 30. May 2016
* Added Example Image showing the Project Configuration
* Added Comment for the "onBuildTriggeredWith(...)" method which can maybe later extended to transform the provided parameter (which could be a short version of the name) into the correct working URL


### 24. May 2016
* Initial Version
