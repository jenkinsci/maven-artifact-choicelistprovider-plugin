package org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus;

import org.sonatype.nexus.rest.model.NexusIndexerResponse;
import org.sonatype.nexus.rest.model.NexusNGArtifact;
import org.sonatype.nexus.rest.model.NexusNGRepositoryDetail;

/**
 * REST Response object for a list of search results, contains the
 * typical 'data' parameter, which is a
 * list of search results.
 * 
 * Patched Verison of the Class as the XML returned by Nexus is not valid according to the XSD
 * https://issues.sonatype.org/browse/NEXUS-6755
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings("all")
@com.thoughtworks.xstream.annotations.XStreamAlias(value = "searchNGResponse")
@javax.xml.bind.annotation.XmlRootElement(name = "searchNGResponse")
@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)

public class PatchedSearchNGResponse extends NexusIndexerResponse implements java.io.Serializable {

	// --------------------------/
	// - Class/Member Variables -/
	// --------------------------/

	/**
	 * The grand total number of results found on index.
	 */
	private int totalCount = 0;

	/**
	 * The starting index of the results.
	 */
	private int from = 0;

	/**
	 * The number of results in this response.
	 */
	private int count = 0;

	/**
	 * Flag that states if too many results were found.
	 */
	private boolean tooManyResults = false;

	/**
	 * Flag that states if result set is collapsed, and shows
	 * latest versions only.
	 */
	private boolean collapsed = false;

	/**
	 * Field repoDetails.
	 */
	@javax.xml.bind.annotation.XmlElementWrapper(name = "repoDetails")
	@javax.xml.bind.annotation.XmlElement(name = "org.sonatype.nexus.rest.model.NexusNGRepositoryDetail")
	private java.util.List<NexusNGRepositoryDetail> repoDetails;

	/**
	 * Field data.
	 */
	@javax.xml.bind.annotation.XmlElementWrapper(name = "data")
	@javax.xml.bind.annotation.XmlElement(name = "artifact")
	private java.util.List<NexusNGArtifact> data;

	// -----------/
	// - Methods -/
	// -----------/

	/**
	 * Method addData.
	 * 
	 * @param nexusNGArtifact TBD
	 */
	public void addData(NexusNGArtifact nexusNGArtifact) {
		getData().add(nexusNGArtifact);
	} // -- void addData( NexusNGArtifact )

	/**
	 * Method addRepoDetail.
	 * 
	 * @param nexusNGRepositoryDetail TBD
	 */
	public void addRepoDetail(NexusNGRepositoryDetail nexusNGRepositoryDetail) {
		getRepoDetails().add(nexusNGRepositoryDetail);
	} // -- void addRepoDetail( NexusNGRepositoryDetail )

	/**
	 * Get the number of results in this response.
	 * 
	 * @return int
	 */
	public int getCount() {
		return this.count;
	} // -- int getCount()

	/**
	 * Method getData.
	 * 
	 * @return List
	 */
	public java.util.List<NexusNGArtifact> getData() {
		if (this.data == null) {
			this.data = new java.util.ArrayList<NexusNGArtifact>();
		}

		return this.data;
	} // -- java.util.List<NexusNGArtifact> getData()

	/**
	 * Get the starting index of the results.
	 * 
	 * @return int
	 */
	public int getFrom() {
		return this.from;
	} // -- int getFrom()

	/**
	 * Method getRepoDetails.
	 * 
	 * @return List
	 */
	public java.util.List<NexusNGRepositoryDetail> getRepoDetails() {
		if (this.repoDetails == null) {
			this.repoDetails = new java.util.ArrayList<NexusNGRepositoryDetail>();
		}

		return this.repoDetails;
	} // -- java.util.List<NexusNGRepositoryDetail> getRepoDetails()

	/**
	 * Get the grand total number of results found on index.
	 * 
	 * @return int
	 */
	public int getTotalCount() {
		return this.totalCount;
	} // -- int getTotalCount()

	/**
	 * Get flag that states if result set is collapsed, and shows
	 * latest versions only.
	 * 
	 * @return boolean
	 */
	public boolean isCollapsed() {
		return this.collapsed;
	} // -- boolean isCollapsed()

	/**
	 * Get flag that states if too many results were found.
	 * 
	 * @return boolean TBD
	 */
	public boolean isTooManyResults() {
		return this.tooManyResults;
	} // -- boolean isTooManyResults()

	/**
	 * Method removeData.
	 * 
	 * @param nexusNGArtifact TBD
	 */
	public void removeData(NexusNGArtifact nexusNGArtifact) {
		getData().remove(nexusNGArtifact);
	} // -- void removeData( NexusNGArtifact )

	/**
	 * Method removeRepoDetail.
	 * 
	 * @param nexusNGRepositoryDetail TBD
	 */
	public void removeRepoDetail(NexusNGRepositoryDetail nexusNGRepositoryDetail) {
		getRepoDetails().remove(nexusNGRepositoryDetail);
	} // -- void removeRepoDetail( NexusNGRepositoryDetail )

	/**
	 * Set flag that states if result set is collapsed, and shows
	 * latest versions only.
	 * 
	 * @param collapsed TBD
	 */
	public void setCollapsed(boolean collapsed) {
		this.collapsed = collapsed;
	} // -- void setCollapsed( boolean )

	/**
	 * Set the number of results in this response.
	 * 
	 * @param count TBD
	 */
	public void setCount(int count) {
		this.count = count;
	} // -- void setCount( int )

	/**
	 * Set list of found artifacts.
	 * 
	 * @param data TBD
	 */
	public void setData(java.util.List<NexusNGArtifact> data) {
		this.data = data;
	} // -- void setData( java.util.List )

	/**
	 * Set the starting index of the results.
	 * 
	 * @param from TBD
	 */
	public void setFrom(int from) {
		this.from = from;
	} // -- void setFrom( int )

	/**
	 * Set list of repositories artifacts found in.
	 * 
	 * @param repoDetails TBD
	 */
	public void setRepoDetails(java.util.List<NexusNGRepositoryDetail> repoDetails) {
		this.repoDetails = repoDetails;
	} // -- void setRepoDetails( java.util.List )

	/**
	 * Set flag that states if too many results were found.
	 * 
	 * @param tooManyResults TBD
	 */
	public void setTooManyResults(boolean tooManyResults) {
		this.tooManyResults = tooManyResults;
	} // -- void setTooManyResults( boolean )

	/**
	 * Set the grand total number of results found on index.
	 * 
	 * @param totalCount TBD
	 */
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	} // -- void setTotalCount( int )

}
