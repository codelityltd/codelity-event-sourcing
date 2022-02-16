package uk.co.codelity.event.sourcing.core.utils.reflection;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.codelity.event.sourcing.common.annotation.Event;
import uk.co.codelity.event.sourcing.common.annotation.EventHandler;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReflectionUtilityTest {

    @Mock
    private ResourceLookup resourceLookup;

    private MockedStatic<ResourceLookupFactory> resourceLookupFactoryMock;

    @BeforeEach
    void setUp() throws Exception {
        doReturn(new HashSet<>(asList(SampleEventClass.class, SampleHandlerClass.class))).when(resourceLookup).getClasses();
        resourceLookupFactoryMock = Mockito.mockStatic(ResourceLookupFactory.class);
        resourceLookupFactoryMock.when(() -> ResourceLookupFactory.create(any(), any())).thenReturn(resourceLookup);
    }

    @AfterEach
    void tearDown() {
        resourceLookupFactoryMock.close();
    }

    @Test
    void shouldGetClasses() throws Exception {
        Set<Class<?>> classes = ReflectionUtility.getClasses(getClass().getPackageName());
        assertThat(classes, containsInAnyOrder(SampleEventClass.class, SampleHandlerClass.class));
    }

    @Test
    void shouldGetClassesWithAnnotation() throws Exception {
        Set<Class<?>> classes = ReflectionUtility.getClassesWithAnnotation(getClass().getPackageName(), Event.class);
        assertThat(classes, containsInAnyOrder(SampleEventClass.class));
    }

    @Test
    void shouldGetAnyClassWithAnnotation() throws Exception {
        Optional<Class<?>> clazz = ReflectionUtility.getAnyClassWithAnnotation(getClass().getPackageName(), Event.class);
        assertThat(clazz.isPresent(), is(true));
        assertThat(clazz.get(), is(SampleEventClass.class));
    }

    @Test
    void shouldGetMethodsWithAnnotation() throws Exception {
        List<String> methods = ReflectionUtility.getMethodsWithAnnotation(getClass().getPackageName(), EventHandler.class).stream()
                        .map(Method::getName)
                        .collect(Collectors.toList());

        assertThat(methods, containsInAnyOrder("handleEvent"));
    }

    @Event(name="sample-event")
    static class SampleEventClass {

    }

    static class SampleHandlerClass {

        @EventHandler
        public void handleEvent(SampleEventClass event) {

        }
    }

}