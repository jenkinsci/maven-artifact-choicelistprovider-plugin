package org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.IVersionReader;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.ValidAndInvalidClassifier;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.VersionReaderException;
import org.sonatype.nexus.rest.model.NexusNGArtifact;
import org.sonatype.nexus.rest.model.NexusNGArtifactHit;
import org.sonatype.nexus.rest.model.NexusNGArtifactLink;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class NexusLuceneSearchService implements IVersionReader {

	/**
	 * Default Read Timeout when connecting to Nexus
	 */
	private static final int NEXUS_READ_TIMEOUT = 5000;

	private static final String PACKAGING_ALL = "*";

	private static final String LUCENE_SEARCH_SERVICE_URI = "service/local/lucene/search";

	private static final Logger LOGGER = Logger.getLogger(NexusLuceneSearchService.class.getName());

	private final String mURL;

	private String mUserName;
	private String mUserPassword;

	private WebResource mInstance;

	public NexusLuceneSearchService(String pURL) {
		super();
		this.mURL = pURL;
	}

	void init() {
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);

		if (StringUtils.isNotEmpty(mUserName) && StringUtils.isNotEmpty(mUserPassword)) {
			LOGGER.fine("setting username to: " + mUserName);
			client.addFilter(new HTTPBasicAuthFilter(mUserName, mUserPassword));
		} else {
			LOGGER.fine("no username AND password provided");
		}

		client.setReadTimeout(getTimeout());
		mInstance = client.resource(UriBuilder.fromUri(getURL()).build());
		mInstance = mInstance.path(LUCENE_SEARCH_SERVICE_URI);
		LOGGER.info("lucene search service at: " + mInstance.getURI().toString());
	}

	/**
	 * Search in Nexus for the artifact using the Lucene Service.
	 * https://repository.sonatype.org/nexus-indexer-lucene-plugin/default/docs/path__lucene_search.html
	 */
	public List<String> retrieveVersions(String pGroupId, String pArtifactId, String pPackaging,
			ValidAndInvalidClassifier pClassifier) throws VersionReaderException {
		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.fine("query nexus with arguments: r:" + mURL + ", g:" + pGroupId + ", a:" + pArtifactId + ", p:"
					+ pPackaging + ", c: " + pClassifier.toString());
		}

		MultivaluedMap<String, String> requestParams = new MultivaluedHashMap<String, String>();
		if (pGroupId != "")
			requestParams.putSingle("g", pGroupId);
		if (pArtifactId != "")
			requestParams.putSingle("a", pArtifactId);
		if (pPackaging != "" && !PACKAGING_ALL.equals(pPackaging))
			requestParams.putSingle("p", pPackaging);
		if (pClassifier != null) {
			// FIXME: There is of course a better way how to do it...
			final List<String> query = new ArrayList<String>();
			for (String current : pClassifier.getValid())
				query.add(current);

			if (!query.isEmpty())
				requestParams.put("c", query);
		}

		Set<String> retVal = new LinkedHashSet<String>();

		try {
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
		} catch (UniformInterfaceException e) {
			final String msg;
			if (e.getResponse() != null) {
				switch (e.getResponse().getStatus()) {
					case 401:
						msg = "Your repository requires user-authentication. Please configure a username and a password to list the content of this repository";
						break;
					default:
						msg = "HTTP Server Error: " + e.getResponse().getStatus() + ": " + e.getMessage();
						break;
				}
			} else {
				msg = "General Error:" + e.getMessage();
			}
			throw new VersionReaderException(msg, e);
		} catch (Exception e) {
			if (e instanceof com.sun.jersey.api.client.ClientHandlerException) {
				throw new VersionReaderException(
						"Timeout while connecting to your Nexus. Please consider the Jenkins-Proxy settings. If using HTTPs also invalid certificates can be the root cause.",
						e);
			} else {
				throw new VersionReaderException("failed to retrieve versions from nexus for r:" + getURL() + ", g:"
						+ pGroupId + ", a:" + pArtifactId + ", p:" + pPackaging + ", c:" + pClassifier, e);
			}
		}
		return new ArrayList<String>(retVal);
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

	public String getURL() {
		return mURL;
	}

	WebResource getInstance() {
		if (mInstance == null) {
			init();
		}
		return mInstance;
	}

	public String getUserName() {
		return mUserName;
	}

	public void setUserName(String mUserName) {
		this.mUserName = mUserName;
	}

	public void setUserPassword(String mUserPassword) {
		this.mUserPassword = mUserPassword;
	}

	@Override
	public void setCredentials(String pUserName, String pUserPassword) {
		this.setUserName(pUserName);
		this.setUserPassword(pUserPassword);
	}

	/**
	 * Return the configured read timeout in milliseconds. Can be overwritten in super classes.
	 * 
	 * @return timeout in milliseconds
	 */
	protected int getTimeout() {
		return NEXUS_READ_TIMEOUT;
	}
}
