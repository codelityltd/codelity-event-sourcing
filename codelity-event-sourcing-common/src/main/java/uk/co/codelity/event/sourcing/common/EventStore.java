package uk.co.codelity.event.sourcing.common;

import uk.co.codelity.event.sourcing.common.exceptions.EventLoadException;
import uk.co.codelity.event.sourcing.common.exceptions.EventPersistenceException;

import java.util.List;

public interface EventStore {

    /***
     * Appends events to the stream.
     * @param streamId id of the stream events to be added
     * @param events events to be added to the stream
     * @throws EventPersistenceException
     */
    void append(String streamId, List<Object> events) throws EventPersistenceException;

    /***
     * Load events from a stream.
     * @param streamId id of the stream.
     * @throws EventLoadException
     */
    Iterable<EventInfo> loadEvents(String streamId) throws EventLoadException;
}