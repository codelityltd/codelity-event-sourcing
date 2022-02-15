package uk.co.codelity.event.sourcing.spring;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import uk.co.codelity.event.sourcing.core.context.EventSourcingContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootConfiguration
@SpringBootTest(classes = { App.class, AutoConfiguration.class})
class AutoConfigurationTest {
    @Autowired
    EventSourcingContext eventSourcingContext;

    @Test
    void eventHandlingContext() {
        System.out.println(eventSourcingContext);
    }
}