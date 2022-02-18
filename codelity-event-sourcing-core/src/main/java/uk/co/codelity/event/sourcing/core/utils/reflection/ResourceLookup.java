package uk.co.codelity.event.sourcing.core.utils.reflection;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Set;

interface ResourceLookup {
    Set<Class<?>> getClasses() throws URISyntaxException, IOException, ClassNotFoundException;
}
