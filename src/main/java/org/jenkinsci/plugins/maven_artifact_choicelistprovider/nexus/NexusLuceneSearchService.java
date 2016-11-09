package org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
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
import org.sonatype.nexus.rest.model.NexusNGRepositoryDetail;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class NexusLuceneSearchService implements IVersionReader {

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
			client.addFilter(new HTTPBasicAuthFilter(mUserName, mUserPassword));
		} else {
			LOGGER.fine("no username AND password provided");
		}

		mInstance = client.resource(UriBuilder.fromUri(getURL()).build());
		mInstance = mInstance.path(LUCENE_SEARCH_SERVICE_URI);
		// String respAsString = service.path("nexus/service/local/lucene/search")
		// .queryParam("g", "com.wincornixdorf.pnc.releases").queryParam("a", "pnc-brass-maven")
		// .accept(MediaType.APPLICATION_XML).get(String.class);
		// System.out.println(respAsString);
		//
	}

	public List<String> retrieveVersions(String pGroupId, String pArtifactId, String pPackaging)
			throws VersionReaderException {
		return retrieveVersions(pGroupId, pArtifactId, pPackaging, ValidAndInvalidClassifier.getDefault());
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
			// Call the Service
			final PatchedSearchNGResponse xmlResult = getInstance().queryParams(requestParams)
					.accept(MediaType.APPLICATION_XML).get(PatchedSearchNGResponse.class);

			if (xmlResult == null) {
				LOGGER.info("response from Nexus is NULL.");
			} else if (xmlResult.getTotalCount() == 0) {
				LOGGER.info("response from Nexus does not contain any results.");
			} else {
				final Map<String, String> repoURLs = retrieveRepositoryURLs(xmlResult.getRepoDetails());
				retVal = parseResponse(xmlResult, repoURLs, pPackaging, pClassifier);
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
			throw new VersionReaderException("failed to retrieve versions from nexus for r:" + getURL() + ", g:"
					+ pGroupId + ", a:" + pArtifactId + ", p:" + pPackaging + ", c:" + pClassifier + e.getMessage(), e);
		}
		return new ArrayList<String>(retVal);
	}

	Set<String> parseResponse(final PatchedSearchNGResponse pXMLResult, final Map<String, String> pRepoURLs,
			final String pPackaging, final ValidAndInvalidClassifier pClassifier) {
		// Use a Map instead of a List to filter duplicated entries and also linked to keep the order of XML response
		final Set<String> retVal = new LinkedHashSet<String>();

		// https://davis.wincor-nixdorf.com/nexus/content/repositories/wn-ps-us-pnc/com/wincornixdorf/pnc/releases/pnc-brass-maven/106/pnc-brass-maven-106.tar.gz
		for (NexusNGArtifact current : pXMLResult.getData()) {
			final StringBuilder theBaseDownloadURL = new StringBuilder();
			// theDownloadURL.append(repoURL);
			theBaseDownloadURL.append("/");
			theBaseDownloadURL.append(current.getGroupId().replace(".", "/"));
			theBaseDownloadURL.append("/");
			theBaseDownloadURL.append(current.getArtifactId());
			theBaseDownloadURL.append("/");
			theBaseDownloadURL.append(current.getVersion());

			final StringBuilder theArtifactSuffix = new StringBuilder();
			theArtifactSuffix.append("/");
			theArtifactSuffix.append(current.getArtifactId());
			theArtifactSuffix.append("-");
			theArtifactSuffix.append(current.getVersion());

			for (NexusNGArtifactHit currentHit : current.getArtifactHits()) {
				for (NexusNGArtifactLink currentLink : currentHit.getArtifactLinks()) {
					final String repo = pRepoURLs.get(currentHit.getRepositoryId());

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
						final String baseUrl = repo + theBaseDownloadURL.toString();
						final String artifactSuffix = theArtifactSuffix.toString();
						final String classifier = (currentLink.getClassifier() == null ? ""
								: "-" + currentLink.getClassifier());

						if (addCurrentyEntryAsFolder) {
							retVal.add(baseUrl);
						} else {
							retVal.add(baseUrl + artifactSuffix + classifier + "." + currentLink.getExtension());
						}
					}
				}
			}
		}
		return retVal;
	}

	Map<String, String> retrieveRepositoryURLs(final List<NexusNGRepositoryDetail> pRepoDetails) {
		Map<String, String> retVal = new HashMap<String, String>();

		for (NexusNGRepositoryDetail currentRepo : pRepoDetails) {
			String theURL = currentRepo.getRepositoryURL();

			// FIXME: Repository URL can be retrieved somehow...
			theURL = theURL.replace("service/local", "content");
			retVal.put(currentRepo.getRepositoryId(), theURL);
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

}
