package uk.co.codelity.event.sourcing.core.utils.reflection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.MalformedURLException;
import java.net.URL;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ResourceLookupFactoryTest {

    @Test
    void create() throws MalformedURLException {
        URL url = new URL(ResourceLookupFactory.JAR, "host", "file");
        ResourceLookup resourceLookup = ResourceLookupFactory.create(url, "packageName");
        assertThat(resourceLookup, is(instanceOf(JarResourceLookup.class)));
    }

    @Test
    void create2() throws MalformedURLException {
        URL url = new URL(ResourceLookupFactory.FILE, "host", "file");
        ResourceLookup resourceLookup = ResourceLookupFactory.create(url, "packageName");
        assertThat(resourceLookup, is(instanceOf(FileResourceLookup.class)));
    }

    @Test
    void create3() throws MalformedURLException {
        URL url = new URL("HTTP", "host", "file");
        ResourceLookup resourceLookup = ResourceLookupFactory.create(url, "packageName");
        assertThat(resourceLookup, is(nullValue()));
    }
}