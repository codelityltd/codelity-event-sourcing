package uk.co.codelity.event.sourcing.core.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HandlerLambdaFactoryTest {

    @Mock
    TestAggregate testAggregate;

    @Test
    void createHandlerLambda() throws Throwable {
        TestEvent event = new TestEvent();

        BiConsumer<TestAggregate, TestEvent> consumer = HandlerLambdaFactory.createHandlerLambda(TestAggregate.class, TestEvent.class, "handleEvent");
        consumer.accept(testAggregate, event);
        verify(testAggregate, times(1)).handleEvent(event);
    }

    interface TestAggregate {
        void handleEvent(TestEvent event);
    }

    static class TestEvent {

    }
}