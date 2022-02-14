package uk.co.codelity.event.sourcing.core.utils.reflection;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ReflectionUtilityTest {

    @Test
    void getClasses() throws Exception {

        Enumeration<URL> resources = getResources("uk.co.codelity.event.sourcing.core.utils.reflection");
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            System.out.println(url);
        }

//        FileResourceLookup fileResourceLookup = new FileResourceLookup(url, "uk.co.codelity.event.sourcing.core.utils.reflection");
//        fileResourceLookup.getClasses().forEach(System.out::println);
    }

    private static Enumeration<URL> getResources(String packageName) throws IOException {
        return ClassLoader.getSystemClassLoader()
                .getResources(packageName.replaceAll("[.]", "/"));
    }
}