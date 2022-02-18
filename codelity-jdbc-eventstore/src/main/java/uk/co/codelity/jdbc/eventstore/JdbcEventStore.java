package uk.co.codelity.jdbc.eventstore;

import uk.co.codelity.event.sourcing.common.EventInfo;
import uk.co.codelity.event.sourcing.common.EventStore;
import uk.co.codelity.event.sourcing.common.exceptions.EventLoadException;
import uk.co.codelity.event.sourcing.common.exceptions.EventPublishException;

import java.util.List;

public class JdbcEventStore implements EventStore {
    @Override
    public void append(String s, List<Object> list) throws EventPublishException {

    }

    @Override
    public Iterable<EventInfo> loadEvents(String s) throws EventLoadException {
        return null;
    }
}
