package org.jenkinsci.plugins.maven_artifact_choicelistprovider.maven;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.jenkinsci.plugins.maven_artifact_choicelistprovider.AbstractRESTfulVersionReader;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.IVersionReader;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.ValidAndInvalidClassifier;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.VersionReaderException;

public class MavenMetadataSearchService extends AbstractRESTfulVersionReader implements IVersionReader {
	
	private static final Logger LOGGER = Logger.getLogger(MavenMetadataSearchService.class.getName());

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

		MavenMetaData xmlResult = null;
		
		try {
			Unmarshaller unmarshaller = JAXBCONTEXT.get().createUnmarshaller();

			StringReader reader = new StringReader(pResponse);
			xmlResult = (MavenMetaData) unmarshaller.unmarshal(reader);

			if (xmlResult.getVersioning() != null) {
				for(Version current :  xmlResult.getVersioning().getVersions()) {
					retVal.add(current.getVersion());
				}
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

	private static final ThreadLocal<JAXBContext> JAXBCONTEXT = ThreadLocal.withInitial(() -> {
		try {
			return (JAXBContext.newInstance(MavenMetaData.class));
		} catch (JAXBException e) {
			LOGGER.log(Level.SEVERE, "failed to init JAXB context", e);
			return null;
		}
	});
	
}
