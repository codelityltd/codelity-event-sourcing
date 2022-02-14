package uk.co.codelity.event.sourcing.core.utils.reflection;

import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

class ResourceLookupFactoryTest {

    @Test
    void shouldCreateJarResourceLookup() throws MalformedURLException {
        URL url = new URL(ResourceLookupFactory.JAR, "host", "file");
        ResourceLookup resourceLookup = ResourceLookupFactory.create(url, "packageName");
        assertThat(resourceLookup, is(instanceOf(JarResourceLookup.class)));
    }

    @Test
    void shouldCreateFileResourceLookup() throws MalformedURLException {
        URL url = new URL(ResourceLookupFactory.FILE, "host", "file");
        ResourceLookup resourceLookup = ResourceLookupFactory.create(url, "packageName");
        assertThat(resourceLookup, is(instanceOf(FileResourceLookup.class)));
    }

    @Test
    void shouldReturnNullForProtocolNotSupported() throws MalformedURLException {
        URL url = new URL("HTTP", "host", "file");
        ResourceLookup resourceLookup = ResourceLookupFactory.create(url, "packageName");
        assertThat(resourceLookup, is(nullValue()));
    }
}