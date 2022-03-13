package uk.co.codelity.inventory.utility;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.HttpClientErrorException;
import uk.co.codelity.event.sourcing.common.Metadata;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RequestUtilsTest {

    @Test
    void shouldThrowBadRequestWhenCorrelationIdIsMissing() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("UserId", "12345");
        assertThrows(HttpClientErrorException.class,  () -> RequestUtils.buildMetadata(httpHeaders));
    }

    @Test
    void shouldThrowBadRequestWhenUserIdIsMissing() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("CorrelationId", UUID.randomUUID().toString());
        assertThrows(HttpClientErrorException.class,  () -> RequestUtils.buildMetadata(httpHeaders));
    }

    @Test
    void shouldReturnMetadata() {
        String userId = "12345";
        UUID correlationId = UUID.randomUUID();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("UserId", userId);
        httpHeaders.add("CorrelationId", correlationId.toString());
        Metadata metadata = RequestUtils.buildMetadata(httpHeaders);
        assertThat(metadata.getCorrelationId(), is(correlationId));
        assertThat(metadata.getUserId(), is(userId));

    }
}