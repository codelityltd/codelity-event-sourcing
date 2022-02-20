package uk.co.codelity.event.sourcing.core.utils;

import uk.co.codelity.event.sourcing.core.exceptions.ObjectCreationException;

public class ObjectFactoryImpl implements ObjectFactory {
    @Override
    public <T> T create(Class<T> clazz) throws ObjectCreationException {
        try {
            return clazz.getConstructor().newInstance();
        } catch (Exception ex) {
          throw new ObjectCreationException("Instance could not be created.", ex);
        }
    }
}
