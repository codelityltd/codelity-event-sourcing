package uk.co.codelity.event.sourcing.spring;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import uk.co.codelity.event.sourcing.core.exceptions.ObjectCreationException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpringObjectFactoryTest {

    @Mock
    ApplicationContext applicationContext;

    @Test
    void shouldReturnBean() throws ObjectCreationException {
        Object testBean = new Object();
        SpringObjectFactory springObjectFactory = new SpringObjectFactory(applicationContext);
        when(applicationContext.getBean(any(Class.class))).thenReturn(testBean);
        Object actual = springObjectFactory.create(Object.class);
        assertThat(actual, is(testBean));
    }
}