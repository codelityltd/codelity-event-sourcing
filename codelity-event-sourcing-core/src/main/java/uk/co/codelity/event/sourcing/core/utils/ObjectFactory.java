package uk.co.codelity.event.sourcing.core.utils;

import uk.co.codelity.event.sourcing.core.exceptions.ObjectCreationException;

public interface ObjectFactory {
    <T> T create(Class<T> clazz) throws ObjectCreationException;
}
