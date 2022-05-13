package org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus;

import java.io.StringReader;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.jenkinsci.plugins.maven_artifact_choicelistprovider.AbstractRESTfulVersionReader;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.IVersionReader;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.ValidAndInvalidClassifier;
import org.sonatype.nexus.rest.model.NexusNGArtifact;
import org.sonatype.nexus.rest.model.NexusNGArtifactHit;
import org.sonatype.nexus.rest.model.NexusNGArtifactLink;

public class NexusLuceneSearchService extends AbstractRESTfulVersionReader implements IVersionReader {

	private static final String LUCENE_SEARCH_SERVICE_URI = "service/local/lucene/search";

	private static final Logger LOGGER = Logger.getLogger(NexusLuceneSearchService.class.getName());

	private boolean mUseRESTfulAPI;

	private static final ThreadLocal<JAXBContext> JAXBCONTEXT = ThreadLocal.withInitial(() -> {
		try {
			return (JAXBContext.newInstance(PatchedSearchNGResponse.class));
		} catch (JAXBException e) {
			LOGGER.log(Level.SEVERE, "failed to init JAXB context", e);
			return null;
		}
	});
	
	public NexusLuceneSearchService(String pURL) {
		super(pURL);
	}

	public NexusLuceneSearchService(String pURL, boolean useRESTfulAPI) {
		super(pURL);
		mUseRESTfulAPI = useRESTfulAPI;
	}

	/**
	 * 
	 * Search in Nexus for the artifact using the Lucene Service.
	 * https://repository.sonatype.org/nexus-indexer-lucene-plugin/default/docs/path__lucene_search.html
	 */
	@Override
	public Set<String> callService(final String pRepositoryId, final String pGroupId, final String pArtifactId,
			final String pPackaging, final ValidAndInvalidClassifier pClassifier) {

		final MultivaluedMap<String, String> requestParams = new StandardRESTfulParameterBuilder().create(pRepositoryId,
				pGroupId, pArtifactId, pPackaging, pClassifier);

		Set<String> retVal = new LinkedHashSet<>();
		LOGGER.info("call nexus service");

		WebTarget theInstance = getInstance();
		for (Map.Entry<String, List<String>> entries : requestParams.entrySet()) {
			theInstance = theInstance.queryParam(entries.getKey(), entries.getValue().toArray());
		}

		LOGGER.info("final URL: " + theInstance.getUri().toString());

		// TODO: I dont know why this is not working. This should work...
		// PatchedSearchNGResponse xmlResult =
		// response.readEntity(PatchedSearchNGResponse.class);
		// Response response = theInstance.request(MediaType.APPLICATION_XML).get();

		String response = theInstance.request(MediaType.APPLICATION_XML).get(String.class);

		PatchedSearchNGResponse xmlResult = null;

		try {
			Unmarshaller unmarshaller = JAXBCONTEXT.get().createUnmarshaller();

			StringReader reader = new StringReader(response);
			xmlResult = (PatchedSearchNGResponse) unmarshaller.unmarshal(reader);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "failed to parse xml from response", e);
			if(LOGGER.isLoggable(Level.FINE)) {
				LOGGER.log(Level.INFO, "xml response: " + response);
			}
		}

		if (xmlResult == null) {
			LOGGER.info("response from Nexus is NULL.");
		} else if (xmlResult.getTotalCount() == 0) {
			LOGGER.info("response from Nexus does not contain any results.");
		} else {
			retVal = parseResponse(xmlResult, pPackaging, pClassifier);
		}

		return retVal;
	}

	/**
	 * Parses the XML response from Nexus and creates a list of links where the
	 * artifacts can be retrieved.
	 * 
	 * @param pXMLResult
	 * @param pPackaging
	 * @param pClassifier
	 * @return a unique list of URLs that are matching the search criteria, sorted
	 *         by the order of the Nexus service.
	 */
	Set<String> parseResponse(final PatchedSearchNGResponse pXMLResult, final String pPackaging,
			final ValidAndInvalidClassifier pClassifier) {
		// Use a Map instead of a List to filter duplicated entries and also linked to
		// keep the order of XML response
		final Set<String> retVal = new LinkedHashSet<>();

		for (NexusNGArtifact current : pXMLResult.getData()) {
			final IArtifactURLBuilder artifactURL;
			if (mUseRESTfulAPI) {
				artifactURL = new NexusContentServiceArtifactURLBuilder();
			} else {
				artifactURL = new DirectArtifactURLBuilder();
			}
			artifactURL.setNexusURL(getURL()).setGroupId(current.getGroupId()).setArtifactId(current.getArtifactId())
					.setVersion(current.getVersion());

			for (NexusNGArtifactHit currentHit : current.getArtifactHits()) {

				// RepositoryId from the ArtifactHit
				artifactURL.setRepositoryId(currentHit.getRepositoryId());

				for (NexusNGArtifactLink currentLink : currentHit.getArtifactLinks()) {
					boolean addCurrentEntry = true;
					boolean addCurrentyEntryAsFolder = false;

					// if packaging configuration is set but does not match
					if ("".equals(pPackaging)) {
						addCurrentyEntryAsFolder = true;
					} else if (PACKAGING_ALL.equals(pPackaging)) {
						// then always add
					} else if (!pPackaging.equals(currentLink.getExtension())) {
						addCurrentEntry &= false;
					}

					// check the classifier.
					if (!pClassifier.isValid(currentLink.getClassifier())) {
						addCurrentEntry &= false;
					}

					if (addCurrentEntry) {
						artifactURL.setClassifier(currentLink.getClassifier()).setPackaging(currentLink.getExtension());
						retVal.add(artifactURL.build(addCurrentyEntryAsFolder));
					}
				}
			}
		}
		return retVal;
	}

	/**
	 * Return the configured service endpoint in this repository.
	 * 
	 * @return the configured service endpoint in this repository.
	 */
	@Override
	public String getRESTfulServiceEndpoint() {
		return LUCENE_SEARCH_SERVICE_URI;
	}

}
