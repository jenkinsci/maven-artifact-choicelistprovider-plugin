package org.jenkinsci.plugins.maven_artifact_choicelistprovider;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLHandshakeException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.client.WebTarget;

import org.apache.commons.lang.StringUtils;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

/**
 * 
 * Basic Class for perform artifact searches against an RESTful service API,
 * like Nexus and Artifactory.
 *
 * @author stephan.watermeyer, Diebold Nixdorf
 */
public abstract class AbstractRESTfulVersionReader implements IVersionReader {

	/**
	 * Default Read Timeout when connecting to the Repository Service
	 */
	private static final int SERVICE_READ_TIMEOUT = 5000;

	protected static final String PACKAGING_ALL = "*";

	private static final Logger LOGGER = Logger.getLogger(AbstractRESTfulVersionReader.class.getName());

	private final String mURL;

	private String mUserName;
	private String mUserPassword;

	private WebTarget mInstance;

	public AbstractRESTfulVersionReader(String pURL) {
		super();
		this.mURL = pURL;
	}

	void init() {
		ClientConfig config = new ClientConfig();
		Client client = ClientBuilder.newClient(config);

		if (StringUtils.isNotEmpty(mUserName) && StringUtils.isNotEmpty(mUserPassword)) {
			LOGGER.fine("setting username to: " + mUserName);
			HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(mUserName, mUserPassword);
			client.register(feature);
		} else {
			LOGGER.fine("no username AND password provided");
		}

		client.property(ClientProperties.READ_TIMEOUT, getTimeout());
		mInstance = client.target(getURL()).path(getRESTfulServiceEndpoint());
		LOGGER.info("repository search service at: " + mInstance.getUri().toString());
	}

	public List<String> retrieveVersions(String pRepositoryId, String pGroupId, String pArtifactId, String pPackaging,
			ValidAndInvalidClassifier pClassifier) throws VersionReaderException {
		try {
			final Set<String> result = callService(pRepositoryId, pGroupId, pArtifactId, pPackaging, pClassifier);
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.log(Level.FINE, "result: " + result.size());
				for (String current : result) {
					LOGGER.log(Level.FINE, current);
				}
			}

			return new ArrayList<String>(result);
		} catch (ResponseProcessingException e) {
			final String msg;
			if (e.getResponse() != null) {
				switch (e.getResponse().getStatus()) {
				case 401:
					msg = "Your repository requires user-authentication. Please configure a username and a password to list the content of this repository";
					break;
				case 404:
					msg = "The artifact you are looking for does not exist (HTTP404). Have you used the correct repositoryId? For Nexus 3 please check Plugin Release Notes";
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
			if (e instanceof SSLHandshakeException) {
				throw new VersionReaderException("The certificate of the target repository is untrusted by this JVM",
						e);
			} else if (e instanceof ProcessingException) {
				throw new VersionReaderException(
						"Timeout while connecting to your Repository Service. Please consider the Jenkins-Proxy settings. If using HTTPs also invalid certificates can be the root cause.",
						e);
			} else {
				throw new VersionReaderException("failed to retrieve versions from repository for r:" + getURL()
						+ ", g:" + pGroupId + ", a:" + pArtifactId + ", p:" + pPackaging + ", c:" + pClassifier, e);
			}
		}
	}

	public String getURL() {
		return mURL;
	}

	protected WebTarget getInstance() {
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
	 * Return the configured read timeout in milliseconds. Can be overwritten in
	 * super classes.
	 * 
	 * @return timeout in milliseconds
	 */
	protected int getTimeout() {
		return SERVICE_READ_TIMEOUT;
	}

	/**
	 * Return the configured service endpoint in this repository.
	 * 
	 * @return the service endpoint URI.
	 */
	public abstract String getRESTfulServiceEndpoint();

	public abstract Set<String> callService(final String pRepositoryId, final String pGroupId, final String pArtifactId,
			final String pPackaging, final ValidAndInvalidClassifier pClassifier);
}
