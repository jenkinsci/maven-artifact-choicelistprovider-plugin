package org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.jenkinsci.plugins.maven_artifact_choicelistprovider.AbstractRESTfulVersionReader;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.IVersionReader;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.RESTfulParameterBuilder;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.ValidAndInvalidClassifier;
import org.sonatype.nexus.rest.model.NexusNGArtifact;
import org.sonatype.nexus.rest.model.NexusNGArtifactHit;
import org.sonatype.nexus.rest.model.NexusNGArtifactLink;

public class NexusLuceneSearchService extends AbstractRESTfulVersionReader implements IVersionReader {

	private static final String LUCENE_SEARCH_SERVICE_URI = "service/local/lucene/search";

	private static final Logger LOGGER = Logger.getLogger(NexusLuceneSearchService.class.getName());

	public NexusLuceneSearchService(String pURL) {
		super(pURL);
	}

	/**
	 * 
	 * Search in Nexus for the artifact using the Lucene Service.
	 * https://repository.sonatype.org/nexus-indexer-lucene-plugin/default/docs/path__lucene_search.html
	 */
	@Override
	public Set<String> callService(final String pGroupId, final String pArtifactId, final String pPackaging,
			final ValidAndInvalidClassifier pClassifier) {

		final MultivaluedMap<String, String> requestParams = RESTfulParameterBuilder.create(pGroupId, pArtifactId, pPackaging,
				pClassifier);

		Set<String> retVal = new LinkedHashSet<String>();
		LOGGER.info("call nexus service");
		final PatchedSearchNGResponse xmlResult = getInstance().queryParams(requestParams)
				.accept(MediaType.APPLICATION_XML).get(PatchedSearchNGResponse.class);

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
	 * Parses the XML response from Nexus and creates a list of links where the artifacts can be downloadad.
	 * 
	 * @param pXMLResult
	 * @param pPackaging
	 * @param pClassifier
	 * @return a unique list of URLs that are matching the search criteria, sorted by the order of the Nexus service.
	 */
	Set<String> parseResponse(final PatchedSearchNGResponse pXMLResult, final String pPackaging,
			final ValidAndInvalidClassifier pClassifier) {
		// Use a Map instead of a List to filter duplicated entries and also linked to keep the order of XML response
		final Set<String> retVal = new LinkedHashSet<String>();

		for (NexusNGArtifact current : pXMLResult.getData()) {
			final IArtifactURLBuilder artifactURL = /* new DirectArtifactURLBuilder() */new NexusContentServiceArtifactURLBuilder()
					.setNexusURL(getURL()).setGroupId(current.getGroupId()).setArtifactId(current.getArtifactId())
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
	 * @return
	 */
	@Override
	public String getRESTfulServiceEndpoint() {
		return LUCENE_SEARCH_SERVICE_URI;
	}

}
