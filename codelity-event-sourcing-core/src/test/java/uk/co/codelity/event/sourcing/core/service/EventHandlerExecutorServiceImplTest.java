package uk.co.codelity.event.sourcing.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.codelity.event.sourcing.common.EventInfo;
import uk.co.codelity.event.sourcing.common.exceptions.EventHandlerException;
import uk.co.codelity.event.sourcing.core.context.EventSourcingContext;
import uk.co.codelity.event.sourcing.core.context.EventSubscription;
import uk.co.codelity.event.sourcing.core.exceptions.EventHandlerNotFoundException;
import uk.co.codelity.event.sourcing.core.exceptions.EventNotFoundException;
import uk.co.codelity.event.sourcing.core.exceptions.ObjectCreationException;
import uk.co.codelity.event.sourcing.core.utils.ObjectFactory;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventHandlerExecutorServiceImplTest {
    public static final String EXAMPLE_PAYLOAD = "Example payload";
    public static final String HANDLER_CODE = "HandlerCode";
    @Mock
    private EventSourcingContext eventSourcingContext;

    @Mock
    private ObjectFactory objectFactory;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private TestEventHandler testEventHandler;

    @InjectMocks
    private EventHandlerExecutorServiceImpl eventHandlerExecutorService;

    @Test
    void shouldExecuteEventHandler() throws EventHandlerException, EventHandlerNotFoundException, ObjectCreationException, JsonProcessingException, EventNotFoundException {
        EventSubscription eventSubscription = new EventSubscription(HANDLER_CODE,
                TestEventHandler.class,
                (o1, o2) -> handlerProxy((TestEventHandler) o1, (String) o2), false);

        when(objectFactory.create(any())).thenReturn(testEventHandler);
        when(objectMapper.readValue(any(String.class), any(Class.class))).thenReturn(EXAMPLE_PAYLOAD);
        when(eventSourcingContext.getEventSubscription(any(), any())).thenReturn(eventSubscription);
        doReturn(String.class).when(eventSourcingContext).getEventType(any());
        eventHandlerExecutorService.execute(createEventInfo("StreamId", 1), HANDLER_CODE);
        verify(testEventHandler, times(1)).handle(EXAMPLE_PAYLOAD);

    }

    private void handlerProxy(TestEventHandler eventHandlerTest, String payload) {
        eventHandlerTest.handle(payload);
    }

    private EventInfo createEventInfo(final String streamId, final int position) {
        return new EventInfo(
                streamId,
                position,
                "Event-" + position,
                "",
                EXAMPLE_PAYLOAD);
    }

    interface TestEventHandler {
        void handle(String payload);
    }

}