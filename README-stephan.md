# Instructions
Deploy on Wincor Notebook

1. Disconnect from the Network
2. Startup Mobile Hotsport
3. Logoff from Windows (reset environment)
4. Login
5. Connect WiFi
6. Build
´´´
mvn clean release:prepare release:perform -DskipTests -Darguments="-DskipTests" --settings C:/Users/stephan.watermeyer/.m2/settings-jenkins.xml
´´´
