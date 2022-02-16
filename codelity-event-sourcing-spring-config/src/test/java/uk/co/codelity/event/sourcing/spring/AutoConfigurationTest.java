package uk.co.codelity.event.sourcing.spring;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import uk.co.codelity.event.sourcing.core.bootstrap.Bootstrapper;
import uk.co.codelity.event.sourcing.core.context.EventSourcingContext;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AutoConfigurationTest {
    @Mock
    ApplicationContext applicationContext;

    @Mock
    Bootstrapper bootstrapper;

    @Captor
    ArgumentCaptor<Class<?>> appClassCaptor;

    @Test
    void shouldRunBootstrapperInitContext() throws Exception {
        Map<String, Object> beans = new HashMap<>();
        beans.put("app", new App());
        when(applicationContext.getBeansWithAnnotation(eq(SpringBootApplication.class))).thenReturn(beans);
        AutoConfiguration autoConfiguration = new AutoConfiguration();
        EventSourcingContext eventSourcingContext = autoConfiguration.eventHandlingContext(applicationContext, bootstrapper);
        verify(bootstrapper, times(1)).initContext(appClassCaptor.capture());
        assertThat(appClassCaptor.getValue(), is(App.class));
    }

    @SpringBootApplication
    static class App {

    }
}