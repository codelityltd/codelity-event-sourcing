package uk.co.codelity.event.sourcing.core.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.BiConsumer;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HandlerLambdaFactoryTest {

    @Mock
    TestAggregate1 testAggregate;

    @Test
    void createHandlerLambda() throws Throwable {
        TestEvent1 event = new TestEvent1();

        BiConsumer<TestAggregate1, TestEvent1> consumer = HandlerLambdaFactory.createHandlerLambda(TestAggregate1.class, TestEvent1.class, "handleEvent");
        consumer.accept(testAggregate, event);
        verify(testAggregate, times(1)).handleEvent(event);
    }

    interface TestAggregate1 {
        void handleEvent(TestEvent1 event);
    }

    static class TestEvent1 {

    }
}