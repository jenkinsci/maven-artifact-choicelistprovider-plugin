package org.jenkinsci.plugins.maven_artifact_choicelistprovider.central;

import com.google.gson.annotations.SerializedName;

public class MavenCentralResponseModel {

	@SerializedName("responseHeader")
	ResponseHeader responseHeader;

	@SerializedName("response")
	Response response;

	public MavenCentralResponseModel() {
		// Important to do nothing
	}

	public ResponseHeader getResponseHeader() {
		return responseHeader;
	}

	public void setResponseHeader(ResponseHeader responseHeader) {
		this.responseHeader = responseHeader;
	}

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}

}

class Response {
	int numFound;
	int start;
	ResponseDoc[] docs;

	public Response() {
		// Important to do nothing
	}

	public int getNumFound() {
		return numFound;
	}

	public void setNumFound(int numFound) {
		this.numFound = numFound;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public ResponseDoc[] getDocs() {
		return docs;
	}

	public void setDocs(ResponseDoc[] docs) {
		this.docs = docs;
	}

}

class ResponseDoc {

	String id;

	@SerializedName("g")
	String groupId;

	@SerializedName("a")
	String artifactId;

	@SerializedName("v")
	String version;

	@SerializedName("p")
	String packaging;

	@SerializedName("tags")
	String[] tags;

	@SerializedName("ec")
	String[] ec;

	long timestamp;

	public ResponseDoc() {
		// Important to do nothing
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getPackaging() {
		return packaging;
	}

	public void setPackaging(String packaging) {
		this.packaging = packaging;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String[] getTags() {
		return tags;
	}

	public void setTags(String[] tags) {
		this.tags = tags;
	}

	public String[] getEc() {
		return ec;
	}

	public void setEc(String[] ec) {
		this.ec = ec;
	}

}

class ResponseHeader {

	@SerializedName("status")
	private int status;

	@SerializedName("QTime")
	private int qtime;

	@SerializedName("params")
	private ResponseHeaderParams params;

	public ResponseHeader() {
		// Important to do nothing
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getQtime() {
		return qtime;
	}

	public void setQtime(int qtime) {
		this.qtime = qtime;
	}

	public ResponseHeaderParams getParams() {
		return params;
	}

	public void setParams(ResponseHeaderParams params) {
		this.params = params;
	}

}

class ResponseHeaderParams {

	String fl;
	String sort;
	String indent;
	String q;
	String core;
	String wt;
	String rows;
	String version;

	public ResponseHeaderParams() {
		// Important to do nothing
	}

	public String getFl() {
		return fl;
	}

	public void setFl(String fl) {
		this.fl = fl;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getIndent() {
		return indent;
	}

	public void setIndent(String indent) {
		this.indent = indent;
	}

	public String getQ() {
		return q;
	}

	public void setQ(String q) {
		this.q = q;
	}

	public String getCore() {
		return core;
	}

	public void setCore(String core) {
		this.core = core;
	}

	public String getWt() {
		return wt;
	}

	public void setWt(String wt) {
		this.wt = wt;
	}

	public String getRows() {
		return rows;
	}

	public void setRows(String rows) {
		this.rows = rows;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}
