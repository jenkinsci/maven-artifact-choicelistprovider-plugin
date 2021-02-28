package org.jenkinsci.plugins.maven_artifact_choicelistprovider.maven;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.AbstractRESTfulVersionReader;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.IVersionReader;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.ValidAndInvalidClassifier;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class MavenMetadataSearchService extends AbstractRESTfulVersionReader implements IVersionReader {
    private static final Logger LOGGER = Logger.getLogger(MavenMetadataSearchService.class.getName());

    public MavenMetadataSearchService(String mURL) {
        super(mURL);
    }

    /**
     * Path used to configure a {@link WebTarget} instance initialised by super class. For this service further parameters
     * are required to calculate the path of the endpoint.
     *
     * @return a string which doesn't ruin the base url provided by this class constructor
     * @deprecated needed only for super class to make it compile
     */
    @Deprecated
    @Override
    public String getRESTfulServiceEndpoint() {
        return "";
    }


    /**
     * Ordering of returned versions is important so using a LinkedHashSet inside.
     * {@inheritDoc}
     */
    @Override
    public Set<String> callService(String pRepositoryId, String pGroupId, String pArtifactId, String pPackaging, ValidAndInvalidClassifier pClassifier) {
        LOGGER.info("Fetch maven");

        WebTarget theInstance = getInstance()
                                        .path(pRepositoryId.trim())
                                        .path(pGroupId.trim().replace(".", "/"))
                                        .path(pArtifactId.trim());
        final String baseURL = theInstance.getUri().toString();

        theInstance = theInstance.path("maven-metadata.xml");
        LOGGER.info("Final URL: " + theInstance.getUri().toString());
        final String response = theInstance.request(MediaType.APPLICATION_XML).get(String.class);

        LOGGER.info("Received response. Parsing maven-metadata.xml of size: " + response.length());
        final LinkedHashSet<String> retVal = new LinkedHashSet<>();
        for (String version : parseVersions(response)) {
            retVal.add(baseURL + "/" + version);
        }
        return retVal;
    }

    static List<String> parseVersions(String response) {
        final List<String> retVal = new ArrayList<>();

        try {

            final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            final DocumentBuilder db = dbf.newDocumentBuilder();
            final Document doc = db.parse(IOUtils.toInputStream(response, StandardCharsets.UTF_8));
            final XPath xPath = XPathFactory.newInstance().newXPath();
            final String expression = "/metadata/versioning/versions/version";
            final NodeList versions = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);

            for (int i = 0; i < versions.getLength(); ++i) {
                retVal.add(versions.item(i).getTextContent().trim());
            }

        } catch (ParserConfigurationException | IOException | XPathExpressionException | ClassCastException e) {
            // ParserConfigurationException cannot be thrown since default configuration is used
            // IOException cannot be caused by reading a string
            // XPathExpressionException only predefined and valid XPath expressions are used
            // ClassCastException XPath.evaluate would throw an IllegalArgumentException if cannot return the desired type
            throw new IllegalStateException(e);
        } catch (SAXException e) {
            final String reason = "Failed to parse. Retrieved maven-metadata.xml is not a valid xml document.";
            LOGGER.warning(reason + " " + e.getMessage());
            throw new RuntimeException(reason, e);
        }

        return retVal;
    }

}
