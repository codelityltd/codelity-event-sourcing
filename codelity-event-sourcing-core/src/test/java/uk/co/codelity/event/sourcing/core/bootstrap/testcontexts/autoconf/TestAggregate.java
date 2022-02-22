package uk.co.codelity.event.sourcing.core.bootstrap.testcontexts.autoconf;

import uk.co.codelity.event.sourcing.common.annotation.AggregateEventHandler;

public class TestAggregate {

    @AggregateEventHandler
    public void handleEvent1(Event1 event) {
    }

    @AggregateEventHandler
    public void handleEvent2(Event2 event) {
    }
}
