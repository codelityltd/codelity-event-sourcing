package uk.co.codelity.event.sourcing.common;

import uk.co.codelity.event.sourcing.common.exceptions.EventLoadException;
import uk.co.codelity.event.sourcing.common.exceptions.EventPersistenceException;

import java.util.stream.Stream;

public interface EventStore {

    /***
     * Appends events to the stream.
     * @param streamId id of the stream events to be added
     * @param events events to be added to the stream
     * @throws EventPersistenceException
     */
    void append(String streamId, Stream<EventInfo> events) throws EventPersistenceException;

    /***
     * Load stream by streamId.
     * @param streamId id of the stream.
     * @throws EventLoadException
     */
    EventStream getStreamById(String streamId) throws EventLoadException;
}
