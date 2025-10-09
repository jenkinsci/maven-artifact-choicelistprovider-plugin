package org.jenkinsci.plugins.maven_artifact_choicelistprovider.central;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.InputStream;
import java.util.Scanner;
import org.junit.jupiter.api.Test;

class MavenCentralResponseParserTest {

    @Test
    void testSth() {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("response.json");
        assertNotNull(resourceAsStream);

        Scanner s = new Scanner(resourceAsStream);
        s.useDelimiter("\\A");
        String in = s.hasNext() ? s.next() : "";
        s.close();

        MavenCentralResponseModel fromJson = MavenCentralResponseParser.parse(in);

        System.out.println(fromJson.getResponseHeader().getStatus());
        System.out.println(fromJson.getResponseHeader().getQtime());
        System.out.println(fromJson.getResponseHeader().getParams().getCore());
        System.out.println(fromJson.getResponseHeader().getParams().getWt());

        System.out.println(fromJson.getResponse().getNumFound());
        System.out.println(fromJson.getResponse().getStart());

        for (ResponseDoc current : fromJson.getResponse().getDocs()) {
            System.out.println(current.getId());
            System.out.println(current.getGroupId());
            System.out.println(current.getArtifactId());
            System.out.println(current.getVersion());
            System.out.println(current.getPackaging());
            for (String c2 : current.getTags()) {
                System.out.println(" " + c2);
            }

            for (String c3 : current.getEc()) {
                System.out.println(" " + c3);
            }
            System.out.println("----");
        }
    }
}
