package uk.co.codelity.event.sourcing.core.utils.reflection;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static java.util.Objects.requireNonNull;

class JarResourceLookup implements ResourceLookup {
    private final URL url;

    public JarResourceLookup(URL url) {
        this.url = url;
    }

    @Override
    public Set<Class<?>> getClasses() throws IOException, ClassNotFoundException {
        requireNonNull(url);

        JarURLConnection urlcon = (JarURLConnection)url.openConnection();
        try (JarFile jar = urlcon.getJarFile();) {
            Enumeration<JarEntry> entries = jar.entries();
            Set<Class<?>> result = new HashSet<>();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().endsWith(".class")) {
                    String fqn = entry.getName().replace("/", ".").replace(".class", "");
                    Class<?> clazz = Class.forName(fqn);
                    result.add(clazz);
                }
            }
            return result;
        }
    }
}
