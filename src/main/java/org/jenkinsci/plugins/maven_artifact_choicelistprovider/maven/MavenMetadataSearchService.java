package org.jenkinsci.plugins.maven_artifact_choicelistprovider.maven;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.AbstractRESTfulVersionReader;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.IVersionReader;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.ValidAndInvalidClassifier;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.VersionReaderException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class MavenMetadataSearchService extends AbstractRESTfulVersionReader implements IVersionReader {
	
	private static final Logger LOGGER = Logger.getLogger(MavenMetadataSearchService.class.getName());

	private static final XPathExpression XPATH;
	
	static {
		final String expression = "/metadata/versioning/versions/version";
		try {
			final XPath xPath = XPathFactory.newInstance().newXPath();
			XPATH = xPath.compile(expression);
		} catch (XPathExpressionException e) {
			throw new RuntimeException("cannot compile xpath expression: " + expression, e);
		}
	}
	public MavenMetadataSearchService(String mURL) {
		super(mURL);
	}

	/**
	 * Path used to configure a {@link WebTarget} instance initialised by super
	 * class. For this service further parameters are required to calculate the path
	 * of the endpoint.
	 *
	 * @return a string which doesn't ruin the base url provided by this class
	 *         constructor
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
	 * 
	 * @throws VersionReaderException
	 */
	@Override
	public Set<String> callService(String pRepositoryId, String pGroupId, String pArtifactId, String pPackaging,
			ValidAndInvalidClassifier pClassifier) throws VersionReaderException {
		LOGGER.info("Fetch maven");

		WebTarget theInstance = getInstance().path(pRepositoryId).path(pGroupId.replace(".", "/")).path(pArtifactId);
		final String baseURL = theInstance.getUri().toString();

		theInstance = theInstance.path("maven-metadata.xml");
		LOGGER.info("Final URL: " + theInstance.getUri().toString());
		final String response = theInstance.request(MediaType.APPLICATION_XML).get(String.class);

		LOGGER.info("Received response. Parsing maven-metadata.xml of size: " + response.length());
		final List<String> parseVersions = parseVersions(response);
		
		final LinkedHashSet<String> retVal = new LinkedHashSet<>();
		for (String version : parseVersions) {
			retVal.add(baseURL + "/" + version);
		}
		return retVal;
	}

	List<String> parseVersions(final String pResponse) throws VersionReaderException {
		final List<String> retVal = new ArrayList<>();

		try {
			final DocumentBuilder db = threadLocalBuilder.get().newDocumentBuilder();
			final Document doc = db.parse(IOUtils.toInputStream(pResponse, StandardCharsets.UTF_8));

			final NodeList versions = (NodeList) XPATH.evaluate(doc, XPathConstants.NODESET);

			for (int i = 0; i < versions.getLength(); ++i) {
				retVal.add(versions.item(i).getTextContent().trim());
			}

		} catch (Exception e) {
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.log(Level.FINE, "xml response: " + pResponse);
			}
			throw new VersionReaderException(
					"Failed to parse XML response. Retrieved maven-metadata.xml seems to be invalid", e);
		}

		return retVal;
	}

	ThreadLocal<DocumentBuilderFactory> threadLocalBuilder = new ThreadLocal<DocumentBuilderFactory>() {
		@Override
		protected DocumentBuilderFactory initialValue() {
			return DocumentBuilderFactory.newInstance();
		}
	};

}
