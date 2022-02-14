package uk.co.codelity.event.sourcing.core.utils.reflection;

import java.util.Set;

interface ResourceLookup {
    Set<Class<?>> getClasses() throws Exception;
}
