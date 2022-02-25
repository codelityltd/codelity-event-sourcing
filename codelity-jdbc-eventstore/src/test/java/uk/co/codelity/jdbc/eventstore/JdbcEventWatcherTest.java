package uk.co.codelity.jdbc.eventstore;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.codelity.event.sourcing.common.EventHandlerExecutorService;
import uk.co.codelity.jdbc.eventstore.entity.Event;
import uk.co.codelity.jdbc.eventstore.entity.EventDelivery;
import uk.co.codelity.jdbc.eventstore.repository.EventDeliveryRepository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JdbcEventWatcherTest {
    @Mock
    EventDeliveryRepository eventDeliveryRepository;

    @Mock
    EventHandlerExecutorService eventHandlerExecutorService;

    @Spy
    JdbcEventWatcherConfig config = new JdbcEventWatcherConfig(1, 1, 1, 10);

    @InjectMocks
    JdbcEventWatcher jdbcEventWatcher;


    @Test
    void start() throws SQLException {
        when(eventDeliveryRepository.getEventsToBeDelivered(any(), any(), any(), any()))
                .thenReturn(List.of(createEventDelivery()));
        jdbcEventWatcher.start();

    }

    private EventDelivery createEventDelivery(){
        return new EventDelivery(1L,
                "streamId",
                1,
                1L,
                0,
                0,
                null,
                null,
                "handlerCode",
                new Event(1L,
                        "streamId",
                        1,
                        "event.name",
                        "",
                        "payload",
                        LocalDateTime.now()));
    }
}