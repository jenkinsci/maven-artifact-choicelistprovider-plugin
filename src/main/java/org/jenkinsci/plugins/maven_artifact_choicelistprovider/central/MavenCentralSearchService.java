package org.jenkinsci.plugins.maven_artifact_choicelistprovider.central;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;

import org.jenkinsci.plugins.maven_artifact_choicelistprovider.IVersionReader;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.ValidAndInvalidClassifier;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.VersionReaderException;

/**
 * Maven Central offers a search service located at {@link #REPO_SEARCH_SERVICE_URL} which can be used to query the
 * maven central repository via a RESTful interface. This class makes use of this API and return a list of artifacts.
 * <br>
 * <br>
 * As the API is public, but Maven Central is the only service offering it, there is no reason to modify the URLs for
 * searching and retrieving the artifacts. <br>
 * Anyway, if there should be another repository using the same API, this class
 * can be inherited and {@link #getSearchURL()} and {@link #getRetrieveURL()} and {@link #createItemURLs(ResponseDoc, String)}
 * can be overriden.
 *
 * @author stephan.watermeyer, Diebold Nixdorf
 */
public class MavenCentralSearchService implements IVersionReader {

	public static final String REPO_RETRIEVE_URL = "https://repo1.maven.org/maven2/";

	/** Expected Charset for MavenCentral response */
	private static final Charset UTF8 = Charset.forName("UTF-8");

	public static final String REPO_SEARCH_SERVICE_URL = "http://search.maven.org/solrsearch/";

	private static final Logger LOGGER = Logger.getLogger(MavenCentralSearchService.class.getName());

	/** A valid header needs to be set */
	private static final String USER_AGENT = "Mozilla/5.0";

	/** Number of results returned from the API */
	private static final int MAX_SEARCH_RESULTS = 100;

	@Override
	public List<String> retrieveVersions(String pRepositoryId, String pGroupId, String pArtifactId, String pPackaging,
			ValidAndInvalidClassifier pClassifier) throws VersionReaderException {
		try {
			final String targetURL = createURL(pGroupId, pArtifactId, pPackaging, pClassifier);
			LOGGER.fine("target url:" + targetURL);

			final String response = sendAndReceive(targetURL.toString());
			if (LOGGER.isLoggable(Level.FINEST)) {
				LOGGER.finest(response);
			}

			final MavenCentralResponseModel responseObj = MavenCentralResponseParser.parse(response);
			final Set<String> retVal = new LinkedHashSet<String>();
			if (containsResponses(responseObj)) {
				for (ResponseDoc current : responseObj.getResponse().getDocs()) {
					retVal.addAll(createItemURLs(current, pPackaging));
				}
			}

			return new ArrayList<String>(retVal);
		} catch (VersionReaderException e) {
			throw e;
		} catch (Exception e) {
			throw new VersionReaderException(
					"failed to retrieve versions from maven-central for r:" + getSearchURL() + ", g:" + pGroupId
							+ ", a:" + pArtifactId + ", p:" + pPackaging + ", c:" + pClassifier + e.getMessage(),
					e);
		}
	}

	/**
	 * 
	 * @param pResponseEntry
	 * @param pRequestedPackaging
	 * @return The items to add, but never NULL.
	 */
	List<String> createItemURLs(final ResponseDoc pResponseEntry, final String pRequestedPackaging) {
		final List<String> retVal = new ArrayList<String>();
		final StringBuilder sb = new StringBuilder();

		// base url
		sb.append(getRetrieveURL());
		sb.append(pResponseEntry.getGroupId().replace(".", "/")).append("/");
		sb.append(pResponseEntry.getArtifactId()).append("/");
		sb.append(pResponseEntry.getVersion()).append("/");

		// artifact
		sb.append(pResponseEntry.getArtifactId()).append("-");
		sb.append(pResponseEntry.getVersion());

		// packaging
		for (String currentEC : pResponseEntry.getEc()) {
			if (validPackaging(currentEC, pRequestedPackaging)) {
				retVal.add(sb.toString() + currentEC);
			} else {
				LOGGER.fine("ignoring packaging '" + currentEC + "', accepted is: '" + pRequestedPackaging + "'");
			}
		}

		return retVal;
	}

