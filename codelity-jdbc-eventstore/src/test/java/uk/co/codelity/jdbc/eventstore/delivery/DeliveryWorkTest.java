package uk.co.codelity.jdbc.eventstore.delivery;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.codelity.event.sourcing.common.EventHandlerExecutorService;
import uk.co.codelity.event.sourcing.common.EventInfo;
import uk.co.codelity.event.sourcing.common.exceptions.EventHandlerException;
import uk.co.codelity.jdbc.eventstore.entity.Event;
import uk.co.codelity.jdbc.eventstore.entity.EventDelivery;
import uk.co.codelity.jdbc.eventstore.repository.EventDeliveryRepository;

import java.sql.SQLException;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeliveryWorkTest {

    private static final String HANDLER_CODE = "HandlerCode";
    private static final String STREAM_ID = "StreamId";
    private static final String PAYLOAD = "Payload";

    @Spy
    private EventDelivery eventDelivery = createEventDelivery();

    @Mock
    private EventHandlerExecutorService eventHandlerExecutorService;

    @Mock
    private EventDeliveryRepository eventDeliveryRepository;

    @InjectMocks
    private DeliveryWork deliveryWork;

    @Captor
    private ArgumentCaptor<EventInfo> eventInfoArgumentCaptor;

    @Test
    void shouldDeliverMessage() throws EventHandlerException, SQLException {
        deliveryWork.run();
        verify(eventHandlerExecutorService, times(1))
                .execute(eventInfoArgumentCaptor.capture(), eq(HANDLER_CODE));

        assertThat(eventInfoArgumentCaptor.getValue().streamId, is(STREAM_ID));
        assertThat(eventInfoArgumentCaptor.getValue().payload, is(PAYLOAD));
        verify(eventDeliveryRepository, times(1)).markDeliveryAsCompleted(eventDelivery);
    }


    @Test
    void shouldMarkAsFailedWhenDeliveryFailed() throws SQLException, EventHandlerException {
        doThrow(new EventHandlerException("EventHandlerException", null))
                .when(eventHandlerExecutorService).execute(any(), any());

        deliveryWork.run();

        verify(eventDeliveryRepository, times(1)).markDeliveryAsFailed(eventDelivery.id);
    }

    private EventDelivery createEventDelivery() {
        return new EventDelivery(
                1L,
                STREAM_ID,
                1,
                1L,
                1,
                1,
                null,
                null,
                HANDLER_CODE,
                new Event(1L,
                        STREAM_ID,
                        1,
                        "name",
                        "metadata",
                        PAYLOAD,
                        LocalDateTime.now())
        );

    }
}