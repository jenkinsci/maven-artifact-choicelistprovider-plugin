package org.jenkinsci.plugins.maven_artifact_choicelistprovider.central;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import org.jenkinsci.plugins.maven_artifact_choicelistprovider.IVersionReader;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.ValidAndInvalidClassifier;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.VersionReaderException;

/**
 * 
 * Maven Central offers a search service located at {@link #REPO_SEARCH_SERVICE_URL} which can be used to query the
 * maven central repository via a RESTful interface. This class makes use of this API and return a list of artifacts.
 * <br/>
 * <br/>
 * As the API is public, but Maven Central is the only service offering it, there is no reason to modify the URLs for
 * searching and retrieving the artifacts. <br/>
 * Anyway, if there should be another repository using the same API, this class
 * can be inherited and {@link #getSearchURL()} and {@link #getRetrieveURL()} and {@link #createItemURLs(ResponseDoc)} can be
 * overriden.
 *
 * @author stephan.watermeyer, Diebold Nixdorf
 */
public class MavenCentralSearchService implements IVersionReader {

	public static final String REPO_RETRIEVE_URL = "https://repo1.maven.org/maven2/";

	public static final String REPO_SEARCH_SERVICE_URL = "http://search.maven.org/solrsearch/";

	private static final Logger LOGGER = Logger.getLogger(MavenCentralSearchService.class.getName());

	private static final String USER_AGENT = "Mozilla/5.0";

	private final String mGroupId;
	private final String mArtifactId;
	private final String mPackaging;
	private final ValidAndInvalidClassifier mClassifier;

	public MavenCentralSearchService(String pGroupId, String pArtifactId, String pPackaging) {
		this(pGroupId, pArtifactId, pPackaging, ValidAndInvalidClassifier.getDefault());
	}

	public MavenCentralSearchService(String pGroupId, String pArtifactId, String pPackaging,
			ValidAndInvalidClassifier pAcceptedClassifier) {
		super();
		this.mGroupId = pGroupId;
		this.mArtifactId = pArtifactId;
		this.mPackaging = pPackaging;
		this.mClassifier = pAcceptedClassifier;
	}

	@Override
	public List<String> retrieveVersions() throws VersionReaderException {
		try {
			String targetURL = createURL();

			String response = sendAndReceive(targetURL.toString());
			if (LOGGER.isLoggable(Level.FINEST)) {
				LOGGER.finest(response);
			}

			MavenCentralResponseModel responseObj = MavenCentralResponseParser.parse(response);
			Set<String> retVal = new LinkedHashSet<String>();
			if (containsResponses(responseObj)) {
				for (ResponseDoc current : responseObj.getResponse().getDocs()) {
					retVal.addAll(createItemURLs(current));
				}
			}

			return new ArrayList<String>(retVal);
		} catch (VersionReaderException e) {
			throw e;
		} catch (Exception e) {
			throw new VersionReaderException(
					"failed to retrieve versions from maven-central for r:" + getSearchURL() + ", g:" + getGroupId() + ", a:"
							+ getArtifactId() + ", p:" + getPackaging() + ", c:" + getClassifier() + e.getMessage(),
					e);
		}
	}

	List<String> createItemURLs(final ResponseDoc pResponseEntry) {
		List<String> retVal = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();

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
			if (getPackaging() == "" || getPackaging().equalsIgnoreCase(currentEC)) {
				retVal.add(sb.toString() + currentEC);
			} else {
				LOGGER.fine("packaging is explictly set and thus ignore current: " + currentEC);
			}
		}

		return retVal;
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

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
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

	private String createURL() {
		StringBuilder targetURL = new StringBuilder();
		targetURL.append(getSearchURL());
		targetURL.append("select?q=");

		if (!"".equals(mGroupId)) {
			targetURL.append("g:\"" + mGroupId + "\"+AND+");
		}

		if (!"".equals(mArtifactId)) {
			targetURL.append("a:\"" + mArtifactId + "\"+AND+");
		}

		if (!"".equals(mClassifier) && !mClassifier.getValid().isEmpty()) {
			targetURL.append("c:\"");
			for (String currentClassifier : mClassifier.getValid()) {
				targetURL.append(currentClassifier);
			}
			targetURL.append("\"+AND+");
		}

		// TODO: Packaging is not working correctly. So we will filter for the results later.
		// if (!"".equals(mPackaging)) {
		// targetURL.append("p:\"" + mPackaging + "\"+AND+");
		// }

		targetURL.append("&rows=20&core=gav");

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

	public String getGroupId() {
		return mGroupId;
	}

	public String getArtifactId() {
		return mArtifactId;
	}

	public String getPackaging() {
		return mPackaging;
	}

	public ValidAndInvalidClassifier getClassifier() {
		return mClassifier;
	}

	/**
	 * Can over overriden.
	 * 
	 * @return the URL where the artifacts are stored.
	 */
	public String getRetrieveURL() {
		return REPO_RETRIEVE_URL;
	}

}