	/**
	 * Validates the given packaging and will figure out if the packaging is accepted.
	 * 
	 * @param pRequestedPackaging
	 * @param pCurrentPackaging
	 * @return TRUE if pRequestedPackaging is empty or pRequestedPackaging equals pCurrentPackaging. Will add a leading
	 *         "." to pCurrentPackaging
	 */
	boolean validPackaging(final String pCurrentPackaging, final String pRequestedPackaging) {
		if (StringUtils.isEmpty(pRequestedPackaging.trim())) {
			return true;
		}

		// in case the packaging is not empty, the equals has to check the given package plus "."
		// as MavenCentral API is returning packages with a leading dot.
		return pCurrentPackaging.equalsIgnoreCase("." + pRequestedPackaging);
	}

	boolean containsResponses(final MavenCentralResponseModel pResponse) {
		if (pResponse == null) {
			return false;
		}

		if (pResponse.getResponse() == null) {
			return false;
		}

		if (pResponse.getResponse().getDocs() == null) {
			return false;
		}

		return pResponse.getResponse().getDocs().length > 0;
	}

	String sendAndReceive(final String pURL) throws VersionReaderException {
		try {
			URL obj = new URL(pURL.toString());
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", USER_AGENT);

			int responseCode = con.getResponseCode();
			if (HttpServletResponse.SC_OK != responseCode) {
				throw new VersionReaderException("server replied with an error: " + responseCode);
			}

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), UTF8));
			String inputLine;
			StringBuffer retVal = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				retVal.append(inputLine);
			}
			in.close();

			return retVal.toString();
		} catch (VersionReaderException e) {
			throw e;
		} catch (Exception e) {
			throw new VersionReaderException("general error while retrieving versions from url " + pURL, e);
		}
	}

	private String createURL(final String pGroupId, final String pArtifactId, final String pPackaging,
			final ValidAndInvalidClassifier pClassifier) {
		StringBuilder targetURL = new StringBuilder();
		targetURL.append(getSearchURL());
		targetURL.append("select?q=");

		if (!"".equals(pGroupId)) {
			targetURL.append("g:\"" + pGroupId + "\"+AND+");
		}

		if (!"".equals(pArtifactId)) {
			targetURL.append("a:\"" + pArtifactId + "\"+AND+");
		}

		if (pClassifier != null && !pClassifier.getValid().isEmpty()) {
			targetURL.append("c:\"");
			for (String currentClassifier : pClassifier.getValid()) {
				targetURL.append(currentClassifier);
			}
			targetURL.append("\"+AND+");
		}

		// TODO: Packaging is not working correctly. So we will filter for the results later.
		// if (!"".equals(mPackaging)) {
		// targetURL.append("p:\"" + mPackaging + "\"+AND+");
		// }

		targetURL.append("&rows=" + getSearchLimit() + "&core=gav");

		// Replace too many AND
		String retVal = targetURL.toString();
		retVal = retVal.replace("+AND+&", "&");
		return retVal;
	}

	@Override
	public void setUserName(String pUserName) {
		// Nothing to do
	}

	@Override
	public void setUserPassword(String pUserPassword) {
		// Nothing to do

	}

	@Override
	public void setCredentials(String pUserName, String pUserPassword) {
		// Nothing to do
	}

	public String getSearchURL() {
		return REPO_SEARCH_SERVICE_URL;
	}

	/**
	 * Can over overriden.
	 * 
	 * @return the URL where the artifacts are stored.
	 */
	public String getRetrieveURL() {
		return REPO_RETRIEVE_URL;
	}

	/**
	 * The maximum number of results that are displayed.
	 * 
	 * @return the maximum number of results that are returned.
	 */
	int getSearchLimit() {
		return MAX_SEARCH_RESULTS;
	}

}
