package uk.co.codelity.event.sourcing.common;

import uk.co.codelity.event.sourcing.common.exceptions.EventPersistenceException;

import java.util.List;

public interface EventWriter {
    void append(String streamId, List<Envelope<?>> events) throws EventPersistenceException;
}
