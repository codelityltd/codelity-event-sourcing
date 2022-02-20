package uk.co.codelity.event.sourcing.spring;

import org.springframework.context.ApplicationContext;
import uk.co.codelity.event.sourcing.core.exceptions.ObjectCreationException;
import uk.co.codelity.event.sourcing.core.utils.ObjectFactory;

public class SpringObjectFactory implements ObjectFactory {
    ApplicationContext applicationContext;

    public SpringObjectFactory(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public <T> T create(Class<T> aClass) throws ObjectCreationException {
        return applicationContext.getBean(aClass);
    }
}
