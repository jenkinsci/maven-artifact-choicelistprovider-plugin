package org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus3;

import javax.ws.rs.core.MultivaluedMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.ws.rs.core.MultivaluedHashMap;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.IVersionReader2;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.RESTfulParameterBuilder;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.ValidAndInvalidClassifier;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.VersionReaderException;

public class Nexus3RestApiSearchService extends AbstractNexus3RestApiSearchService implements IVersionReader2 {

	private final Nexus3RESTfulParameterBuilderForSearch mMapper;

    private static final Logger LOGGER = Logger.getLogger(Nexus3RestApiSearchService.class.getName());

	public Nexus3RestApiSearchService(String pURL) {
		super(pURL);
		mMapper = new Nexus3RESTfulParameterBuilderForSearch();
	}

	@Override
	protected MultivaluedMap<String, String> createRequestParameters(String pRepository, String pGroup,
			String pName, String token) {
		return mMapper.create(pRepository, pGroup, pName, token);
	}

    @Override
    public List<String> retrieveVersions(MultivaluedMap<String, String> pParams) throws VersionReaderException {
       return this.retrieveVersions(pParams, null);
    }

    @Override
    public List<String> retrieveVersions(MultivaluedMap<String, String> pParams, ValidAndInvalidClassifier pClassifier)
            throws VersionReaderException {
        Set<String> callService = super.callService(pParams, pClassifier);
        return callService.stream().collect(Collectors.toList());
    }

}

class Nexus3RESTfulParameterBuilderForSearch {

    private static final Logger LOGGER = Logger.getLogger(Nexus3RESTfulParameterBuilderForSearch.class.getName());

    public static final String PARAMETER_REPOSITORY = "repository";

    public static final String PARAMETER_NAME = "name";

    public static final String PARAMETER_GROUP = "group";
   
    public static final String PARAMETER_TOKEN = "continuationToken";
    
    public static final String PACKAGING_ALL = "*";
    
    public static final String PARAMETER_SORT = "sort";


    public String getContinuationToken() {
        return PARAMETER_TOKEN;
    }

	public String getSortOrder() {
		return PARAMETER_SORT;
	}

    public String getName() {
        return PARAMETER_NAME;
    }

    public String getRepository() {
        return PARAMETER_REPOSITORY;
    }

    public MultivaluedMap<String, String> create(String pRepository, String pGroup, String pName, String pToken) {
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("create parameters for: repository: " + pRepository + " g:" + pGroup + ", n:" + pName + ", t:" + pToken);
        }

        MultivaluedMap<String, String> requestParams = new MultivaluedHashMap<String, String>();
        if (!StringUtils.isEmpty(pRepository)) {
            requestParams.putSingle(PARAMETER_REPOSITORY, pRepository);
        }
        if (!StringUtils.isEmpty(pGroup)) {
            requestParams.putSingle(PARAMETER_GROUP, pGroup);
        }
        if (!StringUtils.isEmpty(pName)) {
            requestParams.putSingle(PARAMETER_NAME, pName);
        }
      
        if (!StringUtils.isEmpty(pToken)) {
            requestParams.putSingle(getContinuationToken(), pToken);
        }
        
        requestParams.putSingle(getSortOrder(), RESTfulParameterBuilder.DEFAULT_SORTORDER);
        
        return requestParams;
    }

}
