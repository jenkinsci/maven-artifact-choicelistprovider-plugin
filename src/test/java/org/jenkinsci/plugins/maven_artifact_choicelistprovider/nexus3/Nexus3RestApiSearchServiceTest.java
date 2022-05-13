package org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus3;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.ValidAndInvalidClassifier;
import org.junit.Test;

import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class Nexus3RestApiSearchServiceTest {

    private List<WebTarget> instances = new ArrayList<>();

    @Test
    public void testCallService() {
        Nexus3RestApiSearchService service = new TestService("http://nexus");
        service.callService("repositoryId", "groupId", "artifactId", "tar.gz", ValidAndInvalidClassifier.getDefault());

        assertThat(instances).hasSize(2);
		verify(instances.get(0), times(5)).queryParam(anyString(), anyString());
		verify(instances.get(1), times(6)).queryParam(anyString(), anyString());
    }

    private class TestService extends Nexus3RestApiSearchService {

        public TestService(String pURL) {
            super(pURL);
        }

        @Override
        protected WebTarget getInstance() {

            Nexus3RestResponse response = new Nexus3RestResponse();
            response.setItems(new Item[0]);

            if (instances.isEmpty()) {
				response.setContinuationToken("12345");
			}

            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonResponse = objectMapper.writeValueAsString(response);

                WebTarget mock = mock(WebTarget.class);
                Invocation.Builder builder = mock(Invocation.Builder.class);
                when(builder.get(String.class)).thenReturn(jsonResponse);

                when(mock.queryParam(anyString(), anyString())).thenReturn(mock);
                when(mock.request(anyString())).thenReturn(builder);

				instances.add(mock);
				return mock;

            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
