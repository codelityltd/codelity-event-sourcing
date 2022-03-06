package uk.co.codelity.jdbc.eventstore.delivery;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.codelity.event.sourcing.common.EventHandlerExecutorService;
import uk.co.codelity.jdbc.eventstore.entity.Event;
import uk.co.codelity.jdbc.eventstore.entity.EventDelivery;
import uk.co.codelity.jdbc.eventstore.repository.EventDeliveryRepository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeliveryWorkerTest {
    private static final String HANDLER_CODE = "HandlerCode";
    private static final String STREAM_ID = "StreamId";
    private static final String PAYLOAD = "Payload";

    @Mock
    private DeliveryWork deliveryWork;

    @Mock
    private DeliveryWorkFactory deliveryWorkFactory;

    @Mock
    private EventHandlerExecutorService eventHandlerExecutorService;

    @Mock
    private EventDeliveryRepository eventDeliveryRepository;

    @Spy
    private JdbcEventDeliveryConfig config = new JdbcEventDeliveryConfig(1,
            2,
            1,
            10,
            500);

    @InjectMocks
    DeliveryWorker deliveryWorker;

    @SuppressWarnings("java:S2925")
    @Test
    void shouldDoTwoWorks() throws InterruptedException, SQLException {
        when(eventDeliveryRepository.getEventsToBeDelivered(any(), any(), any(), any()))
                .thenReturn(List.of(createEventDelivery(1L), createEventDelivery(2L)));


        when(deliveryWorkFactory.create(any(), any(), any())).thenReturn(deliveryWork);

        deliveryWorker.start();
        Thread.sleep(200);
        deliveryWorker.stop();
        verify(deliveryWorkFactory, timeout(1000).times(2)).create(any(), any(), any());
        verify(deliveryWork, timeout(1000).times(2)).run();

    }

    private EventDelivery createEventDelivery(long id) {
        return new EventDelivery(
                id,
                STREAM_ID,
                1,
                id,
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