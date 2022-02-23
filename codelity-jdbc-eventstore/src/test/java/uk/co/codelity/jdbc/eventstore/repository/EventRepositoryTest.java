package uk.co.codelity.jdbc.eventstore.repository;

import org.h2.tools.Server;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.codelity.jdbc.eventstore.entity.DeliveryStatus;
import uk.co.codelity.jdbc.eventstore.entity.Event;
import uk.co.codelity.jdbc.eventstore.entity.EventDelivery;
import uk.co.codelity.jdbc.eventstore.repository.helpers.JdbcTestHelper;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

class EventRepositoryTest {
    private EventRepository eventRepository;
    private static Server server;

    @BeforeAll
    static void setUp() throws Exception {
        server = JdbcTestHelper.startServer();
    }

    @AfterAll
    static void tearDown() {
        server.stop();
    }

    @BeforeEach
    void cleanUp() throws SQLException {
        eventRepository = new EventRepository(JdbcTestHelper.URL, JdbcTestHelper.USER, JdbcTestHelper.PASSWORD);
        JdbcTestHelper.cleanUp();
    }

    @Test
    void shouldPersistsAndReadsEvents() throws Exception {
        final String streamId = UUID.randomUUID().toString();
        whenEventsAreSaved(streamId, givenEventsWithStreamId(streamId, 2));

        final List<Event> events = eventRepository.findEventsByStreamIdOrderedByPosition(streamId);
        assertThat(events.size(), is(2));
        assertThat(events.get(0).position, is(1));
        assertThat(events.get(1).position, is(2));

        List<EventDelivery> eventDeliveryList = JdbcTestHelper.getDeliveryListByStreamId(streamId);
        assertThat(eventDeliveryList.size(), is(2));
        assertThat(eventDeliveryList.get(0).deliveryOrder, is(1));
        assertThat(eventDeliveryList.get(0).status, is(DeliveryStatus.READY_TO_TRANSFER));
        assertThat(eventDeliveryList.get(1).deliveryOrder, is(2));
        assertThat(eventDeliveryList.get(1).status, is(DeliveryStatus.PENDING));
    }

    @Test
    void shouldPersistsAndReadsEventsWithoutDelivery() throws Exception {
        final String streamId = UUID.randomUUID().toString();
        whenEventsAreSaved(streamId, List.of(createEventWithoutDelivery(streamId, 1)));

        final List<Event> events = eventRepository.findEventsByStreamIdOrderedByPosition(streamId);
        assertThat(events.size(), is(1));
        assertThat(events.get(0).position, is(1));

        List<EventDelivery> eventDeliveryList = JdbcTestHelper.getDeliveryListByStreamId(streamId);
        assertThat(eventDeliveryList.size(), is(0));
    }


    @Test
    void statusShouldBePendingWhenPreviousDeliveryIsNotCompleted() throws Exception {
        final String streamId = UUID.randomUUID().toString();
        whenEventsAreSaved(streamId, givenEventsWithStreamId(streamId, 1));
        whenEventsAreSaved(streamId, givenEventsWithStreamId(streamId, 1));
        final List<Event> events = eventRepository.findEventsByStreamIdOrderedByPosition(streamId);
        assertThat(events.size(), is(2));
        assertThat(events.get(0).position, is(1));
        assertThat(events.get(1).position, is(2));

        List<EventDelivery> eventDeliveryList = JdbcTestHelper.getDeliveryListByStreamId(streamId);
        assertThat(eventDeliveryList.size(), is(2));
        assertThat(eventDeliveryList.get(0).deliveryOrder, is(1));
        assertThat(eventDeliveryList.get(0).status, is(DeliveryStatus.READY_TO_TRANSFER));
        assertThat(eventDeliveryList.get(1).deliveryOrder, is(2));
        assertThat(eventDeliveryList.get(1).status, is(DeliveryStatus.PENDING));
    }

    @Test
    void statusShouldBeReadyWhenPreviousDeliveryIsCompleted() throws Exception {
        final String streamId = UUID.randomUUID().toString();
        whenEventsAreSaved(streamId, givenEventsWithStreamId(streamId, 1));
        JdbcTestHelper.setAllEventsAsDelivered(streamId);

        whenEventsAreSaved(streamId, givenEventsWithStreamId(streamId, 1));
        final List<Event> events = eventRepository.findEventsByStreamIdOrderedByPosition(streamId);
        assertThat(events.size(), is(2));
        assertThat(events.get(0).position, is(1));
        assertThat(events.get(1).position, is(2));

        List<EventDelivery> eventDeliveryList = JdbcTestHelper.getDeliveryListByStreamId(streamId);
        assertThat(eventDeliveryList.size(), is(2));
        assertThat(eventDeliveryList.get(0).deliveryOrder, is(1));
        assertThat(eventDeliveryList.get(0).status, is(DeliveryStatus.COMPLETED));
        assertThat(eventDeliveryList.get(1).deliveryOrder, is(2));
        assertThat(eventDeliveryList.get(1).status, is(DeliveryStatus.READY_TO_TRANSFER));
    }

    private void whenEventsAreSaved(final String streamId, final List<Event> eventsToBeSaved) throws SQLException {
        eventRepository.saveAll(streamId, eventsToBeSaved);
    }

    private List<Event> givenEventsWithStreamId(final String streamId, final int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> createEventAndDelivery(streamId, i))
                .collect(Collectors.toList());
    }

    private Event createEventAndDelivery(final String streamId, final int position) {
        return new Event(
                null,
                streamId,
                null,
                "Event-" + position,
                "",
                "<payload>",
                LocalDateTime.now(),
                List.of("HANDLER_CODE"));
    }

    private Event createEventWithoutDelivery(final String streamId, final int position) {
        return new Event(
                null,
                streamId,
                null,
                "Event-" + position,
                "",
                "<payload>",
                LocalDateTime.now(),
                Collections.emptyList());
    }
}