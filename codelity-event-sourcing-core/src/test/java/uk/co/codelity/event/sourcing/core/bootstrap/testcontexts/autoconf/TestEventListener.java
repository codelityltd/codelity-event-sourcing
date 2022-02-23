package uk.co.codelity.event.sourcing.core.bootstrap.testcontexts.autoconf;

import uk.co.codelity.event.sourcing.common.annotation.EventHandler;

public class TestEventListener {

    @EventHandler
    public void handleEvent1(Event1 event) {
    }

    @EventHandler
    public void handleEvent2(Event2 event) {
    }
}
