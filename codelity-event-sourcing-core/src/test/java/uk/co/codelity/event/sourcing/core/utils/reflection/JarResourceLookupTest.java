package uk.co.codelity.event.sourcing.core.utils.reflection;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

class JarResourceLookupTest {

    @Test
    void getClasses() throws Exception {
        URL url = getPackageURL("uk.co.codelity.event.sourcing.common");
        System.out.println(url);
        JarResourceLookup jarResourceLookup = new JarResourceLookup(url);
        List<String> classNames = jarResourceLookup.getClasses().stream().map(Class::getSimpleName).toList();
        System.out.println(classNames);
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