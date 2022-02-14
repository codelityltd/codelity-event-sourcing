package uk.co.codelity.event.sourcing.core.utils.reflection;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;

class JarResourceLookupTest {

    @Test
    void shouldLoadClassesInCommonJar() throws Exception {
        URL url = getPackageURL("uk.co.codelity.event.sourcing.common");
        JarResourceLookup jarResourceLookup = new JarResourceLookup(url);
        List<String> classNames = jarResourceLookup.getClasses().stream()
                .map(Class::getSimpleName)
                .collect(Collectors.toList());

        assertThat(classNames, hasItem("EventInfo"));
    }

    private static URL getPackageURL(String packageName) throws IOException {
        String name = packageName.replaceAll("[.]", "/");
        Enumeration<URL> urls = ClassLoader.getSystemClassLoader()
                .getResources(name);

        if (!urls.hasMoreElements()) {
            return null;
        }

        return urls.nextElement();
    }
}