# MavenArtifact ChoiceListProvider
## What is this?
This Plugin adds an additional ChoiceListProvider to famous <a href="https://wiki.jenkins-ci.org/display/JENKINS/Extensible+Choice+Parameter+plugin">Extensible Choice Parameter</a> Plugin.

With this extension its possible to use the Lucene Service from a Nexus Repository to search for artifacts using groupId, artifactId and the packaging.

This plugin provides a build parameter and will let the user choose a version from the available artifact versions in Nexus. The Plugin will return the full URL of the choosen artifact, so that it will be available during the build, i.E. you can retrieve the artifact by using "wget"

### Example
We are using this plugin to let our QA department choose between the various available versions of our software. In combination with the "Publish via SSH" plugin the choosen artifact URL is passed to the testserver which is then able to retrieve the artifact and install it.

## Configuration Example
![Alt text](/src/site/resources/project-config-1.jpg?raw=true "Example Project Configuration")

## Links
* Thanks for the hint, but this plugin is very simliar to mine https://github.com/jenkinsci/repository-connector-plugin

## Authors
Stephan Watermeyer <stephan@phreakadelle.de>

## License
Licensed under the [MIT License (MIT)](https://github.com/heremaps/buildrotator-plugin/blob/master/LICENSE).

## Changelog

### 11. November 2016 - 1.0.2
* ADD: New dependency to extensible-choice-plugin in version 1.3.3
* FIX: POM Update 

### 10. November 2016 - 1.0.1
* ADD: New Feature to use the search.maven.org REST API to display artifacts in Jenkins
* FIX: Some changes to support the official release of this software

### 09. November 2016 - 1.0.0
* ADD: Plugin is released as an official Jenkins Plugin
* FIX: Changed unit tests to use a public nexus for testing
* ADD: Prepartion to use shorter artifact names in SelectBox that will be resolved once the build has been started (onBuildTriggeredWith(...)). Only preparation, as i dont have a solution how to change build environments once the build has been started.

### 25. July 2016 - 0.0.8
* ADD: Added Configuration to configure UserCredentials for a Nexus Server. Could be a Token or a real Username.

### 25. July 2016 - 0.0.6
* ADD: Quickfix to add user credentials for Nexus. Will be put into Jenkins-Credentials in the next version

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
