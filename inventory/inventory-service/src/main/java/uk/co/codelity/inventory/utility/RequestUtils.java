package uk.co.codelity.inventory.utility;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import uk.co.codelity.event.sourcing.common.Metadata;

import java.util.UUID;

import static java.util.Objects.isNull;

public class RequestUtils {

    public static final String CORRELATION_ID = "CorrelationId";
    public static final String USER_ID = "UserId";

    private RequestUtils() {
    }

    public static Metadata buildMetadata(HttpHeaders httpHeaders) {
        String correlationId = httpHeaders.getFirst(CORRELATION_ID);
        String userId = httpHeaders.getFirst(USER_ID);

        if (isNull(correlationId)) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "CorrelationId is required");
        }

        if (isNull(userId)) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "UserId is required");
        }

        return new Metadata(
                UUID.fromString(correlationId),
                userId);
    }
}
