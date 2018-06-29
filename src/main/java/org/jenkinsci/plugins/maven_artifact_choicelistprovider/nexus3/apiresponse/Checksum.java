package org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus3.apiresponse;

public class Checksum {
	private String md5;

    private String sha1;

    public String getMd5 ()
    {
        return md5;
    }

    public void setMd5 (String md5)
    {
        this.md5 = md5;
    }

    public String getSha1 ()
    {
        return sha1;
    }

    public void setSha1 (String sha1)
    {
        this.sha1 = sha1;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [md5 = "+md5+", sha1 = "+sha1+"]";
    }
}
