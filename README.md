# MavenArtifact ChoiceListProvider
## What is this?
This Plugin adds an additional ChoiceListProvider to famous <a href="https://wiki.jenkins-ci.org/display/JENKINS/Extensible+Choice+Parameter+plugin">Extensible Choice Parameter</a> Plugin.

With this extension its possible to use the Lucene Service from a Nexus Repository to search for artifacts using groupId, artifactId and the packaging.
## Configuration Example
![Alt text](/src/site/resources/project-config-1.jpg?raw=true "Example Project Configuration")

## Changelog
### 20. July 2016
* Changed Implementation of Set containing the results from Nexus to LinkedHashSet as this implementation keeps the order as it is replied from Nexus and also makes sure that entries are only contained once.

### 29. June 2016
* Added Checkbox to have the response list in reverse order
* Changed Packaging-Textbox: Empty Value will only return the parent folder. The * character will return all entries for that artifact. Or use special entries like "tar.gz" or "zip" 

### 30. May 2016
* Added Example Image showing the Project Configuration
* Added Comment for the "onBuildTriggeredWith(...)" method which can maybe later extended to transform the provided parameter (which could be a short version of the name) into the correct working URL


### 24. May 2016
* Initial Version
