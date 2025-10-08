package org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus3;

public class Checksum {
    private String md5;

    private String sha1;

    private String sha256;

    private String sha512;

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getSha1() {
        return sha1;
    }

    public void setSha1(String sha1) {
        this.sha1 = sha1;
    }

    public String getSha256() {
        return sha256;
    }

    public void setSha256(String sha256) {
        this.sha256 = sha256;
    }

    public String getSha512() {
        return sha512;
    }

    public void setSha512(String sha512) {
        this.sha512 = sha512;
    }

    @Override
    public String toString() {
        return "ClassPojo [md5 = " + md5 + ", sha256 = " + sha256 + ", sha1 = " + sha1 + ", sha512 = " + sha512 + "]";
    }
}
