package uk.co.codelity.jdbc.eventstore.entity;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

class StreamPositionSummaryTest {

    @Test
    void createInitialSummary() {
        StreamPositionSummary streamPositionSummary = StreamPositionSummary.createInitialSummary();
        assertThat(streamPositionSummary.maxPosition, is(0));
        assertThat(streamPositionSummary.maxDeliveryOrder, is(0));
        assertThat(streamPositionSummary.latestEventStatus, is(DeliveryStatus.COMPLETED));
    }
}