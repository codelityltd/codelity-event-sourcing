package uk.co.codelity.event.sourcing.core.utils.reflection;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

class FileResourceLookupTest {

    @Test
    void shouldGetClassesInCurrentPackage() throws IOException, URISyntaxException {
        final String[] expected = new String[] {"FileResourceLookup", "ReflectionUtility", "ResourceLookup", "JarResourceLookup", "ResourceLookupFactory"};
        URL url = getPackageURL(getClass().getPackageName());
        FileResourceLookup fileResourceLookup = new FileResourceLookup(url, getClass().getPackageName());

        List<String> classes = fileResourceLookup.getClasses().stream()
                .map(Class::getSimpleName)
                .collect(Collectors.toList());

        assertThat(classes, is(containsInAnyOrder(expected)));
    }

    private static URL getPackageURL(String packageName) throws IOException {
        String name = packageName.replaceAll("[.]", "/");
        Enumeration<URL> urls = ClassLoader.getSystemClassLoader()
                .getResources(name);

        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            if (url.getPath().contains("/main/")) {
                return url;
            }
        }
        return null;
    }
}